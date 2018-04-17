package org.manlier.srapp.config

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.spark.sql.SparkSession
import org.springframework.context.annotation
import org.springframework.context.annotation.{Bean, Scope}

@annotation.Configuration
class SparkCustomConfiguration {

  @Bean
  def sparkSession(): SparkSession = {
    SparkSession.builder()
      .appName("Components Recommend")
      .master("local")
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .config("spark.kryo.registrator","org.manlier.srapp.config.MyKyroRegister")
      .getOrCreate()
  }

  @Bean
  def hbaseConfiguration(): Configuration = {
    HBaseConfiguration.create()
  }
}
