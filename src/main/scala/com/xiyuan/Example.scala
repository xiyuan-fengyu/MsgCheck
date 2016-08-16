package com.xiyuan

import com.xiyuan.msgCheck.checker.DefaultMsgChecker

/**
  * Created by xiyuan_fengyu on 2016/8/16.
  */
object Example {

  def main(args: Array[String]) {
    println(DefaultMsgChecker.isDirty("高港区口岸小姐找可以联系包夜电话信息"))
    println(DefaultMsgChecker.isDirty("830suncity"))
    println(DefaultMsgChecker.isDirty("服\uE708务\uE708阜\uE708平\uE708找\uE708一\uE708妹\uE708子\uE708"))
    println(DefaultMsgChecker.isDirty("长沙洞井小&#x59D0;找可以上门多少钱电话信息"))
  }

}
