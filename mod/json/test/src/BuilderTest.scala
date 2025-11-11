import munit.*
import mcp.Builder
import mcp.json.*

class BuilderTest extends FunSuite:
  case class Hello(a: Int, b: String) derives ReadWriter
  case class Bye(t: List[String]) derives ReadWriter
  case class Touch(grass: Boolean) derives ReadWriter

  val MyType = Builder[Hello]("hello").orElse[Bye]("bye").orElse[Touch]("touch")
  type MyType = MyType.BuilderType

  // test subtyping relationship
  summon[Hello <:< MyType]
  summon[Bye <:< MyType]
  summon[Touch <:< MyType]

  test("Parsing"):
    val jsonHello = """{"a": 5, "b": "yo"}"""
    assertEquals(read[MyType](jsonHello), Hello(5, "yo"))

    val jsonBye = """{"t": ["a"]}"""
    assertEquals(read[MyType](jsonBye), Bye(List("a")))

    val jsonTouch = """{"grass": true}"""
    assertEquals(read[MyType](jsonTouch), Touch(true))
end BuilderTest
