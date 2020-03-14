scalaVersion := "2.13.1"

name := "my-grpc-experiment"
version := "1.0"

PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
)

libraryDependencies ++= Seq(
  "com.github.sebruck" %% "opencensus-scala-core" % "0.7.0",
  "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
  "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
  "io.grpc" % "grpc-netty" % scalapb.compiler.Version.grpcJavaVersion,
  "io.opencensus" % "opencensus-exporter-trace-logging" % "0.23.0",
  "io.opencensus" % "opencensus-exporter-trace-stackdriver" % "0.23.0",
)