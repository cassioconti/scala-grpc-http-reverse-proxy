scalaVersion := "2.12.11"

name := "my-grpc-experiment"
version := "1.0"

PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
)

PB.protoSources in Compile ++= Seq(
  file(sys.env.get("GOPATH").get + "/src/github.com/grpc-ecosystem/grpc-gateway/third_party/googleapis"),
  file(sys.env.get("GOPATH").get + "/src/github.com/grpc-ecosystem/grpc-gateway"),
)

libraryDependencies ++= Seq(
  "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
  "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
  "io.grpc" % "grpc-netty" % scalapb.compiler.Version.grpcJavaVersion,
  "io.opencensus" % "opencensus-api" % "0.26.0",
  "io.opencensus" % "opencensus-exporter-trace-logging" % "0.26.0",
  "io.opencensus" % "opencensus-exporter-trace-stackdriver" % "0.26.0",
  "io.opencensus" % "opencensus-impl" % "0.26.0",
)

// No need to run tests while building jar
test in assembly := {}
// Simple and constant jar name
assemblyJarName in assembly := s"app-assembly.jar"
// Merge strategy for assembling conflicts
assemblyMergeStrategy in assembly := {
  case PathList("reference.conf") => MergeStrategy.concat
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case _ => MergeStrategy.first
}
