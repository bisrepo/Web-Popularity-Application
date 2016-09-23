from cassandra.cluster import Cluster
from kafka import KafkaConsumer
from kafka.errors import KafkaError

import argparse
import atexit
import json
import logging

# - default kafka topic to read from
topic_name = 'user-behaviour-topic'

# - default kafka broker location
kafka_broker = '127.0.0.1:9092'

# - default cassandra nodes to connect
contact_points = '192.168.99.101'

# - default keyspace to use
key_space = 'userBehaviour'

# - default table to use
data_table = 'userBehaviour'

logger_format = '%(asctime)-15s %(message)s'
logging.basicConfig(format=logger_format)
logger = logging.getLogger('data-storage')
logger.setLevel(logging.DEBUG)


def persist_data(user_behaviour, cassandra_session):
    """
    persist user behaviour messages into cassandra
    :param user_behaviour:
        the stock data looks like this:
        "page1|2|7.123|1|Android"
    :param cassandra_session:

    :return: None
    """
    try:
        logger.debug('Start to persist data to cassandra %s', user_behaviour)
        parsed = user_behaviour.split("|")
        pageID = parsed[0]
        clickTime = int(parsed[1])
        stayTime = float(parsed[2])
        likeStatus = int(parsed[3])
        deviceType = parsed[4]
        statement = "INSERT INTO %s (page_id, click_time, stay_time, like_status, device_type) VALUES ('%s', %d, %f, %d, '%s')" % (data_table, pageID, clickTime, stayTime, likeStatus, deviceType)
        cassandra_session.execute(statement)
        logger.info('Persistend data to cassandra for Id: %s, Click Time: %f, Stay Time: %s' % (pageID, clickTime, stayTime))
    except Exception:
        logger.error('Failed to persist data to cassandra %s', user_behaviour)


def shutdown_hook(consumer, session):
    """
    a shutdown hook to be called before the shutdown
    :param consumer: instance of a kafka consumer
    :param session: instance of a cassandra session
    :return: None
    """
    try:
        logger.info('Closing Kafka Consumer')
        consumer.close()
        logger.info('Kafka Consumer closed')
        logger.info('Closing Cassandra Session')
        session.shutdown()
        logger.info('Cassandra Session closed')
    except KafkaError as kafka_error:
        logger.warn('Failed to close Kafka Consumer, caused by: %s', kafka_error.message)
    finally:
        logger.info('Existing program')


if __name__ == '__main__':
    # - setup command line arguments
    parser = argparse.ArgumentParser()
    parser.add_argument('topic_name', help='the kafka topic to subscribe from')
    parser.add_argument('kafka_broker', help='the location of the kafka broker')
    parser.add_argument('key_space', help='the keyspace to use in cassandra')
    parser.add_argument('data_table', help='the data table to use')
    parser.add_argument('contact_points', help='the contact points for cassandra')

    # - parse arguments
    args = parser.parse_args()
    topic_name = args.topic_name
    kafka_broker = args.kafka_broker
    key_space = args.key_space
    data_table = args.data_table
    contact_points = args.contact_points

    # - initiate a simple kafka consumer
    consumer = KafkaConsumer(
        topic_name,
        bootstrap_servers=kafka_broker
    )

    # - initiate a cassandra session
    cassandra_cluster = Cluster(
        contact_points=contact_points.split(',')
    )
    session = cassandra_cluster.connect(key_space)

    # - setup proper shutdown hook
    atexit.register(shutdown_hook, consumer, session)

    for msg in consumer:
        persist_data(msg.value, session)
