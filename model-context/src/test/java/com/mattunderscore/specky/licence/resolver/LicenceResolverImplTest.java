package com.mattunderscore.specky.licence.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.junit.Test;

/**
 * Unit tests for {@link LicenceResolverImpl}.
 *
 * @author Matt Champion 24/01/2017
 */
public final class LicenceResolverImplTest {
    @Test
    public void register() {
        final LicenceResolver resolver = new LicenceResolverImpl();

        final CompletableFuture<Void> result = resolver.register("name", "licence");

        assertTrue(result.isDone());
        assertFalse(result.isCompletedExceptionally());
    }

    @Test
    public void registerDefault() {
        final LicenceResolver resolver = new LicenceResolverImpl();

        final CompletableFuture<Void> result = resolver.register("licence");

        assertTrue(result.isDone());
        assertFalse(result.isCompletedExceptionally());
    }

    @Test
    public void resolveUnknown() {
        final LicenceResolver resolver = new LicenceResolverImpl();

        final Optional<String> result = resolver.resolve("name");

        assertFalse(result.isPresent());
    }

    @Test
    public void registerResolve() {
        final LicenceResolver resolver = new LicenceResolverImpl();

        resolver.register("name", "licence");

        final Optional<String> result = resolver.resolve("name");

        assertTrue(result.isPresent());
        assertEquals("licence", result.get());
    }

    @Test
    public void registerResolveDefault() {
        final LicenceResolver resolver = new LicenceResolverImpl();

        resolver.register("licence");

        final Optional<String> result = resolver.resolve(null);

        assertTrue(result.isPresent());
        assertEquals("licence", result.get());
    }

    @Test
    public void registerDuplicate() {
        final LicenceResolver resolver = new LicenceResolverImpl();

        resolver.register("name", "licence");

        final CompletableFuture<Void> result = resolver.register("name", "licence");

        assertTrue(result.isDone());
        assertTrue(result.isCompletedExceptionally());
    }

    @Test
    public void registerDefaultDuplicate() {
        final LicenceResolver resolver = new LicenceResolverImpl();

        resolver.register("licence");

        final CompletableFuture<Void> result = resolver.register("licence");

        assertTrue(result.isDone());
        assertTrue(result.isCompletedExceptionally());
    }
}
