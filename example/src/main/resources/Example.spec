
// Copyright © 2016 Matthew Champion

package com.example

value PersonValue {
  Integer id default 5
  String name
  options {
    immutable builder
  }
}

value StrangePersonValue {
  Integer id default 5
  optional String name
  options {
    builder
  }
}

bean PersonBean {
  Integer id
  String name
  options {
    constructor
  }
}

bean StrangePersonBean {
  Integer id default 5
  String name
  options {
    builder
  }
}
