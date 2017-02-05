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

package com.mattunderscore.specky.generator;

import static java.util.Collections.singletonList;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.mattunderscore.specky.generator.property.field.FieldGeneratorForProperty;
import com.mattunderscore.specky.model.ImplementationDesc;
import com.mattunderscore.specky.model.PropertyDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.mattunderscore.specky.model.TypeDesc;
import com.mattunderscore.specky.model.ValueDesc;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

/**
 * Unit tests for {@link TypeGenerator}.
 *
 * @author Matt Champion on 07/12/2016
 */
public class TypeGeneratorTest {
    @Mock
    private TypeInitialiser<ImplementationDesc> typeInitialiser;
    @Mock
    private TypeAppender<ImplementationDesc> constructionMethodAppender;
    @Mock
    private TypeAppender<TypeDesc> superTypeAppender;
    @Mock
    private FieldGeneratorForProperty<ImplementationDesc> fieldGeneratorForProperty;
    @Mock
    private MethodGeneratorForProperty<ImplementationDesc> methodGeneratorForProperty;
    @Mock
    private MethodGeneratorForType<ImplementationDesc> methodGeneratorForType;

    private final SpecDesc specDesc = SpecDesc.builder().build();
    private final PropertyDesc propertyDesc = PropertyDesc.builder().build();
    private final ImplementationDesc implementationDesc = ValueDesc
        .builder()
        .properties(singletonList(propertyDesc))
        .build();
    private final TypeSpec.Builder typeBuilder = TypeSpec.classBuilder("A");
    private final MethodSpec methodBuilder = MethodSpec.methodBuilder("a").build();

    @Before
    public void setUp() {
        initMocks(this);

        when(typeInitialiser.create(isA(SpecDesc.class), isA(ImplementationDesc.class))).thenReturn(typeBuilder);
    }

    @Test
    public void generate() {
        final TypeGenerator<ImplementationDesc> generator = new TypeGenerator<>(
            typeInitialiser,
            Arrays.<TypeAppender<? super ImplementationDesc>>asList(
                constructionMethodAppender,
                superTypeAppender,
                methodGeneratorForType),
            singletonList(fieldGeneratorForProperty),
            singletonList(methodGeneratorForProperty));

        generator.generate(specDesc, implementationDesc);

        verify(typeInitialiser).create(specDesc, implementationDesc);
        verify(constructionMethodAppender).append(typeBuilder, specDesc, implementationDesc);
        verify(methodGeneratorForProperty).append(typeBuilder, specDesc, implementationDesc, propertyDesc);
        verify(methodGeneratorForType).append(isA(TypeSpec.Builder.class), eq(specDesc), eq(implementationDesc));
    }
}
