package com.xiyuan.msgCheck.filter.impl.letterMatrix.trainAndTest

import com.xiyuan.msgCheck.filter.impl.letterMatrix.model.LetterMatrix
import com.xiyuan.template.util.ClassUtil

/**
  * Created by xiyuan_fengyu on 2016/8/23 14:22.
  */
object TrainModel {

  def main(args: Array[String]) {
    val model = new LetterMatrix
    model.trainDirtyFromFile("D:\\SoftwareForCode\\MyEclipseProject\\CarMsgFilter\\target\\classes\\DirtyMsg.raw")
    model.trainDirtyFromFile("D:\\SoftwareForCode\\MyEclipseProject\\CarMsgFilter\\target\\classes\\NormalMsg.raw")
    model.saveModel(ClassUtil.classRoot + "/LetterMatrix.mdl")
  }

}