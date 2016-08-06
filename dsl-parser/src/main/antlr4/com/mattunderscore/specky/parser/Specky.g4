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

parser grammar Specky;

options { tokenVocab=SpeckyLexer; }

construction
    :   CONSTRUCTOR
    |   MUTABLE_BUILDER
    |   IMMUTABLE_BUILDER
    ;

default_value
    :    DEFAULT LITERAL_INLINE_WS ANYTHING
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

constraint_expression
    :   constraint_predicate
    |   NEGATION CONSTRAINT_INLINE_WS constraint_expression
    ;

constraint_disjunctions_expression
    :   constraint_expression
    |   OPEN_PARENTHESIS CONSTRAINT_INLINE_WS? constraint_expression (CONSTRAINT_INLINE_WS DISJUNCTION CONSTRAINT_INLINE_WS constraint_expression)* CONSTRAINT_INLINE_WS? CLOSE_PARENTHESIS
    ;

constraint_conjunctions_expression
    :   constraint_disjunctions_expression (CONSTRAINT_INLINE_WS CONJUNCTION CONSTRAINT_INLINE_WS constraint_disjunctions_expression)*
    ;

constraint_statement
    :   CONSTRAINT_EXPRESSION CONSTRAINT_INLINE_WS constraint_conjunctions_expression CONSTRAINT_END
    ;

property
    :   (OPTIONAL INLINE_WS)? Identifier typeParameters? INLINE_WS propertyName (INLINE_WS default_value)? (INLINE_WS constraint_statement)? (INLINE_WS StringLiteral)?
    ;

qualifiedName
    :   Identifier (PACKAGE_SEPARATOR Identifier)*
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
        INLINE_WS? construction? LINE_BREAK
    ;

implementationSpec
    :   (VALUE | BEAN ) INLINE_WS Identifier (INLINE_WS EXTENDS INLINE_WS Identifier)? (INLINE_WS StringLiteral)? LINE_BREAK
        (INLINE_WS? props)?
        (INLINE_WS? opts)?
    ;

typeSpec
    :   TYPE INLINE_WS Identifier (INLINE_WS StringLiteral)? LINE_BREAK
        (INLINE_WS? props)?
    ;

author
    :   AUTHOR INLINE_WS StringLiteral
    ;

spec
    :   LINE_BREAK*
        (author LINE_BREAK+)?
        package_name LINE_BREAK+
        (imports LINE_BREAK*)?
        ((typeSpec | implementationSpec) LINE_BREAK*)+
    ;
