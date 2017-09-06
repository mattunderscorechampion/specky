package com.mattunderscore.specky;

import com.mattunderscore.specky.constraint.model.NFConjoinedDisjointPredicates;
import com.mattunderscore.specky.error.listeners.InternalSemanticErrorListener;
import com.mattunderscore.specky.literal.model.IntegerLiteral;
import com.mattunderscore.specky.literal.model.StringLiteral;
import com.mattunderscore.specky.model.AbstractTypeDesc;
import com.mattunderscore.specky.model.ConstructionMethod;
import com.mattunderscore.specky.model.PropertyDesc;
import com.mattunderscore.specky.model.ValueDesc;
import com.mattunderscore.specky.model.generator.scope.SectionScopeResolver;
import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.SpeckyLexer;
import com.mattunderscore.specky.type.resolver.SpecTypeResolver;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.UnbufferedTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toMap;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Unit tests for {@link AbstractTypeListener}.
 *
 * @author Matt Champion 09/01/2017
 */
public final class ValueListenerTest {
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
            new SectionImportValueListener(errorListener, sectionScopeResolver);
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

        final AbstractTypeListener abstractTypeListener = new AbstractTypeListener(sectionScopeResolver, errorListener);
        ParseTreeWalker.DEFAULT.walk(abstractTypeListener, spec);
        final Map<String, AbstractTypeDesc> abstractTypes = abstractTypeListener
            .getAbstractTypeDescs()
            .stream()
            .collect(toMap(abstractTypeDesc -> abstractTypeDesc.getPackageName() + "." + abstractTypeDesc.getName(), abstractTypeDesc -> abstractTypeDesc));

        final ValueListener valueListener = new ValueListener(sectionScopeResolver, abstractTypes, errorListener);
        ParseTreeWalker.DEFAULT.walk(valueListener, spec);

        final List<ValueDesc> valueDescs = valueListener.getValueDescs();

        assertEquals(1, valueDescs.size());

        final ValueDesc valueDesc = valueDescs.get(0);

        assertEquals("FirstValue", valueDesc.getName());
        assertEquals("Matt Champion", valueDesc.getAuthor());
        assertEquals("Value type FirstValue.\n\nAuto-generated from specification.", valueDesc.getDescription());
        assertEquals("com.example", valueDesc.getPackageName());
        assertEquals("default licence", valueDesc.getLicence());
        assertEquals(singletonList("TestType"), valueDesc.getSupertypes());
        assertEquals(ConstructionMethod.CONSTRUCTOR, valueDesc.getConstructionMethod());
        final PropertyDesc p0 = PropertyDesc
            .builder()
            .name("num")
            .type("java.lang.Integer")
            .typeParameters(emptyList())
            .override(true)
            .optional(false)
            .defaultValue(IntegerLiteral.builder().integerLiteral("0").build())
            .constraint(NFConjoinedDisjointPredicates.builder().predicates(emptyList()).build())
            .build();
        final PropertyDesc p1 = PropertyDesc
            .builder()
            .name("str")
            .type("java.lang.String")
            .typeParameters(emptyList())
            .override(false)
            .optional(false)
            .defaultValue(StringLiteral.builder().stringLiteral("").build())
            .constraint(NFConjoinedDisjointPredicates.builder().predicates(emptyList()).build())
            .build();
        assertThat(
            valueDesc.getProperties(),
            containsInAnyOrder(
                p0,
                p1));
    }
}
