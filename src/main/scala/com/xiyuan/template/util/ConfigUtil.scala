package com.xiyuan.template.util

import java.io.{File, FileOutputStream}
import java.util.Properties

/**
  * Created by xiyuan_fengyu on 2016/7/1.
  */
object ConfigUtil {

  private val sourceDirctory = "./src/main/scala/"
  private val rLongOrInt: String = "-[0-9]{1,19}|[+]{0,1}[0-9]{1,19}"
  private val rDouble: String = "[-+]{0,1}[0-9]+\\.[0-9]+"
  private val rBoolean: String = "true|false"

  def main(args: Array[String]) {
    propertiesToClass("HttpServerConfig.properties", "com.xiyuan.netty.config")
  }

  private def propertiesToClass(fileName: String, packageStr: String) {
    val properties: Properties = new Properties
    try {
      properties.load(this.getClass.getClassLoader.getResourceAsStream(fileName))

      val strBld = new StringBuilder

      val keyIt = properties.keys()
      while (keyIt.hasMoreElements) {
        val key = keyIt.nextElement().asInstanceOf[String]
        val value = properties.getProperty(key)
        val keyInJava: String = key.replaceAll("\\.", "_")

        if (value.matches(rBoolean)) {
          strBld.append("\tval " + keyInJava + " = properties.getProperty(\"" + key + "\").toBoolean\n\n")
        }
        else if (value.matches(rDouble)) {
          strBld.append("\tval " + keyInJava + " = properties.getProperty(\"" + key + "\").toDouble\n\n")
        }
        else if (value.matches(rLongOrInt)) {
          val tempL = value.toLong
          if (tempL >= Int.MinValue && tempL <= Int.MaxValue) {
            strBld.append("\tval " + keyInJava + " = properties.getProperty(\"" + key + "\").toInt\n\n")
          }
          else if (tempL >= Long.MinValue && tempL <= Long.MaxValue) {
            strBld.append("\tval " + keyInJava + " = properties.getProperty(\"" + key + "\").toLong\n\n")
          }
          else {
            strBld.append("\tval " + keyInJava + " = properties.getProperty(\"" + key + "\")\n\n")
          }
        }
        else {
          strBld.append("\tval " + keyInJava + " = properties.getProperty(\"" + key + "\")\n\n")
        }

      }

      val lastSperator = fileName.replaceAll("\\\\", "/").lastIndexOf("/") + 1
      var className: String = fileName.substring(lastSperator).split("\\.")(0)
      val cArr: Array[Char] = className.toCharArray
      if (cArr.length > 0 && cArr(0) >= 'a' && cArr(0) <= 'z') {
        cArr(0) = (cArr(0) + 'A'.toInt - 'a'.toInt).toChar
        className = new String(cArr)
      }
      val classStr: String = "package " + packageStr + "\n\n" + "import " + this.getClass.getPackage.getName + ".ConfigUtil\n\n" + "object " + className + " {\n\n" + "\tprivate val properties = ConfigUtil.loadProperties(\"" + fileName + "\")\n\n" + strBld.toString + "}"
      val dir: File = new File(sourceDirctory + packageStr.replaceAll("\\.", "/"))
      if (!dir.exists) {
        dir.mkdirs
      }
      val classFile: File = new File(sourceDirctory + packageStr.replaceAll("\\.", "/") + "/" + className + ".scala")
      if (!classFile.exists) {
        classFile.createNewFile
      }
      val out: FileOutputStream = new FileOutputStream(classFile)
      out.write(classStr.getBytes("UTF-8"))
      out.flush()
      out.close()
    }
    catch {
      case e: Exception =>
        e.printStackTrace()
    }

  }

  def loadProperties(fileName: String): Properties = {
    val properties: Properties = new Properties
    try {
      properties.load(this.getClass.getClassLoader.getResourceAsStream(fileName))
    }
    catch {
      case e: Exception =>
        e.printStackTrace()
    }
    properties
  }

}