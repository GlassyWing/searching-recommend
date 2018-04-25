package org.manlier.srapp.utils

import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.spark.sql.SparkSession
import org.apache.phoenix.spark._
import org.manlier.srapp.constraints.Schemas.HBaseComponentSchema

object ExportComponentsToCSV {

  val conf = HBaseConfiguration.create()

  val sparkSession: SparkSession = SparkSession.builder()
    .master("local[2]")
    .getOrCreate()

  def main(args: Array[String]): Unit = {
    sparkSession.sqlContext.phoenixTableAsDataFrame(HBaseComponentSchema.TABLE_NAME
    , Seq(HBaseComponentSchema.NAME_QUALIFIER, HBaseComponentSchema.ID_QUALIFIER, HBaseComponentSchema.DESC_QUALIFIER)
    , conf = conf).coalesce(1).write.csv("example/components")
  }

}
