package com.mattunderscore.specky;

import static java.util.Arrays.asList;

import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.SpeckyListener;

/**
 * A delegating parse tree listener.
 *
 * @author Matt Champion 14/09/2017
 */
public final class DelegatingParseListener implements SpeckyListener {
    private final List<SpeckyListener> delegates;

    /**
     * Constructor.
     */
    public DelegatingParseListener(SpeckyListener... delegates) {
        this.delegates = asList(delegates);
    }

    @Override
    public void visitTerminal(TerminalNode node) {
        delegates.forEach(delegate -> delegate.visitTerminal(node));
    }

    @Override
    public void visitErrorNode(ErrorNode node) {
        delegates.forEach(delegate -> delegate.visitErrorNode(node));
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        delegates.forEach(delegate -> delegate.enterEveryRule(ctx));
    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
        delegates.forEach(delegate -> delegate.exitEveryRule(ctx));
    }

    @Override
    public void enterString_value(Specky.String_valueContext ctx) {
        delegates.forEach(delegate -> delegate.enterString_value(ctx));
    }

    @Override
    public void exitString_value(Specky.String_valueContext ctx) {
        delegates.forEach(delegate -> delegate.exitString_value(ctx));
    }

    @Override
    public void enterConstruction(Specky.ConstructionContext ctx) {
        delegates.forEach(delegate -> delegate.enterConstruction(ctx));
    }

    @Override
    public void exitConstruction(Specky.ConstructionContext ctx) {
        delegates.forEach(delegate -> delegate.exitConstruction(ctx));
    }

    @Override
    public void enterDefault_value(Specky.Default_valueContext ctx) {
        delegates.forEach(delegate -> delegate.enterDefault_value(ctx));
    }

    @Override
    public void exitDefault_value(Specky.Default_valueContext ctx) {
        delegates.forEach(delegate -> delegate.exitDefault_value(ctx));
    }

    @Override
    public void enterValue_expression(Specky.Value_expressionContext ctx) {
        delegates.forEach(delegate -> delegate.enterValue_expression(ctx));
    }

    @Override
    public void exitValue_expression(Specky.Value_expressionContext ctx) {
        delegates.forEach(delegate -> delegate.exitValue_expression(ctx));
    }

    @Override
    public void enterDefault_value_expression(Specky.Default_value_expressionContext ctx) {
        delegates.forEach(delegate -> delegate.enterDefault_value_expression(ctx));
    }

    @Override
    public void exitDefault_value_expression(Specky.Default_value_expressionContext ctx) {
        delegates.forEach(delegate -> delegate.exitDefault_value_expression(ctx));
    }

    @Override
    public void enterTypeParameters(Specky.TypeParametersContext ctx) {
        delegates.forEach(delegate -> delegate.enterTypeParameters(ctx));
    }

    @Override
    public void exitTypeParameters(Specky.TypeParametersContext ctx) {
        delegates.forEach(delegate -> delegate.exitTypeParameters(ctx));
    }

    @Override
    public void enterPropertyName(Specky.PropertyNameContext ctx) {
        delegates.forEach(delegate -> delegate.enterPropertyName(ctx));
    }

    @Override
    public void exitPropertyName(Specky.PropertyNameContext ctx) {
        delegates.forEach(delegate -> delegate.exitPropertyName(ctx));
    }

    @Override
    public void enterConstraint_operator(Specky.Constraint_operatorContext ctx) {
        delegates.forEach(delegate -> delegate.enterConstraint_operator(ctx));
    }

    @Override
    public void exitConstraint_operator(Specky.Constraint_operatorContext ctx) {
        delegates.forEach(delegate -> delegate.exitConstraint_operator(ctx));
    }

    @Override
    public void enterConstraint_literal(Specky.Constraint_literalContext ctx) {
        delegates.forEach(delegate -> delegate.enterConstraint_literal(ctx));
    }

    @Override
    public void exitConstraint_literal(Specky.Constraint_literalContext ctx) {
        delegates.forEach(delegate -> delegate.exitConstraint_literal(ctx));
    }

    @Override
    public void enterConstraint_predicate(Specky.Constraint_predicateContext ctx) {
        delegates.forEach(delegate -> delegate.enterConstraint_predicate(ctx));
    }

    @Override
    public void exitConstraint_predicate(Specky.Constraint_predicateContext ctx) {
        delegates.forEach(delegate -> delegate.exitConstraint_predicate(ctx));
    }

    @Override
    public void enterConstraint_proposition(Specky.Constraint_propositionContext ctx) {
        delegates.forEach(delegate -> delegate.enterConstraint_proposition(ctx));
    }

    @Override
    public void exitConstraint_proposition(Specky.Constraint_propositionContext ctx) {
        delegates.forEach(delegate -> delegate.exitConstraint_proposition(ctx));
    }

    @Override
    public void enterConstraint_expression(Specky.Constraint_expressionContext ctx) {
        delegates.forEach(delegate -> delegate.enterConstraint_expression(ctx));
    }

    @Override
    public void exitConstraint_expression(Specky.Constraint_expressionContext ctx) {
        delegates.forEach(delegate -> delegate.exitConstraint_expression(ctx));
    }

    @Override
    public void enterConstraint_statement(Specky.Constraint_statementContext ctx) {
        delegates.forEach(delegate -> delegate.enterConstraint_statement(ctx));
    }

    @Override
    public void exitConstraint_statement(Specky.Constraint_statementContext ctx) {
        delegates.forEach(delegate -> delegate.exitConstraint_statement(ctx));
    }

    @Override
    public void enterProperty(Specky.PropertyContext ctx) {
        delegates.forEach(delegate -> delegate.enterProperty(ctx));
    }

    @Override
    public void exitProperty(Specky.PropertyContext ctx) {
        delegates.forEach(delegate -> delegate.exitProperty(ctx));
    }

    @Override
    public void enterQualifiedName(Specky.QualifiedNameContext ctx) {
        delegates.forEach(delegate -> delegate.enterQualifiedName(ctx));
    }

    @Override
    public void exitQualifiedName(Specky.QualifiedNameContext ctx) {
        delegates.forEach(delegate -> delegate.exitQualifiedName(ctx));
    }

    @Override
    public void enterPackage_name(Specky.Package_nameContext ctx) {
        delegates.forEach(delegate -> delegate.enterPackage_name(ctx));
    }

    @Override
    public void exitPackage_name(Specky.Package_nameContext ctx) {
        delegates.forEach(delegate -> delegate.exitPackage_name(ctx));
    }

    @Override
    public void enterSingleImport(Specky.SingleImportContext ctx) {
        delegates.forEach(delegate -> delegate.enterSingleImport(ctx));
    }

    @Override
    public void exitSingleImport(Specky.SingleImportContext ctx) {
        delegates.forEach(delegate -> delegate.exitSingleImport(ctx));
    }

    @Override
    public void enterImports(Specky.ImportsContext ctx) {
        delegates.forEach(delegate -> delegate.enterImports(ctx));
    }

    @Override
    public void exitImports(Specky.ImportsContext ctx) {
        delegates.forEach(delegate -> delegate.exitImports(ctx));
    }

    @Override
    public void enterProps(Specky.PropsContext ctx) {
        delegates.forEach(delegate -> delegate.enterProps(ctx));
    }

    @Override
    public void exitProps(Specky.PropsContext ctx) {
        delegates.forEach(delegate -> delegate.exitProps(ctx));
    }

    @Override
    public void enterOpts(Specky.OptsContext ctx) {
        delegates.forEach(delegate -> delegate.enterOpts(ctx));
    }

    @Override
    public void exitOpts(Specky.OptsContext ctx) {
        delegates.forEach(delegate -> delegate.exitOpts(ctx));
    }

    @Override
    public void enterSupertypes(Specky.SupertypesContext ctx) {
        delegates.forEach(delegate -> delegate.enterSupertypes(ctx));
    }

    @Override
    public void exitSupertypes(Specky.SupertypesContext ctx) {
        delegates.forEach(delegate -> delegate.exitSupertypes(ctx));
    }

    @Override
    public void enterLicence(Specky.LicenceContext ctx) {
        delegates.forEach(delegate -> delegate.enterLicence(ctx));
    }

    @Override
    public void exitLicence(Specky.LicenceContext ctx) {
        delegates.forEach(delegate -> delegate.exitLicence(ctx));
    }

    @Override
    public void enterImplementationSpec(Specky.ImplementationSpecContext ctx) {
        delegates.forEach(delegate -> delegate.enterImplementationSpec(ctx));
    }

    @Override
    public void exitImplementationSpec(Specky.ImplementationSpecContext ctx) {
        delegates.forEach(delegate -> delegate.exitImplementationSpec(ctx));
    }

    @Override
    public void enterTypeSpec(Specky.TypeSpecContext ctx) {
        delegates.forEach(delegate -> delegate.enterTypeSpec(ctx));
    }

    @Override
    public void exitTypeSpec(Specky.TypeSpecContext ctx) {
        delegates.forEach(delegate -> delegate.exitTypeSpec(ctx));
    }

    @Override
    public void enterAuthor(Specky.AuthorContext ctx) {
        delegates.forEach(delegate -> delegate.enterAuthor(ctx));
    }

    @Override
    public void exitAuthor(Specky.AuthorContext ctx) {
        delegates.forEach(delegate -> delegate.exitAuthor(ctx));
    }

    @Override
    public void enterCopyrightHolder(Specky.CopyrightHolderContext ctx) {
        delegates.forEach(delegate -> delegate.enterCopyrightHolder(ctx));
    }

    @Override
    public void exitCopyrightHolder(Specky.CopyrightHolderContext ctx) {
        delegates.forEach(delegate -> delegate.exitCopyrightHolder(ctx));
    }

    @Override
    public void enterLicenceDeclaration(Specky.LicenceDeclarationContext ctx) {
        delegates.forEach(delegate -> delegate.enterLicenceDeclaration(ctx));
    }

    @Override
    public void exitLicenceDeclaration(Specky.LicenceDeclarationContext ctx) {
        delegates.forEach(delegate -> delegate.exitLicenceDeclaration(ctx));
    }

    @Override
    public void enterNote(Specky.NoteContext ctx) {
        delegates.forEach(delegate -> delegate.enterNote(ctx));
    }

    @Override
    public void exitNote(Specky.NoteContext ctx) {
        delegates.forEach(delegate -> delegate.exitNote(ctx));
    }

    @Override
    public void enterSectionContent(Specky.SectionContentContext ctx) {
        delegates.forEach(delegate -> delegate.enterSectionContent(ctx));
    }

    @Override
    public void exitSectionContent(Specky.SectionContentContext ctx) {
        delegates.forEach(delegate -> delegate.exitSectionContent(ctx));
    }

    @Override
    public void enterSectionDeclaration(Specky.SectionDeclarationContext ctx) {
        delegates.forEach(delegate -> delegate.enterSectionDeclaration(ctx));
    }

    @Override
    public void exitSectionDeclaration(Specky.SectionDeclarationContext ctx) {
        delegates.forEach(delegate -> delegate.exitSectionDeclaration(ctx));
    }

    @Override
    public void enterDefaultSectionDeclaration(Specky.DefaultSectionDeclarationContext ctx) {
        delegates.forEach(delegate -> delegate.enterDefaultSectionDeclaration(ctx));
    }

    @Override
    public void exitDefaultSectionDeclaration(Specky.DefaultSectionDeclarationContext ctx) {
        delegates.forEach(delegate -> delegate.exitDefaultSectionDeclaration(ctx));
    }

    @Override
    public void enterSpec(Specky.SpecContext ctx) {
        delegates.forEach(delegate -> delegate.enterSpec(ctx));
    }

    @Override
    public void exitSpec(Specky.SpecContext ctx) {
        delegates.forEach(delegate -> delegate.exitSpec(ctx));
    }
}
