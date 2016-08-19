package com.xiyuan

import com.xiyuan.msgCheck.checker.DefaultMsgChecker

/**
  * Created by xiyuan_fengyu on 2016/8/16.
  */
object Example {

  def main(args: Array[String]) {
    println(DefaultMsgChecker.isDirty("嘉年华两厢 防盗锁上了 如何解开嘉年华两厢的 防盗锁上了 如何解开"))
    println(DefaultMsgChecker.isDirty("正网开户亚洲总代理"))
  }

}
