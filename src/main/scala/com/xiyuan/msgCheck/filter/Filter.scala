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

  def isDirty(str: String): Boolean

}
