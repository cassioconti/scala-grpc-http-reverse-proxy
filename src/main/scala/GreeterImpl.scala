import com.example.protos.hello.{GreeterGrpc, HelloReply, HelloRequest}
import io.opencensus.scala.Tracing._

import scala.concurrent.Future

class GreeterImpl extends GreeterGrpc.Greeter {
  override def sayHello(req: HelloRequest) = {
    val span = startSpan("my test trace")
    val reply = HelloReply(message = "Hello " + req.name)
    val childSpan = startSpanWithParent("child span", span)
    Thread.sleep(3000)
    endSpan(childSpan)
    endSpan(span)
    Future.successful(reply)
  }
}