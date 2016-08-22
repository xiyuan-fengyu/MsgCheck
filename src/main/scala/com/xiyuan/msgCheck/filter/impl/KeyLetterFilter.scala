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

  override def check(str: String): Boolean = {
    val normalScore = normalLetter.score(str)
    val dirtyScore = dirtyLetter.score(str)

    if (normalScore > dirtyScore) {
      false
    }
    else {
      if (dirtyScore >= minScoreToUpdateLetterCount && dirtyScore / normalScore >= 3.0) {
        dirtyLetter.lettersIncrease(str)
      }
      true
    }
  }

  override def afterNextCheck(str: String, isDirty: Boolean): Unit = {
    if (isDirty) {
      dirtyLetter.lettersIncrease(str)
    }
    else {
      normalLetter.lettersIncrease(str)
    }
  }

}
