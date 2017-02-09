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
        final MutableLicenceResolver resolver = new LicenceResolverImpl();

        final CompletableFuture<Void> result = resolver.register("name", "licence");

        assertTrue(result.isDone());
        assertFalse(result.isCompletedExceptionally());
    }

    @Test
    public void registerDefault() {
        final MutableLicenceResolver resolver = new LicenceResolverImpl();

        final CompletableFuture<Void> result = resolver.register("licence");

        assertTrue(result.isDone());
        assertFalse(result.isCompletedExceptionally());
    }

    @Test
    public void resolveUnknown() {
        final MutableLicenceResolver resolver = new LicenceResolverImpl();

        final Optional<String> result = resolver.resolveLicence("name");

        assertFalse(result.isPresent());
    }

    @Test
    public void registerResolve() {
        final MutableLicenceResolver resolver = new LicenceResolverImpl();

        resolver.register("name", "licence");

        final Optional<String> result = resolver.resolveLicence("name");

        assertTrue(result.isPresent());
        assertEquals("licence", result.get());
    }

    @Test
    public void registerResolveDefault() {
        final MutableLicenceResolver resolver = new LicenceResolverImpl();

        resolver.register("licence");

        final Optional<String> result = resolver.resolveLicence(null);

        assertTrue(result.isPresent());
        assertEquals("licence", result.get());
    }

    @Test
    public void registerDuplicate() {
        final MutableLicenceResolver resolver = new LicenceResolverImpl();

        resolver.register("name", "licence");

        final CompletableFuture<Void> result = resolver.register("name", "licence");

        assertTrue(result.isDone());
        assertTrue(result.isCompletedExceptionally());
    }

    @Test
    public void registerDefaultDuplicate() {
        final MutableLicenceResolver resolver = new LicenceResolverImpl();

        resolver.register("licence");

        final CompletableFuture<Void> result = resolver.register("licence");

        assertTrue(result.isDone());
        assertTrue(result.isCompletedExceptionally());
    }
}
