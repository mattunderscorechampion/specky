/* Copyright © 2016 Matthew Champion All rights reserved.

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

package com.mattunderscore.specky;

import com.mattunderscore.specky.model.generator.scope.SectionScopeResolver;
import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.SpeckyBaseListener;

import net.jcip.annotations.NotThreadSafe;

/**
 * AST listener for section scopes.
 *
 * @author Matt Champion 25/12/2016
 */
@NotThreadSafe
public final class SectionScopeListener extends SpeckyBaseListener {
    private final SectionScopeResolver sectionScopeResolver;
    private String sectionName;

    /**
     * Constructor.
     */
    public SectionScopeListener(SectionScopeResolver sectionScopeResolver) {

        this.sectionScopeResolver = sectionScopeResolver;
    }

    @Override
    public void enterDefaultSectionDeclaration(Specky.DefaultSectionDeclarationContext ctx) {
        sectionName = null;
    }

    @Override
    public void enterSectionDeclaration(Specky.SectionDeclarationContext ctx) {
        sectionName = ctx.string_value().getText();
    }

    @Override
    public void enterSectionContent(Specky.SectionContentContext ctx) {
        sectionScopeResolver.beginNewScope(sectionName);
    }

    @Override
    public void exitSectionContent(Specky.SectionContentContext ctx) {
        sectionScopeResolver.completeScope();
    }
}
