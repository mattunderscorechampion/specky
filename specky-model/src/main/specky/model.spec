
section "Specky model"

note """The Specky model is an in-memory representation of a resolved
specification. Types have been resolved to their fully qualified names, the
default values have been selected, etc."""

author "Matt Champion"

package com.mattunderscore.specky.model

imports
    com.mattunderscore.specky.model.ConstructionMethod default ConstructionMethod.CONSTRUCTOR
    com.mattunderscore.specky.constraint.model.NFConjoinedDisjointPredicates
    com.squareup.javapoet.CodeBlock

value PropertyDesc "Description of a property."
    properties
        String name "Name of the property."
        String type "Type name of the property."
        List<String> typeParameters
        boolean optional "If the property is optional."
        boolean override "If the property is inherited."
        optional CodeBlock defaultValue "Default value of the property."
        optional NFConjoinedDisjointPredicates constraint "Constraint applied to the property."
        optional String description "Description of the property."
    licence BSD3Clause
    options
        immutable builder

type TypeDesc "Description of a type."
    licence BSD3Clause
    properties
        optional String licence "Licence of the type."
        optional String author "Author of the type."
        String packageName "Name of the package the type is in."
        String name "Name of the type."
        List<PropertyDesc> properties "Properties of the type."
        List<String> supertypes "Supertypes of the type."
        optional String description "Description of the type."

value AbstractTypeDesc : TypeDesc "Description of an abstract type."
    licence BSD3Clause
    options
        immutable builder

type ImplementationDesc : TypeDesc "Description of an implementation."
    licence BSD3Clause
    properties
        ConstructionMethod constructionMethod "Construction method of the type."
        boolean withModification "If it has methods that return a modified version."

value ValueDesc : ImplementationDesc "Description of a value type."
    licence BSD3Clause
    options
        immutable builder

value BeanDesc : ImplementationDesc "Description of a bean type."
    licence BSD3Clause
    options
        immutable builder

value SpecDesc "Description of a specification."
    properties
        List<String> importTypes "Types from outside the specification to import."
        List<ImplementationDesc> implementations "Implementations of the specification."
        List<AbstractTypeDesc> abstractTypes "Abstract types of the specification."
        List<TypeDesc> types "Types of the specification."
    licence BSD3Clause
    options
        immutable builder
