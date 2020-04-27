import com.example.protos.hello.{GreeterGrpc, HelloReply, HelloRequest}
import io.opencensus.exporter.trace.stackdriver.{StackdriverTraceConfiguration, StackdriverTraceExporter}
import io.opencensus.trace.Tracing
import io.opencensus.trace.samplers.Samplers

import scala.concurrent.Future
import io.opencensus.exporter.trace.logging.LoggingTraceExporter
import java.util.logging.Logger

class GreeterImpl extends GreeterGrpc.Greeter {
  StackdriverTraceExporter.createAndRegister(StackdriverTraceConfiguration.builder.build)
  LoggingTraceExporter.register
  private val tracer = Tracing.getTracer
  private val logger = Logger.getLogger(this.getClass.getName)

  override def sayHello(req: HelloRequest) = {
    val span = tracer.spanBuilder("sayHello").setSampler(Samplers.alwaysSample()).startSpan()
    val reply = HelloReply(message = "Hello " + req.name)
    span.addAnnotation("created reply message")
    val span2 = tracer.spanBuilderWithExplicitParent("SecondWorkSpan", span).startSpan()
    logger.info("FirstWorkSpan: " + span.getContext.toString)
    logger.info("SecondWorkSpan: " + span2.getContext.toString)
    span2.addAnnotation("printed details to output")
    span2.end()
    span.end()
    Future.successful(reply)
  }
}
