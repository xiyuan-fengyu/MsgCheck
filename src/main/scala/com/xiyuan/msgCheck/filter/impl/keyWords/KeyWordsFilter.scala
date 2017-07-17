package com.xiyuan.msgCheck.filter.impl.keyWords

import com.xiyuan.msgCheck.filter.Filter

import scala.io.Source

/**
  * Created by xiyuan_fengyu on 2017/7/17.
  */
class KeyWordsFilter extends Filter {

  private def loadData(dataPath: String): KeyWordsTree = {
    val temp = new KeyWordsTree()
    Source.fromInputStream(classOf[KeyWordsFilter].getClassLoader.getResourceAsStream(dataPath), "UTF-8").getLines().foreach(line => {
      if (!line.isEmpty) {
        temp.addWord(line)
      }
    })
    temp
  }

  protected val blackKeyWordsTree = loadData("blackKeyWords.data")

  protected val whiteKeyWordsTree = loadData("whiteKeyWords.data")

  /**
    * @param str
    * @return
    */
  override def check(str: String): Boolean = {
    val w = whiteKeyWordsTree.firstKeyWord(str)
    val b = blackKeyWordsTree.firstKeyWord(str)
    if (w == null) {
      b != null
    }
    else {
      b != null && !w.contains(b)
    }
  }
}
