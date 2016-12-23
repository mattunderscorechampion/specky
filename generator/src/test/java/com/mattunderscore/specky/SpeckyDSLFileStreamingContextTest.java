package com.mattunderscore.specky;

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.Test;

/**
 * Unit tests for {@link SpeckyDSLFileStreamingContext}.
 *
 * @author Matt Champion on 05/12/2016
 */
public final class SpeckyDSLFileStreamingContextTest {
    @Test
    public void parser() throws IOException, ParsingError {
        final SpeckyDSLFileStreamingContext context = new SpeckyDSLFileStreamingContext();

        context.addFileToParse(Paths.get("src/test/specky/Bean.spec"));
        context.addFileToParse(Paths.get("src/test/specky/constraint.spec"));
        context.addFileToParse(Paths.get("src/test/specky/dsl.spec"));
        context.addFileToParse(Paths.get("src/test/specky/licence.spec"));
        context.addFileToParse(Paths.get("src/test/specky/model.spec"));
        context.addFileToParse(Paths.get("src/test/specky/readme.spec"));
        context.addFileToParse(Paths.get("src/test/specky/scope.spec"));
        context.addFileToParse(Paths.get("src/test/specky/sections.spec"));
        context.addFileToParse(Paths.get("src/test/specky/Value.spec"));
        context.addFileToParse(Paths.get("src/test/specky/AbstractType.spec"));
        context.addFileToParse(Paths.get("src/test/specky/withMod.spec"));

        final SpeckyDSLParsingContext dslParsingContext = context.open();

        dslParsingContext.parse();
    }
}
