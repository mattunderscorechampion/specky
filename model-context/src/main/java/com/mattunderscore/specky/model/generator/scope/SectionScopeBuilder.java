package com.mattunderscore.specky.model.generator.scope;

/**
 * Builder for section scopes.
 * @author Matt Champion 28/12/2016
 */
public interface SectionScopeBuilder {
    /**
     * @return a new scope
     */
    PendingScope beginNewScope(String sectionName);

    /**
     * Complete the scope.
     */
    void completeScope();

    /**
     * @return the current pending scope
     */
    PendingScope currentScope();
}
