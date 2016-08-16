package com.xiyuan.template

import com.xiyuan.template.log.XYLog

/**
  * Created by xiyuan_fengyu on 2016/8/9 16:05.
  */
object Test {

  def main(args: Array[String]) {
    XYLog.d(Array("1", "2", 3), "\t数组打印测试", "\tok")
  }

}