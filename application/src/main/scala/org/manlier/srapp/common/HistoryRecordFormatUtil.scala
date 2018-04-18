package org.manlier.srapp.common

import java.util.regex.Pattern

object HistoryRecordFormatUtil {

  private val pattern: Pattern = Pattern.compile("\\w+,\\w+,\\w+")

  def isValidate(recordStr: String): Boolean = {
    pattern.matcher(recordStr).matches()
  }

}
