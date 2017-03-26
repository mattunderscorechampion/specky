
section "Literal model"

author "Matt Champion"

package com.mattunderscore.specky.literal.model.test

type LiteralDesc "Description of a literal."

value UnstructuredLiteral : LiteralDesc "Represents an unstructured literal."
    licence BSD3Clause
    properties
        String literal
    options
        builder

value ConstantLiteral : LiteralDesc "Represents a constant or enum literal."
    licence BSD3Clause
    properties
        String typeName
        String constant
    options
        builder

value IntegerLiteral : LiteralDesc "Represents an integer literal."
    licence BSD3Clause
    properties
        String integerLiteral
    options
        builder

value RealLiteral : LiteralDesc "Represents an real number literal."
    licence BSD3Clause
    properties
        String realLiteral
    options
        builder

value StringLiteral : LiteralDesc "Represents a string literal."
    licence BSD3Clause
    properties
        String stringLiteral
    options
        builder

value ComplexLiteral : LiteralDesc "Represents a composite literal."
    licence BSD3Clause
    properties
        String typeName
        List<LiteralDesc> subvalues
    options
        builder

value NullLiteral : LiteralDesc "Represents a null literal."
    licence BSD3Clause
