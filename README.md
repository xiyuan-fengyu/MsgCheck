# MsgCheck
敏感信息，垃圾信息，黄赌毒信息判断


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
