package com.mattunderscore.specky;

import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.UnbufferedTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.mattunderscore.specky.constraint.model.NFConjoinedDisjointPredicates;
import com.mattunderscore.specky.construction.method.resolver.ConstructionMethodResolver;
import com.mattunderscore.specky.construction.method.resolver.MutableConstructionMethodResolver;
import com.mattunderscore.specky.error.listeners.InternalSemanticErrorListener;
import com.mattunderscore.specky.literal.model.IntegerLiteral;
import com.mattunderscore.specky.model.AbstractTypeDesc;
import com.mattunderscore.specky.model.PropertyDesc;
import com.mattunderscore.specky.model.generator.scope.SectionScopeResolver;
import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.SpeckyLexer;
import com.mattunderscore.specky.type.resolver.SpecTypeResolver;

/**
 * Unit tests for {@link AbstractTypeListener}.
 *
 * @author Matt Champion 09/01/2017
 */
public final class AbstractTypeListenerTest {
    @Mock
    private InternalSemanticErrorListener errorListener;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @After
    public void postConditions() {
        verifyNoMoreInteractions(errorListener);
    }

    @Test
    public void test() throws IOException {
        final CharStream stream = new ANTLRInputStream(SectionScopeListenerTest
            .class
            .getClassLoader()
            .getResourceAsStream("SectionTest.spec"));
        final SpeckyLexer lexer = new SpeckyLexer(stream);
        final Specky parser = new Specky(new UnbufferedTokenStream<CommonToken>(lexer));

        final ConstructionMethodResolver constructionMethodResolver = new MutableConstructionMethodResolver();
        final SpecTypeResolver typeResolver =
            new SpecTypeResolver();
        final SectionScopeResolver sectionScopeResolver =
            new SectionScopeResolver(typeResolver, Paths.get("./path"));
        final FileTypeListener fileTypeListener =
            new FileTypeListener(errorListener, typeResolver);
        final SectionLicenceListener sectionLicenceListener =
            new SectionLicenceListener(sectionScopeResolver, errorListener);
        final SectionImportTypeListener sectionImportTypeListener =
            new SectionImportTypeListener(errorListener, sectionScopeResolver);
        final SectionImportValueListener sectionImportValueListener =
            new SectionImportValueListener(
                new ValueParser(errorListener, constructionMethodResolver),
                errorListener,
                sectionScopeResolver);
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

        final Specky.SpecContext spec = parser.spec();

        final AbstractTypeListener abstractTypeListener = new AbstractTypeListener(
            sectionScopeResolver,
            errorListener,
            new ValueParser(errorListener, constructionMethodResolver));
        ParseTreeWalker.DEFAULT.walk(abstractTypeListener, spec);

        final List<AbstractTypeDesc> abstractTypeDescs = abstractTypeListener.getAbstractTypeDescs();

        assertEquals(1, abstractTypeDescs.size());

        final AbstractTypeDesc abstractTypeDesc = abstractTypeDescs.get(0);

        assertEquals("TestType", abstractTypeDesc.getName());
        assertEquals("Matt Champion", abstractTypeDesc.getAuthor());
        assertEquals("Abstract type TestType.\n\nAuto-generated from specification path.", abstractTypeDesc.getDescription());
        assertEquals("com.example", abstractTypeDesc.getPackageName());
        assertEquals("default licence", abstractTypeDesc.getLicence());
        assertEquals(emptyList(), abstractTypeDesc.getSupertypes());
        assertThat(
            abstractTypeDesc.getProperties(),
            contains(PropertyDesc
                .builder()
                .name("num")
                .type("java.lang.Integer")
                .defaultValue(IntegerLiteral.builder().integerLiteral("0").build())
                .constraint(NFConjoinedDisjointPredicates.builder().predicates(emptyList()).build())
                .build()));
    }
}
