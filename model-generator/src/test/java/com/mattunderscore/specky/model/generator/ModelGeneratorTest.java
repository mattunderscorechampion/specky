package com.mattunderscore.specky.model.generator;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.mattunderscore.specky.licence.resolver.LicenceResolver;
import com.mattunderscore.specky.value.resolver.OptionalValueResolver;
import org.junit.Test;

import com.mattunderscore.specky.dsl.model.DSLAbstractTypeDesc;
import com.mattunderscore.specky.dsl.model.DSLPropertyDesc;
import com.mattunderscore.specky.dsl.model.DSLSpecDesc;
import com.mattunderscore.specky.dsl.model.DSLValueDesc;
import com.mattunderscore.specky.model.ConstructionMethod;
import com.mattunderscore.specky.model.ImplementationDesc;
import com.mattunderscore.specky.model.PropertyDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.mattunderscore.specky.type.resolver.PropertyTypeResolver;
import com.mattunderscore.specky.type.resolver.SpecTypeResolver;
import com.mattunderscore.specky.type.resolver.TypeResolver;
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
        final TypeResolver resolver = new TypeResolverBuilder().registerResolver(typeResolver).build();
        final ModelGenerator generator = new ModelGenerator(
            singletonList(spec),
            resolver,
            new PropertyTypeResolver(resolver),
            new CompositeValueResolver()
                .with(new JavaStandardDefaultValueResolver())
                .with(new NullValueResolver()),
            new LicenceResolver());

        final SpecDesc specDesc = generator.get();

        assertNotNull(specDesc);
        assertEquals(1, specDesc.getTypes().size());
        final ImplementationDesc implementationDesc = specDesc.getTypes().get(0);
        assertEquals("com.example", implementationDesc.getPackageName());
        assertEquals("Example", implementationDesc.getName());
        assertEquals(ConstructionMethod.CONSTRUCTOR, implementationDesc.getConstructionMethod());
        assertEquals(1, implementationDesc.getProperties().size());
        final PropertyDesc property = implementationDesc.getProperties().get(0);
        assertEquals("intProp", property.getName());
        assertEquals("int", property.getType());
    }

    @Test
    public void extending() {
        final DSLSpecDesc spec = DSLSpecDesc
            .builder()
            .author("")
            .packageName("com.example")
            .views(singletonList(DSLAbstractTypeDesc
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
        final TypeResolver resolver = new TypeResolverBuilder().registerResolver(typeResolver).build();
        final ModelGenerator generator = new ModelGenerator(
            singletonList(spec),
            resolver,
            new PropertyTypeResolver(resolver),
            new CompositeValueResolver()
                .with(new OptionalValueResolver())
                .with(new JavaStandardDefaultValueResolver())
                .with(new NullValueResolver()),
            new LicenceResolver());

        final SpecDesc specDesc = generator.get();

        assertNotNull(specDesc);
        assertEquals(1, specDesc.getTypes().size());
        final ImplementationDesc implementationDesc = specDesc.getTypes().get(0);
        assertEquals("com.example", implementationDesc.getPackageName());
        assertEquals("Example", implementationDesc.getName());
        assertEquals(ConstructionMethod.CONSTRUCTOR, implementationDesc.getConstructionMethod());
        assertEquals(2, implementationDesc.getProperties().size());
        final PropertyDesc property0 = implementationDesc.getProperties().get(1);
        assertEquals("intProp", property0.getName());
        assertEquals("int", property0.getType());
        assertEquals("0", property0.getDefaultValue());
        assertFalse(property0.isOverride());
        final PropertyDesc property1 = implementationDesc.getProperties().get(0);
        assertEquals("objectProp", property1.getName());
        assertEquals("java.lang.Object", property1.getType());
        assertTrue(property1.isOverride());
        assertEquals("Integer.ZERO", property1.getDefaultValue());
    }

    @Test
    public void optionalPrimative() {
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
                    .optional(true)
                    .type("int")
                    .build()))
                .build()))
            .build();

        final SpecTypeResolver typeResolver = new SpecTypeResolver();
        spec.getViews().forEach(view -> typeResolver.registerTypeName(spec.getPackageName(), view.getName()));
        spec.getTypes().forEach(value -> typeResolver.registerTypeName(spec.getPackageName(), value.getName()));
        final TypeResolver resolver = new TypeResolverBuilder().registerResolver(typeResolver).build();
        final ModelGenerator generator = new ModelGenerator(
            singletonList(spec),
            resolver,
            new PropertyTypeResolver(resolver),
            new CompositeValueResolver()
                .with(new JavaStandardDefaultValueResolver())
                .with(new NullValueResolver()),
            new LicenceResolver());

        final SpecDesc specDesc = generator.get();

        assertNotNull(specDesc);
        assertEquals(1, specDesc.getTypes().size());
        final ImplementationDesc implementationDesc = specDesc.getTypes().get(0);
        assertEquals("com.example", implementationDesc.getPackageName());
        assertEquals("Example", implementationDesc.getName());
        assertEquals(ConstructionMethod.CONSTRUCTOR, implementationDesc.getConstructionMethod());
        assertEquals(1, implementationDesc.getProperties().size());
        final PropertyDesc property = implementationDesc.getProperties().get(0);
        assertEquals("intProp", property.getName());
        assertEquals("java.lang.Integer", property.getType());
    }
}
