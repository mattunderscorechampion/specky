
package com.example

value PersonValue {
  immutable builder
  Integer id
  String name
}

value StrangePersonValue {
  builder
  Integer id
  optional String name
}

bean PersonBean {
  Integer id
  String name
}
