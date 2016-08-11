
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

package com.mattunderscore.specky.model.test

imports
    com.mattunderscore.specky.model.test.ConstructionMethod default ConstructionMethod.CONSTRUCTOR
    com.mattunderscore.specky.constraint.model.test.NFConjoinedDisjointPredicates

value PropertyDesc "Description of a property."
    properties
        String name "Name of the property."
        String type "Type name of the property."
        List<String> typeParameters
        boolean optional "If the property is optional."
        boolean override "If the property is inherited."
        optional String defaultValue "Default value of the property."
        optional NFConjoinedDisjointPredicates constraint "Constraint applied to the property."
        optional String description "Description of the property."
    options
        immutable builder

type TypeDesc "Description of a type."
    properties
        optional String licence "Licence of the type."
        optional String author "Author of the type."
        String packageName "Name of the package the type is in."
        String name "Name of the type."
        List<PropertyDesc> properties "Properties of the type."
        List<String> supertypes "Supertypes of the type."
        optional String description "Description of the type."

value AbstractTypeDesc : TypeDesc "Description of an abstract type."
    options
        immutable builder

type ImplementationDesc : TypeDesc "Description of an implementation."
    properties
        ConstructionMethod constructionMethod "Construction method of the type."

value ValueDesc : ImplementationDesc "Description of a value type."
    options
        immutable builder

value BeanDesc : ImplementationDesc "Description of a bean type."
    options
        immutable builder

value SpecDesc "Description of a specification."
    properties
        List<String> importTypes "Types from outside the specification to import."
        List<ImplementationDesc> types "Types of the specification."
        List<AbstractTypeDesc> views "Views of the specification."
    options
        immutable builder
