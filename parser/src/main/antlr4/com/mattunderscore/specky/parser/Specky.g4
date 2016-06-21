
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

opts
    : OPTIONS OPEN_BLOCK construction? CLOSE_BLOCK
    ;

typeSpec
    : (VALUE | BEAN) TypeName OPEN_BLOCK (property)+ opts? CLOSE_BLOCK
    ;

spec
    : r_package typeSpec+
    ;
