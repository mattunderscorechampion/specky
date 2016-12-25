/* Copyright © 2016 Matthew Champion
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

package com.mattunderscore.specky.dsl;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.mattunderscore.specky.dsl.model.DSLAbstractTypeDesc;
import com.mattunderscore.specky.dsl.model.DSLBeanDesc;
import com.mattunderscore.specky.dsl.model.DSLImplementationDesc;
import com.mattunderscore.specky.dsl.model.DSLImportDesc;
import com.mattunderscore.specky.dsl.model.DSLLicence;
import com.mattunderscore.specky.dsl.model.DSLPropertyDesc;
import com.mattunderscore.specky.dsl.model.DSLSpecDesc;
import com.mattunderscore.specky.dsl.model.DSLValueDesc;
import com.mattunderscore.specky.model.ConstructionMethod;
import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.Specky.ImplementationSpecContext;
import com.mattunderscore.specky.parser.Specky.ImportsContext;
import com.mattunderscore.specky.parser.Specky.PropertyContext;
import com.mattunderscore.specky.parser.Specky.SpecContext;
import com.mattunderscore.specky.parser.Specky.TypeParametersContext;
import com.mattunderscore.specky.parser.Specky.TypeSpecContext;
import com.mattunderscore.specky.proposition.Normaliser;

/**
 * Processor for the ANTLR4 generated AST. Returns a better representation of the DSL.
 *
 * @author Matt Champion on 05/06/16
 */
public final class SpecBuilder {

    private final Normaliser normaliser = new Normaliser();
    private final ConstraintFactory constraintFactory = new ConstraintFactory();

    /**
     * Constructor.
     */
    public SpecBuilder() {
    }

    /**
     * @return the list of {@link DSLSpecDesc} from a {@link SpecContext}.
     */
    public List<DSLSpecDesc> build(SpecContext context) {

        final Stream<Specky.SectionContentContext> stream0 = Stream
            .of(context.defaultSectionDeclaration())
            .filter(Objects::nonNull)
            .map(Specky.DefaultSectionDeclarationContext::sectionContent);

        final Stream<Specky.SectionContentContext> stream1 = context
            .sectionDeclaration()
            .stream()
            .filter(Objects::nonNull)
            .map(Specky.SectionDeclarationContext::sectionContent);

        return Stream
            .concat(stream0, stream1)
            .map(this::build)
            .collect(toList());
    }

    private DSLSpecDesc build(Specky.SectionContentContext context) {
        final ImportsContext importsContext = context.imports();
        final List<DSLImportDesc> imports = importsContext == null ?
            emptyList() :
            importsContext
                .singleImport()
                .stream()
                .map(singleImportContext -> DSLImportDesc
                    .builder()
                    .typeName(singleImportContext.qualifiedName().getText())
                    .defaultValue(singleImportContext.default_value() == null ?
                        null :
                        singleImportContext.default_value().ANYTHING().getText())
                    .build())
                .collect(toList());

        return DSLSpecDesc
            .builder()
            .ifThen(context.author() != null, builder -> builder.author(toValue(context.author().string_value())))
            .ifThen(
                context.licenceDeclaration() != null,
                builder -> builder.licences(context.licenceDeclaration()
                    .stream()
                    .map(licence -> DSLLicence
                        .builder()
                        .ifThen(
                            licence.Identifier() != null,
                            licenceBuilder -> licenceBuilder.identifier(licence.Identifier().getText()))
                        .licence(toValue(licence.string_value()))
                        .build())
                    .collect(toList())))
            .packageName(context.package_name().qualifiedName().getText())
            .importTypes(imports)
            .types(context
                .typeSpec()
                .stream()
                .map(this::createAbstractType)
                .collect(toList()))
            .implementations(context
                .implementationSpec()
                .stream()
                .map(this::createType)
                .collect(toList()))
            .build();
    }

    private DSLImplementationDesc createType(ImplementationSpecContext context) {
        final String typeName = context.Identifier().getText();

        final Specky.LicenceContext licence = context.licence();
        DSLLicence dslLicence = null;
        if (licence != null) {
            dslLicence = DSLLicence
                .builder()
                .ifThen(
                    licence.Identifier() != null,
                    licenceBuilder -> licenceBuilder.identifier(licence.Identifier().getText()))
                .licence(toValue(licence.string_value()))
                .build();
        }

        final List<DSLPropertyDesc> properties = context.props() == null ?
            emptyList() :
            context
                .props()
                .property()
                .stream()
                .map(this::createProperty)
                .collect(toList());
        final ConstructionMethod constructionMethod = toConstructionDesc(context);
        final List<String> supertypes;
        if (context.supertypes() != null) {
            supertypes = context
                .supertypes()
                .Identifier()
                .stream()
                .map(TerminalNode::getText)
                .collect(toList());
        }
        else {
            supertypes = emptyList();
        }
        if (context.BEAN() == null) {
            return DSLValueDesc
                .builder()
                .name(typeName)
                .licence(dslLicence)
                .properties(properties)
                .constructionMethod(constructionMethod)
                .withModification(withModifications(context))
                .supertypes(supertypes)
                .description(context.StringLiteral() == null ?
                    "Value type $L.\n\nAuto-generated from specification." :
                    context.StringLiteral().getText().substring(1, context.StringLiteral().getText().length() - 1))
                .build();
        }
        else {
            return DSLBeanDesc
                .builder()
                .name(typeName)
                .licence(dslLicence)
                .properties(properties)
                .constructionMethod(constructionMethod)
                .withModification(withModifications(context))
                .supertypes(supertypes)
                .description(context.StringLiteral() == null ?
                    "Bean type $L.\n\nAuto-generated from specification." :
                    context.StringLiteral().getText().substring(1, context.StringLiteral().getText().length() - 1))
                .build();
        }
    }

    private DSLAbstractTypeDesc createAbstractType(TypeSpecContext context) {
        final Specky.LicenceContext licence = context.licence();
        DSLLicence dslLicence = null;
        if (licence != null) {
            dslLicence = DSLLicence
                .builder()
                .ifThen(
                    licence.Identifier() != null,
                    licenceBuilder -> licenceBuilder.identifier(licence.Identifier().getText()))
                .licence(toValue(licence.string_value()))
                .build();
        }
        final String typeName = context.Identifier().getText();
        final List<String> supertypes;
        if (context.supertypes() != null) {
            supertypes = context
                .supertypes()
                .Identifier()
                .stream()
                .map(TerminalNode::getText)
                .collect(toList());
        }
        else {
            supertypes = emptyList();
        }
        final List<DSLPropertyDesc> properties = context.props() == null ?
            emptyList() :
            context
            .props()
            .property()
            .stream()
            .map(this::createProperty)
            .collect(toList());
        return DSLAbstractTypeDesc
            .builder()
            .name(typeName)
            .licence(dslLicence)
            .properties(properties)
            .supertypes(supertypes)
            .description(context.StringLiteral() == null ?
                "Abstract type $L.\n\nAuto-generated from specification." :
                context.StringLiteral().getText().substring(1, context.StringLiteral().getText().length() - 1))
            .build();
        }

    private DSLPropertyDesc createProperty(PropertyContext context) {
        final String defaultValue = context.default_value() == null ?
            null :
            context.default_value().ANYTHING().getText();
        final TypeParametersContext parametersContext = context
                .typeParameters();
        final List<String> typeParameters = parametersContext == null ?
            emptyList() :
            parametersContext
                .Identifier()
                .stream()
                .map(ParseTree::getText)
                .collect(toList());
        return DSLPropertyDesc
            .builder()
            .name(context
                .propertyName()
                .getText())
            .type(context
                .Identifier()
                .getText())
            .typeParameters(typeParameters)
            .optional(context.OPTIONAL() != null)
            .defaultValue(defaultValue)
            .constraint(normaliser
                .normalise(constraintFactory
                    .create(context.propertyName().getText(), context.constraint_statement())))
            .description(context.StringLiteral() == null ?
                null :
                context.StringLiteral().getText().substring(1, context.StringLiteral().getText().length() - 1))
            .build();
    }

    private ConstructionMethod toConstructionDesc(ImplementationSpecContext typeSpec) {
        final Specky.OptsContext options = typeSpec.opts();

        if (options == null || options.construction() == null) {
            return ConstructionMethod.CONSTRUCTOR;
        }

        final String token = options.construction().getText();
        if ("constructor".equals(token)) {
            return ConstructionMethod.CONSTRUCTOR;
        }
        else if ("builder".equals(token)) {
            return ConstructionMethod.MUTABLE_BUILDER;
        }
        else if ("immutable builder".equals(token)) {
            return ConstructionMethod.IMMUTABLE_BUILDER;
        }
        else if ("from defaults".equals(token)) {
            return ConstructionMethod.FROM_DEFAULTS;
        }
        else {
            throw new IllegalArgumentException("Unsupported type");
        }
    }

    private boolean withModifications(ImplementationSpecContext typeSpec) {
        final Specky.OptsContext options = typeSpec.opts();

        return !(options == null || options.WITH_MODIFICATION() == null);
    }

    private String toValue(Specky.String_valueContext stringValue) {
        if (stringValue == null) {
            return null;
        }

        final TerminalNode multiline = stringValue.MULTILINE_STRING_LITERAL();
        if (multiline != null) {
            final String literal = multiline.getText();
            return literal.substring(3, literal.length() - 3);
        }
        else {
            final String literal = stringValue.StringLiteral().getText();
            return literal.substring(1, literal.length() - 1);
        }
    }
}
