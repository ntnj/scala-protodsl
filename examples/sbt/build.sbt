scalaVersion := "3.8.4"

resolvers += "jitpack" at "https://jitpack.io"

Compile / PB.targets := Seq(
  scalaprotodsl.gen() -> (Compile / sourceManaged).value,
  PB.gens.java -> (Compile / sourceManaged).value
)
