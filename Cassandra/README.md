# Cassandra

## data-storage.py
This program consumes and persists data from kafka to cassandra.

### library dependency
cassandra-driver    https://github.com/datastax/python-driver

cql

```sh
pip install -r requirements.txt
```

### Running the code
Assuming the Cassandra container is running within a docker-machine named "alpha", the ip address of which is 192.168.99.100.

create a keyspace and table using cqlsh client.
```sh
CREATE KEYSPACE "userBehaviour" WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1} AND durable_writes = 'true';
USE userBehaviour;
CREATE TABLE userBehaviour (page_id text, click_time int, stay_time float, like_status int, device_type text);
```

```sh
python data-storage.py user-behaviour-topic 192.168.99.100:9092 userBehaviour userBehaviour 192.168.99.100
```
