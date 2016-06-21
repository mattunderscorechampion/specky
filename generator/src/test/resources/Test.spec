
// Copyright © 2016 Matthew Champions

package com.example

value FirstValue {
  Integer num
  String str
}

value SecondValue {
  builder
  Integer num
  Double dbl
}

value ValueWithBooleans {
  immutable builder
  Integer num
  Boolean boolVal
}

bean FirstBean {
  constructor
  Integer num default 5
  String str
}
