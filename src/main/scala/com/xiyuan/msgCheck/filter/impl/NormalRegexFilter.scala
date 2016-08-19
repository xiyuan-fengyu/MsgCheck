package com.xiyuan.msgCheck.filter.impl

import java.io._

import com.xiyuan.msgCheck.filter.Filter
import com.xiyuan.template.util.ClassUtil

import scala.collection.mutable.ArrayBuffer
import scala.io.Source

/**
  * Created by xiyuan_fengyu on 2016/8/16.
  * 通过正则表达式来判断是否为正常信息
  */
class NormalRegexFilter extends Filter {

  private val regexes = new ArrayBuffer[String]()

  override final def check(str: String): Boolean = {
    if (regexes.isEmpty) {
      initRegexes()
    }
    !regexes.exists(str.matches)
  }


  /**
    * 如果检测为正常消息，是否需要传递给之后的过滤器做检测
    *
    * @return
    */
  override def needNextCheck: Boolean = false

  def initRegexes(): Unit = {
    val regexsFile = new File(ClassUtil.classRoot + "/normalRegexes.data")

    if (!regexsFile.exists()) {
      //从jar中提取data文件，并存储到class目录下
      val in = this.getClass.getClassLoader.getResourceAsStream("normalRegexes.data")
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

    if (regexsFile.exists()) {
      Source.fromFile(regexsFile, "utf-8").getLines().foreach(line => {
        if (line.nonEmpty) {
          regexes += line
        }
      })
    }

  }

}
