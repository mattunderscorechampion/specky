package com.mattunderscore.specky;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.UnbufferedTokenStream;
import org.junit.Test;

import com.mattunderscore.specky.model.PropertySpec;
import com.mattunderscore.specky.model.SpecDesc;
import com.mattunderscore.specky.model.TypeDesc;
import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.Specky.SpecContext;
import com.mattunderscore.specky.parser.SpeckyLexer;
import com.mattunderscore.specky.type.resolver.TypeResolver;
import com.mattunderscore.specky.type.resolver.TypeResolverBuilder;

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
        final TypeResolver resolver = new TypeResolverBuilder().build(spec);
        final SpecBuilder specBuilder = new SpecBuilder(resolver);

        final SpecDesc specDesc = specBuilder.build(spec);

        assertEquals("com.example", specDesc.getPackageName());
        final List<TypeDesc> values = specDesc.getValues();
        assertEquals(3, values.size());

        final TypeDesc valueDesc0 = values.get(0);
        assertEquals("FirstValue", valueDesc0.getName());
        final List<PropertySpec> properties0 = valueDesc0.getProperties();
        assertEquals(2, properties0.size());

        final PropertySpec propertySpec0 = properties0.get(0);
        assertEquals("num", propertySpec0.getName());
        assertEquals("java.lang.Integer", propertySpec0.getType());
        final PropertySpec propertySpec1 = properties0.get(1);
        assertEquals("str", propertySpec1.getName());
        assertEquals("java.lang.String", propertySpec1.getType());

        final TypeDesc valueDesc1 = values.get(1);
        assertEquals("SecondValue", valueDesc1.getName());
        final List<PropertySpec> properties1 = valueDesc1.getProperties();
        assertEquals(2, properties1.size());

        final PropertySpec propertySpec2 = properties1.get(0);
        assertEquals("num", propertySpec2.getName());
        assertEquals("java.lang.Integer", propertySpec2.getType());
        final PropertySpec propertySpec3 = properties1.get(1);
        assertEquals("dbl", propertySpec3.getName());
        assertEquals("java.lang.Double", propertySpec3.getType());

        final TypeDesc beanSpec0 = values.get(2);
        assertEquals("FirstBean", beanSpec0.getName());
        final List<PropertySpec> properties2 = beanSpec0.getProperties();
        assertEquals(2, properties0.size());

        final PropertySpec propertySpec4 = properties2.get(0);
        assertEquals("num", propertySpec4.getName());
        assertEquals("java.lang.Integer", propertySpec4.getType());
        final PropertySpec propertySpec5 = properties2.get(1);
        assertEquals("str", propertySpec5.getName());
        assertEquals("java.lang.String", propertySpec5.getType());
    }
}
