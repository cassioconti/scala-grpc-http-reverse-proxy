import java.util.logging.Logger

import com.example.protos.hello.GreeterGrpc
import io.grpc.{Server, ServerBuilder}

import scala.concurrent.ExecutionContext

object GrpcServer {
  def main(args: Array[String]): Unit = {
    val server = new GrpcServer(ExecutionContext.global)
    server.start()
    server.blockUntilShutdown()
  }

  private val port = 50051
}

class GrpcServer(executionContext: ExecutionContext) {
  private val logger = Logger.getLogger(this.getClass.getName)
  private var server: Server = null

  private def start(): Unit = {
    val serverImpl = new GreeterImpl
    this.server = ServerBuilder.forPort(GrpcServer.port).addService(GreeterGrpc.bindService(serverImpl, executionContext)).build.start
    this.logger.info("Server started, listening on " + GrpcServer.port)
    sys.addShutdownHook {
      System.err.println("*** shutting down gRPC server since JVM is shutting down")
      this.stop()
      System.err.println("*** server shut down")
    }
  }

  private def stop(): Unit = {
    if (this.server != null) {
      this.server.shutdown()
    }
  }

  private def blockUntilShutdown(): Unit = {
    if (this.server != null) {
      this.server.awaitTermination()
    }
  }
}
