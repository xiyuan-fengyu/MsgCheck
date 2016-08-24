package com.xiyuan.msgCheck.filter.impl.letterMatrix.model

import com.xiyuan.msgCheck.filter.impl.letterMatrix.util.CharCheck

import scala.collection.mutable.ArrayBuffer

/**
  * Created by xiyuan_fengyu on 2016/8/23.
  */
class MsgItem(val content: String) {

  val chars: Array[Char] = {
    if (content == null || content.isEmpty) {
      Array[Char]()
    }
    else {
      val buffer = ArrayBuffer[Char]()
      content.toLowerCase.foreach(c => {
        if (CharCheck.isExpected(c)) {
          buffer += c
        }
      })
      buffer.toArray
    }
  }

}
