
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
  int id default 5
  optional String name
  options {
    builder
  }
}
