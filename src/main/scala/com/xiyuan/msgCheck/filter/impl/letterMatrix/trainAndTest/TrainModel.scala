package com.xiyuan.msgCheck.filter.impl.letterMatrix.trainAndTest

import com.xiyuan.msgCheck.filter.impl.letterMatrix.model.LetterMatrix
import com.xiyuan.template.util.ClassUtil

/**
  * Created by xiyuan_fengyu on 2016/8/23 14:22.
  训练数据格式说明：
  训练数据包括垃圾信息和正常信息，分别放在两个文件中，每行一条消息，一条消息中不能有\n换行符
  垃圾消息和正常消息的数量必须相当，且总数最好能够上1000万，这样训练出来的效果会非常好

  垃圾消息训练文件内容示例：
  冕宁小姐找可以联系包夜电话信息
  南城小姐真的找上门多少钱电话
  徐州铜山区柳新镇保健按摩美女全套上门服务
  看泊头市白光麻将牌专用透-视隐形眼镜看泊头市白光麻将牌专用透-视隐形眼镜
  网上查移动通话清单网上查移动通话清单

  正常消息训练文件内容示例：
  日产逍客保修年限%2f历程是多少?
  逍客最低价上路多少钱%3f?
  尼桑逍客15000公里保养大概多少钱?
  尼桑逍客最高多少钱?
  日产逍客铝合金轮毂多少钱一只?
  逍客的一年保养费多少?
  逍客涉水深度是多少？?
  逍客最高开多少码?
  */
object TrainModel {

  def main(args: Array[String]) {
    val model = new LetterMatrix
    model.trainDirty("D:\\SoftwareForCode\\MyEclipseProject\\CarMsgFilter\\target\\classes\\DirtyMsg.raw")
    model.trainNormal("D:\\SoftwareForCode\\MyEclipseProject\\CarMsgFilter\\target\\classes\\NormalMsg.raw")
    model.saveModel(ClassUtil.classRoot + "/LetterMatrix.mdl")
  }

}