//> using dep com.lihaoyi::upickle::4.1.0
//> using dep com.lihaoyi::pprint::0.9.0
//> using dep com.indoorvivants::rendition::0.0.4
//> using dep com.lihaoyi::os-lib::0.11.4
//> using scala 3.7.0-RC4

package mcp

import mcp.{*, given}
import upickle.default.*
import upickle.core.TraceVisitor.TraceException
import scala.annotation.targetName
import rendition.*

enum MCPError(msg: String, tr: Throwable = null) extends Throwable(msg, tr):
  case FailureParsing(in: ujson.Value, reason: Throwable = null)
      extends MCPError(s"Failed to parse input '$in' $reason", reason)
  case StubError(msg: String) extends MCPError(msg)
end MCPError

object types:
  val MixedType = Builder[String]("string")
    .orElse[List[String]]("list")
  type MixedType = MixedType.BuilderType

  lazy val DefDef = Builder[AnyOf]("anyof")
    .orElse[EnumDefinition]("definition")
    .orElse[ObjectDefinition]("definition")
    .orElse[ArrayDefinition]("definition")
    .orElse[MixedTypeDefinition]("definition")
    .orElse[Ref]("ref")
  type DefDef = DefDef.BuilderType

  case class Str(
      const: Option[String] = None,
      format: Option[String] = None,
      description: Option[String] = None,
      `enum`: Option[List[String]] = None,
      `type`: "string"
  ) derives Reader
  case class Arr(
      `type`: "array",
      items: Property,
      description: Option[String] = None
  ) derives Reader
  case class Num(
      description: Option[String] = None,
      `type`: "number"
  ) derives Reader
  case class Integer(`type`: "integer") derives Reader
  case class AdditionalProperties(
      `type`: "object",
      additionalProperties: Boolean
  ) derives Reader
  case class Obj(
      `type`: "object",
      properties: Map[String, Property] = Map.empty,
      required: List[String] = List.empty,
      description: Option[String] = None
      // additionalProperties: Option[AdditionalProperties] = None
  ) derives Reader
  case class Bool(`type`: "boolean") derives Reader
  case class Mixed(`type`: List[String]) derives Reader
  case class Data(description: Option[String] = None) derives Reader

  lazy val Property = Builder[Str]("str")
    .orElse[Arr]("arr")
    .orElse[Num]("num")
    .orElse[Ref]("ref")
    .orElse[Integer]("integer")
    .orElse[Obj]("obj")
    .orElse[Bool]("bool")
    .orElse[AnyOf]("anyOf")
    .orElse[Mixed]("mixed")
    .orElse[Data]("data")

  type Property = Property.BuilderType

end types

import types.*

case class EnumDefinition(
    `type`: "string",
    `enum`: Option[List[String]] = None,
    description: Option[String] = None
) derives Reader

case class ObjectDefinition(
    `type`: "object",
    description: Option[String] = None,
    properties: Map[String, Property] = Map.empty,
    required: List[String] = List.empty
) derives Reader

case class MixedTypeDefinition(
    `type`: MixedType,
    description: Option[String] = None
) derives Reader

case class ArrayDefinition(
    `type`: "array",
    description: Option[String] = None,
    items: DefDef
) derives Reader

case class Ref(`$ref`: String, description: Option[String] = None)
    derives Reader

case class AnyOf(anyOf: List[DefDef]) derives Reader

case class Schema(
    definitions: Map[String, DefDef]
) derives Reader

case class RenderingStreams(flush: (String, LineBuilder) => Unit):
  private val streams = collection.mutable.Map.empty[String, LineBuilder]
  def get(name: String) =
    streams.getOrElseUpdate(name, LineBuilder())

  def in(name: String)(f: RenderingContext ?=> Unit) =
    val lb = get(name)
    lb.use(f)
    flush(name, lb)

  def renderMapping() =
    streams.toMap.map((k, v) => k -> v.result)
end RenderingStreams

@main def generator =
  val cont = io.Source.fromFile("./schema.json").getLines.mkString("\n")
  val schema = read[Schema](cont)
  val base = os.pwd / "mcp_generated"
  os.remove.all(base)
  os.makeDir.all(base)
  val streams = RenderingStreams(flush =
    (name, lb) => os.write.over(base / s"$name.scala", lb.result)
  )

  val toRender = Set(
    "AudioContent",
    "Annotations",
    "Role",
    "InitializeRequest",
    "ClientCapabilities",
    "Implementation",
    "InitializeResult",
    "ServerCapabilities",
    "ListToolsRequest",
    "ListToolsResult",
    "ToolAnnotations",
    "Tool"
  )

  def scaladoc(s: Option[String])(using RenderingContext) =
    s.foreach { str =>
      line(s"/** $str */")
    }

  def propType(s: Property): String = s match
    case s: Str =>
      val const = s.const.map(s => s""" "$s"   """.trim)
      const.getOrElse("String")
    case s: Num  => "Double"
    case r: Ref  => s"mcp.${r.`$ref`.stripPrefix("#/definitions/")}"
    case s: Bool => "Boolean"
    case a: Arr => 
      s"Seq[${propType(a.items)}]"
    case o: Obj =>
      if o.properties.isEmpty then "ujson.Value"
      else
        val props = o.properties.toList.sortBy(_._1)
        props
          .map { case (name, prop) =>
            val typeWrap: String => String =
              if o.required.contains(name) then identity
              else (s => s"Option[$s] = None")
            s"${sanitise(name)}: ${propType(prop)}"
          }
          .mkString("(", ", ", ")")

  schema.definitions.filter(k => toRender(k._1)).foreach {
    case (name, defDef) =>
      defDef match
        case defDef: ObjectDefinition =>
          streams.in(name):
            line("package mcp")
            emptyLine()
            line("import upickle.default.*")
            line("import upicklex.namedTuples.Macros.Implicits.given")
            emptyLine()
            scaladoc(defDef.description)
            block(s"case class $name(", ") derives ReadWriter"):
              val sortedProps = defDef.properties.toList.sortBy: (propName, _) =>
                (!defDef.required.contains(propName), propName)
                
              sortedProps.foreach {
                case (name, prop) =>
                  val cleanName = sanitise(name)
                  val typeWrap: String => String =
                    if defDef.required.contains(name) then identity
                    else (s => s"Option[$s] = None")
                  prop match
                    case s: Str =>
                      val const = s.const.map(s => s""" "$s"   """.trim)
                      scaladoc(s.description)
                      line(
                        s"$cleanName: ${typeWrap(propType(s))},"
                      )
                    case s: Num =>
                      scaladoc(s.description)
                      line(s"$cleanName: ${typeWrap(propType(s))},")
                    case r: Ref =>
                      scaladoc(r.description)
                      line(s"$cleanName: ${typeWrap(propType(r))},")
                    case Arr(_, r @ Ref(_, _), description) =>
                      scaladoc(description)
                      line(
                        s"$cleanName: ${typeWrap(s"Seq[${{ propType(r) }}]")},"
                      )

                    case o: Obj =>
                      scaladoc(o.description)
                      line(
                        s"$cleanName: ${typeWrap(propType(o))},"
                      )

                    case other =>
                      line(
                        s"$cleanName: ${typeWrap(propType(other))},"
                      )
                  end match

              }
        case e: EnumDefinition =>
          streams.in(name):
            line("package mcp")
            emptyLine()
            line("import upickle.default.*")
            emptyLine()
            scaladoc(e.description)
            block(s"enum $name:", s"end $name"):
              e.`enum`.toList.flatten.foreach: l =>
                line(s"case $l")
            emptyLine()
            block("object Role:", ""):
              line(
                s"private val mapping = $name.values.map(r => r.toString -> r).toMap"
              )
              block(s"given ReadWriter[$name] = ", ""):
                block("summon[ReadWriter[String]].bimap(", ")"):
                  line("s => s.toString,")
                  line(
                    "r => mapping.getOrElse(r, throw new IllegalArgumentException(s\"Invalid role: $r\"))"
                  )
  }
end generator

def sanitise(name: String) =
  val prohibited =
    Set(
      "type",
      "class",
      "enum",
      "abstract",
      "def",
      "import",
      "export",
      "macro"
    )

  if prohibited(name) then s"`$name`" else name
end sanitise
