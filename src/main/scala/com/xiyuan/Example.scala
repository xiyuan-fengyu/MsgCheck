package com.xiyuan

import com.xiyuan.msgCheck.checker.DefaultMsgChecker
import com.xiyuan.msgCheck.filter.impl.LetterCount
import com.xiyuan.template.log.XYLog

/**
  * Created by xiyuan_fengyu on 2016/8/16.
  */
object Example {

  def main(args: Array[String]) {
    val str = "马鞍山哪里有红灯区"
    println(DefaultMsgChecker.isDirty(str))

    val dirtyCount = new LetterCount("Dirty")
    val normalCount = new LetterCount("Normal")
    XYLog.d("dirtyScore\t", dirtyCount.score(str).toString)
    XYLog.d("normalScore\t", normalCount.score(str).toString)


  }

}
