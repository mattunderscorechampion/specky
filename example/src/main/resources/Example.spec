
package com.example

value PersonValue {
  immutable builder
  Integer id default 5
  String name
}

value StrangePersonValue {
  builder
  Integer id default 5
  optional String name
}

bean PersonBean {
  Integer id
  String name
}
