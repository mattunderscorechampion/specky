Specky
======

[![Build Status](https://travis-ci.org/mattunderscorechampion/specky.svg?branch=master)](https://travis-ci.org/mattunderscorechampion/specky)

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

#### Provided licence names

Several licences can be included in the generated source code by name.

BSD 3 clause can be included by the names:

* BSD-3-Clause
* BSD3Clause
* BSD3

BSD 2 clause can be included by the names:

* BSD-2-Clause
* BSD2Clause
* BSD2

MIT can be included by the names:

* MIT

Apache 2 can be included by the names:

* Apache-2
* Apache2

GPL 3 can be included by the names:

* GPL-3
* GPL3

GPL 2 can be included by the names:

* GPL-2
* GPL2
