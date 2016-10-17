package com.mattunderscore.specky.model.generator;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.mattunderscore.specky.CountingSemanticErrorListener;
import com.mattunderscore.specky.SemanticErrorListener;
import com.mattunderscore.specky.SemanticError;
import com.mattunderscore.specky.dsl.model.DSLAbstractTypeDesc;
import com.mattunderscore.specky.dsl.model.DSLPropertyDesc;
import com.mattunderscore.specky.dsl.model.DSLSpecDesc;
import com.mattunderscore.specky.dsl.model.DSLValueDesc;
import com.mattunderscore.specky.model.ConstructionMethod;
import com.mattunderscore.specky.model.ImplementationDesc;
import com.mattunderscore.specky.model.PropertyDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.mattunderscore.specky.model.generator.scope.ScopeResolver;
import com.squareup.javapoet.CodeBlock;

/**
 * Unit tests for {@link ModelGenerator}.
 *
 * @author Matt Champion on 12/07/2016
 */
public final class ModelGeneratorTest {

    @Mock
    private SemanticErrorListener semanticErrorListener;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @After
    public void postConditions() {
        verifyNoMoreInteractions(semanticErrorListener);
    }

    @Test
    public void simple() throws SemanticError {
        final DSLSpecDesc spec = DSLSpecDesc
            .builder()
            .author("")
            .packageName("com.example")
            .types(emptyList())
            .implementations(singletonList(DSLValueDesc
                .builder()
                .name("Example")
                .supertypes(emptyList())
                .constructionMethod(ConstructionMethod.CONSTRUCTOR)
                .properties(singletonList(DSLPropertyDesc
                    .builder()
                    .name("intProp")
                    .typeParameters(emptyList())
                    .optional(false)
                    .type("int")
                    .build()))
                .build()))
            .build();

        final ScopeResolver scopeResolver = new ScopeResolver(new CountingSemanticErrorListener())
            .createScopes(singletonList(spec));
        final TypeDeriver typeDeriver = new TypeDeriver(
            scopeResolver,
            semanticErrorListener);
        final ModelGenerator generator = new ModelGenerator(
            singletonList(spec),
            scopeResolver,
            typeDeriver,
            semanticErrorListener);

        final SpecDesc specDesc = generator.get();

        assertNotNull(specDesc);
        assertEquals(1, specDesc.getImplementations().size());
        final ImplementationDesc implementationDesc = specDesc.getImplementations().get(0);
        assertEquals("com.example", implementationDesc.getPackageName());
        assertEquals("Example", implementationDesc.getName());
        assertEquals(ConstructionMethod.CONSTRUCTOR, implementationDesc.getConstructionMethod());
        assertEquals(1, implementationDesc.getProperties().size());
        final PropertyDesc property = implementationDesc.getProperties().get(0);
        assertEquals("intProp", property.getName());
        assertEquals("int", property.getType());
    }

    @Test
    public void extending() throws SemanticError {
        final DSLSpecDesc spec = DSLSpecDesc
            .builder()
            .author("")
            .packageName("com.example")
            .types(singletonList(DSLAbstractTypeDesc
                .builder()
                .name("SuperType")
                .properties(singletonList(DSLPropertyDesc
                    .builder()
                    .name("objectProp")
                    .type("Object")
                    .typeParameters(emptyList())
                    .defaultValue("Integer.ZERO")
                    .optional(false)
                    .build()))
                .build()))
            .implementations(singletonList(DSLValueDesc
                .builder()
                .name("Example")
                .supertypes(singletonList("com.example.SuperType"))
                .constructionMethod(ConstructionMethod.CONSTRUCTOR)
                .properties(singletonList(DSLPropertyDesc
                    .builder()
                    .name("intProp")
                    .optional(false)
                    .type("int")
                    .typeParameters(emptyList())
                    .build()))
                .build()))
            .build();

        final ScopeResolver scopeResolver = new ScopeResolver(new CountingSemanticErrorListener())
            .createScopes(singletonList(spec));
        final TypeDeriver typeDeriver = new TypeDeriver(scopeResolver, semanticErrorListener);
        final ModelGenerator generator = new ModelGenerator(
            singletonList(spec),
            scopeResolver,
            typeDeriver,
            semanticErrorListener);

        final SpecDesc specDesc = generator.get();

        assertNotNull(specDesc);
        assertEquals(1, specDesc.getImplementations().size());
        final ImplementationDesc implementationDesc = specDesc.getImplementations().get(0);
        assertEquals("com.example", implementationDesc.getPackageName());
        assertEquals("Example", implementationDesc.getName());
        assertEquals(ConstructionMethod.CONSTRUCTOR, implementationDesc.getConstructionMethod());
        assertEquals(2, implementationDesc.getProperties().size());
        final PropertyDesc property0 = implementationDesc.getProperties().get(1);
        assertEquals("intProp", property0.getName());
        assertEquals("int", property0.getType());
        assertEquals(CodeBlock.of("$L", 0), property0.getDefaultValue());
        assertFalse(property0.isOverride());
        final PropertyDesc property1 = implementationDesc.getProperties().get(0);
        assertEquals("objectProp", property1.getName());
        assertEquals("java.lang.Object", property1.getType());
        assertTrue(property1.isOverride());
        assertEquals(CodeBlock.of("Integer.ZERO"), property1.getDefaultValue());
    }

    @Test
    public void optionalPrimative() throws SemanticError {
        final DSLSpecDesc spec = DSLSpecDesc
            .builder()
            .author("")
            .packageName("com.example")
            .types(emptyList())
            .implementations(singletonList(DSLValueDesc
                .builder()
                .name("Example")
                .supertypes(emptyList())
                .constructionMethod(ConstructionMethod.CONSTRUCTOR)
                .properties(singletonList(DSLPropertyDesc
                    .builder()
                    .name("intProp")
                    .typeParameters(emptyList())
                    .optional(true)
                    .type("int")
                    .build()))
                .build()))
            .build();

        final ScopeResolver scopeResolver = new ScopeResolver(new CountingSemanticErrorListener())
            .createScopes(singletonList(spec));
        final TypeDeriver typeDeriver = new TypeDeriver(scopeResolver, semanticErrorListener);
        final ModelGenerator generator = new ModelGenerator(
            singletonList(spec),
            scopeResolver,
            typeDeriver,
            semanticErrorListener);

        final SpecDesc specDesc = generator.get();

        assertNotNull(specDesc);
        assertEquals(1, specDesc.getImplementations().size());
        final ImplementationDesc implementationDesc = specDesc.getImplementations().get(0);
        assertEquals("com.example", implementationDesc.getPackageName());
        assertEquals("Example", implementationDesc.getName());
        assertEquals(ConstructionMethod.CONSTRUCTOR, implementationDesc.getConstructionMethod());
        assertEquals(1, implementationDesc.getProperties().size());
        final PropertyDesc property = implementationDesc.getProperties().get(0);
        assertEquals("intProp", property.getName());
        assertEquals("java.lang.Integer", property.getType());
    }
}
