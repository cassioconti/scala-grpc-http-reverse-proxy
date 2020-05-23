import java.util.concurrent.TimeUnit
import java.util.logging.Logger

import com.example.protos.hello.{GreeterGrpc, HelloRequest}
import io.grpc.stub.{AbstractStub, MetadataUtils}
import io.grpc.{ManagedChannelBuilder, Metadata}
import io.opencensus.exporter.trace.stackdriver.{StackdriverTraceConfiguration, StackdriverTraceExporter}
import io.opencensus.trace.samplers.Samplers
import io.opencensus.trace.{Span, SpanContext, TraceId, Tracing}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import io.opencensus.exporter.trace.logging.LoggingTraceExporter
import io.opencensus.trace.TraceOptions
import io.opencensus.trace.SpanId

object GrpcClient {
  implicit val ec = ExecutionContext.global
  StackdriverTraceExporter.createAndRegister(StackdriverTraceConfiguration.builder.build)
  LoggingTraceExporter.register
  private val tracer = Tracing.getTracer
  private val logger = Logger.getLogger(this.getClass.getName)

  def main(args: Array[String]): Unit = {
    val span = tracer.spanBuilder("[GrpcClient] sayHello").setSampler(Samplers.alwaysSample()).startSpan()
    val channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build
    span.addAnnotation("channel created")
    val name = args.headOption.getOrElse("Cassio")
    logger.info("Will try to greet " + name + " ...")
    span.addAnnotation("logged name to greet")
    val request = HelloRequest(name = name, traceId = span.getContext().getTraceId().toLowerBase16(), spanId = span.getContext().getSpanId().toLowerBase16())
    span.addAnnotation("request created")
    val stub = addTracingMetadata(GreeterGrpc.stub(channel), span)
    val f: Future[Unit] = stub
      .sayHello(request)
      .map { response =>
        logger.info("Greeting: " + response.message)
        span.addAnnotation("got successful response")
      }
      .recover {
        case err =>
          logger.warning(s"Failed: $err")
          span.addAnnotation("request failed")
      }

    Await.ready(f, Duration(10, TimeUnit.SECONDS))
    channel.shutdown.awaitTermination(10, TimeUnit.SECONDS)
    span.end()
    Tracing.getExportComponent().shutdown()
  }

  def addTracingMetadata[A <: AbstractStub[A]](stub: A, span: Span): A = {
    val headers = new Metadata
    val traceIdKey = Metadata.Key.of("traceId", Metadata.ASCII_STRING_MARSHALLER)
    headers.put(traceIdKey, span.getContext.getTraceId.toString)
    MetadataUtils.attachHeaders(stub, headers)
  }
}
