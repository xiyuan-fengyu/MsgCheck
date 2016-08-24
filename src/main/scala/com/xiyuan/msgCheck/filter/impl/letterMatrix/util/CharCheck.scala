package com.xiyuan.msgCheck.filter.impl.letterMatrix.util

/**
  * Created by xiyuan_fengyu on 2016/8/23.
  */
object CharCheck {

  val startChinese = '\u4e00'

  val endChinese = '\u9fa5'

  def isExpected(c: Char): Boolean = {
    (c >= startChinese && c <= endChinese) || (c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') || c == '.'
  }

}
