package kafkaperf

object Common {
  val messages = 1_000_000

  val topicName = "test-topic-1"

  val consumerConfig = Map(
    "bootstrap.servers" -> "localhost:9091",
  )

  val config = Map(
    "bootstrap.servers" -> "localhost:9091",
    "compression.type" -> "zstd",
    "batch.size" -> "200000",
    "linger.ms" -> "5",
    "retries" -> "30",
    "retry.backoff.ms" -> "1000",
    "max.request.size" -> "10000000",
    "enable.idempotence" -> "true",
    "acks" -> "all")
}
