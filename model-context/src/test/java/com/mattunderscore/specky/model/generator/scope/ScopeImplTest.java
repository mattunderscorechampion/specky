/* Copyright © 2017 Matthew Champion
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
 * Neither the name of mattunderscore.com nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL MATTHEW CHAMPION BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package com.mattunderscore.specky.model.generator.scope;

import static com.mattunderscore.specky.model.generator.scope.EmptyScope.INSTANCE;
import static java.util.Optional.empty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.mattunderscore.specky.context.file.TemplateContext;
import com.mattunderscore.specky.licence.resolver.LicenceResolver;
import com.mattunderscore.specky.type.resolver.TypeResolver;
import com.mattunderscore.specky.value.resolver.DefaultValueResolver;
import com.squareup.javapoet.CodeBlock;

/**
 * Unit tests for {@link ScopeImpl}.
 *
 * @author Matt Champion 10/02/2017
 */
public final class ScopeImplTest {
    @Mock
    private Scope parentScope;
    @Mock
    private DefaultValueResolver valueResolver;
    @Mock
    private TypeResolver typeResolver;
    @Mock
    private LicenceResolver licenceResolver;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @After
    public void postConditions() {
        verifyNoMoreInteractions(typeResolver, valueResolver, licenceResolver, parentScope);
    }

    @Test
    public void getAuthor() throws Exception {
        final Scope scope = new ScopeImpl(INSTANCE, valueResolver, typeResolver, licenceResolver, "author", "package", "copyright");

        assertEquals("author", scope.getAuthor());
    }

    @Test
    public void getPackage() throws Exception {
        final Scope scope = new ScopeImpl(INSTANCE, valueResolver, typeResolver, licenceResolver, "author", "package", "copyright");

        assertEquals("package", scope.getPackage());
    }

    @Test
    public void getCopyrightHolder() throws Exception {
        final Scope scope = new ScopeImpl(INSTANCE, valueResolver, typeResolver, licenceResolver, "author", "package", "copyright");

        assertEquals("copyright", scope.getCopyrightHolder());
    }

    @Test
    public void resolveLicence() throws Exception {
        when(licenceResolver.resolveLicence("licence")).thenReturn(Optional.of("licence"));

        final Scope scope = new ScopeImpl(INSTANCE, valueResolver, typeResolver, licenceResolver, "author", "package", "copyright");

        assertEquals("licence", scope.resolveLicence("licence").get());
        verify(licenceResolver).resolveLicence("licence");
    }

    @Test
    public void resolveType() throws Exception {
        when(typeResolver.resolveType("type")).thenReturn(Optional.of("type"));

        final Scope scope = new ScopeImpl(INSTANCE, valueResolver, typeResolver, licenceResolver, "author", "package", "copyright");

        assertEquals("type", scope.resolveType("type").get());
        verify(typeResolver).resolveType("type");
    }

    @Test
    public void resolveValue() throws Exception {
        when(valueResolver.resolveValue("value", false)).thenReturn(Optional.of(CodeBlock.of("value")));

        final Scope scope = new ScopeImpl(INSTANCE, valueResolver, typeResolver, licenceResolver, "author", "package", "copyright");

        assertEquals(CodeBlock.of("value"), scope.resolveValue("value", false).get());
        verify(valueResolver).resolveValue("value", false);
    }

    @Test
    public void getAuthorFromParent() throws Exception {
        when(parentScope.getAuthor()).thenReturn("author");

        final Scope scope = new ScopeImpl(parentScope, valueResolver, typeResolver, licenceResolver, null, null, "copyright");

        assertEquals("author", scope.getAuthor());
        verify(parentScope).getAuthor();
    }

    @Test
    public void getPackageFromParent() throws Exception {
        when(parentScope.getPackage()).thenReturn("package");

        final Scope scope = new ScopeImpl(parentScope, valueResolver, typeResolver, licenceResolver, null, null, "copyright");

        assertEquals("package", scope.getPackage());
        verify(parentScope).getPackage();
    }

    @Test
    public void resolveLicenceFromParent() throws Exception {
        when(licenceResolver.resolveLicence("licence")).thenReturn(empty());
        when(parentScope.resolveLicence("licence")).thenReturn(Optional.of("licence"));

        final Scope scope = new ScopeImpl(parentScope, valueResolver, typeResolver, licenceResolver, "author", "package", "copyright");

        assertEquals("licence", scope.resolveLicence("licence").get());
        verify(licenceResolver).resolveLicence("licence");
        verify(parentScope).resolveLicence("licence");
    }

    @Test
    public void resolveTypeFromParent() throws Exception {
        when(typeResolver.resolveType("type")).thenReturn(empty());
        when(parentScope.resolveType("type")).thenReturn(Optional.of("type"));

        final Scope scope = new ScopeImpl(parentScope, valueResolver, typeResolver, licenceResolver, "author", "package", "copyright");

        assertEquals("type", scope.resolveType("type").get());
        verify(typeResolver).resolveType("type");
        verify(parentScope).resolveType("type");
    }

    @Test
    public void resolveValueFromParent() throws Exception {
        when(valueResolver.resolveValue("value", false)).thenReturn(empty());
        when(parentScope.resolveValue("value", false)).thenReturn(Optional.of(CodeBlock.of("value")));

        final Scope scope = new ScopeImpl(parentScope, valueResolver, typeResolver, licenceResolver, "author", "package", "copyright");

        assertEquals(CodeBlock.of("value"), scope.resolveValue("value", false).get());
        verify(valueResolver).resolveValue("value", false);
        verify(parentScope).resolveValue("value", false);
    }

    @Test
    public void toTemplateContext() {
        final Scope scope = new ScopeImpl(parentScope, valueResolver, typeResolver, licenceResolver, "author", "package", "copyright");
        final TemplateContext templateContext = scope.toTemplateContext("Type");

        assertEquals("Type", templateContext.getTypeName());
        assertEquals("author", templateContext.getAuthor());
        assertEquals("copyright", templateContext.getCopyrightHolder());
    }
}
