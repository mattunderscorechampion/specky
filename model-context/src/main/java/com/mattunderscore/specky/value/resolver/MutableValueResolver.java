package com.mattunderscore.specky.value.resolver;

import com.mattunderscore.specky.literal.model.LiteralDesc;

import java.util.concurrent.CompletableFuture;

/**
 * A mutable resolver.
 * @author Matt Champion 29/12/2016
 */
public interface MutableValueResolver extends DefaultValueResolver {
    /**
     * Register a default value for a type.
     */
    CompletableFuture<Void> register(String type, LiteralDesc defaultValue);
}
