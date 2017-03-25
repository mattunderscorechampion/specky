
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
