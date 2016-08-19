package com.xiyuan.msgCheck.filter

/**
  * Created by xiyuan_fengyu on 2016/8/16.
  */
abstract class Filter {

  private var next: Filter = null

  final def setNext(filter: Filter): Unit = {
    next = filter
  }

  final def nextCheck(str: String): Boolean = {
    if (next != null) {
      next.isDirty(str)
    }
    else false
  }

  final def isDirty(str: String): Boolean = {
    if (check(str)) {
      true
    }
    else if (needNextCheck) {
      val nextCheckResult = nextCheck(str)
      afterNextCheck(str, nextCheckResult)
      nextCheckResult
    }
    else false
  }

  /**
    * @param str
    * @return
    */
  def check(str: String): Boolean

  /**
    * 如果检测为正常消息，是否需要传递给之后的过滤器做检测
    * @return
    */
  def needNextCheck: Boolean = true

  def afterNextCheck(str: String, isDirty: Boolean): Unit ={

  }

}
