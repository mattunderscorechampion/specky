package com.mattunderscore.specky.model.generator;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mattunderscore.specky.model.ConstructionMethod;
import com.mattunderscore.specky.dsl.model.DSLPropertyDesc;
import com.mattunderscore.specky.dsl.model.DSLSpecDesc;
import com.mattunderscore.specky.dsl.model.DSLValueDesc;
import com.mattunderscore.specky.dsl.model.DSLViewDesc;
import com.mattunderscore.specky.model.PropertyDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.mattunderscore.specky.model.TypeDesc;
import com.mattunderscore.specky.type.resolver.SpecTypeResolver;
import com.mattunderscore.specky.type.resolver.TypeResolverBuilder;
import com.mattunderscore.specky.value.resolver.CompositeValueResolver;
import com.mattunderscore.specky.value.resolver.JavaStandardDefaultValueResolver;
import com.mattunderscore.specky.value.resolver.NullValueResolver;

/**
 * Unit tests for {@link ModelGenerator}.
 *
 * @author Matt Champion on 12/07/2016
 */
public final class ModelGeneratorTest {

    @Test
    public void simple() {
        final DSLSpecDesc spec = DSLSpecDesc
            .builder()
            .author("")
            .packageName("com.example")
            .views(emptyList())
            .types(singletonList(DSLValueDesc
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

        final SpecTypeResolver typeResolver = new SpecTypeResolver();
        spec.getViews().forEach(view -> typeResolver.registerTypeName(spec.getPackageName(), view.getName()));
        spec.getTypes().forEach(value -> typeResolver.registerTypeName(spec.getPackageName(), value.getName()));
        final ModelGenerator generator = new ModelGenerator(
            singletonList(spec),
            new TypeResolverBuilder().registerResolver(typeResolver).build(),
            new CompositeValueResolver()
                .with(new JavaStandardDefaultValueResolver())
                .with(new NullValueResolver()));

        final SpecDesc specDesc = generator.get();

        assertNotNull(specDesc);
        assertEquals(1, specDesc.getTypes().size());
        final TypeDesc typeDesc = specDesc.getTypes().get(0);
        assertEquals("com.example", typeDesc.getPackageName());
        assertEquals("Example", typeDesc.getName());
        assertEquals(ConstructionMethod.CONSTRUCTOR, typeDesc.getConstructionMethod());
        assertEquals(1, typeDesc.getProperties().size());
        final PropertyDesc property = typeDesc.getProperties().get(0);
        assertEquals("intProp", property.getName());
        assertEquals("int", property.getType());
    }

    @Test
    public void extending() {
        final DSLSpecDesc spec = DSLSpecDesc
            .builder()
            .author("")
            .packageName("com.example")
            .views(singletonList(DSLViewDesc
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
            .types(singletonList(DSLValueDesc
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

        final SpecTypeResolver typeResolver = new SpecTypeResolver();
        spec.getViews().forEach(view -> typeResolver.registerTypeName(spec.getPackageName(), view.getName()));
        spec.getTypes().forEach(value -> typeResolver.registerTypeName(spec.getPackageName(), value.getName()));
        final ModelGenerator generator = new ModelGenerator(
            singletonList(spec),
            new TypeResolverBuilder().registerResolver(typeResolver).build(),
            new CompositeValueResolver()
                .with(new JavaStandardDefaultValueResolver())
                .with(new NullValueResolver()));

        final SpecDesc specDesc = generator.get();

        assertNotNull(specDesc);
        assertEquals(1, specDesc.getTypes().size());
        final TypeDesc typeDesc = specDesc.getTypes().get(0);
        assertEquals("com.example", typeDesc.getPackageName());
        assertEquals("Example", typeDesc.getName());
        assertEquals(ConstructionMethod.CONSTRUCTOR, typeDesc.getConstructionMethod());
        assertEquals(2, typeDesc.getProperties().size());
        final PropertyDesc property0 = typeDesc.getProperties().get(1);
        assertEquals("intProp", property0.getName());
        assertEquals("int", property0.getType());
        assertEquals("0", property0.getDefaultValue());
        assertFalse(property0.isOverride());
        final PropertyDesc property1 = typeDesc.getProperties().get(0);
        assertEquals("objectProp", property1.getName());
        assertEquals("java.lang.Object", property1.getType());
        assertTrue(property1.isOverride());
        assertEquals("Integer.ZERO", property1.getDefaultValue());
    }
}
