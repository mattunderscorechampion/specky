package com.mattunderscore.specky;

import com.mattunderscore.specky.constraint.model.NFConjoinedDisjointPredicates;
import com.mattunderscore.specky.context.file.FileContext;
import com.mattunderscore.specky.error.listeners.SemanticErrorListener;
import com.mattunderscore.specky.error.listeners.SyntaxErrorListener;
import com.mattunderscore.specky.literal.model.IntegerLiteral;
import com.mattunderscore.specky.literal.model.StringLiteral;
import com.mattunderscore.specky.literal.model.UnstructuredLiteral;
import com.mattunderscore.specky.model.AbstractTypeDesc;
import com.mattunderscore.specky.model.ConstructionMethod;
import com.mattunderscore.specky.model.ImplementationDesc;
import com.mattunderscore.specky.model.PropertyDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.mattunderscore.specky.model.TypeDesc;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.nio.file.Paths;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Unit tests for {@link ModelGenerator}.
 *
 * @author Matt Champion 07/01/2017
 */
public final class ModelGeneratorTest {
    @Mock
    private SemanticErrorListener errorListener;
    @Mock
    private SyntaxErrorListener syntaxErrorListener;

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
        final ANTLRInputStream stream = new ANTLRInputStream(SectionImportValueListenerTest
            .class
            .getClassLoader()
            .getResourceAsStream("SectionTest.spec"));

        final ModelGenerator modelGenerator = new ModelGenerator(errorListener, syntaxErrorListener);

        final FileContext fileContext = new FileContext();
        fileContext.setFile(Paths.get("."));
        fileContext.setAntlrStream(stream);
        final SpecDesc specDesc = modelGenerator.build(singletonList(fileContext));

        final List<TypeDesc> types = specDesc.getTypes();
        final List<AbstractTypeDesc> abstractTypes = specDesc.getAbstractTypes();
        final List<ImplementationDesc> implementations = specDesc.getImplementations();

        assertEquals(5, types.size());
        assertEquals(1, abstractTypes.size());
        assertEquals(4, implementations.size());

        final AbstractTypeDesc abstractType = abstractTypes.get(0);

        assertEquals("TestType", abstractType.getName());
        assertEquals("Matt Champion", abstractType.getAuthor());
        assertEquals("Abstract type TestType.\n\nAuto-generated from specification ..", abstractType.getDescription());
        assertEquals("com.example", abstractType.getPackageName());
        assertEquals("default licence", abstractType.getLicence());
        assertEquals(emptyList(), abstractType.getSupertypes());
        assertThat(
            abstractType.getProperties(),
            contains(PropertyDesc
                .builder()
                .name("num")
                .type("java.lang.Integer")
                .defaultValue(IntegerLiteral.builder().integerLiteral("0").build())
                .constraint(NFConjoinedDisjointPredicates.builder().predicates(emptyList()).build())
                .build()));

        final ImplementationDesc valueDesc = implementations.get(3);

        assertEquals("FirstValue", valueDesc.getName());
        assertEquals("Matt Champion", valueDesc.getAuthor());
        assertEquals("Value type FirstValue.\n\nAuto-generated from specification ..", valueDesc.getDescription());
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
            .defaultValue(IntegerLiteral.builder().integerLiteral("0").build())
            .constraint(NFConjoinedDisjointPredicates.builder().predicates(emptyList()).build())
            .build();
        final PropertyDesc vp1 = PropertyDesc
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
                vp0,
                vp1));

        final PropertyDesc bp0 = PropertyDesc
            .builder()
            .name("num")
            .type("java.lang.Integer")
            .typeParameters(emptyList())
            .override(false)
            .optional(false)
            .defaultValue(UnstructuredLiteral.builder().literal("5").build())
            .constraint(NFConjoinedDisjointPredicates.builder().predicates(emptyList()).build())
            .build();
        final PropertyDesc bp1 = PropertyDesc
            .builder()
            .name("str")
            .type("java.lang.String")
            .typeParameters(emptyList())
            .override(false)
            .optional(false)
            .defaultValue(StringLiteral.builder().stringLiteral("").build())
            .constraint(NFConjoinedDisjointPredicates.builder().predicates(emptyList()).build())
            .build();

        final ImplementationDesc beanDesc0 = implementations.get(0);

        assertEquals("FirstBean", beanDesc0.getName());
        assertEquals("Matt Champion", beanDesc0.getAuthor());
        assertEquals("Bean type FirstBean.\n\nAuto-generated from specification ..", beanDesc0.getDescription());
        assertEquals("com.example", beanDesc0.getPackageName());
        assertEquals("default licence of named section", beanDesc0.getLicence());
        assertEquals(emptyList(), beanDesc0.getSupertypes());
        assertEquals(ConstructionMethod.CONSTRUCTOR, beanDesc0.getConstructionMethod());
        assertThat(
            beanDesc0.getProperties(),
            containsInAnyOrder(
                bp0,
                bp1));

        final ImplementationDesc beanDesc1 = implementations.get(1);

        assertEquals("SecondBean", beanDesc1.getName());
        assertEquals("Matt Champion", beanDesc1.getAuthor());
        assertEquals("Bean type SecondBean.\n\nAuto-generated from specification ..", beanDesc1.getDescription());
        assertEquals("com.example", beanDesc1.getPackageName());
        assertEquals(emptyList(), beanDesc1.getSupertypes());
        assertEquals(ConstructionMethod.CONSTRUCTOR, beanDesc1.getConstructionMethod());
        assertThat(
            beanDesc1.getProperties(),
            containsInAnyOrder(
                bp0,
                bp1));

        final ImplementationDesc beanDesc2 = implementations.get(2);

        assertEquals("ThirdBean", beanDesc2.getName());
        assertEquals("Matt Champion", beanDesc2.getAuthor());
        assertEquals("Bean type ThirdBean.\n\nAuto-generated from specification ..", beanDesc2.getDescription());
        assertEquals("com.example", beanDesc2.getPackageName());
        assertEquals(emptyList(), beanDesc2.getSupertypes());
        assertEquals(ConstructionMethod.CONSTRUCTOR, beanDesc2.getConstructionMethod());
    }

    @Test
    public void multipleFiles() throws Exception {
        final ANTLRInputStream stream0 = new ANTLRInputStream(SectionImportValueListenerTest
            .class
            .getClassLoader()
            .getResourceAsStream("AbstractType.spec"));
        final ANTLRInputStream stream1 = new ANTLRInputStream(SectionImportValueListenerTest
            .class
            .getClassLoader()
            .getResourceAsStream("Bean.spec"));

        final ModelGenerator modelGenerator = new ModelGenerator(errorListener, syntaxErrorListener);

        final FileContext fileContext0 = new FileContext();
        fileContext0.setFile(Paths.get("."));
        fileContext0.setAntlrStream(stream0);
        final FileContext fileContext1 = new FileContext();
        fileContext1.setFile(Paths.get("."));
        fileContext1.setAntlrStream(stream1);
        final SpecDesc specDesc = modelGenerator.build(asList(fileContext0, fileContext1));

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
            .defaultValue(StringLiteral.builder().stringLiteral("").build())
            .constraint(NFConjoinedDisjointPredicates.builder().predicates(emptyList()).build())
            .build();
        final PropertyDesc p1 = PropertyDesc
            .builder()
            .name("id")
            .type("int")
            .typeParameters(emptyList())
            .override(true)
            .optional(false)
            .defaultValue(IntegerLiteral.builder().integerLiteral("0").build())
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
            .defaultValue(IntegerLiteral.builder().integerLiteral("0L").build())
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
        final ANTLRInputStream stream = new ANTLRInputStream(SectionImportValueListenerTest
            .class
            .getClassLoader()
            .getResourceAsStream("optional.spec"));

        final ModelGenerator modelGenerator = new ModelGenerator(errorListener, syntaxErrorListener);

        final FileContext fileContext = new FileContext();
        fileContext.setFile(Paths.get("."));
        fileContext.setAntlrStream(stream);
        final SpecDesc specDesc = modelGenerator.build(singletonList(fileContext));

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
            .defaultValue(UnstructuredLiteral.builder().literal("null").build())
            .constraint(NFConjoinedDisjointPredicates.builder().predicates(emptyList()).build())
            .build();

        assertThat(
            valueDesc.getProperties(),
            containsInAnyOrder(
                p0));
    }
}
