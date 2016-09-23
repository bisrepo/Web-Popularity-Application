# Spark Streaming Job

## [PopularityCalcJob.scala](/src/main/scala-2.11/PopularityExample/)

This streaming job calculates three values: webpage real-time popularity, webpage accumulative popularity, and device statistics. One worth mention point is that to calculate the accumulative popularity value, we applied the stateful API - [updateStateByKey](http://spark.apache.org/docs/latest/streaming-programming-guide.html). The popularity value formula is:
```sh
popularity value("page1") = 0.8*click_time + 0.8*stay_time + Like_it_or_Not
```

## Library Dependency

Kafka https://mvnrepository.com/artifact/org.apache.kafka/kafka_2.10/0.10.0.0
Kafka-clients https://mvnrepository.com/artifact/org.apache.kafka/kafka-clients/0.10.0.0
Spark-core
https://mvnrepository.com/artifact/org.apache.spark/spark-core_2.11/1.6.2
Spark Streaming
https://mvnrepository.com/artifact/org.apache.spark/spark-streaming_2.11/1.6.2
Ficus
https://mvnrepository.com/artifact/net.ceedubs/ficus_2.10/1.0.1
Config
https://mvnrepository.com/artifact/com.typesafe/config/1.3.0


## Running the code

We use sbt as the project build tool, it uses ```build.sbt``` for the project description and the library dependencies. The calculation job is located at [/src/main/scala-2.11/PopularityExample/](/src/main/scala-2.11/PopularityExample/). To submit the job to spark, we first need to package the project.
```sh
sbt package
```
Then submit the job to spark cluster:
```sh
spark-submit  --class PopularityExample.PopularityCalJob target/scala-2.11/sparkStreaming-project_2.11-1.0.jar build.sbt
```
this assume that spark-submit is in your PATH environmental variable. If not, you want to use absolute path to spark-submit directory on your machine.
