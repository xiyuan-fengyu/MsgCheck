package com.xiyuan

import com.xiyuan.msgCheck.checker.DefaultMsgChecker
import com.xiyuan.msgCheck.filter.impl.letterMatrix.LetterMatrixFilter
import com.xiyuan.template.log.XYLog

/**
  * Created by xiyuan_fengyu on 2016/8/16.
  */
object Example {

  def main(args: Array[String]) {
    val str = "怎么查询删除别人的通话记录清单怎么查询删除别人的通话记录清单，请找扣33722577.找他不错，希望帮到大家！\n    皇后人选是先帝钦定，冯家千挑万捡送进来一个冯卉，他的母后更早早存了别的心思。总归是亲娘，没闹出什么，他也只当没那么回事，但冯家想再出来一个冯太后是不可能的。\n    两人的心思都转个不停，章煜却没有话想说，他也还不十分习惯与宋淑好这样身份的人有太多的交流，并找不到共同话题。"
    DefaultMsgChecker.isDirty("")
    println("加载完毕")
    //可以使用多层过滤系统
    val now = System.currentTimeMillis()
    println(DefaultMsgChecker.isDirty(str))
    println("耗时：" + (System.currentTimeMillis() - now))

    //也可以直接使用LetterMatrixFilter来判断，这种实现的正确率已经非常高了，上面的过滤系统最后一层过滤器就是使用的这个
    val letterMatrixFilter = new LetterMatrixFilter()
    XYLog.d(str + "\n" + letterMatrixFilter.check(str))
  }

}
