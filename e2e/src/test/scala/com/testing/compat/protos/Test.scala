package com.testing.compat.protos

import munit.Clue.generate
import com.testing.compat.protos.test.*
import com.testing.compat.protos.multiple.*

class GenTest extends munit.FunSuite:
  test("builder") {
    val a = Test.HelloMessage.newBuilder()
    a.f = "hi"
    a.test.b = 34
    a.putIm(2, "hi")
    val p = a.build()
    assertEquals(p.f, "hi")
    assertEquals(p.test.b, 34)
    assertEquals(p.im(2), "hi")
  }

  test("multiple") {
    // val a = testing1 {
    //   aa = 1
    //   println("hi")
    //   bb = "hi"
    // }
    // assertEquals(a.bb, "hi")
  }
