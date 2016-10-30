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

import static com.mattunderscore.specky.generator.object.method.ToStringGenerator.COMMA_AND_SPACE_SEPARATOR;
import static com.mattunderscore.specky.generator.object.method.ToStringGenerator.SIMPLE_PROPERTY_FORMATTER;
import static com.mattunderscore.specky.generator.object.method.ToStringGenerator.SQUARE_BRACKETS;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static javax.lang.model.element.Modifier.PUBLIC;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.mattunderscore.specky.generator.BeanGenerator;
import com.mattunderscore.specky.generator.BeanInitialiser;
import com.mattunderscore.specky.generator.ConstructionMethodAppender;
import com.mattunderscore.specky.generator.DefaultsGenerator;
import com.mattunderscore.specky.generator.Generator;
import com.mattunderscore.specky.generator.InstantiateNewType;
import com.mattunderscore.specky.generator.MethodGeneratorForProperty;
import com.mattunderscore.specky.generator.SuperTypeAppender;
import com.mattunderscore.specky.generator.TypeAppender;
import com.mattunderscore.specky.generator.TypeInitialiser;
import com.mattunderscore.specky.generator.ValueGenerator;
import com.mattunderscore.specky.generator.ValueInitialiser;
import com.mattunderscore.specky.generator.ViewGenerator;
import com.mattunderscore.specky.generator.ViewInitialiser;
import com.mattunderscore.specky.generator.builder.BuildMethodGenerator;
import com.mattunderscore.specky.generator.builder.BuilderInitialiser;
import com.mattunderscore.specky.generator.builder.ImmutableBuilderGenerator;
import com.mattunderscore.specky.generator.builder.MutableBuilderGenerator;
import com.mattunderscore.specky.generator.constructor.AllPropertiesConstructorGenerator;
import com.mattunderscore.specky.generator.constructor.DefaultConstructorGenerator;
import com.mattunderscore.specky.generator.constructor.EmptyConstructorGenerator;
import com.mattunderscore.specky.generator.object.method.EqualsGenerator;
import com.mattunderscore.specky.generator.object.method.HashCodeGenerator;
import com.mattunderscore.specky.generator.object.method.ToStringGenerator;
import com.mattunderscore.specky.generator.property.AccessorGenerator;
import com.mattunderscore.specky.generator.property.AccessorJavadocGenerator;
import com.mattunderscore.specky.generator.property.MutatorGenerator;
import com.mattunderscore.specky.generator.property.WithModifierGenerator;
import com.mattunderscore.specky.model.SpecDesc;
import com.squareup.javapoet.JavaFile;

/**
 * Generates Java code.
 *
 * @author Matt Champion on 02/07/2016
 */
public final class SpeckyGeneratingContext {
    private final SpecDesc spec;
    private final AtomicBoolean consumed = new AtomicBoolean(false);
    private volatile ToStringGenerator toStringGenerator =
        new ToStringGenerator(
            SQUARE_BRACKETS,
            COMMA_AND_SPACE_SEPARATOR,
            SIMPLE_PROPERTY_FORMATTER);
    private MethodGeneratorForProperty accessorGenerator = new AccessorGenerator();
    private MethodGeneratorForProperty mutatorGenerator = new MutatorGenerator();

    /*package*/ SpeckyGeneratingContext(SpecDesc spec) {
        this.spec = spec;
    }

    /**
     * Set the toString generator.
     */
    public SpeckyGeneratingContext toStringGenerator(ToStringGenerator toStringGenerator) {
        this.toStringGenerator = toStringGenerator;
        return this;
    }

    /**
     * Set the accessor generator.
     */
    public SpeckyGeneratingContext accessorGenerator(AccessorGenerator accessorGenerator) {
        this.accessorGenerator = accessorGenerator;
        return this;
    }

    /**
     * Set the mutator generator.
     */
    public SpeckyGeneratingContext mutatorGenerator(MutatorGenerator mutatorGenerator) {
        this.mutatorGenerator = mutatorGenerator;
        return this;
    }

    /**
     * Generate the Java code.
     * @throws IllegalStateException if has been called before
     */
    public SpeckyWritingContext generate() {
        if (consumed.compareAndSet(false, true)) {
            final BuildMethodGenerator buildMethodGenerator = new BuildMethodGenerator();
            final TypeInitialiser builderInitialiser = new BuilderInitialiser();
            final MutableBuilderGenerator mutableBuilderGenerator = new MutableBuilderGenerator(
                builderInitialiser,
                buildMethodGenerator);
            final ImmutableBuilderGenerator immutableBuilderGenerator = new ImmutableBuilderGenerator(
                builderInitialiser,
                buildMethodGenerator);
            final TypeAppender defaultsGenerator = new DefaultsGenerator();
            final HashCodeGenerator hashCodeGenerator = new HashCodeGenerator();
            final EqualsGenerator equalsGenerator = new EqualsGenerator();
            final TypeAppender superTypeAppender = new SuperTypeAppender();
            final MethodGeneratorForProperty withGenerator = new WithModifierGenerator("", new InstantiateNewType());
            final Generator generator = new Generator(
                new ValueGenerator(
                    new ValueInitialiser(),
                    new ConstructionMethodAppender(
                        asList(new AllPropertiesConstructorGenerator(PUBLIC), new DefaultConstructorGenerator(PUBLIC)),
                        mutableBuilderGenerator,
                        immutableBuilderGenerator,
                        defaultsGenerator),
                    superTypeAppender,
                    asList(accessorGenerator, withGenerator),
                    asList(toStringGenerator, hashCodeGenerator, equalsGenerator)),
                new BeanGenerator(
                    new BeanInitialiser(),
                    new ConstructionMethodAppender(
                        singletonList(new EmptyConstructorGenerator()),
                        mutableBuilderGenerator,
                        immutableBuilderGenerator,
                        defaultsGenerator),
                    superTypeAppender,
                    asList(accessorGenerator, mutatorGenerator, withGenerator),
                    asList(toStringGenerator, hashCodeGenerator, equalsGenerator)),
                new ViewGenerator(new ViewInitialiser(), new AccessorJavadocGenerator()));

            final List<JavaFile> javaFiles = new ArrayList<>();
            javaFiles.addAll(generator.generate(spec));

            return new SpeckyWritingContext(javaFiles);
        }
        else {
            throw new IllegalStateException("Context has already been generated");
        }
    }
}
