package org.manlier.srapp.history

import java.util
import java.util.concurrent.CompletableFuture

import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.manlier.srapp.common.{HistoryRecordFormatUtil, PhoenixPool}
import org.manlier.srapp.dao.HistoryDAO
import org.manlier.srapp.domain.{HistoryRecord, NumOfUsers, TotalFreq}
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class HistoryServiceImpl(@Autowired val sparkSession: SparkSession
                         , @Autowired val properties: HistoryKafkaProperties
                         , @Autowired val historyDAO: HistoryDAO
                        ) extends HistoryService with DisposableBean with Serializable {

  private val ssc = new StreamingContext(sparkSession.sparkContext, Seconds(2))

  private val historyRecordProducer =
    new KafkaProducer[String, String](properties.getKafkaParamsProducer)

  def init(): Unit = {
    CompletableFuture.runAsync(new Runnable {
      override def run(): Unit = {
        //        getHistoryRecordStream.print()
        saveHistoryRecords(getHistoryRecordStream)
        ssc.start()
        ssc.awaitTermination()
      }
    })
  }

  /**
    * 从kafka的topic中获得历史记录，并连接到Spark streaming中
    *
    * @return DStream
    */
  def getHistoryRecordStream: DStream[(String, String, String, Long)] = {
    ssc.checkpoint(properties.getCheckpointDir)
    // Spark Streaming 对接Kafka
    KafkaUtils.createDirectStream(ssc
      , LocationStrategies.PreferConsistent
      , ConsumerStrategies.Subscribe[String, String](properties.getTopics, properties.getKafkaParamsConsumer))
      .map(_.value())
      .filter(HistoryRecordFormatUtil.isValidate)
      .map(x => (x, 1L))
      // 窗口时间设定为4s，并统计这段时间内用户使用构件的次数
      .reduceByKeyAndWindow(_ + _, _ - _, Seconds(4), Seconds(4), 2)
      .map(kv => {
        val parts = kv._1.split(",")
        (parts(0), parts(1), parts(2), kv._2)
      })
      //  过滤掉不必要的记录，减少数据库连接
      .filter(tuple => tuple._4 != 0)
  }

  /**
    * 将历史记录保存到数据库
    *
    * @param historyStream 历史记录流
    */
  def saveHistoryRecords(historyStream: DStream[(String, String, String, Long)]): Unit = {
    historyStream.foreachRDD { rdd =>
      rdd.foreachPartition(partitionRecords => {
        if (partitionRecords.nonEmpty) {
          try {
            val conn = PhoenixPool.getConnection
            val stat = conn.createStatement()
            conn.setAutoCommit(false)
            partitionRecords.foreach(tuple => {
              //              println(tuple)
              stat.addBatch(HistoryDBUtil.generateUpsertSQL(tuple._1, tuple._2, tuple._3, tuple._4))
            })
            stat.executeBatch()
            conn.commit()
          } catch {
            case e: Exception =>
              e.printStackTrace()
          }
        }
      })
    }
  }

  /**
    * 在系统关闭前进行清理
    */
  override def destroy(): Unit = {
    historyRecordProducer.close()
    ssc.stop()
  }

  /**
    * 添加历史记录
    *
    * @param historyRecord 历史记录
    */
  override def addHistoryRecord(historyRecord: String): Unit = {
    if (!HistoryRecordFormatUtil.isValidate(historyRecord)) {
      throw new HistoryFormatException(s"Invalid format for history string: $historyRecord")
    }
    val topic = properties.getTopics.get(0)
    historyRecordProducer.send(
      new ProducerRecord[String, String](topic, historyRecord)
      , new Callback {
        override def onCompletion(recordMetadata: RecordMetadata, e: Exception): Unit = {
        }
      })
  }

  /**
    * 获得有多少用户在使用完构件1之后又使用其它构件
    *
    * @param compName 构件1的名字
    * @return 用户数
    */
  override def getNumOfUsers(compName: String): util.List[NumOfUsers] = {
    historyDAO.getNumOfUsers(compName)
  }

  /**
    * 获得用户使用构件1之后又使用了哪些构件
    *
    * @param userName 用户名
    * @param compName 构件1的名字
    * @return 使用历史
    */
  override def getHistoryForUser(userName: String, compName: String): util.List[HistoryRecord] = {
    historyDAO.getHistoryForUser(userName, compName)
  }

  /**
    * 获得使用完构件1又使用其它构件的总次数
    *
    * @param compName 构件1的名字
    * @return 总次数
    */
  override def getTotalFreq(compName: String): util.List[TotalFreq] = {
    historyDAO.getTotalFreq(compName)
  }
}
