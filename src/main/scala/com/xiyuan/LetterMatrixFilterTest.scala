package com.xiyuan

import java.util.Scanner

import com.xiyuan.msgCheck.checker.DefaultMsgChecker
import com.xiyuan.msgCheck.filter.impl.letterMatrix.LetterMatrixFilter
import com.xiyuan.template.log.XYLog

/**
  * Created by xiyuan_fengyu on 2016/10/10 11:27.
  */
object LetterMatrixFilterTest {

  def main(args: Array[String]) {
    XYLog.d("正在加载模型...")
    val letterMatrixFilter = new LetterMatrixFilter()
    XYLog.d("模型加载完毕")
    val scanner = new Scanner(System.in)
    var line = scanner.nextLine()
    while (line != "QUIT") {
      if(line.nonEmpty) {
        XYLog.d(if (letterMatrixFilter.check(line)) "垃圾" else "正常")
      }
      line = scanner.nextLine()
    }
  }

}