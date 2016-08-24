package com.xiyuan.msgCheck.filter.impl.letterMatrix

import com.xiyuan.msgCheck.filter.Filter
import com.xiyuan.msgCheck.filter.impl.letterMatrix.model.LetterMatrix

/**
  * Created by xiyuan_fengyu on 2016/8/24.
  */
class LetterMatrixFilter extends Filter {

  private val letterMatrix = new LetterMatrix()
  letterMatrix.loadModel(this.getClass.getClassLoader.getResourceAsStream("LetterMatrix.mdl"))

  /**
    * @param str
    * @return
    */
  override def check(str: String): Boolean = {
    val score = letterMatrix.score(str)
    val temp = score._1 / math.max(score._2, 0.000000000001)
    temp <= 1
  }

}
