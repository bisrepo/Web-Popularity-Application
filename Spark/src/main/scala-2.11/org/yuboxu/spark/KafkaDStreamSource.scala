package org.yuboxu.spark

import kafka.serializer.{StringDecoder, DefaultDecocder}
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka.KafkaUtils

class KafkaDStreamSource(config: Map[String, String]) {
  def createSource(ssc: StreamingContext, topic: String): DStream[KafkaPayload] = {
    val kafkaParams = config
    val kafkaTopics = Set(topic)

    KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
      // spark streaming context
      ssc,
      // kafka configuration parameters
      kafkaParams,
      // names of the topics to consume
      kafkaTopics).map(dstream => KafkaPayload(Option(dstream._1), dstream._2)
    )
  }
}

object KafkaDStreamSource {
  def apply(config: Map[String, String]): KafkaDStreamSource = new KafkaDStreamSource(config)
}