package org.yuboxu.spark

import org.apache.spark.{SparkConf, SparkContext}

trait SparkApplication {
  def sparkConfig: Map[String, String]

  def withSparkContext(f: SparkContext => Unit): Unit = {
    val conf = new SparkConf()

    sparkConfig.foreach { case (k, v) => conf.setIfMissing(k, v) }

    val sc = new SparkContext(conf)

    f(sc)
  }
}