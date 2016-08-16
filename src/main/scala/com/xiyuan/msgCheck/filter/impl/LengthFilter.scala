package com.xiyuan.msgCheck.filter.impl

import com.xiyuan.msgCheck.filter.Filter

/**
  * Created by xiyuan_fengyu on 2016/8/16.
  * 长度低于 minLen 的视为垃圾信息
  */
class LengthFilter(minLen: Int) extends Filter {

  override def isDirty(str: String): Boolean = {
    if (str == null || str.length < minLen) {
      true
    }
    else {
      nextCheck(str)
    }
  }

}
