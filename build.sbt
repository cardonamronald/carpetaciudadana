lazy val root = (project in file(".")).settings(
  name := "simple-scala-rest-api",
  version in ThisBuild := sys.env.get("APP_VER").getOrElse("0.0.0"),
  scalaVersion := "2.12.7",
  test in assembly := {}
)

resolvers ++= Seq(
  Resolver.DefaultMavenRepository,
  Resolver.bintrayRepo("lonelyplanet", "maven")
)

libraryDependencies ++= {

  val configVersion           = "1.3.2"
  val akkaVersion             = "2.5.19"
  val akkaHttpVersion         = "10.1.7"
  val akkaStreamVersion       = "2.5.19"
  val scalaJava8CompatVersion = "0.9.0"
  val postgresqlVersion       = "42.2.5"
  val sprayVersion            = "1.3.5"

  val scalaTestVersion = "3.0.5"

  Seq(
    "com.typesafe"           % "config"                % configVersion,
    "com.typesafe.akka"      %% "akka-actor"           % akkaVersion,
    "com.typesafe.akka"      %% "akka-http"            % akkaHttpVersion,
    "com.typesafe.akka"      %% "akka-stream"          % akkaStreamVersion,
    "com.typesafe.akka"      %% "akka-http-spray-json" % akkaHttpVersion,
    "io.spray"               %% "spray-json"           % sprayVersion,
    "org.scala-lang.modules" %% "scala-java8-compat"   % scalaJava8CompatVersion,
    "org.postgresql"         % "postgresql"            % postgresqlVersion,
    "org.scalatest"          % "scalatest_2.12"        % scalaTestVersion % "test"
  )
}

mainClass in (Compile, run) := Some("co.s4n.main.Main")

assemblyJarName in assembly := s"${name.value}_${scalaBinaryVersion.value}-${(version in ThisBuild).value}.jar"

logBuffered in Test := false
