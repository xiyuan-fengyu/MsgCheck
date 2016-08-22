package com.xiyuan

import com.xiyuan.msgCheck.checker.DefaultMsgChecker
import com.xiyuan.msgCheck.filter.impl.LetterCount
import com.xiyuan.template.log.XYLog

/**
  * Created by xiyuan_fengyu on 2016/8/16.
  */
object Example {

  def main(args: Array[String]) {
    val str = "2013款的上海通用别克凯越倒车影像时有时无是什么原因引起？"
    println(DefaultMsgChecker.isDirty(str))

    val dirtyCount = new LetterCount("Dirty")
    val normalCount = new LetterCount("Normal")
    XYLog.d("dirtyScore\t", dirtyCount.score(str).toString)
    XYLog.d("normalScore\t", normalCount.score(str).toString)

    //待训练训练句子
//    洗浴桑拿按摩
//    三公高科技赌具
//    铅弹模具
//    网络推广
//    透视麻将眼镜


  }

}
