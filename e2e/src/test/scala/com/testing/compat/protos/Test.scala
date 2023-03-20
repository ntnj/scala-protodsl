package com.testing.compat.protos

import munit.Clue.generate
import com.testing.compat.protos.TestProtoExt.*
import com.testing.compat.protos.MultipleProtoExt.*

class GenTest extends munit.FunSuite:
  test("builder"):
    val a = HelloMessage.newBuilder()
    a.f = "hi"
    a.test.b = 34
    a.putIm(2, "hi")
    val p = a.build()
    assertEquals(p.f, "hi")
    assertEquals(p.test.b, 34)
    assertEquals(p.im(2), "hi")

  test("multiple"):
    val a = testing1:
      aa = 1
      bb = "hi"
    val b = fMessage: it ?=>
      it.f = "hello"
    assertEquals(a.bb, "hi")
    assertEquals(b.f, "hello")

  test("repeated"):
    val b = Testing1.newBuilder
    b.bs += "hi"
    b.bs ++= List("a", "b")
    b.bs ++= java.util.List.of("c")
    assertEquals(b.bs.size, 4)
    assertEquals(b.bs(0), "hi")
    b.bs(1) = "AA"
    assertEquals(b.bs(1), "AA")
    b.bs.clear()
    assertEquals(b.bs.size, 0)
    b.as += 1
    assertEquals(b.as.size, 1)
    assertEquals(b.as(0), 1)

  test("map"):
    val b = Testing1.newBuilder
    b.mis(1L) = "a"
    b.mis(2L) = "b"
    b.mis ++= java.util.Map.of(3L, "c")
    assertEquals(b.mis.size, 3)
    assertEquals(b.mis(1L), "a")
    assert(b.mis.contains(2L))
    b.mis -= 1L
    assert(!b.mis.contains(1L))
    b.mis.clear()
    assertEquals(b.mis.size, 0)
