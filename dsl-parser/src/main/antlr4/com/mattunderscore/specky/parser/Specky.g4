/* Copyright © 2016, 2017 Matthew Champion
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

parser grammar Specky;

options { tokenVocab=SpeckyLexer; }

string_value
    :   (STRING_LITERAL | MULTILINE_STRING_LITERAL)
    ;

construction
    :   CONSTRUCTOR
    |   MUTABLE_BUILDER
    |   IMMUTABLE_BUILDER
    |   FROM_DEFAULTS
    ;

default_value
    :   DEFAULT LITERAL_INLINE_WS ANYTHING
    |   default_value_expression
    ;

value_expression
    :   VALUE_IDENTIFIER VALUE_OPEN_PARAMETER (VALUE_IDENTIFIER VALUE_INLINE_WS value_expression (VALUE_PARAMETER_SEPARATOR VALUE_INLINE_WS VALUE_IDENTIFIER VALUE_INLINE_WS value_expression)*)?  VALUE_CLOSE_PARAMETER
    |   VALUE_IDENTIFIER VALUE_OPEN_PARAMETER (value_expression (VALUE_PARAMETER_SEPARATOR VALUE_INLINE_WS value_expression)*)?  VALUE_CLOSE_PARAMETER
    |   VALUE_IDENTIFIER VALUE_MEMBER_ACCESSOR VALUE_IDENTIFIER
    |   VALUE_INTEGER_LITERAL
    |   VALUE_REAL_LITERAL
    |   STRING_LITERAL
    ;

default_value_expression
    : DEFAULT_EXPRESSION VALUE_INLINE_WS value_expression VALUE_END
    ;

typeParameters
    :   OPEN_TYPE_PARAMETERS Identifier (INLINE_WS Identifier)* CLOSE_TYPE_PARAMETERS
    ;

propertyName
    :   Identifier
    |   VALUE
    |   BEAN
    |   TYPE
    |   CONSTRUCTOR
    |   MUTABLE_BUILDER
    |   OPTIONAL
    |   PROPERTIES
    |   IMPORT
    |   OPTIONS
    |   AUTHOR
    |   LICENCE
    |   SECTION
    |   NOTE
    ;

constraint_operator
    :   GREATER_THAN
    |   LESS_THAN
    |   GREATER_THAN_OR_EQUAL
    |   LESS_THAN_OR_EQUAL
    |   EQUAL_TO
    ;

constraint_literal
    :   REAL_LITERAL
    |   INTEGER_LITERAL
    |   STRING_LITERAL
    ;

constraint_predicate
    :   constraint_operator CONSTRAINT_INLINE_WS constraint_literal
    ;

constraint_proposition
    :   constraint_predicate
    |   NEGATION CONSTRAINT_INLINE_WS constraint_proposition
    |   SIZE_OF CONSTRAINT_INLINE_WS constraint_proposition
    |   HAS_SOME CONSTRAINT_INLINE_WS constraint_proposition
    |   PROPERTY CONSTRAINT_INLINE_WS CONSTRAINT_IDENTIFIER CONSTRAINT_INLINE_WS constraint_proposition
    ;

constraint_expression
    : constraint_proposition
    | constraint_proposition (CONSTRAINT_INLINE_WS DISJUNCTION CONSTRAINT_INLINE_WS constraint_proposition)+
    | constraint_proposition (CONSTRAINT_INLINE_WS CONJUNCTION CONSTRAINT_INLINE_WS constraint_proposition)+
    | OPEN_PARENTHESIS CONSTRAINT_INLINE_WS? constraint_expression CONSTRAINT_INLINE_WS? CLOSE_PARENTHESIS
    ;

constraint_statement
    :   CONSTRAINT_EXPRESSION CONSTRAINT_INLINE_WS constraint_expression CONSTRAINT_END
    ;

property
    :   (OPTIONAL INLINE_WS)? Identifier typeParameters? INLINE_WS propertyName (INLINE_WS default_value)? (INLINE_WS constraint_statement)? (INLINE_WS STRING_LITERAL)?
    ;

qualifiedName
    :   QUALIFIED_NAME
    ;

package_name
    :   PACKAGE INLINE_WS qualifiedName
    ;

singleImport
    :   qualifiedName (INLINE_WS default_value)?
    ;

imports
    :   IMPORT LINE_BREAK
        (INLINE_WS? singleImport LINE_BREAK)+
    ;

props
    :   PROPERTIES LINE_BREAK
        (INLINE_WS? property LINE_BREAK)+
    ;

opts
    :   OPTIONS LINE_BREAK
        (INLINE_WS? construction LINE_BREAK)?
        (INLINE_WS? WITH_MODIFICATION LINE_BREAK)?
    ;

supertypes
    :   EXTENDS (INLINE_WS Identifier)+
    ;

licence
    :   LICENCE (INLINE_WS Identifier | INLINE_WS string_value) LINE_BREAK
    ;

implementationSpec
    :   (VALUE | BEAN) INLINE_WS Identifier (INLINE_WS supertypes)? (INLINE_WS STRING_LITERAL)? LINE_BREAK
        (INLINE_WS? licence)?
        (INLINE_WS? props)?
        (INLINE_WS? opts)?
    ;

typeSpec
    :   TYPE INLINE_WS Identifier (INLINE_WS supertypes)? (INLINE_WS STRING_LITERAL)? LINE_BREAK
        (INLINE_WS? licence)?
        (INLINE_WS? props)?
    ;

author
    :   AUTHOR INLINE_WS string_value
    ;

copyrightHolder
    :   COPYRIGHT_HOLDER INLINE_WS string_value
    ;

licenceDeclaration
    :   LICENCE (INLINE_WS Identifier)? INLINE_WS string_value
    ;

note
    :   NOTE INLINE_WS string_value LINE_BREAK+
    ;

sectionContent
    :   note?
        (licenceDeclaration LINE_BREAK+)*
        note?
        (author LINE_BREAK+)?
        note?
        package_name LINE_BREAK+
        note?
        (imports LINE_BREAK*)?
        note?
        ((typeSpec | implementationSpec) LINE_BREAK*
            note?)+
    ;

sectionDeclaration
    : SECTION (INLINE_WS string_value)? LINE_BREAK+ sectionContent
    ;

defaultSectionDeclaration
    : (copyrightHolder LINE_BREAK+)? sectionContent
    ;

spec
    :   LINE_BREAK* defaultSectionDeclaration? (sectionDeclaration)*
    ;
