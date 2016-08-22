package com.xiyuan.msgCheck.filter.impl

import java.io._

import com.xiyuan.template.util.ClassUtil

import scala.collection.mutable
import scala.io.Source

/**
  * Created by xiyuan_fengyu on 2016/8/16.
  */
class LetterCount(val name: String) {

  private val letterCount = new mutable.HashMap[Char, Int]()

  private var letterCountTotal: Double = 0

  private val startChinese = '\u4e00'

  private val endChinese = '\u9fa5'

  private var changeIndex = 0

  private val changeIndexToSave = 100

  private val maxImportantIndex: Double = 50

  load()

  def score(str: String): Double = {
    var tempScore: Double = 0
    if (str != null && str.nonEmpty) {
      for (i <- str.indices) {
        val c = str.charAt(i)
        if (c >= startChinese && c <= endChinese && letterCount.contains(c)) {
          tempScore += letterCount(c) * math.exp(- i / maxImportantIndex)
        }
      }
    }
    tempScore / letterCountTotal
  }

  def lettersIncrease(str: String): Unit = {
    if (str != null && str.nonEmpty) {
      for (i <- str.indices; if i < maxImportantIndex) {
        val c = str.charAt(i)
        if (c >= startChinese && c <= endChinese) {
          letterIncrease(c)
        }
      }
    }
  }

  def letterIncrease(c: Char): Unit = {
    if (!letterCount.contains(c)) {
      letterCount += c -> 1
    }
    else {
      letterCount(c) += 1
    }
    letterCountTotal += 1

    if (changeIndex > changeIndexToSave) {
      save()
      changeIndex = 0
    }
    else {
      changeIndex += 1
    }
  }

  def print(): Unit ={
    letterCount.foreach(println)
  }

  def save(): Unit = {
    val letterCountOut = new FileOutputStream(ClassUtil.classRoot + s"/letterCount_$name.data")
    var shouldFlush = 0
    letterCount.toArray.sortWith(_._2 > _._2).foreach(item => {
      letterCountOut.write((item._1 + "," + item._2 + "\n").getBytes("utf-8"))
      if (shouldFlush > 500) {
        letterCountOut.flush()
        shouldFlush = 0
      }
      else {
        shouldFlush += 1
      }
    })
    letterCountOut.flush()
    letterCountOut.close()
  }

  def load(): Unit = {
    val saveFile = new File(ClassUtil.classRoot + s"/letterCount_$name.data")

    if (!saveFile.exists()) {
      //从jar中提取data文件，并存储到class目录下
      val in = this.getClass.getClassLoader.getResourceAsStream(s"letterCount_$name.data")
      if (in != null) {
        val reader = new BufferedReader(new InputStreamReader(in, "utf-8"))
        val out = new FileOutputStream(saveFile)
        val writer = new OutputStreamWriter(out, "utf-8")

        var line = reader.readLine()
        while (line != null) {
          writer.write(line + "\n")
          writer.flush()
          line = reader.readLine()
        }

        in.close()
        reader.close()

        out.close()
        writer.close()
      }
    }

    if (saveFile.exists()) {
      Source.fromFile(saveFile, "utf-8").getLines().foreach(line => {
        val split = line.split(",")
        val c = split(0).charAt(0)
        val count = split(1).toInt
        letterCount += c -> count
        letterCountTotal += count
      })
    }
  }

}
