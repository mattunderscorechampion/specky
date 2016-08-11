
licence """Copyright © 2016 Matthew Champion
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
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE."""

author "Matt Champion"

package com.mattunderscore.specky.dsl.model

imports
    com.mattunderscore.specky.model.ConstructionMethod default ConstructionMethod.CONSTRUCTOR
    com.mattunderscore.specky.constraint.model.NFConjoinedDisjointPredicates

value DSLPropertyDesc "Description of a property."
    properties
        String name "Name of the property."
        String type "Type name of the property."
        List<String> typeParameters
        boolean optional "If the property is optional."
        optional String defaultValue "Default value of the property."
        optional NFConjoinedDisjointPredicates constraint "Constraint applied to the property."
        optional String description "Description of the property."
    options
        immutable builder

type DSLTypeDesc "Description of a type."
    properties
        String name "Name of the type."
        List<DSLPropertyDesc> properties "Properties of the type."
        List<String> supertypes "Supertypes of the type."
        optional String description "Description of the type."

value DSLAbstractTypeDesc : DSLTypeDesc "Description of an abstract type."
    options
        immutable builder

type DSLImplementationDesc : DSLTypeDesc "Description of an implementation."
    properties
        ConstructionMethod constructionMethod "Construction method of the type."

value DSLValueDesc : DSLImplementationDesc "Description of a value type."
    options
        immutable builder

value DSLBeanDesc : DSLImplementationDesc "Description of a bean type."
    options
        immutable builder

value DSLSpecDesc "Description of a specification."
    properties
        optional String author "Author of the specification."
        optional String licence "Copyright and licence information."
        String packageName "Name of the package the specification describes."
        List<DSLImportDesc> importTypes "Types from outside the specification to import."
        List<DSLImplementationDesc> types "Types of the specification."
        List<DSLAbstractTypeDesc> views "Views of the specification."
    options
        immutable builder

value DSLImportDesc "Description of the import of a type from outside the specification."
    properties
        String typeName "The name of the type."
        optional String defaultValue "The default value of the type."
    options
        immutable builder
