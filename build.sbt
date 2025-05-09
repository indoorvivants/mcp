import sbt.VirtualAxis.ScalaVersionAxis

Global / excludeLintKeys += logManager
Global / excludeLintKeys += scalaJSUseMainModuleInitializer
Global / excludeLintKeys += scalaJSLinkerConfig

inThisBuild(
  List(
    organization := "com.indoorvivants",
    organizationName := "Anton Sviridov",
    homepage := Some(
      url("https://github.com/indoorvivants/mcp")
    ),
    startYear := Some(2020),
    licenses := List(
      "Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")
    ),
    developers := List(
      Developer(
        "keynmol",
        "Anton Sviridov",
        "keynmol@gmail.com",
        url("https://blog.indoorvivants.com")
      )
    )
  )
)

val Versions = new {
  val Scala3 = "3.7.0"
  val munit = "1.1.0"
  val upickle = "4.1.0"
  val scalaVersions = Seq(Scala3)
}

lazy val munitSettings = Seq(
  libraryDependencies += {
    "org.scalameta" %%% "munit" % Versions.munit % Test
  }
)

lazy val root = project
  .in(file("."))
  .aggregate(mcpProtocol.projectRefs *)
  .aggregate(docs.projectRefs *)
  .settings(noPublish)

lazy val json = projectMatrix
  .in(file("mod/json"))
  .settings(name := "mcp-json")
  .defaultAxes(defaults *)
  .jvmPlatform(Versions.scalaVersions)
  .jsPlatform(Versions.scalaVersions)
  .nativePlatform(Versions.scalaVersions)
  .settings(
    simpleLayout,
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "upickle" % Versions.upickle
    )
  )

lazy val generator = projectMatrix
  .in(file("mod/generator"))
  .settings(name := "mcp-generator")
  .defaultAxes(defaults *)
  .jvmPlatform(Versions.scalaVersions)
  .dependsOn(json)
  .settings(
    simpleLayout,
    noPublish,
    libraryDependencies ++= Seq(
      "com.indoorvivants" %% "rendition" % "0.0.4",
      "com.lihaoyi" %% "os-lib" % "0.11.4",
      "com.lihaoyi" %% "pprint" % "0.9.0"
    )
  )

lazy val mcpProtocol = projectMatrix
  .in(file("mod/protocol"))
  .defaultAxes(defaults *)
  .settings(
    name := "mcp-protocol"
  )
  .dependsOn(json)
  .settings(munitSettings, simpleLayout)
  .jvmPlatform(Versions.scalaVersions)
  .jsPlatform(Versions.scalaVersions)
  .nativePlatform(Versions.scalaVersions)
  .enablePlugins(BuildInfoPlugin)
  .settings(
    buildInfoPackage := "mcp.internal",
    buildInfoKeys := Seq[BuildInfoKey](
      version,
      scalaVersion,
      scalaBinaryVersion
    ),
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule))
  )

lazy val docs = projectMatrix
  .in(file("mcp-docs"))
  .dependsOn(mcpProtocol)
  .defaultAxes(defaults *)
  .settings(
    mdocVariables := Map(
      "VERSION" -> version.value
    )
  )
  .jvmPlatform(Versions.scalaVersions)
  .enablePlugins(MdocPlugin)
  .settings(noPublish)

val noPublish = Seq(
  publish / skip := true,
  publishLocal / skip := true
)

val defaults =
  Seq(VirtualAxis.scalaABIVersion(Versions.Scala3), VirtualAxis.jvm)

val scalafixRules = Seq(
  "OrganizeImports",
  "DisableSyntax",
  "LeakingImplicitClassVal",
  "NoValInForComprehension"
).mkString(" ")

val CICommands = Seq(
  "clean",
  "scalafixEnable",
  "compile",
  "test",
  "docs/mdoc",
  "scalafmtCheckAll",
  "scalafmtSbtCheck",
  s"scalafix --check $scalafixRules",
  "headerCheck"
).mkString(";")

val PrepareCICommands = Seq(
  "scalafixEnable",
  s"scalafix --rules $scalafixRules",
  "scalafmtAll",
  "scalafmtSbt",
  "headerCreate"
).mkString(";")

addCommandAlias("ci", CICommands)

addCommandAlias("preCI", PrepareCICommands)

val simpleLayout: Seq[Setting[?]] = {
  /*
    Project matrix will override baseDirectory, making it look like this:
    <root>/.sbt/matrix/src

    Which means we can't use it to identify sources layout.

    Instead, we're going to use `scalaSource` and go 3 levels up from it:

    sbt:root> show catsJS/scalaSource
    [info] .../modules/framework/cats/src/main/scala
   */
  val moduleBase =
    Def.setting(
      (Compile / scalaSource).value
        .getParentFile()
        .getParentFile()
        .getParentFile()
    )

  def suffixes(axes: Seq[VirtualAxis]) = axes.collect {
    case VirtualAxis.js =>
      List("", "-js", "-jvm-js", "-js-native")
    case VirtualAxis.jvm =>
      List("", "-jvm", "-jvm-js", "-jvm-native")
    case VirtualAxis.native =>
      List("", "-native", "-jvm-native", "-js-native")
  }.toList

  def sequence[A](ll: List[List[A]]): List[List[A]] =
    ll.foldRight(List(List.empty[A])) { case (listA, listListA) =>
      listA.flatMap(a => listListA.map(a :: _))
    }

  def combos(axes: Seq[VirtualAxis]): List[String] =
    sequence(suffixes(axes)).map(_.mkString("src", "", ""))

  Seq(
    Compile / unmanagedSourceDirectories :=
      combos(virtualAxes.value).map(moduleBase.value / _),
    Test / unmanagedSourceDirectories :=
      combos(virtualAxes.value).map(moduleBase.value / "test" / _),
    Test / unmanagedResourceDirectories := Seq(
      moduleBase.value / "test" / "resources"
    ),
    Test / fork := (virtualAxes.value.contains(VirtualAxis.jvm))
  ) // ++ remoteCacheSettings
}

lazy val generatorJVM = generator.jvm(Versions.Scala3)
lazy val protocolJVM = mcpProtocol.jvm(Versions.Scala3)

val generateProtocol = inputKey[Unit]("")
generateProtocol := Def.inputTaskDyn {
  // val girModule = girModuleName.value
  // val girFiles = (ThisBuild / baseDirectory).value / "gir-files"
  val out =
    (protocolJVM / Compile / sourceDirectory).value / "src" / "generated"

  // val generatedFiles =
  //   (Compile / target).value / "fluent-generator" / "files.txt"

  // val task = InputKey[Unit]("scalafmtOnly")

  Def.sequential(
    Def
      .taskDyn {
        (generatorJVM / Compile / run)
          .toTask(
            s" $out"
          )
      }
      // Def.taskDyn {
      //   val files = IO.readLines(generatedFiles)
      //   (Compile / task).toTask(s" ${files.mkString(" ")}")
      // }
  )

}.evaluated
