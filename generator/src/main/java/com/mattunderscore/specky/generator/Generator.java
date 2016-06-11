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

import static com.mattunderscore.specky.generator.BeanGenerator.generateBean;
import static com.mattunderscore.specky.generator.ValueGenerator.generateValue;

import java.util.List;
import java.util.stream.Collectors;

import com.mattunderscore.specky.model.BeanDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.mattunderscore.specky.model.TypeDesc;
import com.mattunderscore.specky.model.ValueDesc;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

/**
 * Code generator for specification.
 * @author Matt Champion on 05/06/16
 */
public final class Generator {
    public List<JavaFile> generate(SpecDesc specDesc) {
        return specDesc
            .getValues()
            .stream()
            .map(this::generateType)
            .map(typeSpec -> JavaFile.builder(specDesc.getPackageName(), typeSpec).build())
            .collect(Collectors.toList());
    }

    private TypeSpec generateType(TypeDesc typeDesc) {
        if (typeDesc instanceof ValueDesc) {
            return generateValue((ValueDesc) typeDesc);
        }
        else if (typeDesc instanceof BeanDesc) {
            return generateBean((BeanDesc) typeDesc);
        }
        else {
            throw new IllegalArgumentException("Unknown type to generate");
        }
    }
}
