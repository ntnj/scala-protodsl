package example

import example.ExampleProtoExt.*

@main def demo(): Unit =
  val p = person: it ?=>
    it.name = "Ann"
    it.emails += "a@x.com"
    it.emails.clear()
    it.emails ++= java.util.List.of("b@x.com", "c@x.com")
    it.attributes("role") = "admin"
    it.attributes.clear()
    it.attributes("team") = "platform"
  println(p)
