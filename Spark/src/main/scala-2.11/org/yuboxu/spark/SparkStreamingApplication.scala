package org.yuboxu.spark

import org.apache.spark.SparkContext
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.concurrent.duration.FiniteDuration

trait SparkStreamingApplication extends SparkApplication {

  def streamingBatchDuration: FiniteDuration

  def streamingCheckpointDir: String

  def withSparkStreamingContext(f: (SparkContext, StreamingContext) => Unit): Unit ={
    withSparkContext {sc =>
      val ssc = new StreamingContext(sc, Seconds(streamingBatchDuration.toSeconds))
      ssc.checkpoint(streamingCheckpointDir)

      f(sc, scc)
      scc.start()
      scc.awaitTermination()
    }
  }
}

