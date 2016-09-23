# Redis
## redis-producer.py
Redis producer, consume message from Kafka brokers and then publish them to redis PUB

### Library depedency
kafka-python    https://github.com/dpkp/kafka-python

redis           https://pypi.python.org/pypi/redis

```sh
pip install -r requirements.txt
```

### Running the code
Assuming Kafka and Redis containers are running within a docker-machine named "alpha", the address of which id 192.168.99.100
```sh
python redis-publisher.py popularity-topic 192.168.99.100:9092 popularity-topic 192.168.99.100 6379
```


