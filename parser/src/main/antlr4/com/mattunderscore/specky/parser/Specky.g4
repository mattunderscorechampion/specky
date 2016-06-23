
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
    : OPTIONAL? Identifier Identifier r_default?
    ;

qualifiedName
    : Identifier (PACKAGE_SEPARATOR Identifier)*
    ;

r_package
    : PACKAGE qualifiedName
    ;

opts
    : OPTIONS OPEN_BLOCK construction? CLOSE_BLOCK
    ;

typeSpec
    : (VALUE | BEAN) Identifier OPEN_BLOCK (property)+ opts? CLOSE_BLOCK
    ;

spec
    : r_package typeSpec+
    ;
