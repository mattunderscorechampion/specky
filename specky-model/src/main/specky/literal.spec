
section "Literal model"

author "Matt Champion"

package com.mattunderscore.specky.literal.model

type LiteralDesc "Description of a literal."

value UnstructuredLiteral : LiteralDesc "Represents an unstructured literal."
    properties
        String literal
    licence BSD3Clause
    options
        builder

value ConstantLiteral : LiteralDesc "Represents a constant or enum literal."
    properties
        String typeName
        String constant
    licence BSD3Clause
    options
        builder

value IntegerLiteral : LiteralDesc "Represents an integer literal."
    properties
        String integerLiteral
    licence BSD3Clause
    options
        builder

value RealLiteral : LiteralDesc "Represents an real number literal."
    properties
        String realLiteral
    licence BSD3Clause
    options
        builder

value StringLiteral : LiteralDesc "Represents a string literal."
    properties
        String stringLiteral
    licence BSD3Clause
    options
        builder

value ComplexLiteral : LiteralDesc "Represents a composite literal."
    properties
        String typeName
        List<LiteralDesc> subvalues
    licence BSD3Clause
    options
        builder
