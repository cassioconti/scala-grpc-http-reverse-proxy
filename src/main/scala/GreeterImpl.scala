import com.example.protos.hello.{GreeterGrpc, HelloReply, HelloRequest}
import io.opencensus.exporter.trace.stackdriver.{StackdriverTraceConfiguration, StackdriverTraceExporter}
import io.opencensus.trace.Tracing
import io.opencensus.trace.samplers.Samplers

import scala.concurrent.Future
import io.opencensus.exporter.trace.logging.LoggingTraceExporter
import java.util.logging.Logger
import io.opencensus.trace.SpanContext
import io.opencensus.trace.TraceId
import io.opencensus.trace.SpanId
import io.opencensus.trace.TraceOptions
import scala.util.Random

class GreeterImpl extends GreeterGrpc.Greeter {
  StackdriverTraceExporter.createAndRegister(StackdriverTraceConfiguration.builder.build)
  LoggingTraceExporter.register
  private val tracer = Tracing.getTracer
  private val logger = Logger.getLogger(this.getClass.getName)

  override def sayHello(req: HelloRequest) = {

    val traceId: TraceId = req.traceId match {
      case ""  => TraceId.generateRandomId(Random.self)
      case str => TraceId.fromLowerBase16(str)
    }

    val spanId: SpanId = req.spanId match {
      case ""  => SpanId.generateRandomId(Random.self)
      case str => SpanId.fromLowerBase16(str)
    }

    val remoteContext: SpanContext = SpanContext.create(traceId, spanId, TraceOptions.DEFAULT)
    val span = tracer.spanBuilderWithRemoteParent("[GrpcServer] sayHello", remoteContext).setSampler(Samplers.alwaysSample()).startSpan()
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
