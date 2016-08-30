# MsgCheck
敏感信息，垃圾信息，黄赌毒信息判断

<br>
示例：
```scala
package com.xiyuan

import com.xiyuan.msgCheck.checker.DefaultMsgChecker
import com.xiyuan.msgCheck.filter.impl.letterMatrix.LetterMatrixFilter
import com.xiyuan.template.log.XYLog

/**
  * Created by xiyuan_fengyu on 2016/8/16.
  */
object Example {

  def main(args: Array[String]) {
    val str = "长安镇长盛小姐找可以联系包夜电话信息"

    //可以使用多层过滤系统
    println(DefaultMsgChecker.isDirty(str))

    //也可以直接使用LetterMatrixFilter来判断，这种实现的正确率已经非常高了，上面的过滤系统最后一层过滤器就是使用的这个
    val letterMatrixFilter = new LetterMatrixFilter()
    XYLog.d(str + "\n" + letterMatrixFilter.check(str))
  }

}
```
<br>
<br>
LetterMatrixFilter的核心组件为LetterMatrix，可以通过 [这里](https://github.com/xiyuan-fengyu/MsgCheck/blob/master/src/main/scala/com/xiyuan/msgCheck/filter/impl/letterMatrix/trainAndTest/ModelTest.scala) 来体验这个组件，这个例子支持三种输入：<br>
key {content}   //用于查看某个key的垃圾占比和正常占比,key的几种形式：AB,A*B,A**B,分别表示A和B相邻，A和B中间隔开一个字符，A和B中间隔开两个字符<br>
delete {key}    //临时删除一个key，如果需要从模型中永久删除这个key，需要在删除后save模型<br>
{content}       //判断一句话是否为垃圾信息<br>
<br>
<br>
LetterMatrix这个模型的训练可以参考 [这里](https://github.com/xiyuan-fengyu/MsgCheck/blob/master/src/main/scala/com/xiyuan/msgCheck/filter/impl/letterMatrix/trainAndTest/TrainModel.scala)<br>
训练数据建议垃圾信息和正常信息各500万条<br>