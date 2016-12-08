package com.mattunderscore.specky.generator;

import com.mattunderscore.specky.model.ImplementationDesc;
import com.mattunderscore.specky.model.PropertyDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.mattunderscore.specky.model.TypeDesc;
import com.mattunderscore.specky.model.ValueDesc;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static java.util.Collections.singletonList;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Unit tests for {@link ImplementationGenerator}.
 *
 * @author Matt Champion on 07/12/2016
 */
public class ImplementationGeneratorTest {
    @Mock
    private TypeInitialiser typeInitialiser;
    @Mock
    private TypeAppender<ImplementationDesc> constructionMethodAppender;
    @Mock
    private TypeAppender<TypeDesc> superTypeAppender;
    @Mock
    private FieldGeneratorForProperty fieldGeneratorForProperty;
    @Mock
    private MethodGeneratorForProperty<ImplementationDesc> methodGeneratorForProperty;
    @Mock
    private MethodGeneratorForType methodGeneratorForType;

    private final SpecDesc specDesc = SpecDesc.builder().build();
    private final PropertyDesc propertyDesc = PropertyDesc.builder().build();
    private final ImplementationDesc implementationDesc = ValueDesc
        .builder()
        .properties(singletonList(propertyDesc))
        .build();
    private final TypeSpec.Builder typeBuilder = TypeSpec.classBuilder("A");
    private final MethodSpec methodBuilder = MethodSpec.methodBuilder("a").build();

    @Before
    public void setUp() {
        initMocks(this);

        when(typeInitialiser.create(isA(SpecDesc.class), isA(ImplementationDesc.class))).thenReturn(typeBuilder);
        when(methodGeneratorForProperty.generate(isA(SpecDesc.class), isA(ImplementationDesc.class), isA(PropertyDesc.class))).thenReturn(methodBuilder);
        when(methodGeneratorForType.generate(isA(SpecDesc.class), isA(ImplementationDesc.class))).thenReturn(methodBuilder);
    }

    @Test
    public void generate() {
        final ImplementationGenerator generator = new ImplementationGenerator(
            typeInitialiser,
            constructionMethodAppender,
            superTypeAppender,
            fieldGeneratorForProperty,
            singletonList(methodGeneratorForProperty),
            singletonList(methodGeneratorForType));

        generator.generate(specDesc, implementationDesc);

        verify(typeInitialiser).create(specDesc, implementationDesc);
        verify(constructionMethodAppender).append(typeBuilder, specDesc, implementationDesc);
        verify(methodGeneratorForProperty).generate(specDesc, implementationDesc, propertyDesc);
        verify(methodGeneratorForType).generate(specDesc, implementationDesc);
    }
}
