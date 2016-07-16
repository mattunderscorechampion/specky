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

package com.mattunderscore.example;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

/**
 * Assertions for class structure.
 * @author Matt Champion on 16/07/16
 */
public final class ReflectionAssertions {
    private ReflectionAssertions() {
    }

    public static <T extends Annotation> void assertHasAnnotation(Class<T> annotationClass, Method method) {
        final T annotation = method.getDeclaredAnnotation(annotationClass);
        if (annotation == null) {
            throw new AssertionError(
                "Expected the annotation " +
                annotationClass.getName() +
                " on method " +
                method.getName());
        }
    }

    public static <T extends Annotation> void assertHasNoAnnotation(Class<T> annotationClass, Method method) {
        final T annotation = method.getDeclaredAnnotation(annotationClass);
        if (annotation != null) {
            throw new AssertionError(
                "Expected the method " +
                method.getName() +
                " not to have the annotation " +
                annotationClass.getName());
        }
    }

    public static <T> void assertHasMethod(Class<?> subjectClass, String methodName, Class<T> returnType, Class<?>... parmeterTypes) {
        try {
            final Method method = subjectClass.getMethod(methodName, parmeterTypes);
            assertHasReturnType(returnType, method);
        } catch (NoSuchMethodException e) {
            throw new AssertionError("Method " + methodName + " not found with requested parameters");
        }
    }

    public static <T> void assertHasReturnType(Class<T> returnType, Method method) {
         assertEquals(returnType, method.getReturnType());
    }

    public static <T> void assertHasParameter(String parameterName, Class<T> parameterType, Method method) {
        final Parameter param = Stream
                .of(method.getParameters())
                .filter(parameter -> parameter.getName().equals(parameterName))
                .findFirst()
                .orElseThrow(() ->
                    new AssertionError(
                        "Parameter " +
                        parameterName +
                        " not found on method "  +
                        method.getName()));
        assertEquals(parameterType, param.getType());
    }

    public static void assertHasNoParameters(Method method) {
        assertEquals(0, method.getParameters().length);
    }
}
