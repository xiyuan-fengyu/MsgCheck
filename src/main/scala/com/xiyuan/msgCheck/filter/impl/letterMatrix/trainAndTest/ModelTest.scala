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
    println("模型加载中...")
    model.loadModel(ClassUtil.classRoot + "/LetterMatrix.mdl")
    println("模型加载完毕\n")
    println(
      """使用方式：
        |退出应用：QUIT
        |查看单个关键词的信息： key 关键词
        |查看一条语句中的所有关键词的信息： keys 语句
        |删除一个关键词： delete 关键词
        |训练一条语句（其中rate为训练倍率，是一个正整数）： tarin [normal|dirty] rate 语句
        |保存模型： save 保存路径
        |判定一条语句的性质： 语句（不符合上述情况的语句）
      """.stripMargin)
    val scanner = new Scanner(System.in)
    var line = scanner.nextLine().trim
    while (line != "QUIT") {
      if(line.nonEmpty) {
        if (line.startsWith("key ")) {
          val key = line.substring(4)
          XYLog.d(key, "\n", model.showKey(key), "\n\n")
        }
        else if (line.startsWith("keys ")) {
          val str = line.substring(5)
          XYLog.d(str + " 的关键词占比信息：\n")
          keys(model, str)
          println("\n\n")
        }
        else if (line.startsWith("delete ")) {
          val key = line.substring(7)
          model.deleteKey(key)
          XYLog.d(key + " 已从model中删除", "\n\n")
        }
        else if (line.startsWith("train ")) {
          val reg = "train (normal|dirty) ([0-9]+) (.+)".r
          try {
            val reg(msgType, rate, sentance) = line
            val rateI = rate.toInt
            XYLog.d("训练前，" + sentance + " 的关键词占比信息：\n")
            keys(model, sentance)
            check(model, sentance)
            if (msgType == "normal") {
              model.trainNormal(sentance, rateI)
            }
            else {
              model.trainDirty(sentance, rateI)
            }
            XYLog.d("训练后，" + sentance + " 的关键词占比信息：\n")
            keys(model, sentance)
            check(model, sentance)
            XYLog.d("模型训练完成", "\n\n")
          }
          catch {
            case e: Exception =>
              XYLog.d("train命令有误", "\n\n")
          }
        }
        else if (line.startsWith("save ")) {
          try {
            val path = line.substring(5)
            XYLog.d("正在保存模型...")
            model.saveModel(path)
            XYLog.d("模型保存成功，保存路径：" + path, "\n\n")
          }
          catch {
            case e: Exception =>
              XYLog.d("模型保存失败", "\n\n")
          }
        }
        else {
          check(model, line)
        }
      }
      line = scanner.nextLine().trim
    }
  }

  private def keys(model: LetterMatrix, sentense: String): Unit = {
    model.keyInfosForStr(sentense).foreach(item => {
      println(item._1 + "\t\t" + item._2 + "\t\t" + item._3)
    })
  }

  private def check(model: LetterMatrix, sentense: String): Unit = {
    val score = model.score(sentense)
    val wil = model.wilcoxon(sentense)
    val temp = score._1 / math.max(score._2, 0.000000000001)

    XYLog.d(sentense + "\n", "normal = " + score._1 + "\t\tdirty = " + score._2 + "\t\tnormal / dirty = " + temp + "\t\t","wil=" + wil + "\t\t", if (temp >= 2 && wil >= -1.5) "正常" else "垃圾", "\n\n")
  }

}
