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

package com.mattunderscore.specky.javapoet.javadoc;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for JavaDoc on methods.
 *
 * @author Matt Champion on 17/06/2016
 */
public final class JavaDocMethodBuilder {
    private final List<Parameter> parameters = new ArrayList<>();
    private String description;
    private String returns;

    /*package*/ JavaDocMethodBuilder() {
    }

    /**
     * Add a parameter.
     * @param name the name of the parameter
     * @param description the description of the parameter
     * @return this builder
     */
    public JavaDocMethodBuilder addParameter(String name, String description) {
        parameters.add(new Parameter(name, description));
        return this;
    }

    /**
     * Set the description for the method.
     * @param description the description of the method
     * @return this builder
     */
    public JavaDocMethodBuilder setMethodDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Set the description for the return value.
     * @param returns the description of the return value
     * @return this builder
     */
    public JavaDocMethodBuilder setReturnsDescription(String returns) {
        this.returns = returns;
        return this;
    }

    /**
     * @return the JavaDoc as a string
     */
    public String toJavaDoc() {
        final StringBuilder stringBuilder = new StringBuilder(120);
        if (description != null) {
            stringBuilder
                .append(description)
                .append('\n');
        }

        for (final Parameter parameter : parameters) {
            stringBuilder
                .append("@param ")
                .append(parameter.name)
                .append(' ')
                .append(parameter.description)
                .append('\n');
        }

        if (returns != null) {
            stringBuilder
                .append("@return ")
                .append(returns)
                .append('\n');
        }

        return stringBuilder.toString();
    }

    private static final class Parameter {
        private final String name;
        private final String description;

        private Parameter(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }
}
