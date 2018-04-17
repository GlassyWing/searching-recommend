package org.manlier.srapp.utils

import org.apache.hadoop.hbase.HBaseConfiguration
import org.manlier.recommend.utils.TestDataGenerator
import org.apache.phoenix.spark._
import org.apache.spark.sql.DataFrame
import org.manlier.srapp.constraints.Schemas._

object GenerateDataToHBase {

  def main(args: Array[String]): Unit = {
    val tables = TestDataGenerator.generateTableWithName(200
      , 600
      , 100
      , 10000
      , 5f
      , Array("A", "B", "C", "D"))
    saveToHBase(tables)
  }

  def saveToHBase(tables: (DataFrame, DataFrame, DataFrame)): Unit = {
    val conf = HBaseConfiguration.create()
    tables._1.toDF("ID", "UUID")
      .saveToPhoenix(HBaseUsersSchema.TABLE_NAME, conf = conf)
    tables._2.toDF("ID", "NAME")
      .saveToPhoenix(HBaseComponentSchema.TABLE_NAME, conf = conf)
    tables._3.toDF("USERNAME", "COMPNAME", "FOLLOWCOMPNAME", "FREQ")
      .saveToPhoenix(HBaseHistorySchema.TABLE_NAME, conf = conf)
  }
}
