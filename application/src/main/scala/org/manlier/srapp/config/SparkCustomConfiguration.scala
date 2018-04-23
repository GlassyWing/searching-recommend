package org.manlier.srapp.config

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.springframework.context.annotation
import org.springframework.context.annotation.Bean

@annotation.Configuration
class SparkCustomConfiguration {

  @Bean
  def sparkConf(): SparkConf = {
    new SparkConf()
      .setMaster("local[2]")
      .setAppName("Components Recommend")
      .set("spark.driver.allowMultipleContexts", "true")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .set("spark.kryo.registrator","org.manlier.srapp.config.MyKyroRegister")
  }

  @Bean
  def sparkSession(): SparkSession = {
    SparkSession.builder().config(sparkConf())
      .getOrCreate()
  }


}
