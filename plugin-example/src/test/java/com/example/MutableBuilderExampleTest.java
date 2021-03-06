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

package com.example;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit tests for {@link MutableBuilderExample}.
 * @author Matt Champion on 11/12/16
 */
public final class MutableBuilderExampleTest {
    @Test
    public void consumerConfiguratorTest() {
        final MutableBuilderExample example = MutableBuilderExample
            .builder()
            .apply(builder -> builder.id(2))
            .build();

        assertEquals(2, (long) example.getId());
        assertEquals("", example.getName());
    }

    @Test
    public void trueBooleanConditionalConsumerConfiguratorTest() {
        final MutableBuilderExample example = MutableBuilderExample
            .builder()
            .ifThen(true, builder -> builder.id(2))
            .build();

        assertEquals(2, (long) example.getId());
        assertEquals("", example.getName());
    }

    @Test
    public void falseBooleanConditionalConsumerConfiguratorTest() {
        final MutableBuilderExample example = MutableBuilderExample
            .builder()
            .ifThen(false, builder -> builder.id(2))
            .build();

        assertEquals(5, (long) example.getId());
        assertEquals("", example.getName());
    }

    @Test
    public void trueSupplierConditionalConsumerConfiguratorTest() {
        final MutableBuilderExample example = MutableBuilderExample
            .builder()
            .ifThen(() -> true, builder -> builder.id(2))
            .build();

        assertEquals(2, (long) example.getId());
        assertEquals("", example.getName());
    }

    @Test
    public void falseSupplierConditionalConsumerConfiguratorTest() {
        final MutableBuilderExample example = MutableBuilderExample
            .builder()
            .ifThen(() -> false, builder -> builder.id(2))
            .build();

        assertEquals(5, (long) example.getId());
        assertEquals("", example.getName());
    }
}
