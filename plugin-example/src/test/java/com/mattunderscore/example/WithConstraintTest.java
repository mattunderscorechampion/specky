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

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;

import com.example.ValueWithList;
import com.example.WithConstraint;

/**
 * Unit tests for {@link ValueWithList}.
 * @author Matt Champion on 17/07/16
 */
public final class WithConstraintTest {
    @Test(expected = IllegalArgumentException.class)
    public void testMinConstraintViolation() {
        WithConstraint.builder().minNumber(5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMaxConstraintViolation() {
        WithConstraint.builder().maxNumber(20);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpperBoundViolation() {
        WithConstraint.builder().upperBound(21L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLowerBoundViolation() {
        WithConstraint.builder().lowerBound(19);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRangeLowViolation() {
        WithConstraint.builder().range(new BigInteger("3"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRangeHighViolation() {
        WithConstraint.builder().range(new BigInteger("25"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnnecessaryViolation() {
        WithConstraint.builder().unnecessary(25);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegatedViolation() {
        WithConstraint.builder().negated(15);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSingleViolation() {
        WithConstraint.builder().negated(19);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAnythingElseViolation() {
        WithConstraint.builder().anythingElse(20);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDisjointViolation() {
        WithConstraint.builder().disjoint(25);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMinDoubleConstraintViolation() {
        WithConstraint.builder().minDbl(20);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMaxDobuleConstraintViolation() {
        WithConstraint.builder().maxDbl(20.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpperBoundDoubleViolation() {
        WithConstraint.builder().upperBoundDbl(new BigDecimal("21"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLowerBoundDoubleViolation() {
        WithConstraint.builder().lowerBoundDbl(19);
    }

    @Test
    public void testConstraint() {
        final WithConstraint withConstraint = WithConstraint
            .builder()
            .minNumber(21)
            .maxNumber(5)
            .upperBound(20L)
            .lowerBound(20)
            .range(new BigInteger("15"))
            .unnecessary(15)
            .negated(25)
            .single(20)
            .anythingElse(15)
            .disjoint(20)
            .disjoint(21)
            .disjoint(23)
            .minDbl(21.0)
            .maxDbl(15.0)
            .upperBoundDbl(new BigDecimal("20.0"))
            .lowerBoundDbl(21.0)
            .build();
        assertEquals(21, withConstraint.getMinNumber());
        assertEquals(5, (int) withConstraint.getMaxNumber());
        assertEquals(20, (long) withConstraint.getUpperBound());
        assertEquals(20, withConstraint.getLowerBound());
        assertEquals(new BigInteger("15"), withConstraint.getRange());
        assertEquals(15, withConstraint.getUnnecessary());
        assertEquals(25, withConstraint.getNegated());
        assertEquals(20, withConstraint.getSingle());
        assertEquals(15, withConstraint.getAnythingElse());
        assertEquals(23, withConstraint.getDisjoint());
        assertEquals(21.0, withConstraint.getMinDbl(), 0.01);
        assertEquals(15.0, withConstraint.getMaxDbl(), 0.01);
        assertEquals(new BigDecimal("20.0"), withConstraint.getUpperBoundDbl());
        assertEquals(21.0, withConstraint.getLowerBoundDbl(), 0.01);
    }
}
