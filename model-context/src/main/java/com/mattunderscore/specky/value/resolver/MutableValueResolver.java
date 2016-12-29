package com.mattunderscore.specky.value.resolver;

import com.squareup.javapoet.CodeBlock;

/**
 * A mutable resolver.
 * @author Matt Champion 29/12/2016
 */
public interface MutableValueResolver extends DefaultValueResolver {
    /**
     * Register a default value for a type.
     * @throws IllegalArgumentException if a mapping already exists
     */
    MutableValueResolver register(String type, CodeBlock defaultValue);
}
