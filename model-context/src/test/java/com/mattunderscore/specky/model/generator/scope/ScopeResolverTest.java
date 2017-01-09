package com.mattunderscore.specky.model.generator.scope;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mattunderscore.specky.CountingSemanticErrorListener;
import com.mattunderscore.specky.dsl.model.DSLAbstractTypeDesc;
import com.mattunderscore.specky.dsl.model.DSLLicence;
import com.mattunderscore.specky.dsl.model.DSLPropertyDesc;
import com.mattunderscore.specky.dsl.model.DSLSpecDesc;
import com.mattunderscore.specky.dsl.model.DSLValueDesc;
import com.mattunderscore.specky.model.ConstructionMethod;

/**
 * Unit tests for {@link ScopeResolver}.
 *
 * @author Matt Champion on 12/10/2016
 */
public final class ScopeResolverTest {

    @Test
    public void testScopes() {
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

        final CountingSemanticErrorListener semanticErrorListener = new CountingSemanticErrorListener();
        final ScopeResolver scopeResolver = new ScopeResolver(semanticErrorListener)
            .createScopes(singletonList(spec));

        final Scope scope = scopeResolver.resolve(spec);

        assertFalse(scope.getLicenceResolver().resolve((DSLLicence)null).isPresent());
        assertTrue(scope.getTypeResolver().resolve("com.example.SuperType").isPresent());
        assertFalse(scope.getLicenceResolver().resolve(DSLLicence.builder().identifier("no-licence").build()).isPresent());
        assertEquals(1, semanticErrorListener.getErrorCount());
    }
}
