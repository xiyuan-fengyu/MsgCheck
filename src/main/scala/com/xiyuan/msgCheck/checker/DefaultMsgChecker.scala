package com.xiyuan.msgCheck.checker

import com.xiyuan.msgCheck.filter.Filter
import com.xiyuan.msgCheck.filter.impl._

/**
  * Created by xiyuan_fengyu on 2016/8/16.
  */
object DefaultMsgChecker {

  private val checker = new MsgChecker

  checker.setFilters(Array[Filter](
    new LengthFilter(5),
    new ChineseFilter(0.3, 2),
    new DirtyRegexFilter,
    new NormalRegexFilter,
    new KeyLetterFilter

  ))

  def addNormalRegexes(strs: Array[String]): Unit = {
    val filter = checker.getFilter(classOf[NormalRegexFilter])
    if (filter != null) {
      filter.addRegexes(strs)
    }
  }

  def addDirtyRegexes(strs: Array[String]): Unit = {
    val filter = checker.getFilter(classOf[DirtyRegexFilter])
    if (filter != null) {
      filter.addRegexes(strs)
    }
  }

  def isDirty(str: String): Boolean = {
    checker.isDirty(str)
  }

}
