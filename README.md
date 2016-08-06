Specky
======

A generator for value types from a DSL.

####Example Spec

```
author "Matt Champion"

package com.mattunderscore.readme

type Person "Description of a person."
    properties
        String name "Persons name."
        long birthTimestamp "Timestamp of persons birth."

bean PersonBean : Person "Bean implementation of {@link Person}."

value PersonValue : Person "Value implementation of {@link Person}."
    options
        immutable builder
```