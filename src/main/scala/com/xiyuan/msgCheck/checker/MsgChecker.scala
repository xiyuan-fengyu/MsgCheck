package com.xiyuan.msgCheck.checker

import com.xiyuan.msgCheck.filter.Filter

import scala.collection.mutable.ArrayBuffer

/**
  * Created by xiyuan_fengyu on 2016/8/16.
  */
class MsgChecker {

  private val filters = new ArrayBuffer[Filter]()

  def getFilter[T](clazz: Class[T]): T = {
    val finder = filters.find(_.getClass == clazz)
    if (finder.nonEmpty) {
      finder.get.asInstanceOf[T]
    }
    else null.asInstanceOf[T]
  }

  def addFilter(filter: Filter): Unit = {
    if (filters.nonEmpty) {
      filters.last.setNext(filter)
    }
    filters += filter
  }

  def setFilters(filters: Array[Filter]): Unit = {
    this.filters.clear()
    if (filters !=  null && filters.nonEmpty) {
      filters.foreach(addFilter)
    }
  }

  def isDirty(str: String): Boolean = {
    if (filters.isEmpty) {
      false
    }
    else {
      val tempStr = str.toLowerCase.replaceAll("\n", "")
      filters.head.isDirty(tempStr)
    }
  }

}
