import java.util.concurrent.TimeUnit
import java.util.logging.Logger

import com.example.protos.hello.{GreeterGrpc, HelloReply, HelloRequest}
import io.grpc.ManagedChannelBuilder

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

object HelloWorldClient {
  implicit val ec = ExecutionContext.global
  private val logger = Logger.getLogger(HelloWorldClient.getClass.getName)

  def main(args: Array[String]): Unit = {
    val channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build
    val name = args.headOption.getOrElse("Cassio")
    logger.info("Will try to greet " + name + " ...")
    val request = HelloRequest(name = name)
    val stub = GreeterGrpc.stub(channel)
    val f: Future[HelloReply] = stub.sayHello(request)
    f.map { response =>
      logger.info("Greeting: " + response.message)
    }.recover { err =>
      logger.warning(s"Failed: $err")
    }

    Await.ready(f, Duration(10, TimeUnit.SECONDS))
    channel.shutdown.awaitTermination(1, TimeUnit.MINUTES)
  }
}
