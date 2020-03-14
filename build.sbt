scalaVersion := "2.12.8"

name := "my-grpc-experiment"
version := "1.0"

PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
)

PB.protoSources in Compile ++= Seq(
  file(sys.env.get("GOPATH").get + "/src/github.com/grpc-ecosystem/grpc-gateway/third_party/googleapis"),
)

libraryDependencies ++= Seq(
  "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
  "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
  "io.grpc" % "grpc-netty" % scalapb.compiler.Version.grpcJavaVersion,
)