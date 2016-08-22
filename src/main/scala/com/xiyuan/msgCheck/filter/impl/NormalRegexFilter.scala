package com.xiyuan.msgCheck.filter.impl

/**
  * Created by xiyuan_fengyu on 2016/8/16.
  * 通过正则表达式来判断是否为正常信息
  */
class NormalRegexFilter extends RegexFilter("Normal") {

  override final def check(str: String): Boolean = {
    val result = super.check(str)
    if (result) {
      needMoreCheck = false
    }
    else {
      needMoreCheck = true
    }
    false
  }

}
