
grammar ValueSpec;

VALUE
    : 'value'
    ;

BEAN
    : 'bean'
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

construction
    : CONSTRUCTOR
    | MUTABLE_BUILDER
    | IMMUTABLE_BUILDER
    ;

property
    : OPTIONAL? TypeName PropertyName
    ;

qualifiedName
    : PropertyName ('.' PropertyName)*
    ;

r_package
    : PACKAGE qualifiedName
    ;

typeSpec
    : (VALUE | BEAN) TypeName OPEN_BLOCK construction? (property)+ CLOSE_BLOCK
    ;

spec
    : r_package typeSpec+
    ;
