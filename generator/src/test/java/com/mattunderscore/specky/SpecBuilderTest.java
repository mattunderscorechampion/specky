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
import com.mattunderscore.specky.model.PropertyImplementationDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.mattunderscore.specky.model.TypeDesc;
import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.Specky.SpecContext;
import com.mattunderscore.specky.parser.SpeckyLexer;
import com.mattunderscore.specky.type.resolver.TypeResolver;
import com.mattunderscore.specky.type.resolver.TypeResolverBuilder;
import com.mattunderscore.specky.value.resolver.CompositeValueResolver;
import com.mattunderscore.specky.value.resolver.JavaStandardDefaultValueResolver;
import com.mattunderscore.specky.value.resolver.NullValueResolver;

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
        final SpecBuilder specBuilder = new SpecBuilder(resolver, new CompositeValueResolver()
            .with(new JavaStandardDefaultValueResolver())
            .with(new NullValueResolver()));

        final SpecDesc specDesc = specBuilder.build(spec);

        assertEquals("com.example", specDesc.getPackageName());
        final List<TypeDesc> values = specDesc.getValues();
        assertEquals(4, values.size());

        final TypeDesc valueDesc0 = values.get(0);
        assertEquals("FirstValue", valueDesc0.getName());
        final List<PropertyImplementationDesc> properties0 = valueDesc0.getProperties();
        assertEquals(2, properties0.size());

        final PropertyImplementationDesc propertyDesc0 = properties0.get(0);
        assertEquals("num", propertyDesc0.getName());
        assertEquals("java.lang.Integer", propertyDesc0.getType());
        final PropertyImplementationDesc propertyDesc1 = properties0.get(1);
        assertEquals("str", propertyDesc1.getName());
        assertEquals("java.lang.String", propertyDesc1.getType());

        final TypeDesc valueDesc1 = values.get(1);
        assertEquals("SecondValue", valueDesc1.getName());
        final List<PropertyImplementationDesc> properties1 = valueDesc1.getProperties();
        assertEquals(2, properties1.size());

        final PropertyImplementationDesc propertyDesc2 = properties1.get(0);
        assertEquals("num", propertyDesc2.getName());
        assertEquals("java.lang.Integer", propertyDesc2.getType());
        final PropertyImplementationDesc propertyDesc3 = properties1.get(1);
        assertEquals("dbl", propertyDesc3.getName());
        assertEquals("java.lang.Double", propertyDesc3.getType());

        final TypeDesc valueDesc2 = values.get(2);
        assertEquals("ValueWithBooleans", valueDesc2.getName());
        final List<PropertyImplementationDesc> properties2 = valueDesc2.getProperties();
        assertEquals(2, properties0.size());

        final PropertyImplementationDesc propertyDesc4 = properties2.get(0);
        assertEquals("num", propertyDesc4.getName());
        assertEquals("java.lang.Integer", propertyDesc4.getType());
        final PropertyImplementationDesc propertyDesc5 = properties2.get(1);
        assertEquals("boolVal", propertyDesc5.getName());
        assertEquals("java.lang.Boolean", propertyDesc5.getType());

        final TypeDesc beanSpec0 = values.get(3);
        assertEquals("FirstBean", beanSpec0.getName());
        final List<PropertyImplementationDesc> properties3 = beanSpec0.getProperties();
        assertEquals(2, properties0.size());

        final PropertyImplementationDesc propertyDesc6 = properties3.get(0);
        assertEquals("num", propertyDesc6.getName());
        assertEquals("java.lang.Integer", propertyDesc6.getType());
        assertEquals("5", propertyDesc6.getDefaultValue());
        final PropertyImplementationDesc propertyDesc7 = properties3.get(1);
        assertEquals("str", propertyDesc7.getName());
        assertEquals("java.lang.String", propertyDesc7.getType());
    }
}
