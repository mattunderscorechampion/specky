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

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import com.google.googlejavaformat.java.JavaFormatterOptions;
import com.google.googlejavaformat.java.JavaFormatterOptions.JavadocFormatter;
import com.google.googlejavaformat.java.JavaFormatterOptions.SortImports;
import com.google.googlejavaformat.java.JavaFormatterOptions.Style;
import com.squareup.javapoet.JavaFile;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

/**
 * Writes generated Java code to file system.
 *
 * @author Matt Champion on 02/07/2016
 */
public final class SpeckyWritingContext {
    private final AtomicBoolean consumed = new AtomicBoolean(false);
    private final List<JavaFile> javaFiles;
    private final Formatter codeFormatter;
    private volatile Path targetPath;

    /*package*/ SpeckyWritingContext(List<JavaFile> javaFiles) {
        this.javaFiles = javaFiles;
        codeFormatter = new Formatter(new JavaFormatterOptions(JavadocFormatter.NONE, Style.AOSP, SortImports.ALSO));
    }

    /**
     * Set the target path to write to.
     */
    public SpeckyWritingContext targetPath(Path path) {
        targetPath = path;
        return this;
    }

    /**
     * Write files.
     * @throws IllegalStateException if has been called before
     */
    public void write() throws IOException, FormatterException {
        if (consumed.compareAndSet(false, true)) {
            for (final JavaFile file : javaFiles) {
                final String code = file.toString();
                final String formattedSource = codeFormatter.formatSource(code);
                Path outputPath = targetPath;
                final String[] packageNameParts = file.packageName.split("\\.");
                for (final String packageNamePart : packageNameParts) {
                    outputPath = outputPath.resolve(packageNamePart);
                }
                Files.createDirectories(outputPath);
                outputPath = outputPath.resolve(file.typeSpec.name + ".java");
                Files.write(outputPath, formattedSource.getBytes(Charset.forName("UTF-8")), CREATE, WRITE, TRUNCATE_EXISTING);
            }
        }
        else {
            throw new IllegalStateException("Context has already been generated");
        }
    }
}
