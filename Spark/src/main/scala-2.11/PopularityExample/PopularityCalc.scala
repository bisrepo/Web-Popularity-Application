package PopularityExample

import org.apache.spark.rdd.RDD
import org.apache.spark.HashPartitioner
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Duration, StreamingContext}
import org.yuboxu.spark.KafkaPayload

import scala.concurrent.duration.FiniteDuration

object PopularityCalc {
  type PopValue = (Long, String, Double)
  def calculate(
                      ssc: StreamingContext,
                      input: DStream[KafkaPayload]):
    (DStream[KafkaPayload], DStream[KafkaPayload], DStream[KafkaPayload]) = {

    import scala.language.implicitConversions
    implicit def finiteDurationToSparkDuration(value: FiniteDuration): Duration = new Duration(value.toMillis)

    val sc = ssc.sparkContext

    val userBehaviour = input.transform(splitLine)

    val popValue = userBehaviour.transform(calcPopularity)

    val device = userBehaviour
      .map { behavior:Array[String] => ( behavior(4), 1.0 )}
      .reduceByKey( _ + _)

    val initialRDD = sc.parallelize(List(("page1", 0.00)))

    val accmPopValue = popValue.updateStateByKey[Double](
      updatePopValue,
      new HashPartitioner(sc.defaultParallelism),
      true,
      initialRDD
    )

    accmPopValue.checkpoint(Duration(40000));
    (formatter(popValue), formatter(accmPopValue), formatter(device))
  }

  // split the kafka message into fields based on delimiter "|"
  val splitLine = (lines: RDD[KafkaPayload]) => lines.map(line => line.value.split("\\|"))

  // add current time to record
  val addTime = (records: RDD[(String, Double)]) =>
    records.map { record => (System.currentTimeMillis(), record._1, record._2) }

  // calculate weighted real time popularity: RDD[(String, double)]
  val calcPopularity = (behaviors: RDD[Array[String]]) =>
    behaviors.map {
      behavior => (
        behavior(0),
        behavior(1).toFloat * 0.8 + behavior(2).toFloat * 0.8 + behavior(3).toInt * 1)
    }

  val updatePopValue = (iterator: Iterator[(String, Seq[Double], Option[Double])]) => {
    iterator.flatMap(t => {
      val newValue: Double = t._2.sum
      val stateValue: Double = t._3.getOrElse(0);
      Some(newValue + stateValue)
    }.map(sumedValue => (t._1, sumedValue)))
  }

  val formatter = (records: DStream[(String, Double)]) =>
    records.transform(addTime).map(_.toString()).map(dstream => KafkaPayload(None, dstream))

}