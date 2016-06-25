
// Copyright © 2016 Matthew Champion

package com.example

bean PersonBean {
  int id
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
