package org.manlier.srapp.recommend

import org.manlier.srapp.domain.Prediction


trait RecommendService {

  def recommendForUser(userName: String, compName: String, num: Int): java.util.List[Prediction]
}
