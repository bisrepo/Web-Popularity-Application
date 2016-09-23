# Kafka Producer

## UserBehaviourMsgProducer.scala
Here we implemented a user behaviour simulator, which produce user behavior message and send it to kafka. Each message contains five fields: web page ID, click time, stay time, Like-it-or-Not, device type, which delimited by "|".


## Library dependency
Kafka
https://mvnrepository.com/artifact/org.apache.kafka/kafka_2.10/0.10.0.0

Kafka-clients
https://mvnrepository.com/artifact/org.apache.kafka/kafka-clients/0.10.0.0

slf4j-api
https://mvnrepository.com/artifact/org.slf4j/slf4j-api/1.7.21

schedule        https://pypi.python.org/pypi/schedule


## Run the code

Since the project is managed using [SBT](http://www.scala-sbt.org/), you need first make sure that it is correctly installed. Then change the working directory to the [Kafka](/Kafka) folder, run the below command
```sh
sbt compile package run
```
 you can also choose to run it in interactive mode. To stop the program, using ctrl + c command.


