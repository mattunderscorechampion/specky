package com.mattunderscore.specky.model.generator;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.mattunderscore.specky.constraint.model.ConstraintOperator;
import com.mattunderscore.specky.constraint.model.NFConjoinedDisjointPredicates;
import com.mattunderscore.specky.constraint.model.NFDisjointPredicates;
import com.mattunderscore.specky.constraint.model.PredicateDesc;
import com.mattunderscore.specky.dsl.model.DSLAbstractTypeDesc;
import com.mattunderscore.specky.dsl.model.DSLPropertyDesc;
import com.mattunderscore.specky.dsl.model.DSLSpecDesc;
import com.mattunderscore.specky.dsl.model.DSLValueDesc;
import com.mattunderscore.specky.model.AbstractTypeDesc;
import com.mattunderscore.specky.model.ConstructionMethod;
import com.mattunderscore.specky.model.ImplementationDesc;
import com.mattunderscore.specky.model.PropertyDesc;
import com.mattunderscore.specky.type.resolver.PropertyTypeResolver;
import com.mattunderscore.specky.type.resolver.SpecTypeResolver;
import com.mattunderscore.specky.type.resolver.TypeResolver;
import com.mattunderscore.specky.type.resolver.TypeResolverBuilder;
import com.mattunderscore.specky.value.resolver.CompositeValueResolver;
import com.mattunderscore.specky.value.resolver.DefaultValueResolver;
import com.mattunderscore.specky.value.resolver.JavaStandardDefaultValueResolver;
import com.mattunderscore.specky.value.resolver.NullValueResolver;

/**
 * Unit tests for {@link TypeDeriver}.
 *
 * @author Matt Champion on 16/08/2016
 */
public final class TypeDeriverTest {

    @Test
    public void deriveType() {
        final DSLAbstractTypeDesc superType = DSLAbstractTypeDesc
            .builder()
            .name("SuperExample")
            .properties(singletonList(DSLPropertyDesc
                .builder()
                .name("num")
                .typeParameters(emptyList())
                .optional(false)
                .type("int")
                .constraint(NFConjoinedDisjointPredicates
                    .builder()
                    .predicates(singletonList(NFDisjointPredicates
                        .builder()
                        .predicates(singletonList(PredicateDesc
                            .builder()
                            .subject("num")
                            .operator(ConstraintOperator.LESS_THAN)
                            .literal("50")
                            .build()))
                        .build()))
                    .build())
                .build()))
            .build();

        final DSLValueDesc valueDesc = DSLValueDesc
            .builder()
            .name("Example")
            .supertypes(singletonList("SuperExample"))
            .constructionMethod(ConstructionMethod.CONSTRUCTOR)
            .properties(singletonList(DSLPropertyDesc
                .builder()
                .name("num")
                .typeParameters(emptyList())
                .optional(false)
                .type("int")
                .constraint(NFConjoinedDisjointPredicates
                    .builder()
                    .predicates(singletonList(NFDisjointPredicates
                        .builder()
                        .predicates(singletonList(PredicateDesc
                            .builder()
                            .subject("num")
                            .operator(ConstraintOperator.GREATER_THAN)
                            .literal("20")
                            .build()))
                        .build()))
                    .build())
                .build()))
            .build();

        final DSLSpecDesc spec = DSLSpecDesc
            .builder()
            .author("")
            .packageName("com.example")
            .views(singletonList(superType))
            .types(singletonList(valueDesc))
            .build();

        final SpecTypeResolver typeResolver = new SpecTypeResolver();
        spec.getViews().forEach(view -> typeResolver.registerTypeName(spec.getPackageName(), view.getName()));
        spec.getTypes().forEach(value -> typeResolver.registerTypeName(spec.getPackageName(), value.getName()));
        final TypeResolver resolver = new TypeResolverBuilder().registerResolver(typeResolver).build();
        final PropertyTypeResolver propertyTypeResolver = new PropertyTypeResolver(resolver);
        final DefaultValueResolver valueResolver = new CompositeValueResolver()
            .with(new JavaStandardDefaultValueResolver())
            .with(new NullValueResolver());
        final Map<String, AbstractTypeDesc> types = new HashMap<>();
        types.put(
            spec.getPackageName() + "." + superType.getName(),
            AbstractTypeDesc
                .builder()
                .name("SuperExample")
                .supertypes(emptyList())
                .properties(singletonList(PropertyDesc
                    .builder()
                    .name("num")
                    .type("int")
                    .constraint(NFConjoinedDisjointPredicates
                        .builder()
                        .predicates(singletonList(NFDisjointPredicates
                            .builder()
                            .predicates(singletonList(PredicateDesc
                                .builder()
                                .subject("num")
                                .operator(ConstraintOperator.LESS_THAN)
                                .literal("50")
                                .build()))
                            .build()))
                        .build())
                    .build()))
                .build());

        final TypeDeriver deriver = new TypeDeriver(typeResolver, propertyTypeResolver, valueResolver, types);

        final ImplementationDesc implementationDesc = deriver.deriveType(spec, valueDesc);

        assertEquals("Example", implementationDesc.getName());
        assertEquals(1, implementationDesc.getProperties().size());
        final PropertyDesc propertyDesc = implementationDesc.getProperties().get(0);
        assertEquals("num", propertyDesc.getName());
        assertEquals("int", propertyDesc.getType());
        assertEquals(2, propertyDesc.getConstraint().getPredicates().size());
    }
}
