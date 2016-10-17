package com.xiyuan.msgCheck.filter.impl.letterMatrix.trainAndTest

import java.util.Scanner

import com.xiyuan.msgCheck.filter.impl.letterMatrix.model.LetterMatrix
import com.xiyuan.template.log.XYLog
import com.xiyuan.template.util.ClassUtil

/**
  * Created by xiyuan_fengyu on 2016/8/23.
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
        if (line.startsWith("key ")) {
          val key = line.substring(4)
          XYLog.d(key, "\n", model.showKey(key), "\n\n")
        }
        else if (line.startsWith("keys ")) {
          val str = line.substring(5)
          XYLog.d(str + " 的关键词占比信息：\n")
          model.keyInfosForStr(str).foreach(item => {
            println(item._1 + "\t\t" + item._2 + "\t\t" + item._3)
          })
          println("\n\n")
        }
        else if (line.startsWith("delete ")) {
          val key = line.substring(7)
          model.deleteKey(key)
          XYLog.d(key + " 已从model中删除", "\n\n")
        }
        else {
          val score = model.score(line)
          val wil = model.wilcoxon(line)
          val temp = score._1 / math.max(score._2, 0.000000000001)

          XYLog.d(line + "\n", "normal = " + score._1 + "\t\tdirty = " + score._2 + "\t\tnormal / dirty = " + temp + "\t\t","wil=" + wil + "\t\t", if (temp >= 2 && wil >= -1.5) "正常" else "垃圾", "\n\n")
        }
      }
      line = scanner.nextLine()
    }
  }

}
