package org.manlier.srapp.recommend

import org.manlier.srapp.component.ComponentService
import org.manlier.srapp.domain.{Prediction, User}
import org.manlier.srapp.prediction.PredictionService
import org.manlier.srapp.user.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RecommendServiceImpl(@Autowired val userService: UserService
                           , @Autowired val compService: ComponentService
                           , @Autowired val predictionService: PredictionService)
  extends RecommendService {

  def recommendForUser(userName: String, compName: String, num: Int): java.util.List[Prediction] = {
    val userQuantum = userService.selectUserByUUID(userName)
    val compQuantum = compService.searchComp(compName)
    //  如果用户不存在，则添加用户
    if (!userQuantum.isPresent) {
      userService.addUser(new User(userName))
    }
    predictionService.getPrediction(userName, compName, num)
  }

}
