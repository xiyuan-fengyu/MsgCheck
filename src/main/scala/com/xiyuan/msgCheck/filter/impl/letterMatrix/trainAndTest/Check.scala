package com.xiyuan.msgCheck.filter.impl.letterMatrix.trainAndTest

import com.xiyuan.msgCheck.filter.impl.letterMatrix.model.LetterMatrix
import com.xiyuan.template.util.{ClassUtil, RandomUtil}

import scala.io.Source

/**
  * Created by xiyuan_fengyu on 2016/8/24 12:34.
  * 用于确定score.normal / score.dirty 和 wilcoxon的阈值设定是否合理
  */
object Check {

  def main(args: Array[String]) {
    val model = new LetterMatrix
    model.loadModel(ClassUtil.classRoot + "/LetterMatrix.mdl")
    println("模型加载完毕")

    var total = 0
    var error: Double = 0

    val interval = 100
    var lineIndex = 0
    var randomIndex = RandomUtil.randomBetween(0, interval)
    Source.fromFile("D:\\SoftwareForCode\\MyEclipseProject\\CarMsgFilter\\target\\classes\\NormalMsg.raw").getLines().foreach(line => {
      if (line.nonEmpty) {
        total += 1

        if (lineIndex >= randomIndex) {
          val score = model.score(line)
          val wil = model.wilcoxon(line)
          val temp = score._1 / math.max(score._2, 0.000000000001)

          randomIndex = RandomUtil.randomBetween(lineIndex + 1, lineIndex + interval)

          if (temp < 2 || wil < -1.5) {
            error += 1
          }

          if (lineIndex % 1000 == 999) {
            print("\r错误率：" + error + " / " + total + " = " + (error / total))
          }
        }
        lineIndex += 1
      }
    })

    lineIndex = 0
    randomIndex = RandomUtil.randomBetween(0, interval)
    Source.fromFile("D:\\SoftwareForCode\\MyEclipseProject\\CarMsgFilter\\target\\classes\\DirtyMsg.raw").getLines().foreach(line => {
      if (line.nonEmpty) {
        total += 1

        if (lineIndex >= randomIndex) {
          val score = model.score(line)
          val wil = model.wilcoxon(line)
          val temp = score._1 / math.max(score._2, 0.000000000001)

          randomIndex = RandomUtil.randomBetween(lineIndex + 1, lineIndex + interval)

          if (temp >= 2 && wil >= -1.5) {
            error += 1
          }

          if (lineIndex % 1000 == 999) {
            print("\r错误率：" + error + " / " + total + " = " + (error / total))
          }
        }
        lineIndex += 1
      }
    })


  }

}