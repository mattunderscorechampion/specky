
section "Literal model"

author "Matt Champion"

package com.mattunderscore.specky.literal.model

type LiteralDesc "Description of a literal."

value IntegerLiteral : LiteralDesc "Represents an integer literal."
    properties
        String integerLiteral
    licence BSD3Clause

value RealLiteral : LiteralDesc "Represents an real number literal."
    properties
        String realLiteral
    licence BSD3Clause
    options
        immutable builder

value StringLiteral : LiteralDesc "Represents a string literal."
    properties
        String stringLiteral
    licence BSD3Clause
    options
        immutable builder

value ComplexValue : LiteralDesc "Represents a composite value."
    properties
        String typeName
        List<LiteralDesc> subvalues
    licence BSD3Clause
    options
        immutable builder
