/* Copyright © 2016-2017 Matthew Champion
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
 * Neither the name of mattunderscore.com nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL MATTHEW CHAMPION BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package com.mattunderscore.specky.generator.property.field;

import com.mattunderscore.specky.literal.model.StringLiteral;
import com.mattunderscore.specky.model.BeanDesc;
import com.mattunderscore.specky.model.ImplementationDesc;
import com.mattunderscore.specky.model.PropertyDesc;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import org.junit.Test;

import javax.lang.model.element.Modifier;

import static com.mattunderscore.specky.model.ConstructionMethod.CONSTRUCTOR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link MutableFieldGenerator}.
 *
 * @author Matt Champion on 07/12/2016
 */
public class MutableFieldGeneratorTest {

    @Test
    public void generate() {
        final MutableFieldGenerator generator = new MutableFieldGenerator();

        final ImplementationDesc beanDesc = BeanDesc
                .builder()
                .constructionMethod(CONSTRUCTOR)
                .build();

        final PropertyDesc propertyDesc = PropertyDesc
                .builder()
                .type("java.lang.String")
                .name("prop")
                .defaultValue(StringLiteral.builder().stringLiteral("").build())
                .build();

        final FieldSpec fieldSpec = generator.generate(null, beanDesc, propertyDesc);

        assertTrue(fieldSpec.hasModifier(Modifier.PRIVATE));
        assertEquals("prop", fieldSpec.name);
        assertEquals(ClassName.get(String.class), fieldSpec.type);
        assertEquals(CodeBlock.of("\"\""), fieldSpec.initializer);
    }
}
