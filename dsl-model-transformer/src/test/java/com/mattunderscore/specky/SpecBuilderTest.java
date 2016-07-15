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

package com.mattunderscore.specky;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.UnbufferedTokenStream;
import org.junit.Test;

import com.mattunderscore.specky.dsl.SpecBuilder;
import com.mattunderscore.specky.dsl.model.DSLPropertyDesc;
import com.mattunderscore.specky.dsl.model.DSLSpecDesc;
import com.mattunderscore.specky.dsl.model.DSLTypeDesc;
import com.mattunderscore.specky.dsl.model.DSLViewDesc;
import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.Specky.SpecContext;
import com.mattunderscore.specky.parser.SpeckyLexer;

/**
 * Tests for {@link SpecBuilder}.
 * @author Matt Champion on 06/06/16
 */
public final class SpecBuilderTest {

    @Test
    public void test() throws IOException {
        final CharStream stream = new ANTLRInputStream(SpecBuilderTest
                .class
                .getClassLoader()
                .getResourceAsStream("Test.spec"));
        final SpeckyLexer lexer = new SpeckyLexer(stream);
        final Specky parser = new Specky(new UnbufferedTokenStream<CommonToken>(lexer));
        final SpecContext spec = parser.spec();
        final SpecBuilder specBuilder = new SpecBuilder();

        final DSLSpecDesc specDesc = specBuilder.build(spec);

        assertEquals("com.example", specDesc.getPackageName());
        final List<DSLViewDesc> views = specDesc.getViews();
        final List<DSLTypeDesc> values = specDesc.getValues();
        assertEquals(1, views.size());
        assertEquals(4, values.size());

        final DSLViewDesc typeDesc0 = views.get(0);
        assertEquals("TestType", typeDesc0.getName());
        final List<DSLPropertyDesc> properties0 = typeDesc0.getProperties();
        assertEquals(1, properties0.size());

        final DSLPropertyDesc propertyDesc0 = properties0.get(0);
        assertEquals("num", propertyDesc0.getName());
        assertEquals("Integer", propertyDesc0.getType());

        final DSLTypeDesc valueDesc0 = values.get(0);
        assertEquals("FirstValue", valueDesc0.getName());
        final List<DSLPropertyDesc> properties1 = valueDesc0.getProperties();
        assertEquals(2, properties1.size());
        final List<String> extend = valueDesc0.getSupertypes();
        assertEquals(1, extend.size());
        assertEquals("TestType", extend.get(0));

        final DSLPropertyDesc propertyDesc1 = properties1.get(0);
        assertEquals("num", propertyDesc1.getName());
        assertEquals("Integer", propertyDesc1.getType());
        final DSLPropertyDesc propertyDesc2 = properties1.get(1);
        assertEquals("str", propertyDesc2.getName());
        assertEquals("String", propertyDesc2.getType());

        final DSLTypeDesc valueDesc1 = values.get(1);
        assertEquals("SecondValue", valueDesc1.getName());
        final List<DSLPropertyDesc> properties2 = valueDesc1.getProperties();
        assertEquals(2, properties2.size());

        final DSLPropertyDesc propertyDesc3 = properties2.get(0);
        assertEquals("num", propertyDesc3.getName());
        assertEquals("Integer", propertyDesc3.getType());
        final DSLPropertyDesc propertyDesc4 = properties2.get(1);
        assertEquals("dbl", propertyDesc4.getName());
        assertEquals("Double", propertyDesc4.getType());

        final DSLTypeDesc valueDesc2 = values.get(2);
        assertEquals("ValueWithBooleans", valueDesc2.getName());
        final List<DSLPropertyDesc> properties3 = valueDesc2.getProperties();
        assertEquals(2, properties3.size());

        final DSLPropertyDesc propertyDesc5 = properties3.get(0);
        assertEquals("num", propertyDesc5.getName());
        assertEquals("Integer", propertyDesc5.getType());
        final DSLPropertyDesc propertyDesc6 = properties3.get(1);
        assertEquals("boolVal", propertyDesc6.getName());
        assertEquals("Boolean", propertyDesc6.getType());

        final DSLTypeDesc beanSpec0 = values.get(3);
        assertEquals("FirstBean", beanSpec0.getName());
        final List<DSLPropertyDesc> properties4 = beanSpec0.getProperties();
        assertEquals(2, properties4.size());

        final DSLPropertyDesc propertyDesc7 = properties4.get(0);
        assertEquals("num", propertyDesc7.getName());
        assertEquals("Integer", propertyDesc7.getType());
        assertEquals("5", propertyDesc7.getDefaultValue());
        final DSLPropertyDesc propertyDesc8 = properties4.get(1);
        assertEquals("str", propertyDesc8.getName());
        assertEquals("String", propertyDesc8.getType());
    }
}
