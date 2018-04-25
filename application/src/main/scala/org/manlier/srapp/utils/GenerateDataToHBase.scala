package org.manlier.srapp.utils

import org.apache.hadoop.hbase.HBaseConfiguration
import org.manlier.recommend.utils.TestDataGenerator
import org.apache.phoenix.spark._
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.manlier.srapp.constraints.Schemas._
import org.apache.phoenix.spark._
import org.manlier.recommend.entities.Component
import collection.JavaConverters._

object GenerateDataToHBase {

  val spark: SparkSession = SparkSession.builder()
    .master("local[2]")
    .getOrCreate()

  val conf = HBaseConfiguration.create()

  import spark.implicits._

  def generateRecordByExistComponents(compsTable: DataFrame
                                      , numCompTuples: Int
                                      , numUsers: Int
                                      , numHistory: Int
                                      , maxCount: Float
                                     ): (DataFrame, DataFrame, DataFrame) = {
    val users = TestDataGenerator.generateUsers(numUsers)
    val comps = compsTable
      .map(row => Component(row.getInt(0), row.getString(1)))
      .collectAsList()
    val compTuples = TestDataGenerator.generateComponentTuples(numCompTuples, comps.asScala.toIndexedSeq)
    val history = TestDataGenerator.generateHistoryWithName(numHistory, maxCount, users, compTuples)
    (users.toDF("ID", "UUID")
      , compsTable
      , history.toDF("USERNAME", "COMPNAME", "FOLLOWCOMPNAME", "FREQ"))
  }


  def main(args: Array[String]): Unit = {
    val compsTable = spark.sqlContext.phoenixTableAsDataFrame(HBaseComponentSchema.TABLE_NAME,
      Seq(HBaseComponentSchema.ID_QUALIFIER, HBaseComponentSchema.NAME_QUALIFIER), conf = conf)
      .coalesce(1)
    val tables = generateRecordByExistComponents(compsTable, 300, 100, 10000, 5f)
    saveToHBase(tables)
  }

  def saveToHBase(tables: (DataFrame, DataFrame, DataFrame)): Unit = {
    val conf = HBaseConfiguration.create()
    tables._1.toDF("ID", "UUID")
      .saveToPhoenix(HBaseUsersSchema.TABLE_NAME, conf = conf)
//    tables._2.toDF("ID", "NAME")
//      .saveToPhoenix(HBaseComponentSchema.TABLE_NAME, conf = conf)
    tables._3.toDF("USERNAME", "COMPNAME", "FOLLOWCOMPNAME", "FREQ")
      .saveToPhoenix(HBaseHistorySchema.TABLE_NAME, conf = conf)
  }
}
