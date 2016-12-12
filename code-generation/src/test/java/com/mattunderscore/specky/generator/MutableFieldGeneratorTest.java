package com.mattunderscore.specky.generator;

import com.mattunderscore.specky.generator.property.field.FieldGeneratorForProperty;
import com.mattunderscore.specky.generator.property.field.MutableFieldGenerator;
import com.mattunderscore.specky.model.BeanDesc;
import com.mattunderscore.specky.model.ImplementationDesc;
import com.mattunderscore.specky.model.PropertyDesc;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import org.junit.Test;

import javax.lang.model.element.Modifier;

import static com.mattunderscore.specky.model.ConstructionMethod.CONSTRUCTOR;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link MutableFieldGenerator}.
 *
 * @author Matt Champion on 07/12/2016
 */
public class MutableFieldGeneratorTest {
    @Test
    public void generate() {
        final FieldGeneratorForProperty generator = new MutableFieldGenerator();

        final ImplementationDesc beanDesc = BeanDesc
                .builder()
                .constructionMethod(CONSTRUCTOR)
                .build();

        final PropertyDesc propertyDesc = PropertyDesc
                .builder()
                .type("java.lang.String")
                .name("prop")
                .defaultValue(CodeBlock.of("\"\""))
                .build();
        final FieldSpec fieldSpec = generator.generate(null, beanDesc, propertyDesc);

        assertTrue(fieldSpec.hasModifier(Modifier.PRIVATE));
        assertEquals("prop", fieldSpec.name);
        assertEquals(ClassName.get(String.class), fieldSpec.type);
        assertEquals(CodeBlock.of("\"\""), fieldSpec.initializer);
    }

}