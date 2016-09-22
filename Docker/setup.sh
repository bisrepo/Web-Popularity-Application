#!/usr/bin/env bash

# - - - - - Get the docker server name from args - - - - - 
MACHINE_NAME=$1

# - - - - - Get the ip of docker-machine - - - - - 
IP=$(docker-machine ip ${MACHINE_NAME})

# - - - - - run a single zookeeper container - - - - - 
echo "start zookeeper"
RUNNING=$(docker inspect --format="{{ .State.Running  }}" zookeeper 2> /dev/null)

if [[ $? -eq 1 ]]; then
    # cannot find docker container named "zookeeper", then create one
    docker run \
        -d \
        -p 2181:2181 \
        -p 2888:2888 \
        -p 3888:3888 \
        --name zookeeper \
        confluent/zookeeper
elif [[ ${RUNNING} == "false" ]]; then
    docker start zookeeper
fi

sleep 2

# - - - - - run the kafka broker container - - - - - 
echo "start kafka"
RUNNING=$(docker inspect --format="{{ .State.Running  }}" kafka 2> /dev/null)

if [[ $? -eq 1 ]]; then
    # cannot find docker container named "kafka", then create one
    docker run \
        -d \
        -p 9092:9092 \
        -e KAFKA_ADVERTISED_HOST_NAME="${IP}" \
        -e KAFKA_ADVERTISED_PORT=9092 \
        --name kafka \
        --link zookeeper:zookeeper \
        confluent/kafka
elif [[ ${RUNNING} == "false" ]]; then
    docker start kafka
fi

# - - - - - run the mesos master container - - - - - 
echo "start mesos master"
RUNNING=$(docker inspect --format="{{ .State.Running }}" mesos-master 2> /dev/null)

if [[ $? -eq 1 ]]; then
    # cannot find container named "mesos-master", then create one
    docker run \
        -d \
        -p 5050:5050 \
        --name mesos-master \
        --net="host" \
        -e "MESOS_HOSTNAME=${IP}" \
        -e "MESOS_IP=${IP}" \
        -e "MESOS_ZK=zk://${IP}:2181/mesos" \
        -e "MESOS_PORT=5050" \
        -e "MESOS_LOG_DIR=/var/log/mesos" \
        -e "MESOS_QUORUM=1" \
        -e "MESOS_REGISTRY=in_memory" \
        -e "MESOS_WORK_DIR=/var/lib/mesos" \
        mesosphere/mesos-master:1.0.1-1.0.90.rc1.ubuntu1404
elif [[ ${RUNNING} == "false" ]]; then
    docker start mesos-master
fi

# - - - - - run the mesos-slave container - - - - - 
echo "start mesos-slave"
RUNNING=$(docker inspect --format="{{ .State.Running }}" mesos-slave 2> /dev/null)

if [[ $? -eq 1 ]]; then
    # cannot find container name "mesos-slave", then create one
    docker run \
        -d \
        --privileged \
        --name mesos-slave \
        --net="host" \
        --entrypoint="mesos-slave" \
        -e "MESOS_MASTER=zk://${IP}:2181/mesos" \
        -e "MESOS_LOG_DIR=/var/log/mesos" \
        -e "MESOS_LOGGING_LEVEL=INFO" \
        -e "MESOS_WORK_DIR=/var/lib/mesos" \
        mesosphere/mesos-slave:1.0.1-1.0.90.rc1.ubuntu1404
elif [[ ${RUNNING} == "false" ]]; then
    docker start -
fi

# - - - - - run the marathon container - - - - - 
echo "start marathon framework"
RUNNING=$(docker inspect --format="{{ .State.Running }}" marathon 2> /dev/null)

if [[ $? -eq 1 ]]; then
    # cannot find container name "marathon", then create one
    docker run \
        -d \
        -p 8080:8080 \
        --name marathon \
        mesosphere/marathon:v1.1.2 --master zk://${IP}:2181/mesos --zk zk://${IP}:2181/marathon

elif [[ ${RUNNING} == "false" ]]; then
    docker start marathon
fi


# - - - - - run the cassandra container - - - - - 
echo "start cassandra"
RUNNING=$(docker inspect --format="{{ .State.Running }}" cassandra 2> /dev/null)

if [[ $? -eq 1 ]]; then
    # cannot find container name "cassandra", then create one
    docker run \
        -d \
        -p 7199:7199 \
        -p 9042:9042 \
        -p 9160:9160 \
        -p 7001:7001 \
        --name cassandra \
        cassandra:3.7
elif [[ ${RUNNING} == "false" ]]; then
    docker start cassandra
fi

# - - - - - run the redis container - - - - - 
echo "start redis"
RUNNING=$(docker inspect --format="{{ .State.Running }}" redis 2> /dev/null)

if [[ $? -eq 1 ]]; then
    # cannot find container name "redis", then create one
    docker run \
        -d \
        -p 6379:6379 \
        --name redis \
        redis:alpine
elif [[ ${RUNNING} == "false" ]]; then
    docker start redis
fi

