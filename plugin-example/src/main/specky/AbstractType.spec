
author "Matt Champion"

package com.example

type PersonType
    licence BSD3Clause
    properties
        int id
        String name "Name of person."


type TypeWithDefault
    licence BSD3Clause
    properties
        int number default 6

type OptionalInt
    licence BSD3Clause
    properties
        optional int opt

type DefaultTest
    licence BSD3Clause
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
