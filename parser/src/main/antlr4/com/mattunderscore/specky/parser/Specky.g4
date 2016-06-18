
parser grammar Specky;

options { tokenVocab=SpeckyLexer; }

construction
    : CONSTRUCTOR
    | MUTABLE_BUILDER
    | IMMUTABLE_BUILDER
    ;

r_default
    : DEFAULT ANYTHING
    ;

property
    : OPTIONAL? TypeName PropertyName r_default?
    ;

qualifiedName
    : PropertyName (PACKAGE_SEPARATOR PropertyName)*
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
