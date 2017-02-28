package com.mattunderscore.specky.model.generator.scope;

import static java.util.regex.Pattern.compile;

import java.util.function.Function;
import java.util.regex.Pattern;

import com.mattunderscore.specky.context.file.TemplateContext;

/**
 * Function to evaluate a template.
 * @author Matt Champion 27/02/2017
 */
public final class EvaluateTemplate implements Function<String, String> {
    private static final Pattern TYPE_PATTERN = compile("\\$\\{type}");
    private static final Pattern AUTHOR_PATTERN = compile("\\$\\{author}");
    private static final Pattern COPYRIGHT_HOLDER_PATTERN = compile("\\$\\{copyrightHolder}");

    private final TemplateContext templateContext;

    /**
     * Constructor.
     */
    public EvaluateTemplate(TemplateContext templateContext) {
        this.templateContext = templateContext;
    }

    @Override
    public String apply(String template) {
        return
            replace(
                replace(
                    replace(
                        template,
                        TYPE_PATTERN,
                        templateContext.getTypeName()),
                    AUTHOR_PATTERN,
                    templateContext.getAuthor()),
                COPYRIGHT_HOLDER_PATTERN,
                templateContext.getCopyrightHolder());
    }

    private static String replace(String template, Pattern pattern, String substitution) {
        return pattern.matcher(template).replaceAll(substitution);
    }
}
