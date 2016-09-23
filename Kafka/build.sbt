name := "kafkaProducer"

organization := "yubo.r.xu@gmail.com"  // organization contact

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-clients" % "0.10.0.0",
  "org.apache.kafka" % "kafka_2.11" % "0.10.0.0",
  "joda-time" % "joda-time" % "2.9.4",
  "org.joda" % "joda-convert" % "1.8",
  "com.typesafe.akka" % "akka-actor_2.11" % "2.4.1",
  "org.slf4j" % "slf4j-api" % "1.7.21",
  "org.slf4j" % "slf4j-simple" % "1.7.21"
)
