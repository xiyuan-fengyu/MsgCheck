package com.xiyuan.msgCheck.filter.impl

import com.xiyuan.msgCheck.filter.Filter

/**
  * Created by xiyuan_fengyu on 2016/8/16.
  * 通过关键汉字来判断是否未垃圾信息
  */
class KeyLetterFilter extends Filter {

  private val dirtyLetter = new LetterCount("Dirty")
  private val normalLetter = new LetterCount("Normal")

  /**
    * 如果分数大于等于这个值就更新LetterCount
    */
  private val minScoreToUpdateLetterCount = 0.03

  private var needMoreCheck = false

  /**
    * 如果检测为正常消息，是否需要传递给之后的过滤器做检测
    *
    * @return
    */
  override def needNextCheck: Boolean = needMoreCheck

  override def check(str: String): Boolean = {
    val normalScore = normalLetter.score(str)
    val dirtyScore = dirtyLetter.score(str)

    if (normalScore > dirtyScore) {
      if (dirtyScore == 0 || normalScore / dirtyScore >= 1.5) {
        normalLetter.lettersIncrease(str)
        needMoreCheck = false
      }
      else {
        needMoreCheck = true
      }

      false
    }
    else {
      if (normalScore == 0 || dirtyScore / normalScore >= 1.5) {
        dirtyLetter.lettersIncrease(str)
        needMoreCheck = false
        true
      }
      else {
        needMoreCheck = true
        false
      }
    }

  }

}
