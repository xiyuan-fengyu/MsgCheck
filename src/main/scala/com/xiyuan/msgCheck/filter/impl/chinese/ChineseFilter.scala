package com.xiyuan.msgCheck.filter.impl.chinese

import com.xiyuan.msgCheck.filter.Filter
import com.xiyuan.msgCheck.values.StaticValues

/**
  * Created by xiyuan_fengyu on 2016/8/16.
  * 以下情况视为垃圾信息
  * 正常中文占比低于 minPercent
  * 非正常字符超过 maxSpecial
  */
class ChineseFilter(minPercent: Double, maxSpecial: Int) extends Filter {

  override def check(str: String): Boolean = {
    if (str == null || str.isEmpty) {
      true
    }
    else {
      var chineseCount = 0
      var specialCount = 0
      str.foreach(c => {
        if (c >= StaticValues.startChinese && c <= StaticValues.endChinese) {
          chineseCount += 1
        }
        else if (!StaticValues.chineseMarks.contains(c) && (c < StaticValues.normalAsciiStart || c > StaticValues.normalAsciiEnd)) {
          specialCount += 1
        }
      })

      chineseCount / str.length.toDouble < minPercent || specialCount > maxSpecial
    }
  }

}
