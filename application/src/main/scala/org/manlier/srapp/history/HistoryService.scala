package org.manlier.srapp.history

import org.apache.spark.streaming.dstream.DStream

trait HistoryService {

  def init(): Unit

  def addHistoryRecord(historyRecord: String)

  def getHistoryRecordStream: DStream[(String, String, String, Long)]

  def saveHistoryRecords(historyStream: DStream[(String, String, String, Long)])

}
