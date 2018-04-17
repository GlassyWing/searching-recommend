package org.manlier.srapp.recommend

import org.apache.hadoop.conf.Configuration
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.apache.phoenix.spark._
import org.apache.spark.SparkContext
import org.manlier.recommend.LinearItemCFModel
import org.manlier.recommend.entities.{History, UserCompPair}
import org.manlier.srapp.component.ComponentService
import org.manlier.srapp.constraints.Schemas._
import org.manlier.srapp.user.UserService
import org.manlier.srapp.constraints.Limits.MAX_RECOMMEND_COMP_NUM
import org.manlier.srapp.dao.PredictionDAO
import org.manlier.srapp.domain.{Component, Prediction, User}
import org.springframework.transaction.annotation.Transactional

@Service
class RecommendServiceImpl(@Autowired val spark: SparkSession
                           , @Autowired val hbaseConfig: Configuration
                           , @Autowired val userService: UserService
                           , @Autowired val compService: ComponentService
                           , @Autowired val predictionDAO: PredictionDAO)
  extends RecommendService with Serializable {

  import spark.implicits._

  private val parallelism = spark.sparkContext.defaultParallelism

  private val itemCFModel = new LinearItemCFModel(spark)

  private var users: Option[DataFrame] = None
  private var components: Option[DataFrame] = None
  private var history: Option[Dataset[History]] = None

  /**
    * 初始化工作
    */
  override def init(): Unit = {
    storePrediction()
    clean()
  }


  /**
    * 清除缓存数据
    */
  def clean(): Unit = {
    users.foreach(users => users.unpersist())
    components.foreach(components => components.unpersist())
    history.foreach(history => history.unpersist())
    itemCFModel.getSimilarities.get.unpersist()

    users = None
    components = None
    history = None
  }

  def recommendForUser(userName: String, compName: String, num: Int): java.util.List[Prediction] = {
    val userQuantum = userService.selectUserByUUID(userName)
    val compQuantum = compService.searchComp(compName)
    if (!userQuantum.isPresent) {
      userService.addUser(new User(userName))
    }
    if (!compQuantum.isPresent) {
      compService.addComp(new Component(compName, ""))
    }
    predictionDAO.getPrediction(userName, compName, num)
  }

  def storePrediction(): Unit = {
    storePrediction(makePrediction())
  }

  /**
    * 将预测存储到数据库
    *
    * @param prediction 预测
    */
  @Transactional
  private def storePrediction(prediction: DataFrame): Unit = {
    prediction.saveToPhoenix(HBasePredictionSchema.TABLE_NAME, conf = hbaseConfig)
  }

  /**
    * 做出预测
    *
    * @return DataFrame
    */
  def makePrediction(): DataFrame = {
    val userSet = getHistory.select("userId", "COMPID")
      .map(row => UserCompPair(row.getInt(0), row.getInt(1)))
    val prediction = itemCFModel.fit(getHistory).recommendForUser(userSet, MAX_RECOMMEND_COMP_NUM).coalesce(parallelism).cache()
    prediction.createOrReplaceTempView("prediction")
    getUsers.createOrReplaceTempView("users")
    getComponents.createOrReplaceTempView("components")
    val predict = spark.sql(
      """
        | SELECT b.uuid as USERNAME, a.COMPNAME as COMPNAME, a.FOLLOWCOMPNAME as FOLLOWCOMPNAME, prediction
        | FROM
        | (SELECT userId, COMPNAME, b.NAME as FOLLOWCOMPNAME, prediction
        | FROM
        | (SELECT a.userId as userId, b.NAME as COMPNAME, a.followCompId as followCompId, prediction
        | FROM prediction a
        | JOIN components b on a.compId = b.ID
        | ) a
        | JOIN components b on a.followCompId = b.ID) a
        | JOIN users b
        | ON a.userId = b.id
      """.stripMargin)
    predict.coalesce(parallelism)
  }

  /**
    * 从数据库中载入用户历史记录
    *
    * @return DataFrame
    */
  private def loadHistory(): DataFrame = {
    spark.sqlContext.phoenixTableAsDataFrame(HBaseHistorySchema.TABLE_NAME
      , Seq(HBaseHistorySchema.USER_QUALIFIER
        , HBaseHistorySchema.COMP_QUALIFIER
        , HBaseHistorySchema.FOLLOW_COMP_QUALIFIER
        , HBaseHistorySchema.FREQ_QUALIFIER)
      , conf = hbaseConfig)
  }


  /**
    * 获得用户表
    *
    * @return DataFrame
    */
  private def getUsers: DataFrame = {
    users.getOrElse {
      val usrs = loadUsers().coalesce(parallelism).cache()
      users = Option(usrs)
      usrs
    }
  }

  /**
    * 载入用户表
    *
    * @return DataFrame
    */
  private def loadUsers(): DataFrame = {
    spark.sqlContext.phoenixTableAsDataFrame(HBaseUsersSchema.TABLE_NAME
      , Seq(HBaseUsersSchema.UUID_QUALIFIER
        , HBaseUsersSchema.ID_QUALIFIER)
      , conf = hbaseConfig)
  }


  /**
    * 获得构件表
    *
    * @return DataFrame
    */
  private def getComponents: DataFrame = {
    components.getOrElse {
      val comps = loadComponents().coalesce(parallelism).cache()
      components = Option(comps)
      comps
    }
  }

  /**
    * 载入构件表
    *
    * @return DataFrame
    */
  private def loadComponents(): DataFrame = {
    spark.sqlContext.phoenixTableAsDataFrame(HBaseComponentSchema.TABLE_NAME
      , Seq(HBaseComponentSchema.NAME_QUALIFIER
        , HBaseComponentSchema.ID_QUALIFIER)
      , conf = hbaseConfig)
  }


  /**
    * 获得用户历史记录，格式为
    * (userId, compId, followCompId, freq)
    *
    * @return Dataset[History]
    */
  private def getHistory: Dataset[History] = {
    history.getOrElse {
      loadHistory().coalesce(parallelism).createOrReplaceTempView("history")
      getUsers.createOrReplaceTempView("users")
      getComponents.createOrReplaceTempView("components")
      val his = spark.sql(
        """
          | SELECT b.id as userId, a.COMPID as COMPID, a.FOLLOWCOMPID as FOLLOWCOMPID, FREQ
          | FROM (SELECT USERNAME, COMPID, b.ID as FOLLOWCOMPID, FREQ
          | FROM
          | (SELECT a.userName as userName, b.id as compId, a.followCompName as followCompName, freq
          | FROM history a
          | JOIN components b on a.compName = b.name
          | ) a
          | JOIN components b on a.followCompName = b.name) a
          | JOIN users b
          | ON a.userName = b.uuid
        """.stripMargin)
        .map(row => History(row.getInt(0), row.getInt(1), row.getInt(2), row.getLong(3)))
        .coalesce(parallelism)
        .cache()
      history = Option(his)
      his
    }
  }

}
