package org.manlier.srapp.prediction

import org.apache.spark.sql.DataFrame
import org.manlier.srapp.domain.Prediction

trait PredictionService {

  def makePrediction(): DataFrame

  def storePrediction(): Unit

  def getPrediction(userName: String, compName: String, num: Int): java.util.List[Prediction]

  def init(): Unit

  def clean(): Unit
}
