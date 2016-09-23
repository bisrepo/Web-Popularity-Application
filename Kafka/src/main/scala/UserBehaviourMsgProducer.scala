package userBehavior

import scala.util.Random
import java.util.Properties
import java.util.concurrent.TimeUnit
import org.apache.kafka.clients.producer.{KafkaProducer => KafkaProducer, ProducerRecord}
import org.apache.kafka.common.errors.{TimeoutException => kafkatimeOut, InterruptException}
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.joda.time.{Seconds, DateTime}

trait logging {
  val logger = LoggerFactory.getLogger(this.getClass())
}

class UserBehaviourMsgProducer(producer: KafkaProducer[String, String], topic: String) extends Runnable with logging{
  private val PAGE_NUM = 100
  private val MAX_MSG_NUM = 3
  private val MAX_CLICK_TIME = 5
  private val MAX_STAY_TIME = 10
  // Like: 1; Dislike: -1; No Feeling: 0
  private val LIKE_OR_NOT = Array[Int](1, 0, -1)
  def run(): Unit = {
    // pageId = (rand.nextInt(PAGE_NUM) + 1)
    val pageId = 1
    logger.debug("start to simulate behaviour for %s".format(pageId))
    val rand = new Random()
    var numOfMsg = 0
    var start = DateTime.now()
    while (true) {
      try {
        numOfMsg += 1
        val msg = new StringBuilder()
        msg.append("page" + pageId) // here we hard code the page num to be "1"
        msg.append("|")
        msg.append(rand.nextInt(MAX_CLICK_TIME) + 1)
        msg.append("|")
        msg.append(rand.nextInt(MAX_STAY_TIME) + rand.nextFloat())
        msg.append("|")
        msg.append(LIKE_OR_NOT(rand.nextInt(3)))
        val m = msg.toString()
        logger.debug("simulated behaviour: %s".format(m))
        sendMessage(m)
        logger.debug("sent user behaviour: Page%d to kafka".format(pageId))
      }
      catch {
        case timeout: kafkatimeOut => logger.warn("Failed to sent user behaviour for Page%d to kafka due to: %d".format(pageId, timeout))
        case e: Exception => logger.warn("Failed to simulate behaviour for Page%d".format(pageId))
      }
      if (numOfMsg == 100000) {
        val end = DateTime.now()
        logger.info("wrote 100000 records to kafka in %d seconds".format((end.getMillis() - start.getMillis()) / 1000d))
        start = end
        numOfMsg = 0
      }
    }
  }

  def sendMessage(message: String): Unit = {
    try {
      val data = new ProducerRecord[String, String](this.topic, message)
      this.producer.send(data)
    } catch {
      case e: Exception => println(e)
    }
  }
}

class ShutDownCeremony(producer: KafkaProducer[String, String]) extends Runnable with logging {
  def run(): Unit ={
    try {
      logger.info("Flushing pending messages to kafka")
      producer.flush()
      logger.info("Finished flusing pending messages to kafka")
    }catch {
      case kafkaError: InterruptException => logger.warn("Failed to flush pending messages to kafka due to:%s".format(kafkaError))
    }finally {
      try {
        logger.info("Closing kafka connection")
        producer.close(10L, TimeUnit.SECONDS)
      }catch {
        case e: Exception => logger.warn("Failed to close kafka connection due to: %s".format(e))
      }
    }
  }
}

object  UserBehaviourMsgProducer {
  def main(args: Array[String]): Unit = {
    if (args.length < 2) {
      println("Usage: UserBehaviourMsgProducer.scala 192.168.99.100 use-behaviour-topic")
      System.exit(1)
    }
    // configure the KafkaProducer
    val brokerList = args(0)
    val targetTopic = args(1)
    val props = new Properties()
    props.put("bootstrap.servers", brokerList)
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    val producer = new KafkaProducer[String, String](props)
    val t = new Thread(new ShutDownCeremony(producer))

    // register shutDown hook
    sys.addShutdownHook(t.run)
    // start new producer thread
    new Thread(new UserBehaviourMsgProducer(producer, targetTopic)).start()
  }
}

