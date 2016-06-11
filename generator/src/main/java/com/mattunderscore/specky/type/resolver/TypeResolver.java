package com.mattunderscore.specky.type.resolver;

import java.util.Optional;

/**
 * Resolve a type name to a fully qualified name.
 *
 * @author Matt Champion on 08/06/16
 */
public interface TypeResolver {
    /**
     * @param name the type name
     * @return optional fully qualified name
     */
    Optional<String> resolve(String name);
}
