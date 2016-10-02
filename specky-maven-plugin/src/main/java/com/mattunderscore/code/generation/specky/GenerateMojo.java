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

package com.mattunderscore.code.generation.specky;

import static java.util.Collections.singletonList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;

import com.google.googlejavaformat.java.FormatterException;
import com.mattunderscore.specky.SemanticException;
import com.mattunderscore.specky.SpeckyDSLFileStreamingContext;
import com.mattunderscore.specky.SpeckyGeneratingContext;
import com.mattunderscore.specky.SpeckyModelGeneratingContext;
import com.mattunderscore.specky.SpeckyParsingException;
import com.mattunderscore.specky.SpeckyWritingContext;

/**
 * @goal generate
 * @phase generate-sources
 * @author Matt Champion on 23/06/2016
 */
public final class GenerateMojo extends AbstractMojo {
    /**
     * @component
     */
    private MavenProject project;

    /**
     * @parameter
     */
    private FileSet fileset;

    /**
     * @parameter default-value="${project.build.directory}/generated-sources/specky"
     */
    private File target;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final FileSet currentFileset = getFileSet();

        final FileSetManager fileSetManager = new FileSetManager(getLog(), true);
        final String[] files = fileSetManager.getIncludedFiles(currentFileset);

        if (files.length == 0) {
            throw new MojoFailureException("No specification files found in " + currentFileset.getDirectory());
        }

        final Path targetPath = target
            .toPath()
            .toAbsolutePath();

        final File targetDirectory = targetPath.toFile();
        if (targetDirectory.exists() && !targetDirectory.isDirectory()) {
            throw new MojoExecutionException("The target path " + targetPath + " exists but is not a directory");
        }

        if (!targetDirectory.exists() && !targetDirectory.mkdirs()) {
            throw new MojoExecutionException("The target path " + targetPath + " could not be created");
        }

        final Path basePath = Paths.get(currentFileset.getDirectory());
        final SpeckyDSLFileStreamingContext streamingContext = new SpeckyDSLFileStreamingContext();
        Stream.of(files)
            .map(basePath::resolve)
            .forEach(streamingContext::addFileToParse);

        final SpeckyModelGeneratingContext generatingContext;
        try {
            generatingContext = streamingContext
                .open()
                .parse();
        }
        catch (IOException e) {
            throw new MojoFailureException("Failed to process specification files", e);
        }
        catch (SpeckyParsingException e) {
            throw new MojoFailureException("Failed to parse specification files", e);
        }

        final SpeckyGeneratingContext speckyGeneratingContext;
        try {
            speckyGeneratingContext = generatingContext.generate();
        }
        catch (SemanticException e) {
            throw new MojoFailureException(e.getMessage());
        }

        final SpeckyWritingContext speckyWritingContext = speckyGeneratingContext
            .generate()
            .targetPath(targetPath);

        try {
            speckyWritingContext.write();
        }
        catch (IOException | FormatterException e) {
            throw new MojoFailureException("Failed to write generated source code", e);
        }

        project.addCompileSourceRoot(targetPath.toString());
    }

    private FileSet getFileSet() {
        if (fileset != null) {
            return fileset;
        }

        final FileSet defaultFileset = new FileSet();
        defaultFileset.setDirectory(project.getBasedir() + "/src/main/specky");
        defaultFileset.setIncludes(singletonList("*.spec"));
        return defaultFileset;
    }
}
