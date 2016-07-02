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
import com.mattunderscore.specky.model.PropertyDesc;
import com.mattunderscore.specky.model.PropertyImplementationDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.mattunderscore.specky.model.TypeDesc;
import com.mattunderscore.specky.model.ViewDesc;
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
        final TypeResolver resolver = new TypeResolverBuilder().addSpecContext(spec).build();
        final SpecBuilder specBuilder = new SpecBuilder(resolver, new CompositeValueResolver()
            .with(new JavaStandardDefaultValueResolver())
            .with(new NullValueResolver()));

        final SpecDesc specDesc = specBuilder.build(spec);

        assertEquals("com.example", specDesc.getPackageName());
        final List<ViewDesc> views = specDesc.getViews();
        final List<TypeDesc> values = specDesc.getValues();
        assertEquals(1, views.size());
        assertEquals(4, values.size());

        final ViewDesc typeDesc0 = views.get(0);
        assertEquals("TestType", typeDesc0.getName());
        final List<? extends PropertyDesc> properties0 = typeDesc0.getProperties();
        assertEquals(1, properties0.size());

        final PropertyDesc propertyDesc0 = properties0.get(0);
        assertEquals("num", propertyDesc0.getName());
        assertEquals("java.lang.Integer", propertyDesc0.getType());

        final TypeDesc valueDesc0 = values.get(0);
        assertEquals("FirstValue", valueDesc0.getName());
        final List<PropertyImplementationDesc> properties1 = valueDesc0.getProperties();
        assertEquals(2, properties1.size());
        final List<String> extend = valueDesc0.getExtend();
        assertEquals(1, extend.size());
        assertEquals("com.example.TestType", extend.get(0));

        final PropertyImplementationDesc propertyDesc1 = properties1.get(0);
        assertEquals("num", propertyDesc1.getName());
        assertEquals("java.lang.Integer", propertyDesc1.getType());
        final PropertyImplementationDesc propertyDesc2 = properties1.get(1);
        assertEquals("str", propertyDesc2.getName());
        assertEquals("java.lang.String", propertyDesc2.getType());

        final TypeDesc valueDesc1 = values.get(1);
        assertEquals("SecondValue", valueDesc1.getName());
        final List<PropertyImplementationDesc> properties2 = valueDesc1.getProperties();
        assertEquals(2, properties2.size());

        final PropertyImplementationDesc propertyDesc3 = properties2.get(0);
        assertEquals("num", propertyDesc3.getName());
        assertEquals("java.lang.Integer", propertyDesc3.getType());
        final PropertyImplementationDesc propertyDesc4 = properties2.get(1);
        assertEquals("dbl", propertyDesc4.getName());
        assertEquals("java.lang.Double", propertyDesc4.getType());

        final TypeDesc valueDesc2 = values.get(2);
        assertEquals("ValueWithBooleans", valueDesc2.getName());
        final List<PropertyImplementationDesc> properties3 = valueDesc2.getProperties();
        assertEquals(2, properties3.size());

        final PropertyImplementationDesc propertyDesc5 = properties3.get(0);
        assertEquals("num", propertyDesc5.getName());
        assertEquals("java.lang.Integer", propertyDesc5.getType());
        final PropertyImplementationDesc propertyDesc6 = properties3.get(1);
        assertEquals("boolVal", propertyDesc6.getName());
        assertEquals("java.lang.Boolean", propertyDesc6.getType());

        final TypeDesc beanSpec0 = values.get(3);
        assertEquals("FirstBean", beanSpec0.getName());
        final List<PropertyImplementationDesc> properties4 = beanSpec0.getProperties();
        assertEquals(2, properties4.size());

        final PropertyImplementationDesc propertyDesc7 = properties4.get(0);
        assertEquals("num", propertyDesc7.getName());
        assertEquals("java.lang.Integer", propertyDesc7.getType());
        assertEquals("5", propertyDesc7.getDefaultValue());
        final PropertyImplementationDesc propertyDesc8 = properties4.get(1);
        assertEquals("str", propertyDesc8.getName());
        assertEquals("java.lang.String", propertyDesc8.getType());
    }
}
