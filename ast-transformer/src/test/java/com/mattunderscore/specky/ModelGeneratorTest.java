package com.mattunderscore.specky;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.List;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.mattunderscore.specky.constraint.model.NFConjoinedDisjointPredicates;
import com.mattunderscore.specky.model.AbstractTypeDesc;
import com.mattunderscore.specky.model.ConstructionMethod;
import com.mattunderscore.specky.model.ImplementationDesc;
import com.mattunderscore.specky.model.PropertyDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.mattunderscore.specky.model.TypeDesc;
import com.squareup.javapoet.CodeBlock;

/**
 * Unit tests for {@link ModelGenerator}.
 *
 * @author Matt Champion 07/01/2017
 */
public final class ModelGeneratorTest {
    @Mock
    private SemanticErrorListener errorListener;
    @Mock
    private ANTLRErrorListener syntaxErrorListener;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @After
    public void postConditions() {
        verifyNoMoreInteractions(errorListener, syntaxErrorListener);
    }

    @Test
    public void build() throws Exception {
        final CharStream stream = new ANTLRInputStream(SectionImportValueListenerTest
            .class
            .getClassLoader()
            .getResourceAsStream("SectionTest.spec"));

        final ModelGenerator modelGenerator = new ModelGenerator(errorListener, syntaxErrorListener);

        final SpecDesc specDesc = modelGenerator.build(singletonList(stream));

        final List<TypeDesc> types = specDesc.getTypes();
        final List<AbstractTypeDesc> abstractTypes = specDesc.getAbstractTypes();
        final List<ImplementationDesc> implementations = specDesc.getImplementations();

        assertEquals(3, types.size());
        assertEquals(1, abstractTypes.size());
        assertEquals(2, implementations.size());

        final AbstractTypeDesc abstractType = abstractTypes.get(0);

        assertEquals("TestType", abstractType.getName());
        assertEquals("Matt Champion", abstractType.getAuthor());
        assertEquals("Abstract type $L.\n\nAuto-generated from specification.", abstractType.getDescription());
        assertEquals("com.example", abstractType.getPackageName());
        assertEquals("default licence", abstractType.getLicence());
        assertEquals(emptyList(), abstractType.getSupertypes());
        assertThat(
            abstractType.getProperties(),
            contains(PropertyDesc
                .builder()
                .name("num")
                .type("java.lang.Integer")
                .defaultValue(CodeBlock.of("0"))
                .constraint(NFConjoinedDisjointPredicates.builder().predicates(emptyList()).build())
                .build()));

        final ImplementationDesc valueDesc = implementations.get(1);

        assertEquals("FirstValue", valueDesc.getName());
        assertEquals("Matt Champion", valueDesc.getAuthor());
        assertEquals("Value type $L.\n\nAuto-generated from specification.", valueDesc.getDescription());
        assertEquals("com.example", valueDesc.getPackageName());
        assertEquals("default licence", valueDesc.getLicence());
        assertEquals(singletonList("TestType"), valueDesc.getSupertypes());
        assertEquals(ConstructionMethod.CONSTRUCTOR, valueDesc.getConstructionMethod());
        final PropertyDesc vp0 = PropertyDesc
            .builder()
            .name("num")
            .type("java.lang.Integer")
            .typeParameters(emptyList())
            .override(true)
            .optional(false)
            .defaultValue(CodeBlock.of("0"))
            .constraint(NFConjoinedDisjointPredicates.builder().predicates(emptyList()).build())
            .build();
        final PropertyDesc vp1 = PropertyDesc
            .builder()
            .name("str")
            .type("java.lang.String")
            .typeParameters(emptyList())
            .override(false)
            .optional(false)
            .defaultValue(CodeBlock.of("\"\""))
            .constraint(NFConjoinedDisjointPredicates.builder().predicates(emptyList()).build())
            .build();
        assertThat(
            valueDesc.getProperties(),
            containsInAnyOrder(
                vp0,
                vp1));

        final ImplementationDesc beanDesc = implementations.get(0);

        assertEquals("FirstBean", beanDesc.getName());
        assertNull(beanDesc.getAuthor());
        assertEquals("Bean type $L.\n\nAuto-generated from specification.", beanDesc.getDescription());
        assertEquals("com.example", beanDesc.getPackageName());
        assertEquals("default licence of named section", beanDesc.getLicence());
        assertEquals(emptyList(), beanDesc.getSupertypes());
        assertEquals(ConstructionMethod.CONSTRUCTOR, beanDesc.getConstructionMethod());
        final PropertyDesc bp0 = PropertyDesc
            .builder()
            .name("num")
            .type("java.lang.Integer")
            .typeParameters(emptyList())
            .override(false)
            .optional(false)
            .defaultValue(CodeBlock.of("5"))
            .constraint(NFConjoinedDisjointPredicates.builder().predicates(emptyList()).build())
            .build();
        final PropertyDesc bp1 = PropertyDesc
            .builder()
            .name("str")
            .type("java.lang.String")
            .typeParameters(emptyList())
            .override(false)
            .optional(false)
            .defaultValue(CodeBlock.of("\"\""))
            .constraint(NFConjoinedDisjointPredicates.builder().predicates(emptyList()).build())
            .build();
        assertThat(
            beanDesc.getProperties(),
            containsInAnyOrder(
                bp0,
                bp1));
    }

    @Test
    public void multipleFiles() throws Exception {
        final CharStream stream0 = new ANTLRInputStream(SectionImportValueListenerTest
            .class
            .getClassLoader()
            .getResourceAsStream("AbstractType.spec"));
        final CharStream stream1 = new ANTLRInputStream(SectionImportValueListenerTest
            .class
            .getClassLoader()
            .getResourceAsStream("Bean.spec"));

        final ModelGenerator modelGenerator = new ModelGenerator(errorListener, syntaxErrorListener);

        final SpecDesc specDesc = modelGenerator.build(asList(stream0, stream1));

        final List<TypeDesc> types0 = specDesc.getTypes();
        final List<AbstractTypeDesc> abstractTypes = specDesc.getAbstractTypes();
        final List<ImplementationDesc> implementations = specDesc.getImplementations();

        assertEquals(2, types0.size());
        assertEquals(1, abstractTypes.size());
        assertEquals(1, implementations.size());

        final ImplementationDesc implementation = implementations.get(0);

        assertEquals("PersonBean", implementation.getName());
        assertEquals("Matt Champion", implementation.getAuthor());
        assertEquals("com.example", implementation.getPackageName());
        assertEquals(singletonList("PersonType"), implementation.getSupertypes());
        assertEquals(ConstructionMethod.CONSTRUCTOR, implementation.getConstructionMethod());

        assertEquals(3, implementation.getProperties().size());

        final PropertyDesc p0 = PropertyDesc
            .builder()
            .name("name")
            .type("java.lang.String")
            .typeParameters(emptyList())
            .description("Name of person.")
            .override(true)
            .optional(false)
            .defaultValue(CodeBlock.of("\"\""))
            .constraint(NFConjoinedDisjointPredicates.builder().predicates(emptyList()).build())
            .build();
        final PropertyDesc p1 = PropertyDesc
            .builder()
            .name("id")
            .type("int")
            .typeParameters(emptyList())
            .override(true)
            .optional(false)
            .defaultValue(CodeBlock.of("0"))
            .constraint(NFConjoinedDisjointPredicates.builder().predicates(emptyList()).build())
            .build();
        final PropertyDesc p2 = PropertyDesc
            .builder()
            .name("birthTimestamp")
            .type("long")
            .typeParameters(emptyList())
            .description("Timestamp of birth.")
            .override(false)
            .optional(false)
            .defaultValue(CodeBlock.of("0L"))
            .constraint(NFConjoinedDisjointPredicates.builder().predicates(emptyList()).build())
            .build();
        assertThat(
            implementation.getProperties(),
            containsInAnyOrder(
                p0,
                p1,
                p2));
    }

    @Test
    public void optionalPrimitives() throws Exception {
        final CharStream stream = new ANTLRInputStream(SectionImportValueListenerTest
            .class
            .getClassLoader()
            .getResourceAsStream("optional.spec"));

        final ModelGenerator modelGenerator = new ModelGenerator(errorListener, syntaxErrorListener);

        final SpecDesc specDesc = modelGenerator.build(singletonList(stream));

        final List<TypeDesc> types = specDesc.getTypes();
        final List<AbstractTypeDesc> abstractTypes = specDesc.getAbstractTypes();
        final List<ImplementationDesc> implementations = specDesc.getImplementations();

        assertEquals(2, types.size());
        assertEquals(1, abstractTypes.size());
        assertEquals(1, implementations.size());

        final ImplementationDesc valueDesc = implementations.get(0);
        assertEquals("OptionalIntImpl", valueDesc.getName());
        assertEquals("com.example", valueDesc.getPackageName());

        final PropertyDesc p0 = PropertyDesc
            .builder()
            .name("opt")
            .type("java.lang.Integer")
            .typeParameters(emptyList())
            .override(true)
            .optional(true)
            .defaultValue(CodeBlock.of("null"))
            .constraint(NFConjoinedDisjointPredicates.builder().predicates(emptyList()).build())
            .build();

        assertThat(
            valueDesc.getProperties(),
            containsInAnyOrder(
                p0));
    }
}
