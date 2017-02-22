package com.mattunderscore.specky;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toMap;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
import com.mattunderscore.specky.error.listeners.InternalSemanticErrorListener;
import com.mattunderscore.specky.model.AbstractTypeDesc;
import com.mattunderscore.specky.model.BeanDesc;
import com.mattunderscore.specky.model.ConstructionMethod;
import com.mattunderscore.specky.model.PropertyDesc;
import com.mattunderscore.specky.model.generator.scope.SectionScopeResolver;
import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.SpeckyLexer;
import com.mattunderscore.specky.type.resolver.SpecTypeResolver;
import com.squareup.javapoet.CodeBlock;

/**
 * Unit tests for {@link AbstractTypeListener}.
 *
 * @author Matt Champion 09/01/2017
 */
public final class BeanListenerTest {
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
            new SectionScopeResolver(typeResolver);
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

        final BeanListener beanListener = new BeanListener(sectionScopeResolver, abstractTypes, errorListener);
        ParseTreeWalker.DEFAULT.walk(beanListener, spec);

        final List<BeanDesc> beanDescs = beanListener.getBeanDescs();

        assertEquals(2, beanDescs.size());

        final PropertyDesc p0 = PropertyDesc
            .builder()
            .name("num")
            .type("java.lang.Integer")
            .typeParameters(emptyList())
            .override(false)
            .optional(false)
            .defaultValue(CodeBlock.of("5"))
            .constraint(NFConjoinedDisjointPredicates.builder().predicates(emptyList()).build())
            .build();
        final PropertyDesc p1 = PropertyDesc
            .builder()
            .name("str")
            .type("java.lang.String")
            .typeParameters(emptyList())
            .override(false)
            .optional(false)
            .defaultValue(CodeBlock.of("\"\""))
            .constraint(NFConjoinedDisjointPredicates.builder().predicates(emptyList()).build())
            .build();

        final BeanDesc beanDesc0 = beanDescs.get(0);

        assertEquals("FirstBean", beanDesc0.getName());
        assertEquals("Matt Champion", beanDesc0.getAuthor());
        assertEquals("Bean type $L.\n\nAuto-generated from specification.", beanDesc0.getDescription());
        assertEquals("com.example", beanDesc0.getPackageName());
        assertEquals("default licence of named section", beanDesc0.getLicence());
        assertEquals(emptyList(), beanDesc0.getSupertypes());
        assertEquals(ConstructionMethod.CONSTRUCTOR, beanDesc0.getConstructionMethod());
        assertThat(
            beanDesc0.getProperties(),
            containsInAnyOrder(
                p0,
                p1));

        final BeanDesc beanDesc1 = beanDescs.get(1);

        assertEquals("SecondBean", beanDesc1.getName());
        assertEquals("Matt Champion", beanDesc1.getAuthor());
        assertEquals("Bean type $L.\n\nAuto-generated from specification.", beanDesc1.getDescription());
        assertEquals("com.example", beanDesc1.getPackageName());
        assertEquals("named licence", beanDesc1.getLicence());
        assertEquals(emptyList(), beanDesc1.getSupertypes());
        assertEquals(ConstructionMethod.CONSTRUCTOR, beanDesc1.getConstructionMethod());
        assertThat(
            beanDesc1.getProperties(),
            containsInAnyOrder(
                p0,
                p1));
    }

    @Test
    public void readme() throws IOException {
        final CharStream stream = new ANTLRInputStream(SectionScopeListenerTest
            .class
            .getClassLoader()
            .getResourceAsStream("readme.spec"));
        final SpeckyLexer lexer = new SpeckyLexer(stream);
        final Specky parser = new Specky(new UnbufferedTokenStream<CommonToken>(lexer));

        final SpecTypeResolver typeResolver =
            new SpecTypeResolver();
        final SectionScopeResolver sectionScopeResolver =
            new SectionScopeResolver(typeResolver);
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

        final BeanListener beanListener = new BeanListener(sectionScopeResolver, abstractTypes, errorListener);
        ParseTreeWalker.DEFAULT.walk(beanListener, spec);

        final List<BeanDesc> beanDescs = beanListener.getBeanDescs();

        assertEquals(1, beanDescs.size());

        final BeanDesc beanDesc = beanDescs.get(0);

        assertEquals("PersonBean", beanDesc.getName());
        assertEquals("Matt Champion", beanDesc.getAuthor());
        assertEquals("Bean implementation of {@link Person}.", beanDesc.getDescription());
        assertEquals("com.mattunderscore.readme", beanDesc.getPackageName());
        assertEquals(singletonList("Person"), beanDesc.getSupertypes());
        assertEquals(ConstructionMethod.CONSTRUCTOR, beanDesc.getConstructionMethod());
        final PropertyDesc p0 = PropertyDesc
            .builder()
            .name("birthTimestamp")
            .type("long")
            .typeParameters(emptyList())
            .description("Timestamp of persons birth.")
            .override(true)
            .optional(false)
            .defaultValue(CodeBlock.of("0L"))
            .constraint(NFConjoinedDisjointPredicates.builder().predicates(emptyList()).build())
            .build();
        final PropertyDesc p1 = PropertyDesc
            .builder()
            .name("name")
            .type("java.lang.String")
            .typeParameters(emptyList())
            .description("Persons name.")
            .override(true)
            .optional(false)
            .defaultValue(CodeBlock.of("\"\""))
            .constraint(NFConjoinedDisjointPredicates.builder().predicates(emptyList()).build())
            .build();
        assertThat(
            beanDesc.getProperties(),
            containsInAnyOrder(
                p0,
                p1));
    }
}
