package com.mattunderscore.specky.model.generator;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.mattunderscore.specky.dsl.model.DSLConstructionMethod;
import com.mattunderscore.specky.dsl.model.DSLPropertyImplementationDesc;
import com.mattunderscore.specky.dsl.model.DSLSpecDesc;
import com.mattunderscore.specky.dsl.model.DSLValueDesc;
import com.mattunderscore.specky.dsl.model.DSLViewDesc;
import com.mattunderscore.specky.processed.model.ConstructionMethod;
import com.mattunderscore.specky.processed.model.PropertyImplementationDesc;
import com.mattunderscore.specky.processed.model.SpecDesc;
import com.mattunderscore.specky.processed.model.TypeDesc;

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
            .packageName("com.example")
            .views(emptyList())
            .values(singletonList(DSLValueDesc
                .builder()
                .name("Example")
                .extend(emptyList())
                .constructionMethod(DSLConstructionMethod.CONSTRUCTOR)
                .properties(singletonList(DSLPropertyImplementationDesc
                    .builder()
                    .name("intProp")
                    .optional(false)
                    .type("int")
                    .build()))
                .build()))
            .build();

        final ModelGenerator generator = new ModelGenerator(singletonList(spec));

        final SpecDesc specDesc = generator.get();

        assertNotNull(specDesc);
        assertEquals(1, specDesc.getValues().size());
        final TypeDesc typeDesc = specDesc.getValues().get(0);
        assertEquals("com.example", typeDesc.getPackageName());
        assertEquals("Example", typeDesc.getName());
        assertEquals(ConstructionMethod.CONSTRUCTOR, typeDesc.getConstructionMethod());
        assertEquals(1, typeDesc.getProperties().size());
        final PropertyImplementationDesc property = typeDesc.getProperties().get(0);
        assertEquals("intProp", property.getName());
        assertEquals("int", property.getType());
    }

    @Test
    public void extending() {
        final DSLSpecDesc spec = DSLSpecDesc
            .builder()
            .packageName("com.example")
            .views(singletonList(DSLViewDesc
                .builder()
                .name("SuperType")
                .properties(singletonList(DSLPropertyImplementationDesc
                    .builder()
                    .name("objectProp")
                    .type("Object")
                    .defaultValue("null")
                    .optional(false)
                    .build()))
                .build()))
            .values(singletonList(DSLValueDesc
                .builder()
                .name("Example")
                .extend(singletonList("com.example.SuperType"))
                .constructionMethod(DSLConstructionMethod.CONSTRUCTOR)
                .properties(singletonList(DSLPropertyImplementationDesc
                    .builder()
                    .name("intProp")
                    .optional(false)
                    .type("int")
                    .build()))
                .build()))
            .build();

        final ModelGenerator generator = new ModelGenerator(singletonList(spec));

        final SpecDesc specDesc = generator.get();

        assertNotNull(specDesc);
        assertEquals(1, specDesc.getValues().size());
        final TypeDesc typeDesc = specDesc.getValues().get(0);
        assertEquals("com.example", typeDesc.getPackageName());
        assertEquals("Example", typeDesc.getName());
        assertEquals(ConstructionMethod.CONSTRUCTOR, typeDesc.getConstructionMethod());
        assertEquals(2, typeDesc.getProperties().size());
        final PropertyImplementationDesc property0 = typeDesc.getProperties().get(1);
        assertEquals("intProp", property0.getName());
        assertEquals("int", property0.getType());
        final PropertyImplementationDesc property1 = typeDesc.getProperties().get(0);
        assertEquals("objectProp", property1.getName());
        assertEquals("Object", property1.getType());
    }
}
