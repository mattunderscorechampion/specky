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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.mattunderscore.specky.context.file.TemplateContext;

/**
 * Unit tests for {@link EmptyScope}.
 *
 * @author Matt Champion 10/02/2017
 */
public final class EmptyScopeTest {
    @Test
    public void resolveLicence() {
        assertFalse(EmptyScope.INSTANCE.resolveLicence("").isPresent());
    }

    @Test
    public void resolveType() {
        assertFalse(EmptyScope.INSTANCE.resolveType("").isPresent());
    }

    @Test
    public void resolveValue() {
        assertFalse(EmptyScope.INSTANCE.resolveValue("" ,false).isPresent());
    }

    @Test
    public void getAuthor() {
        assertNull(EmptyScope.INSTANCE.getAuthor());
    }

    @Test
    public void getPackage() {
        assertNull(EmptyScope.INSTANCE.getPackage());
    }

    @Test
    public void getCopyrightHolder() {
        assertNull(EmptyScope.INSTANCE.getCopyrightHolder());
    }

    @Test
    public void toTemplateContext() {
        final TemplateContext templateContext = EmptyScope.INSTANCE.toTemplateContext("Type");

        assertEquals("Type", templateContext.getTypeName());
        assertNull(templateContext.getAuthor());
        assertNull(templateContext.getCopyrightHolder());
    }
}
