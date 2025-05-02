//> using dep com.lihaoyi::upickle::4.1.0
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
  ) derives ReadWriter

  case class Arr(
      `type`: "array",
      items: Property,
      description: Option[String] = None
  ) derives ReadWriter

  case class Num(
      description: Option[String] = None,
      `type`: "number"
  ) derives ReadWriter

  case class Integer(`type`: "integer") derives ReadWriter

  case class AdditionalProperties(
      `type`: "object",
      additionalProperties: Boolean
  ) derives ReadWriter

  case class Obj(
      `type`: "object",
      properties: Map[String, Property] = Map.empty,
      required: List[String] = List.empty,
      description: Option[String] = None
  ) derives ReadWriter
  case class Bool(`type`: "boolean") derives ReadWriter
  case class Mixed(`type`: List[String]) derives ReadWriter
  case class Data(description: Option[String] = None) derives ReadWriter

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
) derives ReadWriter

case class ObjectDefinition(
    `type`: "object",
    description: Option[String] = None,
    properties: Map[String, Property] = Map.empty,
    required: List[String] = List.empty
) derives ReadWriter

case class MixedTypeDefinition(
    `type`: MixedType,
    description: Option[String] = None
) derives ReadWriter

case class ArrayDefinition(
    `type`: "array",
    description: Option[String] = None,
    items: DefDef
) derives ReadWriter

case class Ref(`$ref`: String, description: Option[String] = None)
    derives ReadWriter

case class AnyOf(anyOf: List[DefDef]) derives ReadWriter

case class Schema(
    definitions: Map[String, DefDef]
) derives ReadWriter

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
  val base = os.pwd / "protocol" / "generated"
  os.remove.all(base)
  os.makeDir.all(base)
  val streams = RenderingStreams(flush =
    (name, lb) => os.write.over(base / s"$name.scala", lb.result)
  )

  val x = Property.embed(Num(`type` = "number"))

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
    "Tool",
    "CallToolRequest",
    "CallToolResult",
    "TextContent",
    "ImageContent",
    "AudioContent",
    "EmbeddedResource",
    "TextResourceContents",
    "BlobResourceContents"
  )

  def scaladoc(s: Option[String])(using RenderingContext) =
    s.foreach { str =>
      line(s"/** $str */")
    }

  def propDefault(s: Property, required: Boolean): Option[String] =
    s match
      case s: Str =>
        s.const.flatMap: value =>
          if required then Some(s"\"$value\"")
          else Some(s"Some(\"$value\")")
      case _ if !required => Some("None")
      case _              => None
  end propDefault

  def propType(s: Property, required: Boolean): String =
    val typeWrap: String => String =
      if required then identity
      else (s => s"Option[$s]")

    val rawType = s match
      case s: Str =>
        val const = s.const.map(s => s""" "$s"   """.trim)
        const.getOrElse("String")
      case s: Num  => "Double"
      case r: Ref  => s"mcp.${r.`$ref`.stripPrefix("#/definitions/")}"
      case s: Bool => "Boolean"
      case a: Arr =>
        s"Seq[${propType(a.items, required = true)}]"
      case AnyOf(cases) =>
        s"Any /*$cases*/"
      case o: Obj =>
        if o.properties.isEmpty then "ujson.Value"
        else
          val props = o.properties.toList.sortBy(_._1)
          props
            .map { case (name, prop) =>
              s"${sanitise(name)}: ${propType(prop, required = o.required.contains(name))}"
            }
            .mkString("(", ", ", ")")

    typeWrap(rawType)
  end propType

  schema.definitions.filter(k => toRender(k._1)).foreach {
    case (name, defDef) =>
      defDef match
        case defDef: ObjectDefinition =>
          streams.in(name):
            line("package mcp")
            emptyLine()
            line("import upickle.default.*")
            emptyLine()
            scaladoc(defDef.description)

            val anonToBuild = List.newBuilder[(String, Obj)]
            val unionsToBuild = List.newBuilder[(String, List[Ref])]

            line("@upickle.implicits.serializeDefaults(true)")
            block(s"case class $name(", ") derives ReadWriter"):
              val sortedProps = defDef.properties.toList.sortBy:
                (propName, _) => (!defDef.required.contains(propName), propName)

              sortedProps.foreach { case (propName, prop) =>
                val cleanName = sanitise(propName)
                val tpe = propType(prop, defDef.required.contains(propName))
                val default = propDefault(
                  prop,
                  defDef.required.contains(propName)
                ).map(" = " + _).getOrElse("")

                val typeWrap: String => String =
                  if defDef.required.contains(propName) then identity
                  else (s => s"Option[$s]")

                prop match
                  case s: Str =>
                    scaladoc(s.description)
                    line(s"$cleanName: $tpe$default,")

                  case s: Num =>
                    scaladoc(s.description)
                    line(s"$cleanName: $tpe$default,")
                  case r: Ref =>
                    scaladoc(r.description)
                    line(s"$cleanName: $tpe$default,")
                  case AnyOf(refs) if refs.forall(_.isInstanceOf[Ref]) =>
                    line(
                      s"$cleanName: $tpe$default,"
                    )
                    unionsToBuild += propName.capitalize -> refs.collect {
                      case r: Ref => r
                    }

                  case Arr(_, AnyOf(refs), description)
                      if refs.forall(_.isInstanceOf[Ref]) =>
                    scaladoc(description)
                    line(
                      s"$cleanName: ${typeWrap(s"Seq[$name.${propName.capitalize}]")},"
                    )
                    unionsToBuild += propName.capitalize -> refs.collect {
                      case r: Ref => r
                    }

                  case Arr(_, r @ Ref(_, _), description) =>
                    scaladoc(description)
                    line(
                      s"$cleanName: ${typeWrap(s"Seq[${{ propType(r, true) }}]")},"
                    )

                  case o: Obj =>
                    scaladoc(o.description)
                    if o.properties.nonEmpty then
                      line(
                        s"$cleanName: ${typeWrap(s"$name.${propName.capitalize}")}$default,"
                      )
                      anonToBuild += propName.capitalize -> o
                    else
                      line(
                        s"$cleanName: ${typeWrap("ujson.Value")}$default,"
                      )
                    end if

                  case other =>
                    line(
                      s"$cleanName: $tpe$default,"
                    )
                end match

              }

            val anons = anonToBuild.result()
            val unions = unionsToBuild.result()
            if anons.nonEmpty || unions.nonEmpty then
              emptyLine()
              block(s"object $name:", ""):
                anons.foreach: (name, o) =>
                  line("@upickle.implicits.serializeDefaults(true)")
                  block(s"case class $name(", ") derives ReadWriter"):

                    val props = o.properties.toList.sortBy(_._1)
                    props
                      .foreach: (name, prop) =>
                        val tpe = propType(prop, o.required.contains(name))
                        val default =
                          propDefault(prop, o.required.contains(name))
                            .map(" = " + _)
                            .getOrElse("")

                        line(
                          s"${sanitise(name)}: $tpe$default,"
                        )

                if anons.nonEmpty then emptyLine()
                unions.foreach: (name, refs) =>
                  refs.map(_.`$ref`.stripPrefix("#/definitions/")) match
                    case h :: rest =>
                      block(
                        s"val $name = ",
                        ""
                      ):
                        line(s"Builder[mcp.${h}](\"$h\")")
                        nest:
                          rest.foreach: h =>
                            line(s".orElse[mcp.${h}](\"$h\")")

                      line(s"type $name = $name.BuilderType")
                    case _ =>
            end if

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
