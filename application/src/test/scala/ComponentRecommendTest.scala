import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.manlier.srapp.constraints.Schemas.{HBaseComponentSchema, HBaseHistorySchema, HBaseUsersSchema}
import org.apache.phoenix.spark._
import org.manlier.recommend.LinearItemCFModel
import org.manlier.recommend.entities.{History, UserCompPair}

object ComponentRecommendTest {

  val spark:SparkSession = SparkSession.builder()
    .master("local")
    .getOrCreate()

  import spark.implicits._

  val itemCFModel: LinearItemCFModel = new LinearItemCFModel(spark)

  val hbaseConfig: Configuration = HBaseConfiguration.create()

  def main(args: Array[String]): Unit = {
    val history = getHistory.map(row => History(row.getInt(0), row.getInt(1), row.getInt(2), row.getLong(3)))
      .coalesce(1)
      .cache()

    itemCFModel.fit(history).recommendForUser(spark.createDataset(Seq(UserCompPair(0, 101))), 3)
      .show(20, truncate = false)

  }

  private def loadHistory(): DataFrame = {
    spark.sqlContext.phoenixTableAsDataFrame(HBaseHistorySchema.TABLE_NAME
      , Seq(HBaseHistorySchema.USER_QUALIFIER
        , HBaseHistorySchema.COMP_QUALIFIER
        , HBaseHistorySchema.FOLLOW_COMP_QUALIFIER
        , HBaseHistorySchema.FREQ_QUALIFIER)
      , conf = hbaseConfig)
      .coalesce(1)
  }

  private def loadUsers(): DataFrame = {
    spark.sqlContext.phoenixTableAsDataFrame(HBaseUsersSchema.TABLE_NAME
      , Seq(HBaseUsersSchema.UUID_QUALIFIER
        , HBaseUsersSchema.ID_QUALIFIER)
      , conf = hbaseConfig)
      .coalesce(1)
  }

  private def loadComponents(): DataFrame = {
    spark.sqlContext.phoenixTableAsDataFrame(HBaseComponentSchema.TABLE_NAME
      , Seq(HBaseComponentSchema.NAME_QUALIFIER
        , HBaseComponentSchema.ID_QUALIFIER)
      , conf = hbaseConfig)
      .coalesce(1)
  }

  private def getHistory: DataFrame = {
    loadHistory().createOrReplaceTempView("history")
    loadUsers().createOrReplaceTempView("users")
    loadComponents().createOrReplaceTempView("components")
    spark.sql(
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
  }

}
