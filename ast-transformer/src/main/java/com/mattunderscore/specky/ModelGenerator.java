/* Copyright © 2017 Matthew Champion All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
 * Neither the name of mattunderscore.com nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL MATTHEW CHAMPION BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package com.mattunderscore.specky;

import static com.mattunderscore.specky.CompositeSyntaxErrorListener.composeSyntaxListeners;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.UnbufferedTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import com.mattunderscore.specky.construction.method.resolver.ConstructionMethodResolver;
import com.mattunderscore.specky.construction.method.resolver.MutableConstructionMethodResolver;
import com.mattunderscore.specky.context.file.FileContext;
import com.mattunderscore.specky.error.listeners.InternalSemanticErrorListener;
import com.mattunderscore.specky.error.listeners.SemanticErrorListener;
import com.mattunderscore.specky.error.listeners.SyntaxErrorListener;
import com.mattunderscore.specky.model.AbstractTypeDesc;
import com.mattunderscore.specky.model.ImplementationDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.mattunderscore.specky.model.TypeDesc;
import com.mattunderscore.specky.model.generator.scope.SectionScopeResolver;
import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.Specky.SpecContext;
import com.mattunderscore.specky.parser.SpeckyLexer;
import com.mattunderscore.specky.type.resolver.MutableTypeResolver;
import com.mattunderscore.specky.type.resolver.SpecTypeResolver;

/**
 * Processor for the ANTLR4 generated AST. Returns a better representation of the DSL.
 *
 * @author Matt Champion 14/01/2017
 */
public final class ModelGenerator {

    private final SemanticErrorListener errorListener;
    private final CountingSyntaxErrorListener errorCounter;
    private final SyntaxErrorListener syntaxErrorListener;

    /**
     * Constructor.
     */
    public ModelGenerator(SemanticErrorListener errorListener, SyntaxErrorListener syntaxErrorListener) {
        this.errorListener = errorListener;
        errorCounter = new CountingSyntaxErrorListener();
        this.syntaxErrorListener = composeSyntaxListeners(errorCounter, syntaxErrorListener);
    }

    /**
     * @return the {@link SpecDesc} from a list of {@link FileContext} or null if there are syntax errors
     */
    public SpecDesc build(List<FileContext> input) {
        final SpecTypeResolver typeResolver = new SpecTypeResolver();

        @SuppressWarnings("PMD.PrematureDeclaration")
        final List<ParseContext> contexts = input
            .stream()
            .map(stream -> firstPass(stream, typeResolver))
            .collect(toList());

        if (errorCounter.getErrorCount() > 0) {
            return null;
        }

        contexts.forEach(this::secondPass);

        // Parse the abstract types
        final List<AbstractTypeDesc> abstractTypes = contexts
            .stream()
            .map(this::thirdPass)
            .flatMap(Collection::stream)
            .collect(toList());

        // Collect a map of names to abstract types
        final Map<String, AbstractTypeDesc> nameToAbstractType = abstractTypes
            .stream()
            .collect(toMap(
                abstractTypeDesc -> abstractTypeDesc.getPackageName() + "." + abstractTypeDesc.getName(),
                abstractTypeDesc -> abstractTypeDesc));

        // Parse the implementations
        final List<ImplementationDesc> implementations = contexts
            .stream()
            .map(context -> fourthPass(context, nameToAbstractType))
            .flatMap(Collection::stream)
            .collect(toList());

        // Combine all the types
        final List<TypeDesc> types = Stream
            .concat(
                abstractTypes.stream(),
                implementations.stream())
            .collect(toList());

        // Create the description of the specification
        return SpecDesc
            .builder()
            .abstractTypes(abstractTypes)
            .implementations(implementations)
            .types(types)
            .build();
    }

    private ParseContext firstPass(FileContext fileContext, MutableTypeResolver typeResolver) {
        final SpeckyLexer lexer = new SpeckyLexer(fileContext.getAntlrStream());
        lexer.removeErrorListeners();
        final BaseErrorListener syntaxErrListener = new BaseErrorListener() {
            @Override
            public void syntaxError(
                    Recognizer<?, ?> recognizer,
                    Object offendingSymbol,
                    int line,
                    int charPositionInLine,
                    String msg,
                    RecognitionException e) {

                syntaxErrorListener.syntaxError(
                    fileContext.getFile(),
                    recognizer,
                    offendingSymbol,
                    line,
                    charPositionInLine,
                    msg,
                    e);
            }
        };
        lexer.addErrorListener(syntaxErrListener);

        final InternalSemanticErrorListener errListener =
                (message, ruleContext) -> errorListener.onSemanticError(fileContext.getFile(), message, ruleContext);

        final SectionScopeResolver sectionScopeResolver =
            new SectionScopeResolver(typeResolver, fileContext.getFile());

        final MutableConstructionMethodResolver constructionMethodResolver = new MutableConstructionMethodResolver();

        final Specky parser = new Specky(new UnbufferedTokenStream<CommonToken>(lexer));

        final FileTypeListener fileTypeListener =
            new FileTypeListener(errListener, typeResolver);
        final FileConstructionMethodListener fileConstructionMethodListener =
            new FileConstructionMethodListener(errListener, constructionMethodResolver);

        parser.addParseListener(fileTypeListener);
        parser.addParseListener(fileConstructionMethodListener);
        parser.removeErrorListeners();
        parser.addErrorListener(syntaxErrListener);

        return new ParseContext(
            fileContext.getFile(),
            parser.spec(),
            sectionScopeResolver,
            errListener,
            constructionMethodResolver);
    }

    private void secondPass(ParseContext context) {
        final InternalSemanticErrorListener errListener = context.errListener;

        final SectionScopeResolver sectionScopeResolver = context.scopeResolver;

        final SectionLicenceListener sectionLicenceListener =
            new SectionLicenceListener(sectionScopeResolver, errListener);
        final SectionImportTypeListener sectionImportTypeListener =
            new SectionImportTypeListener(errListener, sectionScopeResolver);
        final SectionImportValueListener sectionImportValueListener =
            new SectionImportValueListener(
                new ValueParser(errListener, context.constructionMethodResolver),
                errListener,
                sectionScopeResolver);
        final SectionScopeListener sectionScopeListener =
            new SectionScopeListener(sectionScopeResolver);
        final SectionAuthorListener sectionAuthorListener =
            new SectionAuthorListener(sectionScopeResolver);
        final SectionPackageListener sectionPackageListener =
            new SectionPackageListener(sectionScopeResolver);
        final CopyrightHolderListener copyrightHolderListener =
            new CopyrightHolderListener(sectionScopeResolver);

        final DelegatingParseListener parseListener = new DelegatingParseListener(
            sectionLicenceListener,
            sectionImportTypeListener,
            sectionImportValueListener,
            sectionScopeListener,
            sectionAuthorListener,
            sectionPackageListener,
            copyrightHolderListener);

        ParseTreeWalker.DEFAULT.walk(parseListener, context.specContext);
    }

    private List<AbstractTypeDesc> thirdPass(ParseContext ctx) {

        final InternalSemanticErrorListener errListener =
                (message, ruleContext) -> errorListener.onSemanticError(ctx.file, message, ruleContext);

        final AbstractTypeListener abstractTypeListener = new AbstractTypeListener(
            ctx.scopeResolver,
            errListener,
            new ValueParser(errListener, ctx.constructionMethodResolver));
        ParseTreeWalker.DEFAULT.walk(abstractTypeListener, ctx.specContext);
        return abstractTypeListener.getAbstractTypeDescs();
    }

    private List<ImplementationDesc> fourthPass(ParseContext ctx, Map<String, AbstractTypeDesc> nameToAbstractType) {

        final InternalSemanticErrorListener errListener =
                (message, ruleContext) -> errorListener.onSemanticError(ctx.file, message, ruleContext);

        final ValueListener valueListener = new ValueListener(
            ctx.scopeResolver,
            nameToAbstractType,
            errListener,
            new ValueParser(errListener, ctx.constructionMethodResolver));
        final BeanListener beanListener = new BeanListener(
            ctx.scopeResolver,
            nameToAbstractType,
            errListener,
            new ValueParser(errListener, ctx.constructionMethodResolver));

        final DelegatingParseListener parseListener = new DelegatingParseListener(
            valueListener,
            beanListener);
        ParseTreeWalker.DEFAULT.walk(parseListener, ctx.specContext);

        return Stream
            .concat(
                beanListener.getBeanDescs().stream(),
                valueListener.getValueDescs().stream())
            .collect(toList());
    }

    private static final class ParseContext {
        private final Path file;
        private final Specky.SpecContext specContext;
        private final SectionScopeResolver scopeResolver;
        private final InternalSemanticErrorListener errListener;
        private final ConstructionMethodResolver constructionMethodResolver;

        private ParseContext(
            Path file,
            SpecContext specContext,
            SectionScopeResolver scopeResolver,
            InternalSemanticErrorListener errListener,
            ConstructionMethodResolver constructionMethodResolver) {

            this.file = file;
            this.specContext = specContext;
            this.scopeResolver = scopeResolver;
            this.errListener = errListener;
            this.constructionMethodResolver = constructionMethodResolver;
        }
    }
}
