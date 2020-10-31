lazy val root = (project in file(".")).settings(
  name := "carpeta-ciudadana",
  version in ThisBuild := sys.env.get("APP_VER").getOrElse("1.0.0"),
  scalaVersion := "2.12.7",
  test in assembly := {}
)

resolvers ++= Seq(
  Resolver.DefaultMavenRepository,
  Resolver.bintrayRepo("lonelyplanet", "maven")
)

libraryDependencies ++= {

  val configVersion           = "1.3.2"
  val akkaVersion             = "2.6.10"
  val akkaHttpVersion         = "10.2.1"
  val akkaStreamVersion       = "2.6.10"
  val scalaJava8CompatVersion = "0.9.0"
  val postgresqlVersion       = "42.2.5"
  val sprayVersion            = "1.3.5"
  val azureStorageVersion     = "12.8.0"
  val apacheCommons           = "2.8.0"
  val slf4jVersion            = "1.7.30"
  val logbackVersion          = "1.2.3"

  val scalaTestVersion = "3.0.5"

  Seq(
    "commons-io"             % "commons-io"            % apacheCommons,
    "com.azure"              % "azure-storage-blob"    % azureStorageVersion,
    "com.typesafe"           % "config"                % configVersion,
    "com.typesafe.akka"      %% "akka-actor"           % akkaVersion,
    "com.typesafe.akka"      %% "akka-http"            % akkaHttpVersion,
    "com.typesafe.akka"      %% "akka-stream"          % akkaStreamVersion,
    "com.typesafe.akka"      %% "akka-http-spray-json" % akkaHttpVersion,
    "io.spray"               %% "spray-json"           % sprayVersion,
    "org.scala-lang.modules" %% "scala-java8-compat"   % scalaJava8CompatVersion,
    "org.postgresql"         % "postgresql"            % postgresqlVersion,
    "org.scalatest"          % "scalatest_2.12"        % scalaTestVersion % "test",
    "ch.qos.logback"         % "logback-classic"       % logbackVersion,
    "org.slf4j"              % "slf4j-api"             % slf4jVersion
  )
}

mainClass in (Compile, run) := Some("co.edu.eafit.dis.st1607.carpetaciudadana.main.Main")

assemblyJarName in assembly := s"${name.value}_${scalaBinaryVersion.value}-${(version in ThisBuild).value}.jar"

logBuffered in Test := false
