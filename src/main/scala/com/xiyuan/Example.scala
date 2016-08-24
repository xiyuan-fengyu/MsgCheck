package com.xiyuan

import com.xiyuan.msgCheck.checker.DefaultMsgChecker
import com.xiyuan.msgCheck.filter.impl.letterMatrix.model.LetterMatrix
import com.xiyuan.template.log.XYLog

/**
  * Created by xiyuan_fengyu on 2016/8/16.
  */
object Example {

  def main(args: Array[String]) {
    val str = "长安镇长盛小姐找可以联系包夜电话信息"
    println(DefaultMsgChecker.isDirty(str))

    val letterMatrix = new LetterMatrix()
    letterMatrix.loadModel(this.getClass.getClassLoader.getResourceAsStream("LetterMatrix.mdl"))
    XYLog.d("(normal, dirty) = \t", letterMatrix.score(str).toString)
  }

}
