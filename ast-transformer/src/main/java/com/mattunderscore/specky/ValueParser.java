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

package com.mattunderscore.specky;

import java.util.Optional;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.mattunderscore.specky.construction.method.resolver.ConstructionMethodResolver;
import com.mattunderscore.specky.error.listeners.InternalSemanticErrorListener;
import com.mattunderscore.specky.literal.model.ComplexLiteral;
import com.mattunderscore.specky.literal.model.ConstantLiteral;
import com.mattunderscore.specky.literal.model.IntegerLiteral;
import com.mattunderscore.specky.literal.model.LiteralDesc;
import com.mattunderscore.specky.literal.model.NamedComplexLiteral;
import com.mattunderscore.specky.literal.model.RealLiteral;
import com.mattunderscore.specky.literal.model.StringLiteral;
import com.mattunderscore.specky.literal.model.UnstructuredLiteral;
import com.mattunderscore.specky.model.generator.scope.Scope;
import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.Specky.Value_expressionContext;
import com.mattunderscore.specky.type.resolver.TypeResolver;

/**
 * Parser for values.
 * @author Matt Champion on 22/03/17
 */
/*package*/ final class ValueParser {
    private final InternalSemanticErrorListener errorListener;
    private final ConstructionMethodResolver constructionMethodResolver;

    /**
     * Constructor.
     */
    ValueParser(InternalSemanticErrorListener errorListener, ConstructionMethodResolver constructionMethodResolver) {
        this.errorListener = errorListener;
        this.constructionMethodResolver = constructionMethodResolver;
    }

    /**
     * @return code block for the value
     */
    LiteralDesc getValue(Value_expressionContext expressionValue, TypeResolver typeResolver) {
        if (expressionValue.STRING_LITERAL() != null) {
            final String stringValue = expressionValue.STRING_LITERAL().getText();
            return StringLiteral.builder().stringLiteral(stringValue.substring(1, stringValue.length() - 1)).build();
        }

        if (expressionValue.VALUE_REAL_LITERAL() != null) {
            return RealLiteral.builder().realLiteral(expressionValue.VALUE_REAL_LITERAL().getText()).build();
        }

        if (expressionValue.VALUE_INTEGER_LITERAL() != null) {
            return IntegerLiteral.builder().integerLiteral(expressionValue.VALUE_INTEGER_LITERAL().getText()).build();
        }

        if (expressionValue.VALUE_MEMBER_ACCESSOR() != null) {
            return ConstantLiteral
                .builder()
                .typeName(expressionValue.VALUE_IDENTIFIER().get(0).getText())
                .constant(expressionValue.VALUE_IDENTIFIER().get(1).getText())
                .build();
        }

        if (expressionValue.VALUE_IDENTIFIER().size() == 1) {
            return getComplexLiteral(expressionValue, typeResolver);
        }

        return getNamedComplexLiteral(expressionValue, typeResolver);
    }

    private LiteralDesc getComplexLiteral(Value_expressionContext expressionValue, TypeResolver typeResolver) {
        final Optional<String> maybeType = typeResolver.resolveType(expressionValue.VALUE_IDENTIFIER().get(0).getText());
        if (!maybeType.isPresent()) {
            errorListener.onSemanticError("Type name not found", expressionValue);
            return null;
        }

        final String type = maybeType.get();
        final ComplexLiteral.Builder valueBuilder = ComplexLiteral.builder().typeName(type);

        expressionValue
                .value_expression()
                .stream()
                .map(expr -> getValue(expr, typeResolver))
                .forEach(valueBuilder::addSubvalue);

        constructionMethodResolver
            .resolveConstructionMethod(type)
            .ifPresent(valueBuilder::constructionMethod);

        return valueBuilder.build();
    }

    private LiteralDesc getNamedComplexLiteral(Value_expressionContext expressionValue, TypeResolver typeResolver) {
        final Optional<String> maybeType = typeResolver.resolveType(expressionValue.VALUE_IDENTIFIER().get(0).getText());
        if (!maybeType.isPresent()) {
            errorListener.onSemanticError("Type name not found", expressionValue);
            return null;
        }

        final String type = maybeType.get();
        final NamedComplexLiteral.Builder valueBuilder = NamedComplexLiteral.builder().typeName(type);

        expressionValue
            .VALUE_IDENTIFIER()
            .stream()
            .skip(1)
            .map(ParseTree::getText)
            .forEach(valueBuilder::addName);

        expressionValue
                .value_expression()
                .stream()
                .map(expr -> getValue(expr, typeResolver))
                .forEach(valueBuilder::addSubvalue);

        constructionMethodResolver
            .resolveConstructionMethod(type)
            .ifPresent(valueBuilder::constructionMethod);

        return valueBuilder.build();
    }

    /**
     * @return code block for the default value
     */
    LiteralDesc getDefaultValue(Specky.PropertyContext context, Scope scope) {
        if (context.default_value() == null) {
            final Optional<String> maybeType = scope.resolveType(context.Identifier().getText());
            if (!maybeType.isPresent()) {
                errorListener.onSemanticError("Type name not found", context);
                return null;
            }

            final Optional<LiteralDesc> typeDefaultValue = scope
                    .resolveValue(maybeType.get(), context.OPTIONAL() != null);
            return typeDefaultValue.orElse(null);
        }

        final TerminalNode anything = context.default_value().ANYTHING();
        if (anything != null) {
            return UnstructuredLiteral.builder().literal(anything.getText()).build();
        }

        final Value_expressionContext expressionValue = context
                .default_value()
                .default_value_expression()
                .value_expression();

        return getValue(expressionValue, scope);
    }
}
