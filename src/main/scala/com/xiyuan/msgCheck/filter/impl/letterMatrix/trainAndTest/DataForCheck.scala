package com.xiyuan.msgCheck.filter.impl.letterMatrix.trainAndTest

import java.io.FileOutputStream

import com.xiyuan.msgCheck.filter.impl.letterMatrix.model.LetterMatrix
import com.xiyuan.template.util.{ClassUtil, RandomUtil}

import scala.io.Source

/**
  * Created by xiyuan_fengyu on 2016/8/24.
  * 生成keras神经网络的训练数据，用来获取score.normal / score.dirty和wilcoxon两种结果判定垃圾正常的阈值
  */
object DataForCheck {

  def main(args: Array[String]) {

    val model = new LetterMatrix
    model.loadModel(ClassUtil.classRoot + "/LetterMatrix.mdl")
    println("模型加载完毕")

    val out = new FileOutputStream(ClassUtil.classRoot + "/check.data")

    val interval = 100
    var lineIndex = 0
    var randomIndex = RandomUtil.randomBetween(0, interval)
    Source.fromFile("D:\\SoftwareForCode\\MyEclipseProject\\CarMsgFilter\\target\\classes\\NormalMsg.raw").getLines().foreach(line => {
      if (line.nonEmpty) {
        if (lineIndex >= randomIndex) {
          val score = model.score(line)
          val wil = model.wilcoxon(line)
          val temp = score._1 / math.max(score._2, 0.000000000001)
          out.write(("0 " + temp + " " + wil + "\t\t" + line.substring(0, math.min(20, line.length)) + "\n").getBytes("utf-8"))

          randomIndex = RandomUtil.randomBetween(lineIndex + 1, lineIndex + interval)

          if (lineIndex % 1000 == 999) {
            out.flush()
          }
        }
        lineIndex += 1
      }
    })

    lineIndex = 0
    randomIndex = RandomUtil.randomBetween(0, interval)
    Source.fromFile("D:\\SoftwareForCode\\MyEclipseProject\\CarMsgFilter\\target\\classes\\DirtyMsg.raw").getLines().foreach(line => {
      if (line.nonEmpty) {
        if (lineIndex >= randomIndex) {
          val score = model.score(line)
          val wil = model.wilcoxon(line)
          val temp = score._1 / math.max(score._2, 0.000000000001)
          out.write(("1 " + temp + " " + wil + "\t\t" + line.substring(0, math.min(20, line.length)) + "\n").getBytes("utf-8"))

          randomIndex = RandomUtil.randomBetween(lineIndex + 1, lineIndex + interval)

          if (lineIndex % 1000 == 999) {
            out.flush()
          }
        }
        lineIndex += 1
      }
    })


    out.flush()
    out.close()
  }

}
