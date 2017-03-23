
author "Matt Champion"

package com.mattunderscore.specky.context.file

imports
    java.nio.file.Path
    org.antlr.v4.runtime.ANTLRInputStream
    java.time.Instant default Instant.now()
    com.mattunderscore.specky.model.ConstructionMethod default ConstructionMethod.CONSTRUCTOR

bean FileContext "Context of the file."
    properties
        Path file
        ANTLRInputStream antlrStream
    licence BSD3Clause

value TemplateContext "Context to evaluate templates in."
    properties
        String typeName
        Instant buildTime
        optional String copyrightHolder
        optional String author
    licence BSD3Clause
    options
        immutable builder

value ParameterDesc "Description of parameter."
    properties
        String name "Name of the parameter."
        String type "Type name of the parameter."
        List<String> typeParameters "Any generic parameters of the type."
        boolean optional "If the parameter is optional."
    licence BSD3Clause
    options
        immutable builder

value ConstructionDetails "Details of how to construct a value."
    properties
        String packageName "Name of the package the type is in."
        String name "Name of the type."
        List<ParameterDesc> properties "Parameters of the type."
        ConstructionMethod method "Construction method of type."
    licence BSD3Clause
    options
        immutable builder
