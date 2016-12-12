package com.mattunderscore.specky.generator;

import com.mattunderscore.specky.generator.property.field.FieldGeneratorForProperty;
import com.mattunderscore.specky.generator.property.field.ImmutableFieldGenerator;
import com.mattunderscore.specky.model.PropertyDesc;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import org.junit.Test;

import javax.lang.model.element.Modifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link ImmutableFieldGenerator}.
 *
 * @author Matt Champion on 07/12/2016
 */
public class ImmutableFieldGeneratorTest {
    @Test
    public void generate() {
        final FieldGeneratorForProperty generator = new ImmutableFieldGenerator();

        final PropertyDesc propertyDesc = PropertyDesc
                .builder()
                .type("java.lang.String")
                .name("prop")
                .build();
        final FieldSpec fieldSpec = generator.generate(null, null, propertyDesc);

        assertTrue(fieldSpec.hasModifier(Modifier.FINAL));
        assertTrue(fieldSpec.hasModifier(Modifier.PRIVATE));
        assertEquals("prop", fieldSpec.name);
        assertEquals(ClassName.get(String.class), fieldSpec.type);
    }
}
