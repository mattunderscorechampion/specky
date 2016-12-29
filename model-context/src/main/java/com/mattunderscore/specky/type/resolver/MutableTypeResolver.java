package com.mattunderscore.specky.type.resolver;

/**
 * @author Matt Champion 29/12/2016
 */
public interface MutableTypeResolver extends TypeResolver {
    /**
     * Register a type with the resolver.
     */
    MutableTypeResolver registerTypeName(String packageName, String typeName);
}
