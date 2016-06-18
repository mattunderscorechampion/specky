
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

bean FirstBean {
  constructor
  Integer num default 5
  String str
}
