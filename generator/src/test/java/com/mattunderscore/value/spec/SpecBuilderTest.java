package com.mattunderscore.value.spec;

import com.mattunderscore.value.spec.model.PropertySpec;
import com.mattunderscore.value.spec.model.SpecDesc;
import com.mattunderscore.value.spec.model.ValueDesc;
import com.mattunderscore.value.spec.parser.ValueSpecLexer;
import com.mattunderscore.value.spec.parser.ValueSpecParser;
import com.mattunderscore.value.spec.parser.ValueSpecParser.SpecContext;
import com.mattunderscore.value.spec.type.resolver.TypeResolver;
import com.mattunderscore.value.spec.type.resolver.TypeResolverBuilder;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.UnbufferedTokenStream;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

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
        final ValueSpecLexer lexer = new ValueSpecLexer(stream);
        final ValueSpecParser parser = new ValueSpecParser(new UnbufferedTokenStream<CommonToken>(lexer));
        final SpecContext spec = parser.spec();
        final TypeResolver resolver = new TypeResolverBuilder().build(spec);
        final SpecBuilder specBuilder = new SpecBuilder(resolver);

        final SpecDesc specDesc = specBuilder.build(spec);

        assertEquals("com.example", specDesc.getPackageName());
        final List<ValueDesc> values = specDesc.getValues();
        assertEquals(2, values.size());

        final ValueDesc valueDesc0 = values.get(0);
        assertEquals("FirstValue", valueDesc0.getName());
        final List<PropertySpec> properties0 = valueDesc0.getProperties();
        assertEquals(2, properties0.size());

        final PropertySpec propertySpec0 = properties0.get(0);
        assertEquals("num", propertySpec0.getName());
        assertEquals("java.lang.Integer", propertySpec0.getType());
        final PropertySpec propertySpec1 = properties0.get(1);
        assertEquals("str", propertySpec1.getName());
        assertEquals("java.lang.String", propertySpec1.getType());

        final ValueDesc valueDesc1 = values.get(1);
        assertEquals("SecondValue", valueDesc1.getName());
        final List<PropertySpec> properties1 = valueDesc1.getProperties();
        assertEquals(2, properties1.size());

        final PropertySpec propertySpec2 = properties1.get(0);
        assertEquals("num", propertySpec2.getName());
        assertEquals("java.lang.Integer", propertySpec2.getType());
        final PropertySpec propertySpec3 = properties1.get(1);
        assertEquals("dbl", propertySpec3.getName());
        assertEquals("java.lang.Double", propertySpec3.getType());
    }
}
