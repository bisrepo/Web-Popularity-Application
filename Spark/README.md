# Spark Streaming Job

## [PopularityCalcJob.scala](/src/main/scala-2.11/PopularityExample/)

This streaming job calculates three values: webpage real-time popularity, webpage accumulative popularity, and device statistics. One worth mention point is that to calculate the accumulative popularity value, we applied the stateful API - [updateStateByKey](http://spark.apache.org/docs/latest/streaming-programming-guide.html). The popularity value formula is:
```sh
popularity value("page1") = 0.8*click_time + 0.8*stay_time + Like_it_or_Not
```

## Running the code
