
// Copyright © 2016 Matthew Champion

package com.example

type QuickType {
  int id
  String name
}

bean PersonBean : QuickType {
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
