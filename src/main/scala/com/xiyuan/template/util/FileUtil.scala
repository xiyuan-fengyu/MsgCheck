package com.xiyuan.template.util

import java.io.{File, FileOutputStream}

import scala.io.Source

/**
  * Created by xiyuan_fengyu on 2016/4/21.
  */
object FileUtil {

  def writeToFile(content: String, file: File): Unit = {
    writeToFile(content, file, false)
  }

  def appendToFile(content: String, file: File): Unit = {
    writeToFile(content, file, true)
  }

  private[this] def writeToFile(content: String, file: File, isAppend: Boolean) = {
    var out: FileOutputStream = null
    try {
      if(!file.exists()) {
        file.createNewFile()
      }
      out = new FileOutputStream(file, isAppend)
      out.write(content.getBytes("UTF-8"))
      out.flush()
    }
    catch {
      case e: Exception => e.printStackTrace()
    }
    finally {
      try {
        if(out != null) {
          out.close()
        }
      }
      catch {
        case e: Exception => e.printStackTrace()
      }
    }
  }

  def readFromFile(file: File): String = {
    val contentBld = new StringBuilder
    Source.fromFile(file, "UTF-8").getLines().toArray.foreach(line => contentBld.append(line).append('\n'))
    contentBld.toString()
  }

}
