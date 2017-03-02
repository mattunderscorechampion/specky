package com.mattunderscore.specky.model.generator.scope;

import static java.time.LocalDateTime.ofInstant;
import static java.util.regex.Pattern.compile;

import java.time.ZoneId;
import java.util.function.Function;
import java.util.regex.Pattern;

import com.mattunderscore.specky.context.file.TemplateContext;

/**
 * Function to evaluate a template.
 * @author Matt Champion 27/02/2017
 */
public final class EvaluateTemplate implements Function<String, String> {
    private static final Pattern[] PATTERNS = new Pattern[] {
        compile("(?<!\\\\)\\$\\{type}"),
        compile("(?<!\\\\)\\$\\{author}"),
        compile("(?<!\\\\)\\$\\{copyrightHolder}"),
        compile("(?<!\\\\)\\$\\{year}"),
        compile("\\\\\\$\\{type}"),
        compile("\\\\\\$\\{author}"),
        compile("\\\\\\$\\{copyrightHolder}"),
        compile("\\\\\\$\\{year}")
    };
    private final String[] substitutions;

    /**
     * Constructor.
     */
    public EvaluateTemplate(TemplateContext templateContext) {
        substitutions = new String[] {
            templateContext.getTypeName(),
            templateContext.getAuthor(),
            templateContext.getCopyrightHolder(),
            Integer.toString(ofInstant(templateContext.getBuildTime(), ZoneId.systemDefault()).getYear()),
            "\\${type}",
            "\\${author}",
            "\\${copyrightHolder}",
            "\\${year}"
        };

        assert substitutions.length == PATTERNS.length : "Must be the same number of patterns and substitutions";
    }

    @Override
    public String apply(String template) {
        String value = template;

        for (int i = 0; i < PATTERNS.length; i++) {
            value = replace(value, PATTERNS[i], substitutions[i]);
        }

        return value;
    }

    private static String replace(String template, Pattern pattern, String substitution) {
        return pattern.matcher(template).replaceAll(substitution);
    }
}
