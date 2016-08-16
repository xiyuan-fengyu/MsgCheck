package com.xiyuan.msgCheck.checker

import com.xiyuan.msgCheck.filter.Filter
import com.xiyuan.msgCheck.filter.impl.{DirtyRegexFilter, KeyLetterFilter, ChineseFilter, LengthFilter}

/**
  * Created by xiyuan_fengyu on 2016/8/16.
  */
object DefaultMsgChecker {

  private val checker = new MsgChecker

  checker.setFilters(Array[Filter](
    new LengthFilter(5),
    new ChineseFilter(0.6, 2),
    new KeyLetterFilter,
    new DirtyRegexFilter

  ))

  def isDirty(str: String): Boolean = {
    checker.isDirty(str)
  }

}
