
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

propertyView
    : OPTIONAL? Identifier Identifier
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

implSpec
    : (VALUE | BEAN ) Identifier (EXTENDS Identifier)? OPEN_BLOCK (property)+ opts? CLOSE_BLOCK
    ;

typeSpec
    : TYPE Identifier OPEN_BLOCK (propertyView)+ CLOSE_BLOCK
    ;

spec
    : r_package (typeSpec | implSpec)+
    ;
