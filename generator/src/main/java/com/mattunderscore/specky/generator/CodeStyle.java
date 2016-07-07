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

/**
 * Code style for generated code.
 *
 * @author Matt Champion on 05/06/16
 */
public final class CodeStyle {
    private final String indent;

    private CodeStyle(String indent) {
        this.indent = indent;
    }

    /**
     * @return the indent
     */
    public String getIndent() {
        return indent;
    }

    /**
     * @return the builder
     */
    public static Builder builder() {
        return new Builder("    ");
    }

    /**
     * Builder for code style.
     */
    public static final class Builder {
        private final String indent;

        private Builder(String indent) {
            this.indent = indent;
        }

        /**
         * The number of spaces to use for indents.
         */
        public Builder spaces(int width) {
            final StringBuilder builder = new StringBuilder(width);
            for (int i = 0; i < width; i++) {
                builder.append(' ');
            }
            return new Builder(builder.toString());
        }

        /**
         * The number of tabs to use for indents.
         */
        public Builder tabs(int width) {
            final StringBuilder builder = new StringBuilder(width);
            for (int i = 0; i < width; i++) {
                builder.append('\t');
            }
            return new Builder(builder.toString());
        }

        /**
         * @return create the {@link CodeStyle}
         */
        public CodeStyle build() {
            return new CodeStyle(indent);
        }
    }
}
