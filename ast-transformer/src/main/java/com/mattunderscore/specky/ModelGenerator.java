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

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.UnbufferedTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import com.mattunderscore.specky.model.AbstractTypeDesc;
import com.mattunderscore.specky.model.BeanDesc;
import com.mattunderscore.specky.model.ImplementationDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.mattunderscore.specky.model.TypeDesc;
import com.mattunderscore.specky.model.ValueDesc;
import com.mattunderscore.specky.model.generator.scope.SectionScopeResolver;
import com.mattunderscore.specky.parser.Specky;
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

    /**
     * Constructor.
     */
    public ModelGenerator(SemanticErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    /**
     * @return the list of {@link SpecDesc} from a {@link CharStream}
     */
    public SpecDesc build(List<CharStream> input) {
        final SpecTypeResolver typeResolver = new SpecTypeResolver();

        final List<FileContext> contexts = input
            .stream()
            .map(stream -> firstPass(stream, typeResolver))
            .collect(toList());

        final List<AbstractTypeDesc> abstractTypes = contexts
            .stream()
            .map(context -> secondPass(context.specContext, context.sectionScopeResolver))
            .flatMap(Collection::stream)
            .collect(toList());

        final Map<String, AbstractTypeDesc> nameToAbstractType = abstractTypes
            .stream()
            .collect(toMap(
                abstractTypeDesc -> abstractTypeDesc.getPackageName() + "." + abstractTypeDesc.getName(),
                abstractTypeDesc -> abstractTypeDesc));

        final List<ValueDesc> valueDescs = contexts
            .stream()
            .map(context -> thirdPass(context.specContext, context.sectionScopeResolver, nameToAbstractType))
            .flatMap(Collection::stream)
            .collect(toList());

        final List<BeanDesc> beanDescs = contexts
            .stream()
            .map(context -> fourthPass(context.specContext, context.sectionScopeResolver, nameToAbstractType))
            .flatMap(Collection::stream)
            .collect(toList());

        final List<ImplementationDesc> implementations = Stream
            .concat(
                beanDescs.stream(),
                valueDescs.stream())
            .collect(toList());

        final List<TypeDesc> types = Stream
            .concat(
                abstractTypes.stream(),
                implementations.stream())
            .collect(toList());

        return SpecDesc
            .builder()
            .abstractTypes(abstractTypes)
            .implementations(implementations)
            .types(types)
            .build();
    }

    private FileContext firstPass(CharStream input, MutableTypeResolver typeResolver) {
        final SpeckyLexer lexer = new SpeckyLexer(input);

        final SectionScopeResolver sectionScopeResolver =
            new SectionScopeResolver(typeResolver);

        final Specky parser = new Specky(new UnbufferedTokenStream<CommonToken>(lexer));

        final FileTypeListener fileTypeListener =
            new FileTypeListener(typeResolver);
        final SectionLicenceListener sectionLicenceListener =
            new SectionLicenceListener(sectionScopeResolver, errorListener);
        final SectionImportTypeListener sectionImportTypeListener =
            new SectionImportTypeListener(sectionScopeResolver);
        final SectionImportValueListener sectionImportValueListener =
            new SectionImportValueListener(sectionScopeResolver);
        final SectionScopeListener sectionScopeListener =
            new SectionScopeListener(sectionScopeResolver);
        final SectionAuthorListener sectionAuthorListener =
            new SectionAuthorListener(sectionScopeResolver);
        final SectionPackageListener sectionPackageListener =
            new SectionPackageListener(sectionScopeResolver);

        parser.addParseListener(fileTypeListener);
        parser.addParseListener(sectionLicenceListener);
        parser.addParseListener(sectionImportTypeListener);
        parser.addParseListener(sectionImportValueListener);
        parser.addParseListener(sectionScopeListener);
        parser.addParseListener(sectionAuthorListener);
        parser.addParseListener(sectionPackageListener);

        return new FileContext(parser.spec(), sectionScopeResolver);
    }

    private List<AbstractTypeDesc> secondPass(Specky.SpecContext spec, SectionScopeResolver sectionScopeResolver) {
        final AbstractTypeListener abstractTypeListener = new AbstractTypeListener(sectionScopeResolver, errorListener);
        ParseTreeWalker.DEFAULT.walk(abstractTypeListener, spec);
        return abstractTypeListener.getAbstractTypeDescs();
    }

    private List<ValueDesc> thirdPass(
            Specky.SpecContext spec,
            SectionScopeResolver sectionScopeResolver,
            Map<String, AbstractTypeDesc> nameToAbstractType) {
        final ValueListener valueListener = new ValueListener(sectionScopeResolver, nameToAbstractType, errorListener);
        ParseTreeWalker.DEFAULT.walk(valueListener, spec);
        return valueListener.getValueDescs();
    }

    private List<BeanDesc> fourthPass(
            Specky.SpecContext spec,
            SectionScopeResolver sectionScopeResolver,
            Map<String, AbstractTypeDesc> nameToAbstractType) {
        final BeanListener beanListener = new BeanListener(sectionScopeResolver, nameToAbstractType, errorListener);
        ParseTreeWalker.DEFAULT.walk(beanListener, spec);
        return beanListener.getBeanDescs();
    }

    private static final class FileContext {
        private final Specky.SpecContext specContext;
        private final SectionScopeResolver sectionScopeResolver;

        private FileContext(Specky.SpecContext specContext, SectionScopeResolver sectionScopeResolver) {
            this.specContext = specContext;
            this.sectionScopeResolver = sectionScopeResolver;
        }
    }
}
