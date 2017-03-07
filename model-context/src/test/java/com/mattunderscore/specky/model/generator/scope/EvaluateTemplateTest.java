package com.mattunderscore.specky.model.generator.scope;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.mattunderscore.specky.context.file.TemplateContext;

/**
 * Unit tests for {@link EvaluateTemplate}.
 *
 * @author Matt Champion 27/02/2017
 */
public final class EvaluateTemplateTest {
    @Test
    public void apply() throws Exception {
        final TemplateContext templateContext = TemplateContext
            .builder()
            .author("author")
            .copyrightHolder("Matt")
            .typeName("TestType")
            .build();

        final EvaluateTemplate evaluator = new EvaluateTemplate(templateContext);

        final String value = evaluator.apply("${author} \\${author} ${copyrightHolder} ${type} ${author}");
        assertEquals("author ${author} Matt TestType author", value);
    }

    @Test
    public void unknownValues() throws Exception {
        final TemplateContext templateContext = TemplateContext
            .builder()
            .typeName("TestType")
            .build();

        final EvaluateTemplate evaluator = new EvaluateTemplate(templateContext);

        final String value = evaluator.apply("${author} ${copyrightHolder}");
        assertEquals("unknown unknown", value);
    }
}
