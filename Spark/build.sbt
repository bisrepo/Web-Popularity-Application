name := "sparkStreaming"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.apache.spark" % "spark-core_2.11" % "1.6.2",
  "org.apache.spark" % "spark-streaming_2.11" % "1.6.2",
  "org.apache.spark" % "spark-streaming-kafka_2.10" % "1.6.2",
  "org.apache.kafka" % "kafka-clients" % "0.10.0.1",
  "org.apache.kafka" % "kafka_2.10" % "0.10.0.0",
  "joda-time" % "joda-time" % "2.9.4",
  "org.joda" % "joda-convert" % "1.8",
  "org.slf4j" % "slf4j-api" % "1.7.21",
  "org.slf4j" % "slf4j-simple" % "1.7.21",
  "net.ceedubs" % "ficus_2.10" % "1.0.1",
  "com.typesafe" % "config" % "1.3.0"

)

