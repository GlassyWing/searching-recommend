package org.manlier.srapp.config

import com.esotericsoftware.kryo.Kryo
import org.apache.spark.serializer.KryoRegistrator
import org.manlier.recommend.LinearItemCFModel
import org.manlier.srapp.common.HistoryRecordFormatUtil
import org.manlier.srapp.history.{HistoryService, HistoryServiceImpl}
import org.manlier.srapp.prediction.PredictionService

class MyKyroRegister extends KryoRegistrator {
  override def registerClasses(kryo: Kryo): Unit = {
    kryo.register(classOf[PredictionService])
    kryo.register(classOf[LinearItemCFModel])
    kryo.register(classOf[HistoryService])
  }
}
