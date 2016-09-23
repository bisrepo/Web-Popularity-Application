package PopularityExample

import org.apache.spark.storage.StorageLevel
import org.yuboxu.spark._
import scala.concurrent.duration.FiniteDuration

class PopularityCalcJob(config: PopularityJobConfig, source: KafkaDStreamSource) extends SparkStreamingApplication {

  override def sparkConfig: Map[String, String] = config.spark

  override def streamingBatchDuration: FiniteDuration = config.streamingBatchDuration

  override def streamingCheckpointDir: String = config.streamingCheckpointDir

  def start(): Unit = {
    withSparkStreamingContext {(sc, ssc) =>
      val input = source.createSource(ssc, config.inputTopic)

      val (popularity, accmPopularity, device) = PopularityCalc.calculate(ssc, input)

      // cache the data in case of action fails
      popularity.persist(StorageLevel.MEMORY_ONLY_SER)

      // encode kafka payload to string
//      val popValOutput = popularity.map(_.toString()).map()
//      val accmPopValOutput = accmPopularity.map(_.toString())
//      val deviceOutput = device.map(_.toString())

      import KafkaDStreamSink._
      popularity.sendToKafka(config.KafkaSink, config.popTopic)
      accmPopularity.sendToKafka(config.KafkaSink, config.accmPopTopic)
      device.sendToKafka(config.KafkaSink, config.deviceTopic)

    }
  }
}

object PopularityCalcJob {

  def main(args: Array[String]): Unit = {
    val config = PopularityJobConfig()

    val streamingJob = new PopularityCalcJob(config, KafkaDStreamSource(config.KafkaSource))
    streamingJob.start()
  }
}

case class PopularityJobConfig(
                              inputTopic: String,
                              popTopic: String,
                              accmPopTopic: String,
                              deviceTopic: String,
                              spark: Map[String, String],
                              streamingBatchDuration: FiniteDuration,
                              streamingCheckpointDir: String,
                              KafkaSource: Map[String, String],
                              KafkaSink: Map[String, String]
                              )
  extends Serializable

object PopularityJobConfig {
  import com.typesafe.config.{Config, ConfigFactory}
  import net.ceedubs.ficus.Ficus._
  import net.ceedubs.ficus.readers.ArbitraryTypeReader.arbitraryTypeValueReader

  def apply(): PopularityJobConfig = apply(ConfigFactory.load)

  def apply(applicationConfig: Config): PopularityJobConfig = {
    val config = applicationConfig.getConfig("PopularityCalcJob")

    new PopularityJobConfig(
      config.as[String]("input.topic"),
      config.as[String]("output.topic1"),
      config.as[String]("output.topic2"),
      config.as[String]("output.topic3"),
      config.as[Map[String, String]]("spark"),
      config.as[FiniteDuration]("streamingBatchDuration"),
      config.as[String]("streamingCheckpointDir"),
      config.as[Map[String, String]]("kafkaSource"),
      config.as[Map[String, String]]("kafkaSink")
    )
  }
}