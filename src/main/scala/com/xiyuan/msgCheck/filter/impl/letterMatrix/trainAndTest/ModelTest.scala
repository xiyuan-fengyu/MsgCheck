package com.xiyuan.msgCheck.filter.impl.letterMatrix.trainAndTest

import java.util.Scanner

import com.xiyuan.msgCheck.filter.impl.letterMatrix.model.LetterMatrix
import com.xiyuan.template.log.XYLog
import com.xiyuan.template.util.ClassUtil

/**
  * Created by xiyuan_fengyu on 2016/8/23.
  model测试
  */
object ModelTest {

  def main(args: Array[String]) {
    val model = new LetterMatrix
    model.loadModel(ClassUtil.classRoot + "/LetterMatrix.mdl")
    println("模型加载完毕")
    val scanner = new Scanner(System.in)
    var line = scanner.nextLine()
    while (line != "QUIT") {
      if(line.nonEmpty) {
        val score = model.score(line)
        val temp = score._1 / math.max(score._2, 0.000000000001)

        XYLog.d(line + "\n", score._1 + "\t\t" + score._2 + "\t\t" + temp + "\t\t", if (temp >= 1) "正常" else "垃圾", "\n\n")
      }
      line = scanner.nextLine()
    }
  }

}
