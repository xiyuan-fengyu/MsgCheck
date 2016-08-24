package com.xiyuan.msgCheck.filter.impl.letterMatrix.model

import java.io._
import java.util.zip.{Deflater, DeflaterOutputStream, Inflater, InflaterInputStream}

import scala.collection.mutable

/**
  * Created by xiyuan_fengyu on 2016/8/23.
  */
class LetterMatrix {

  private val matrix = mutable.HashMap[String, NormalAndDirty]()

  private var dirtyMatrixTotal = 0

  private var normalMatrixTotal = 0

  private def train(label: Int, dataPath: String): Unit = {
    val in  = new FileInputStream(dataPath)
    val reader = new BufferedReader(new InputStreamReader(in, "utf-8"))
    var line = reader.readLine()
    var count = 0
    while (line != null) {
      val msgItem = new MsgItem(line)
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
              normalAndDirty.normal += 1
            }
            else {
              normalAndDirty.dirty += 1
            }
          }
        }
      }

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

  def trainNormal(dataPath: String): Unit ={
    train(0, dataPath)
  }

  def trainDirty(dataPath: String): Unit ={
    train(1, dataPath)
  }

  def loadModel(modelPath: String): Unit = {
    val in = new FileInputStream(modelPath)
    loadModel(in)
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
            normalScore += temp.normal
            dirtyScore += temp.dirty
          }
        }
      }
    }

    normalScore = normalScore / normalMatrixTotal
    dirtyScore = dirtyScore / dirtyMatrixTotal
    (normalScore, dirtyScore)
  }

  def deleteKey(key: String): Unit ={
    matrix.remove(key)
  }

}
