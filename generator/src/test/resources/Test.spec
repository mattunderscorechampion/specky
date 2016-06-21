
// Copyright © 2016 Matthew Champions

package com.example

value FirstValue {
  Integer num
  String str
}

value SecondValue {
  Integer num
  Double dbl
  options {
      builder
    }
}

value ValueWithBooleans {
  Integer num
  Boolean boolVal
  options {
    immutable builder
  }
}

bean FirstBean {
  Integer num default 5
  String str
  options {
    constructor
  }
}
