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

lexer grammar SpeckyLexer;

VALUE
    : 'value'
    ;

BEAN
    : 'bean'
    ;

TYPE
    : 'type'
    ;

CONSTRUCTOR
    : 'constructor'
    ;

MUTABLE_BUILDER
    : 'builder'
    ;

IMMUTABLE_BUILDER
    : 'immutable builder'
    ;

OPTIONAL
    : 'optional'
    ;

OPEN_BLOCK
    : '{'
    ;

CLOSE_BLOCK
    : '}'
    ;

OPEN_TYPE_PARAMETERS
    : '<'
    ;

CLOSE_TYPE_PARAMETERS
    : '>'
    ;

PACKAGE
    : 'package'
    ;

PACKAGE_SEPARATOR
    : '.'
    ;

DEFAULT
    : 'default' -> pushMode(LITERAL)
    ;

OPTIONS
    : 'options'
    ;

EXTENDS
    : ':'
    ;

IMPORT
    : 'imports'
    ;

fragment
UpperCaseLetter
    :   [A-Z]
    ;

fragment
Letter
    :   [a-zA-Z$_]
    |   ~[\u0000-\u007F\uD800-\uDBFF]
        {Character.isJavaIdentifierStart(_input.LA(-1))}?
    |   [\uD800-\uDBFF] [\uDC00-\uDFFF]
        {Character.isJavaIdentifierStart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
    ;

fragment
LetterOrDigit
    :   [a-zA-Z0-9$_] // these are the "java letters or digits" below 0x7F
    |   ~[\u0000-\u007F\uD800-\uDBFF]
        {Character.isJavaIdentifierPart(_input.LA(-1))}?
    |   [\uD800-\uDBFF] [\uDC00-\uDFFF]
        {Character.isJavaIdentifierPart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
    ;

INLINE_WS
    : [ \t]+ -> channel(HIDDEN)
    ;

LINE_BREAK
    : [\r\n]+ -> channel(HIDDEN)
    ;

LINE_COMMENT
    :   '//' ~[\r\n]* -> skip
    ;

Identifier
    :   Letter LetterOrDigit*
    ;

mode LITERAL;

INLINE_WS_2
    : [ \t]+ -> skip
    ;

ANYTHING
    : ~[ \t\r\n\u000C]+ -> popMode
    ;
