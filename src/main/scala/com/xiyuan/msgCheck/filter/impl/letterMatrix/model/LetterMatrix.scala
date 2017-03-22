package com.xiyuan.msgCheck.filter.impl.letterMatrix.model

import java.io._
import java.util.zip.{Deflater, DeflaterOutputStream, Inflater, InflaterInputStream}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * Created by xiyuan_fengyu on 2016/8/23.
  */
class LetterMatrix {

  private val matrix = mutable.HashMap[String, NormalAndDirty]()

  private var dirtyMatrixTotal = 0

  private var normalMatrixTotal = 0

  private def train(label: Int, sentence: String, rate: Int): Unit = {
    val msgItem = new MsgItem(sentence)
    if (msgItem.chars.length > 1) {
      val len = msgItem.chars.length
      for (i <- msgItem.chars.indices) {
        for (j <- 1 to 3; if i + j < len) {
          val x = msgItem.chars(i)
          val y = msgItem.chars(i + j)
          val key = x.toString + "*" * (j - 1) + y

          val normalAndDirty: NormalAndDirty = if (!matrix.contains(key)) {
            val temp = new NormalAndDirty(0, 0)
            matrix += key -> temp
            temp
          }
          else matrix(key)

          if (label == 0) {
            normalAndDirty.normal += rate
          }
          else {
            normalAndDirty.dirty += rate
          }
        }
      }
    }

  }

  private def trainFromFile(label: Int, dataPath: String, rate: Int): Unit = {
    val in  = new FileInputStream(dataPath)
    val reader = new BufferedReader(new InputStreamReader(in, "utf-8"))
    var line = reader.readLine()
    var count = 0
    while (line != null) {
      train(label, line, rate)

      line = reader.readLine()

      count += 1
      //在循环次数比较多且循环中会创建对象的时候，要注意定时gc
      if (count % 100000 == 99999) {
        print("\r进度：" + count)
        System.gc()
      }
    }
    reader.close()
    in.close()
  }

  def trainNormal(sentence: String, rate: Int = 1): Unit ={
    train(0, sentence, rate)
  }

  def trainDirty(sentence: String, rate: Int = 1): Unit ={
    train(1, sentence, rate)
  }

  def trainNormalFromFile(dataPath: String, rate: Int = 1): Unit ={
    trainFromFile(0, dataPath, rate)
  }

  def trainDirtyFromFile(dataPath: String, rate: Int = 1): Unit ={
    trainFromFile(1, dataPath, rate)
  }

  def loadModel(in: InputStream): Unit = {
    matrix.clear()
    normalMatrixTotal = 0
    dirtyMatrixTotal = 0

    val inflater = new Inflater(true)
    val inflaterIn = new InflaterInputStream(in, inflater, 1024)
    val reader = new BufferedReader(new InputStreamReader(inflaterIn, "utf-8"))
    var line = reader.readLine()
    while (line != null) {
      if (line.nonEmpty) {
        val split = line.split(" ")
        val normal = split(1).toInt
        val dirty = split(2).toInt
        normalMatrixTotal += normal
        dirtyMatrixTotal += dirty
        matrix += split(0) -> new NormalAndDirty(normal, dirty)
      }

      line = reader.readLine()
    }
    reader.close()
    in.close()
  }

  def loadModel(modelPath: String): Unit = {
    val in = new FileInputStream(modelPath)
    loadModel(in)
  }

  def saveModel(modelPath: String): Unit ={
    val out = new FileOutputStream(modelPath)
    val deflater = new Deflater(1, true)
    val deflateOut = new DeflaterOutputStream(out, deflater, 1024)

    var count = 0
    for (item <- matrix) {
      val line = item._1 + " " + item._2.normal + " " + item._2.dirty + "\n"
      deflateOut.write(line.getBytes("utf-8"))

      count += 1
      if (count % 500 == 499) {
        deflateOut.flush()
      }
    }
    deflateOut.flush()
    deflateOut.close()
  }

  def score(str: String): (Double, Double) = {
    val msgItem = new MsgItem(str)
    var normalScore: Double = 0
    var dirtyScore: Double = 0

    val len = msgItem.chars.length
    if (len > 1) {
      for (i <- msgItem.chars.indices) {
        for (j <- 1 to 3; if i + j < len) {
          val x = msgItem.chars(i)
          val y = msgItem.chars(i + j)
          val key = x.toString + "*" * (j - 1) + y

          if (matrix.contains(key)) {
            val temp = matrix(key)
            normalScore += math.pow(temp.normal, 0.5)
            dirtyScore += math.pow(temp.dirty, 0.5)
          }
        }
      }
    }

    normalScore = normalScore / normalMatrixTotal
    dirtyScore = dirtyScore / dirtyMatrixTotal
    (normalScore, dirtyScore)
  }

  def wilcoxon(str: String): Double = {
    if (str.isEmpty) return Int.MinValue

    val msgItem = new MsgItem(str)
    var normalArr = new ArrayBuffer[Int]()
    var dirtyArr = new ArrayBuffer[Int]()

    val len = msgItem.chars.length
    if (len > 1) {
      for (i <- msgItem.chars.indices) {
        for (j <- 1 to 3; if i + j < len) {
          val x = msgItem.chars(i)
          val y = msgItem.chars(i + j)
          val key = x.toString + "*" * (j - 1) + y

          if (matrix.contains(key)) {
            val temp = matrix(key)
            normalArr += temp.normal
            dirtyArr += temp.dirty
          }
        }
      }
    }

    val normalLen = normalArr.length
    val dirtyLen = dirtyArr.length

    if (normalLen + dirtyLen == 0) {
      return Int.MinValue
    }

    val allArr = new ArrayBuffer[MTuple3[Int, Int, Double]](normalLen + dirtyLen)
    normalArr.foreach(item => allArr += new MTuple3[Int, Int, Double](0, item, 0))
    dirtyArr.foreach(item => allArr += new MTuple3[Int, Int, Double](1, item, 0))
    val sortedAllArr = allArr.sortWith(_._2 < _._2)
    val allLen = sortedAllArr.length
    var lastI = 0
    var lastV = sortedAllArr(0)._2
    var curIndexSum = 0
    for (i <- 0 to allLen) {
      if ((i < allLen && sortedAllArr(i)._2 != lastV) || i == allLen) {
        val index = curIndexSum / (i - lastI).toDouble
        for (j <- lastI until i) {
          sortedAllArr(j)._3 = index
        }

        lastI = i
        if (i < allLen) {
          lastV = sortedAllArr(i)._2
        }
        curIndexSum = 0
      }
      curIndexSum += i + 1
    }

    var normalSum: Double = 0
    var dirtySum: Double = 0
    sortedAllArr.foreach(item => {
      if (item._1 == 0) {
        normalSum += item._3
      }
      else {
        dirtySum += item._3
      }
    })

    val t = normalSum
    val result = (t - normalLen * (normalLen + dirtyLen + 1) / 2.0) / math.pow(normalLen * dirtyLen * (normalLen + dirtyLen + 1) / 12.0, 0.5)
    result
  }

  def keyInfosForStr(str: String): Array[Tuple3[String, Double, Double]] = {
    val msgItem = new MsgItem(str)
    var infoArr = new ArrayBuffer[Tuple3[String, Double, Double]]()
    val len = msgItem.chars.length
    if (len > 1) {
      for (i <- msgItem.chars.indices) {
        for (j <- 1 to 3; if i + j < len) {
          val x = msgItem.chars(i)
          val y = msgItem.chars(i + j)
          val key = x.toString + "*" * (j - 1) + y

          if (matrix.contains(key)) {
            val temp = matrix(key)
            infoArr += Tuple3(key, temp.normal / normalMatrixTotal.toDouble, temp.dirty / dirtyMatrixTotal.toDouble)
          }
        }
      }
    }
    infoArr.toArray
  }

  def showKey(key: String): (Int, Double, Int, Double) = {
    if (matrix.contains(key)) {
      val item = matrix(key)
      (item.normal, item.normal / normalMatrixTotal.toDouble, item.dirty, item.dirty / dirtyMatrixTotal.toDouble)
    }
    else (0, 0, 0, 0)
  }

  def deleteKey(key: String): Unit ={
    matrix.remove(key)
  }

}
