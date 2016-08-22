package com.xiyuan.msgCheck.filter.impl

import java.io._

import com.xiyuan.msgCheck.filter.Filter
import com.xiyuan.template.util.ClassUtil

import scala.collection.mutable.ArrayBuffer
import scala.io.Source

/**
  * Created by xiyuan_fengyu on 2016/8/22.
  */
class RegexFilter(val name: String) extends Filter {

  private val regexes = new ArrayBuffer[String]()

  protected var needMoreCheck = true

  def addRegexes(strs: Array[String]): Unit = {
    regexes ++= strs
    save()
  }

  override def check(str: String): Boolean = {
    if (regexes.isEmpty) {
      initRegexes()
    }
    regexes.exists(str.matches)
  }

  override def needNextCheck: Boolean = needMoreCheck

  def initRegexes(): Unit = {
    val regexsFile = new File(ClassUtil.classRoot + s"/${name}Regexes.data")

    if (!regexsFile.exists()) {
      //从jar中提取data文件，并存储到class目录下
      val in = this.getClass.getClassLoader.getResourceAsStream(s"${name}Regexes.data")
      if (in != null) {
        val reader = new BufferedReader(new InputStreamReader(in, "utf-8"))
        val out = new FileOutputStream(regexsFile)
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

    if (regexsFile.exists()) {
      Source.fromFile(regexsFile, "utf-8").getLines().foreach(line => {
        if (line.nonEmpty) {
          regexes += line
        }
      })
    }

  }

  def save(): Unit = {
    val out = new FileOutputStream(ClassUtil.classRoot + s"/${name}Regexes.data")
    regexes.foreach(line => {
      out.write((line + "\n").getBytes("utf-8"))
    })
    out.flush()
    out.close()
  }

}
