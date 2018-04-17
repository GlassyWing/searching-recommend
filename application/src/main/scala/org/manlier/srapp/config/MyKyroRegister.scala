package org.manlier.srapp.config

import com.esotericsoftware.kryo.Kryo
import org.apache.spark.serializer.KryoRegistrator
import org.manlier.recommend.LinearItemCFModel
import org.manlier.srapp.recommend.RecommendService

class MyKyroRegister extends KryoRegistrator {
  override def registerClasses(kryo: Kryo): Unit = {
    kryo.register(classOf[RecommendService])
    kryo.register(classOf[LinearItemCFModel])
  }
}
