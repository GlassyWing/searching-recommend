package org.manlier.srapp.utils

import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.spark.sql.SparkSession
import org.apache.phoenix.spark._
import org.manlier.srapp.constraints.Schemas._

import scala.collection.Seq


object ExportThesaurusToCSV {

  val sparkSession: SparkSession = SparkSession.builder()
    .master("local[2]")
    .getOrCreate()

  val conf = HBaseConfiguration.create()

  import sparkSession.implicits._

  def main(args: Array[String]): Unit = {
    sparkSession.sqlContext.phoenixTableAsDataFrame(
      HBaseThesaurusGroupSchema.TABLE_NAME
      , Seq(HBaseThesaurusGroupSchema.GROUPID_QUALIFIER, HBaseThesaurusGroupSchema.SYNONYMS_QUALIFIER)
      , conf = conf).coalesce(1).write.csv("./example/dict/thesaurus/group")
    sparkSession.sqlContext.phoenixTableAsDataFrame(
      HBaseThesaurusBelongSchema.TABLE_NAME
      , Seq(HBaseThesaurusBelongSchema.WORD_QUALIFIER, HBaseThesaurusBelongSchema.GROUPID_QUALIFIER)
      , conf = conf).coalesce(1).write.csv("./example/dict/thesaurus/belong")
  }

}
