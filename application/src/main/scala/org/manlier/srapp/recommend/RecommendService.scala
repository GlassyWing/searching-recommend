package org.manlier.srapp.recommend

import org.apache.spark.sql.DataFrame
import org.manlier.srapp.domain.Prediction


trait RecommendService {

  def recommendForUser(userName: String, compName: String, num: Int): java.util.List[Prediction]

  def makePrediction(): DataFrame

  def storePrediction(): Unit

  def init(): Unit

  def clean(): Unit
}
