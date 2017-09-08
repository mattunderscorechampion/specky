package com.mattunderscore.specky;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

import java.nio.file.Paths;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.mattunderscore.specky.constraint.model.NFConjoinedDisjointPredicates;
import com.mattunderscore.specky.context.file.FileContext;
import com.mattunderscore.specky.error.listeners.SemanticErrorListener;
import com.mattunderscore.specky.error.listeners.SyntaxErrorListener;
import com.mattunderscore.specky.literal.model.ComplexLiteral;
import com.mattunderscore.specky.literal.model.IntegerLiteral;
import com.mattunderscore.specky.literal.model.LiteralDesc;
import com.mattunderscore.specky.literal.model.StringLiteral;
import com.mattunderscore.specky.literal.model.UnstructuredLiteral;
import com.mattunderscore.specky.model.AbstractTypeDesc;
import com.mattunderscore.specky.model.ConstructionMethod;
import com.mattunderscore.specky.model.ImplementationDesc;
import com.mattunderscore.specky.model.PropertyDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.mattunderscore.specky.model.TypeDesc;

/**
 * Unit tests for {@link ModelGenerator}.
 *
 * @author Matt Champion 07/01/2017
 */
public final class DefaultsTest {
    @Mock
    private SemanticErrorListener errorListener;
    @Mock
    private SyntaxErrorListener syntaxErrorListener;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @After
    public void postConditions() {
        verifyNoMoreInteractions(errorListener, syntaxErrorListener);
    }

    @Test
    public void defaults() throws Exception {
        final ANTLRInputStream stream = new ANTLRInputStream(SectionImportValueListenerTest
            .class
            .getClassLoader()
            .getResourceAsStream("defaults.spec"));

        final ModelGenerator modelGenerator = new ModelGenerator(errorListener, syntaxErrorListener);

        final FileContext fileContext = new FileContext();
        fileContext.setFile(Paths.get("."));
        fileContext.setAntlrStream(stream);
        final SpecDesc specDesc = modelGenerator.build(singletonList(fileContext));

        final List<TypeDesc> types = specDesc.getTypes();
        final List<AbstractTypeDesc> abstractTypes = specDesc.getAbstractTypes();
        final List<ImplementationDesc> implementations = specDesc.getImplementations();

        assertEquals(4, implementations.size());

        final ImplementationDesc implementationDesc0 = implementations.get(0);
        assertEquals("ConstructorSimpleDefaults", implementationDesc0.getName());

        final ImplementationDesc implementationDesc2 = implementations.get(2);
        assertEquals("ComplexDefaults", implementationDesc2.getName());

        final PropertyDesc propertyDesc2 = implementationDesc2.getProperties().get(0);
        final LiteralDesc defaultValue2 = propertyDesc2.getDefaultValue();
        assertTrue(defaultValue2 instanceof ComplexLiteral);
        final ComplexLiteral complexLiteral2 = (ComplexLiteral) defaultValue2;
        final List<LiteralDesc> subvalues2 = complexLiteral2.getSubvalues();
        assertEquals(IntegerLiteral.builder().integerLiteral("6").build(), subvalues2.get(0));
        assertEquals(IntegerLiteral.builder().integerLiteral("7").build(), subvalues2.get(1));
        assertEquals(StringLiteral.builder().stringLiteral("Matthew").build(), subvalues2.get(2));
    }
}
