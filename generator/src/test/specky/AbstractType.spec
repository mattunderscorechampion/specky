
licence """Copyright © 2016 Matthew Champion
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
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE."""

package com.example

type PersonType
    properties
        int id
        String name "Name of person."


type TypeWithDefault
    properties
        int number default 6

type OptionalInt
    properties
        optional int opt

type DefaultTest
    properties
        int reqInt
        long reqLong
        double reqDouble
        boolean reqBoolean
        Integer reqBoxedInt
        Long reqBoxedLong
        Double reqBoxedDouble
        Boolean reqBoxedBoolean
        String reqString
        BigInteger reqBigInt
        BigDecimal reqBigDecimal
        Object reqObject
        List<String> reqList
        Set<String> reqSet
        optional int optInt
        optional long optLong
        optional double optDouble
        optional boolean optBoolean
        optional Integer optBoxedInt
        optional Long optBoxedLong
        optional Double optBoxedDouble
        optional Boolean optBoxedBoolean
        optional String optString
        optional BigInteger optBigInt
        optional BigDecimal optBigDecimal
        optional Object optObject
        optional List<String> optList
        optional Set<String> optSet
