package com.xiyuan.msgCheck.filter.impl.regex

/**
  * Created by xiyuan_fengyu on 2016/8/16.
  * 通过正则表达式来判断是否为垃圾信息
  */
class DirtyRegexFilter extends RegexFilter("Dirty") {

  override final def check(str: String): Boolean = {
    super.check(str)
  }

}
