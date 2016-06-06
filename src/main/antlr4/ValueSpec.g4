grammar ValueSpec;

VALUE
    : 'value'
    ;

OPEN_BLOCK
    : '{'
    ;

CLOSE_BLOCK
    : '}'
    ;

PACKAGE
    : 'package'
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
    : [ \t]+ -> skip
    ;

WS  :  [ \t\r\n\u000C]+ -> skip
    ;

LINE_COMMENT
    :   '//' ~[\r\n]* -> skip
    ;

TypeName
    : UpperCaseLetter LetterOrDigit*
    ;

PropertyName
    :   Letter LetterOrDigit*
    ;

property
    : TypeName PropertyName
    ;

qualifiedName
    : PropertyName ('.' PropertyName)*
    ;

r_package
    : PACKAGE qualifiedName
    ;

spec
    : r_package value+
    ;

value
    : VALUE TypeName OPEN_BLOCK (property)* CLOSE_BLOCK
    ;
