package org.manlier.srapp.history

object HistoryDBUtil {

  def generateUpsertSQL(userName: String, compName: String, followCompName: String, freq: Long): String = {
    s"""
       |UPSERT INTO history(userName, compName, followCompName, freq)
       |VALUES ( '$userName' ,'$compName' , '$followCompName' , $freq )
       |ON DUPLICATE KEY
       |UPDATE freq = freq + $freq
              """.stripMargin
  }

}
