# Scala + gRPC + HTTP reverse proxy

## gRPC scala server setup

- sbt "runMain GrpcServer"
or
- sbt assembly
- java -cp "target/scala-2.12/my-grpc-experiment_2.12-1.0.jar" GrpcServer

## HTTP reverse proxy setup

Change directory to src/main/protobuf.

### Install dependencies
go get -u github.com/grpc-ecosystem/grpc-gateway/protoc-gen-grpc-gateway github.com/grpc-ecosystem/grpc-gateway/protoc-gen-swagger github.com/golang/protobuf/protoc-gen-go

### Generate gRPC for go
protoc -I. -I%GOPATH%/src -I%GOPATH%/src/github.com/grpc-ecosystem/grpc-gateway/third_party/googleapis -I%GOPATH%/src/github.com/grpc-ecosystem/grpc-gateway --go_out=plugins=grpc:. hello.proto

protoc -I. -I$GOPATH/src -I$GOPATH/src/github.com/grpc-ecosystem/grpc-gateway/third_party/googleapis -I$GOPATH/src/github.com/grpc-ecosystem/grpc-gateway --go_out=plugins=grpc:. hello.proto

### Generate reverse-proxy
protoc -I. -I%GOPATH%/src -I%GOPATH%/src/github.com/grpc-ecosystem/grpc-gateway/third_party/googleapis -I%GOPATH%/src/github.com/grpc-ecosystem/grpc-gateway --grpc-gateway_out=logtostderr=true:. hello.proto

protoc -I. -I$GOPATH/src -I$GOPATH/src/github.com/grpc-ecosystem/grpc-gateway/third_party/googleapis -I$GOPATH/src/github.com/grpc-ecosystem/grpc-gateway --grpc-gateway_out=logtostderr=true:. hello.proto

### Generate Swagger
protoc -I. -I%GOPATH%/src -I%GOPATH%/src/github.com/grpc-ecosystem/grpc-gateway/third_party/googleapis -I%GOPATH%/src/github.com/grpc-ecosystem/grpc-gateway --swagger_out=logtostderr=true,disable_default_errors=true:. hello.proto

protoc -I. -I$GOPATH/src -I$GOPATH/src/github.com/grpc-ecosystem/grpc-gateway/third_party/googleapis -I$GOPATH/src/github.com/grpc-ecosystem/grpc-gateway --swagger_out=logtostderr=true,disable_default_errors=true:. hello.proto

### Build
go build

### Run
http-proxy.exe

## Access
http://localhost:8080/v1/example/sayhello?name=Cassio
