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

package com.mattunderscore.specky;

import static com.mattunderscore.specky.generator.ToStringGenerator.COMMA_AND_SPACE_SEPARATOR;
import static com.mattunderscore.specky.generator.ToStringGenerator.SIMPLE_PROPERTY_FORMATTER;
import static com.mattunderscore.specky.generator.ToStringGenerator.SQUARE_BRACKETS;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.mattunderscore.specky.generator.AccessorGenerator;
import com.mattunderscore.specky.generator.BeanGenerator;
import com.mattunderscore.specky.generator.BuildMethodGenerator;
import com.mattunderscore.specky.generator.ConstructorGenerator;
import com.mattunderscore.specky.generator.Generator;
import com.mattunderscore.specky.generator.ImmutableBuilderGenerator;
import com.mattunderscore.specky.generator.MutableBuilderGenerator;
import com.mattunderscore.specky.generator.MutatorGenerator;
import com.mattunderscore.specky.generator.ToStringGenerator;
import com.mattunderscore.specky.generator.ValueGenerator;
import com.mattunderscore.specky.generator.ViewGenerator;
import com.mattunderscore.specky.model.SpecDesc;
import com.squareup.javapoet.JavaFile;

/**
 * @author Matt Champion on 02/07/2016
 */
public class SpeckyGeneratingContext {
    private final List<SpecDesc> specs;
    private final AtomicBoolean consumed = new AtomicBoolean(false);

    /*package*/ SpeckyGeneratingContext(List<SpecDesc> specs) {
        this.specs = specs;
    }

    public SpeckyWritingContext generate() {
        if (consumed.compareAndSet(false, true)) {
            final BuildMethodGenerator buildMethodGenerator = new BuildMethodGenerator();
            final MutableBuilderGenerator mutableBuilderGenerator = new MutableBuilderGenerator(buildMethodGenerator);
            final ImmutableBuilderGenerator immutableBuilderGenerator = new ImmutableBuilderGenerator(buildMethodGenerator);
            final AccessorGenerator accessorGenerator = new AccessorGenerator();
            final ToStringGenerator toStringGenerator = new ToStringGenerator(
                SQUARE_BRACKETS,
                COMMA_AND_SPACE_SEPARATOR,
                SIMPLE_PROPERTY_FORMATTER);
            final Generator generator = new Generator(
                new ValueGenerator(
                    mutableBuilderGenerator,
                    immutableBuilderGenerator,
                    new ConstructorGenerator(),
                    accessorGenerator,
                    toStringGenerator),
                new BeanGenerator(
                    mutableBuilderGenerator,
                    immutableBuilderGenerator,
                    accessorGenerator,
                    new MutatorGenerator(),
                    toStringGenerator),
                new ViewGenerator());

            final List<JavaFile> javaFiles = new ArrayList<>();
            for (SpecDesc spec : specs) {
                javaFiles.addAll(generator.generate(spec));
            }

            return new SpeckyWritingContext(javaFiles);
        }
        else {
            throw new IllegalStateException("Context has already been generated");
        }
    }
}
