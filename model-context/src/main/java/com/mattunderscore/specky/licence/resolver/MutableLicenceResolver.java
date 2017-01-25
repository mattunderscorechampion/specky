package com.mattunderscore.specky.licence.resolver;

import java.util.concurrent.CompletableFuture;

/**
 * A mutable {@link LicenceResolver}.
 *
 * @author Matt Champion 25/01/2017
 */
public interface MutableLicenceResolver extends LicenceResolver {
    /**
     * Register a default licence.
     */
    CompletableFuture<Void> register(String licence);

    /**
     * Register a named licence.
     */
    CompletableFuture<Void> register(String name, String licence);
}
