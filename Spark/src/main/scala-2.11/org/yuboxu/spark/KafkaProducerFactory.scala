package org.yuboxu.spark

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.log4j.Logger

import scala.collection.mutable

object KafkaProducerFactory {

  import scala.collection.JavaConversions._

  private val logger = Logger.getLogger(getClass)

  private val producers = mutable.Map[Map[String, String], KafkaProducer[String, String]]()

  def getOrCreateProducer(config: Map[String, String]): KafkaProducer[String, String] = {

    val defaultConfig = Map(
      "key.serializer" ->  "org.apache.kafka.common.serialization.StringSerializer",
      "value.serializer" ->  "org.apache.kafka.common.serialization.StringSerializer"
    )

    val finalConfig = defaultConfig ++ config

    producers.getOrElseUpdate(finalConfig, {
      logger.info(s"Create Kafka producer , config: $finalConfig")
      val producer = new KafkaProducer[String, String](finalConfig)

      sys.addShutdownHook {
        logger.info(s"Close Kafka producer, config: $finalConfig")
        producer.close()
      }

      producer
    })
  }
}