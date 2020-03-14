# Scala + gRPC + HTTP reverse proxy

## gRPC scala server setup

Under construction...

## HTTP reverse proxy setup

Change directory to src/main/protobuf.

### Install dependencies
go get -u github.com/grpc-ecosystem/grpc-gateway/protoc-gen-grpc-gateway github.com/grpc-ecosystem/grpc-gateway/protoc-gen-swagger github.com/golang/protobuf/protoc-gen-go

### Generate gRPC for go
protoc -I. -I%GOPATH%/src -I%GOPATH%/src/github.com/grpc-ecosystem/grpc-gateway/third_party/googleapis -I%GOPATH%/src/github.com/grpc-ecosystem/grpc-gateway --go_out=plugins=grpc:. hello.proto

### Generate reverse-proxy
protoc -I. -I%GOPATH%/src -I%GOPATH%/src/github.com/grpc-ecosystem/grpc-gateway/third_party/googleapis -I%GOPATH%/src/github.com/grpc-ecosystem/grpc-gateway --grpc-gateway_out=logtostderr=true:. hello.proto

### Generate Swagger
protoc -I. -I%GOPATH%/src -I%GOPATH%/src/github.com/grpc-ecosystem/grpc-gateway/third_party/googleapis -I%GOPATH%/src/github.com/grpc-ecosystem/grpc-gateway --swagger_out=logtostderr=true:. hello.proto

### Move files to subfolder
move /y hello.pb.go ./com_example_protos/hello.pb.go
move /y hello.pb.gw.go ./com_example_protos/hello.pb.gw.go

### Build
go build

### Run
http-proxy.exe

## Access
http://localhost:8080/v1/example/sayhello?name=Cassio
