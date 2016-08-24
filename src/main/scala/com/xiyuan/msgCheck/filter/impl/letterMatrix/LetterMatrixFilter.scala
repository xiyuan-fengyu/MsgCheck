package com.xiyuan.msgCheck.filter.impl.letterMatrix

import com.xiyuan.msgCheck.filter.Filter
import com.xiyuan.msgCheck.filter.impl.letterMatrix.model.LetterMatrix

/**
  * Created by xiyuan_fengyu on 2016/8/24.
  * scoreRatioMin 正常分和垃圾分比值的阈值
  * wilcoxonMin 秩和校验的阈值
  * 目前分别为2.0和-1.5，测试出来的错误率为366.0 / 12267547 = 2.9834815387297886E-5
  */
class LetterMatrixFilter(val scoreRatioMin: Double = 2.0, val wilcoxonMin: Double = -1.5) extends Filter {

  private val letterMatrix = new LetterMatrix()
  letterMatrix.loadModel(this.getClass.getClassLoader.getResourceAsStream("LetterMatrix.mdl"))

  /**
    * @param str
    * @return
    */
  override def check(str: String): Boolean = {
    val score = letterMatrix.score(str)
    val temp = score._1 / math.max(score._2, 0.000000000001)
    val wil = letterMatrix.wilcoxon(str)
    temp < scoreRatioMin || wil < wilcoxonMin
  }

}
