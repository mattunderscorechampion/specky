/* Copyright © 2016 Matthew Champion
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.mattunderscore.specky.model.BeanDesc;
import com.mattunderscore.specky.model.ImplementationDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.mattunderscore.specky.model.TypeDesc;
import com.mattunderscore.specky.model.ValueDesc;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.JavaFile.Builder;
import com.squareup.javapoet.TypeSpec;

/**
 * Code generator for specification.
 * @author Matt Champion on 05/06/16
 */
public final class Generator {
    private final ValueGenerator valueGenerator;
    private final BeanGenerator beanGenerator;
    private final ViewGenerator viewGenerator;

    /**
     * Constructor.
     */
    public Generator(
            ValueGenerator valueGenerator,
            BeanGenerator beanGenerator,
            ViewGenerator viewGenerator) {

        this.valueGenerator = valueGenerator;
        this.beanGenerator = beanGenerator;
        this.viewGenerator = viewGenerator;
    }

    /**
     * @return the Java files implied by the spec.
     */
    public List<JavaFile> generate(SpecDesc specDesc) {
        final List<JavaFile> result = new ArrayList<>();
        result.addAll(specDesc
            .getImplementations()
            .stream()
            .map(valueSpec -> generateImplementationFile(specDesc, valueSpec))
            .collect(Collectors.toList()));

        result.addAll(specDesc
            .getTypes()
            .stream()
            .map(typeDesc -> generateTypeFile(specDesc, typeDesc))
            .collect(Collectors.toList()));

        return result;
    }

    private JavaFile generateImplementationFile(SpecDesc specDesc, ImplementationDesc implementationDesc) {
        final TypeSpec typeSpec = generateType(specDesc, implementationDesc);
        final Builder builder = JavaFile
            .builder(implementationDesc.getPackageName(), typeSpec);

        if (implementationDesc.getLicence() != null) {
            builder.addFileComment(implementationDesc.getLicence());
        }

        return builder
            .skipJavaLangImports(true)
            .build();
    }

    private JavaFile generateTypeFile(SpecDesc specDesc, TypeDesc typeDesc) {
        final TypeSpec typeSpec = generateView(specDesc, typeDesc);
        final Builder builder = JavaFile
            .builder(typeDesc.getPackageName(), typeSpec);

        if (typeDesc.getLicence() != null) {
            builder.addFileComment(typeDesc.getLicence());
        }

        return builder
            .skipJavaLangImports(true)
            .build();
    }

    private TypeSpec generateType(SpecDesc specDesc, ImplementationDesc implementationDesc) {
        if (implementationDesc instanceof ValueDesc) {
            return valueGenerator.generateValue(specDesc, (ValueDesc) implementationDesc);
        }
        else if (implementationDesc instanceof BeanDesc) {
            return beanGenerator.generateBean(specDesc, (BeanDesc) implementationDesc);
        }
        else {
            throw new IllegalArgumentException("Unknown type to generate");
        }
    }

    private TypeSpec generateView(SpecDesc specDesc, TypeDesc typeDesc) {
        return viewGenerator.generateView(specDesc, typeDesc);
    }
}
