# Web Popularity Calculator (WPC): Big Data Analyzing Pipeline Using Docker

## Why?

Nowadays internet economics are greatly related to ad clicks, large website traffic can bring more ad clicks, hence potential revenue increase. Aware of this fact, website admins are utilizing various tools to monitor real-time website visiting activity and dynamically adjust their content organization to cater their clients need. 

In this project, we designed and implemented an application to supply comprehensive analytics of given website page based on user behaviours including: click time, stay time, Like-it-or-not. The organization of this app is based on Lambda Architectrure, which consisted of two layers:

### Speed Layer

Per our purpose to supply a real-time analyzing service, We choose Spark Streaming to perform the streaming calculation. There are three values being calculated for each given web page: the real-time popularity value, the accumulative popularity value, and the visiting device statistic. Each of them will be write back to corresponding topic of Kafka, just also where the original user behaviour data is stored in.

### Serving Layer

Calculated results are then consumed from Kafka and stored into Redis for future front-end dashboard rendering. Also, the results are consumed and persisted to Cassandra in case of system failure. 

Throughout the two layers design, we adopt Kafka act as a data middleware to store the messages in topic-based fashion. Kafka is replicated, distributed log system that ensure fault-tolerance and failure recovery. Its performance has been proved in various production scenarios.


## Getting Started

WPC is an application that deployed using Docker, so make sure that your machine has Docker properly installed. Refer to the [Docker](/Docker) directory for details about how to start up different docker containers that will be used. After making sure that every container is running properly, you'll go to other directories in sequence of [Kafka](/Kafka), [Spark](/Spark), [Redis](/Redis), [Cassandra](/Cassandra), [Node](/Node) to start each program per instruction in README.md file.


## Under The Hood

WPC leverages several awesome open source projects:

* [Docker](https://www.docker.io) simplifies the management of Linux containers
* [Python](http://www.python.org) programming language
* [Kafka](http://kafka.apache.org/) a distributed, partitioned, replicated commit log service
* [Spark Streaming](http://spark.apache.org/streaming/) is a Spark extension that enables scalable, high-throughput, fault-tolerant stream processing of live data streams
* [Redis](http://redis.io/) is an open source, in-memory data structure store
* [Cassandra](http://cassandra.apache.org) is a highly scalable column store
* [Node.js](https://nodejs.org/en/) is a JavaScript runtime built on Chrome's V8 JavaScript engine


## Reference
 1. [Kafka + Spark Streaming 使用示例](http://loveltyoic.github.io/blog/2016/01/19/kafka-plus-spark-streaming/)
 2. [Spark and Kafka Integration Patterns, Part 1](http://allegro.tech/2015/08/spark-kafka-integration.html) 
 3. [Spark and Kafka Integration Patterns, Part 2](http://mkuthan.github.io/blog/2016/01/29/spark-kafka-integration2/)
 4. [Deploy a Mesos Cluster with 7 Commands Using Docker](https://medium.com/@gargar454/deploy-a-mesos-cluster-with-7-commands-using-docker-57951e020586#.t2anuw52a)


##Version Note

Under Construction...

