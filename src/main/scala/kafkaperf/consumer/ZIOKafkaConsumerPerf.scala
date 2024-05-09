package kafkaperf.consumer

import kafkaperf.Common
import Common.*
import zio.kafka.consumer.{CommittableRecord, Consumer, ConsumerSettings, Offset, Subscription}
import zio.*
import zio.kafka.consumer.Consumer.AutoOffsetStrategy.Earliest
import zio.kafka.consumer.Consumer.OffsetRetrieval.Auto
import zio.kafka.serde.Serde
import zio.stream.{ZSink, ZStream}
import zio.{ZIO, ZIOAppDefault}
import zio.durationInt
import zio.kafka.consumer.diagnostics.Diagnostics

import java.io.IOException

object ZIOKafkaConsumerPerf extends ZIOAppDefault {
  private val maxParallelism = 5

  override def run: RIO[Any, Any] = {
    consumerPlainStream.provide(
      Consumer.live,
      ZLayer.succeed(Diagnostics.NoOp),
      ZLayer.succeed(ConsumerSettings(properties = consumerConfig)
        .withOffsetRetrieval(Auto(Earliest))
        .withGroupId(s"test-consumer-${java.lang.System.currentTimeMillis()}")
        .withClientId("test")
        .withPollTimeout(500.millis)
        .withMaxPollRecords(500)
      )
    )

  }

  private val consumerPlainStream: ZIO[Consumer, Throwable, Unit] = {
    val ref = Ref.make(0)
    ref.flatMap(counter =>
      val consumer = Consumer
        .plainStream(Subscription.topics(topicName), Serde.byteArray.asOption, Serde.byteArray.asOption)
        .mapZIOPar(maxParallelism)(handleRecord(counter, _))
        .aggregateAsync(Consumer.offsetBatches)
        .schedule(Schedule.fixed(2.seconds))
        .run(ZSink.foreach(_.commit))

      Console.printLine("Starting plain stream consumer") *> consumer.race(performanceCounter(counter))
    )
  }

  private val consumerPartitionedStream: ZIO[Consumer, Throwable, Unit] = {
    val ref = Ref.make(0)
    ref.flatMap(counter =>
      val consumer = Consumer
        .partitionedStream(Subscription.topics(topicName), Serde.byteArray.asOption, Serde.byteArray.asOption)
        .flatMapPar(maxParallelism) { case (_, stream) => stream.mapZIO(handleRecord(counter, _)) }
        .aggregateAsync(Consumer.offsetBatches)
        .schedule(Schedule.fixed(2.seconds))
        .run(ZSink.foreach(_.commit))

      Console.printLine("Starting partitioned stream consumer") *> consumer.race(performanceCounter(counter))
    )
  }

  private def performanceCounter(counter: Ref[Int]): ZIO[Any, IOException, Unit] = {
    ZStream.repeatZIO(
      counter
        .getAndSet(0)
        .flatMap(count => Console.printLine(s"Consumed $count records"))
    ).schedule(Schedule.fixed(1.seconds)).runDrain
  }

  private def handleRecord(ref: Ref[Int], record: CommittableRecord[Option[Array[Byte]], Option[Array[Byte]]]): ZIO[Any, Nothing, Offset] = {
    for {
      _ <- ref.update(_ + 1)
    } yield record.offset
  }

}
