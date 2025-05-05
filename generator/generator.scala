//> using dep com.lihaoyi::upickle::4.1.0
//> using dep com.indoorvivants::rendition::0.0.4
//> using dep com.lihaoyi::os-lib::0.11.4
//> using dep com.lihaoyi::pprint::0.9.0
//> using scala 3.6.4

package mcp

//import mcp.{*, given}, json.*
import json.{ReadWriter, read}
import upickle.core.TraceVisitor.TraceException
import scala.annotation.targetName
import rendition.{
  LineBuilder,
  RenderingContext,
  Rendering,
  block,
  line,
  emptyLine,
  nest
}
import scala.util.Try

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
    lb.use: config ?=>
      f(using
        config.copy(config =
          config.config.copy(indentSize = Rendering.IndentationSize(3))
        )
      )
    flush(name, lb)
  end in

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
    "BlobResourceContents",
    "PingRequest",
    "PingResult",
    "ProgressToken",
    "ListPromptsRequest",
    "ListPromptsResult",
    "Prompt",
    "PromptArgument",
    "GetPromptRequest",
    "GetPromptResult",
    "PromptMessage",
    "ListResourcesRequest",
    "ListResourcesResult",
    "Resource",
    "InitializedNotification",
    "CancelledNotification",
    "RequestId",
    "ProgressNotification",
    "RootsListChangedNotification",
    "ResourceListChangedNotification",
    "ResourceUpdateNotification",
    "ToolListChangedNotification",
    "LoggingMessageNotification",
    "PromptListChangedNotification",
    "LoggingLevel"
  )

  enum Kind:
    case Request(base: String)
    case Notification(base: String)

  enum Marker:
    case Client
    case Server

  def getRefs(o: DefDef): List[String] =
    Try(o.asInstanceOf[AnyOf]).toOption.toList.flatMap: o =>
      o.anyOf.collect { case r: Ref => r.`$ref`.stripPrefix("#/definitions/") }

  val clientNotifications = getRefs(schema.definitions("ClientNotification"))
    .map(s => s.stripSuffix("Notification") -> Marker.Client)
    .toMap

  val serverNotifications = getRefs(schema.definitions("ServerNotification"))
    .map(s => s.stripSuffix("Notification") -> Marker.Server)
    .toMap

  val clientRequests = getRefs(schema.definitions("ClientRequest"))
    .map(s => s.stripSuffix("Request") -> Marker.Client)
    .toMap

  val serverRequests = getRefs(schema.definitions("ServerRequest"))
    .map(s => s.stripSuffix("Request") -> Marker.Server)
    .toMap

  val all = Seq(
    clientNotifications,
    serverNotifications,
    clientRequests,
    serverRequests
  )

  val directions =
    all.toSet
      .flatMap(_.keySet)
      .map: name =>
        name -> all.flatMap(_.get(name))
      .toMap

  println(directions)

  val requestMethods = Map.newBuilder[String, Kind]

  val synthetic = Seq(
    "PingResult" -> ObjectDefinition(`type` = "object")
  )

  val unhandled = schema.definitions.filter(k => !toRender(k._1))

  println("NOT handled:")
  unhandled.foreach { case (name, defDef) =>
    println(s"  $name")
  }

  (schema.definitions.filter(k => toRender(k._1)) ++ synthetic).foreach {
    case (name, defDef) =>
      defDef match
        case defDef: ObjectDefinition =>
          streams.in(name):
            line("package mcp")
            emptyLine()
            line("import mcp.json.*")
            emptyLine()
            scaladoc(defDef.description)

            method(defDef.properties).foreach: meth =>
              if meth.startsWith("notifications/") then
                requestMethods += meth -> Kind.Notification(
                  name.stripSuffix("Notification")
                )
              else
                requestMethods += meth -> Kind.Request(
                  name.stripSuffix("Request").stripSuffix("Result")
                )

            renderObjectLike(name, defDef.properties, defDef.required)

        case e: EnumDefinition =>
          streams.in(name):
            line("package mcp")
            emptyLine()
            line("import mcp.json.*")
            emptyLine()
            scaladoc(e.description)
            block(s"enum $name:", s"end $name"):
              e.`enum`.toList.flatten.foreach: l =>
                line(s"case $l")
            emptyLine()
            block(s"object $name:", ""):
              line(
                s"private val mapping = $name.values.map(r => r.toString -> r).toMap"
              )
              block(s"given ReadWriter[$name] = ", ""):
                block("summon[ReadWriter[String]].bimap(", ")"):
                  line("s => s.toString,")
                  line(
                    "r => mapping.getOrElse(r, throw new IllegalArgumentException(s\"Invalid role: $r\"))"
                  )
        case m: MixedTypeDefinition =>
          streams.in(name):
            line("package mcp")
            emptyLine()
            line("import mcp.json.*")
            emptyLine()
            scaladoc(m.description)
            val actualTypes =
              def resolve(tpeId: String) =
                tpeId match
                  case "string"  => "String"
                  case "integer" => "Int"

              m.`type` match
                case s: String       => List(resolve(s))
                case l: List[String] => l.map(resolve)
            end actualTypes

            actualTypes match
              case h :: rest =>
                block(s"val $name = Builder[$h](\"$h\")", ""):
                  rest.foreach: tpe =>
                    line(s".orElse[$tpe](\"$tpe\")")

                line(s"type $name = $name.BuilderType")
            end match

  }

  streams.in("requests"):
    case class Node(name: String, var next: Seq[Node])

    val mp = collection.mutable.Map.empty[String, Node]
    var tree = Node("", Seq.empty)

    mp("") = tree

    val methodMap = requestMethods.result()

    methodMap.toSeq
      .sortBy(_._1)
      .foreach: (meth, _) =>
        val segs = meth.split("/").toList
        var parent = mp("")
        segs.init.foreach: seg =>
          parent.next.find(_.name == seg) match
            case None =>
              val p = Node(seg, Seq.empty)
              parent.next = parent.next :+ p
              parent = p
            case Some(p) => parent = p

        parent.next = parent.next :+ Node(segs.last, Seq.empty)

    def traitName(m: Marker) = m match
      case Marker.Client => "FromClient"
      case Marker.Server => "FromServer"

    def markerTraits(base: String) =
      val b = directions.get(base).toSeq.flatten.map(traitName).mkString(", ")
      if b.nonEmpty then s", $b" else ""

    def go(current: Node, prev: Seq[String])(using RenderingContext): Unit =
      val fullPath = (current.name +: prev).filter(_.nonEmpty)
      val methName = fullPath.reverse.mkString("/")

      println(s"Rendering $methName")

      if current.name.nonEmpty then
        // it's a scope
        if current.next.nonEmpty then
          block(s"object ${current.name}:", ""):
            current.next.foreach: n =>
              go(n, fullPath)
        else
          methodMap
            .get(methName)
            .foreach: m =>
              m match
                case Kind.Request(base) =>

                  block(
                    s"object ${current.name} extends MCPRequest(\"$methName\")${markerTraits(base)}:",
                    ""
                  ):
                    line(s"type In = ${base}Request")
                    line(s"type Out = ${base}Result")
                case Kind.Notification(base) =>
                  block(
                    s"object ${current.name} extends MCPNotification(\"$methName\")${markerTraits(base)}:",
                    ""
                  ):
                    line(s"type In = ${base}Notification")
      else
        current.next.foreach: n =>
          go(n, fullPath)
      end if

    end go

    line("package mcp")
    emptyLine()

    go(tree, Seq.empty)

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

def renderObjectLike(
    name: String,
    properties: Map[String, Property],
    required: List[String]
)(using RenderingContext): Unit =
  val anonToBuild = List.newBuilder[(String, Obj)]
  val unionsToBuild = List.newBuilder[(String, List[Ref])]

  // line("@upickle.implicits.serializeDefaults(true)")
  block(s"case class $name(", ") derives ReadWriter"):
    val sortedProps = properties.toList.sortBy: (propName, prop) =>
      (
        !required.contains(propName) || (prop
          .isInstanceOf[Str] && prop.asInstanceOf[Str].const.nonEmpty),
        propName
      )

    sortedProps.foreach { case (propName, prop) =>
      val cleanName = sanitise(propName)
      lazy val tpe = propType(prop, required.contains(propName))
      lazy val default = propDefault(
        prop,
        required.contains(propName)
      ).map(" = " + _).getOrElse("")

      val typeWrap: String => String =
        if required.contains(propName) then identity
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
            s"$cleanName: $name.${propName.capitalize},"
          )
          unionsToBuild += propName.capitalize -> refs.collect { case r: Ref =>
            r
          }

        case Arr(_, AnyOf(refs), description)
            if refs.forall(_.isInstanceOf[Ref]) =>
          scaladoc(description)
          line(
            s"$cleanName: ${typeWrap(s"Seq[$name.${propName.capitalize}]")},"
          )
          unionsToBuild += propName.capitalize -> refs.collect { case r: Ref =>
            r
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
              s"$cleanName: ${typeWrap("ujson.Obj")}$default,"
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
        renderObjectLike(name, o.properties, o.required)

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
end renderObjectLike

def scaladoc(s: Option[String])(using RenderingContext) =
  s.foreach { str =>
    line("/**")
    str
      .split("\n")
      .foreach: l =>
        line(" * " + l)
    line(" */")
  }

def propDefault(s: Property, required: Boolean): Option[String] =
  s match
    case s: Str if required =>
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
    case s: Num     => "Double"
    case s: Integer => "Int"
    case r: Ref     => s"mcp.${r.`$ref`.stripPrefix("#/definitions/")}"
    case s: Bool    => "Boolean"
    case a: Arr =>
      s"Seq[${propType(a.items, required = true)}]"
    case AnyOf(cases) =>
      s"Any /*$cases*/"
    case o: Obj if o.properties.isEmpty => "ujson.Obj"
    case d: Data                        => "ujson.Value"

  typeWrap(rawType)
end propType

def method(p: Iterable[(String, Property)]): Option[String] =
  p
    .collectFirst:
      case ("method", s: Str) =>
        s.const
      case _ => None
    .flatten
