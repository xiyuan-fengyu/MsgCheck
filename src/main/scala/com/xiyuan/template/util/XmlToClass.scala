package com.xiyuan.template.util

import java.io.File
import java.lang.reflect.{Constructor, Field}

import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, Map}
import scala.xml.{Node, XML}

/**
  * Created by YT on 2016/4/20.
  */
object XmlToClass {

  def main(args: Array[String]) {
    xmlToClass(new File("./src/main/resources/SpiderCfg.xml"))
    xmlToClass(new File("./src/main/resources/KafkaCfg.xml"))
  }

  def xmlToClass(xmlFile: File): Unit = {
    classStatement.clear()
    val className = xmlFile.getName.split("\\.")(0)
    val packageName = this.getClass.getPackage.getName

    val rootNode = XML.loadFile(xmlFile)
    parseNode(rootNode, className)
    val classStatementStr = classStatement.map(item => item._2 + '\n').reduce(_ + _)

    val classStrBld = new StringBuilder()
    classStrBld.append("package ").append(packageName).append("\n\n")
      .append("object ").append(className).append(" {\n")
      .append(" val " + rootNode.label + " = XmlToClass.xmlToObject[" + className + "](\"").append(packageName + "." + className).append("\")").append('\n')
      .append("}\n\n")
      .append(classStatementStr)
    FileUtil.writeToFile(classStrBld.toString, new File("./src/main/scala/" + packageName.replaceAll("\\.", "/") + "/" + className + ".scala"))
  }

  def xmlToObject[T](className: String): T = {
    val clazz = Class.forName(className).asInstanceOf[Class[T]]
    val xml = if(isExcuteInJar) {
      XML.load(this.getClass.getResourceAsStream("/" + clazz.getSimpleName + ".xml"))
    }
    else {
      XML.loadFile(new File("./src/main/resources/" + clazz.getSimpleName + ".xml"))
    }
    xmlNodeToObject[clazz.type ](clazz.asInstanceOf[Class[clazz.type ]], xml).asInstanceOf[T]
  }

  def findMapConstructor[T](constructors: Seq[Constructor[T]]): Constructor[T] = {
    var c: Constructor[T] = null
    for(i <- constructors.indices; if c == null) {
      val cc = constructors(i)
      val ccParas = cc.getParameterTypes.toSeq
      if(ccParas.length == 1 && ccParas.head.toString.equals("interface scala.collection.immutable.Map")) {
        c = cc
      }
    }
    c
  }

  def xmlNodeToObject[T](clazz: Class[T], node: Node): T = {
    val paraTypeMap = Map[String, Field]()
    clazz.getDeclaredFields.toSeq.foreach(field => paraTypeMap.update(field.getName, field))

    val paraValMap = Map[String, Any]()
    node.nonEmptyChildren.foreach(child => {
      val childLb = child.label
      if(!childLb.equals("#PCDATA")) {
        val isParaArr = paraTypeMap.contains(childLb + "Arr")
        val paraName = if(isParaArr) childLb + "Arr" else childLb
        val fType = paraTypeMap(paraName).getType
        val fTypeName = fType.getSimpleName
        val fItemTypeName = fTypeName.replaceAll("\\[\\]", "")
        val isSimpleType = typeWeight.contains(fItemTypeName)

        val childVal: Any = if(isSimpleType) {
          fItemTypeName.toLowerCase match {
            case "boolean" => if(child.text.equals("true")) true else false
            case "int" => child.text.toInt
            case "long" => child.text.toLong
            case "double" => child.text.toDouble
            case "string" => child.text
          }
        }
        else {
          val childClazz = if(isParaArr) {
            Class.forName(fType.getName.substring(2, fType.getName.length - 1))
          }
          else {
            Class.forName(fType.getName)
          }
          xmlNodeToObject[childClazz.type ](childClazz.asInstanceOf[Class[childClazz.type ]], child)
        }

        if(isParaArr) {
          if(!paraValMap.contains(paraName)) {
            paraValMap.update(paraName, new ArrayBuffer[Any]())
          }
          paraValMap(paraName).asInstanceOf[ArrayBuffer[Any]] += childVal
        }
        else {
          paraValMap.update(paraName, childVal)
        }
      }
    })

    paraTypeMap.foreach(pT => {
      if(pT._2.getType.getSimpleName.endsWith("[]")) {
        paraValMap.update(pT._1, paraValMap(pT._1).asInstanceOf[ArrayBuffer[Any]].toArray)
      }
    })

    findMapConstructor[T](clazz.getDeclaredConstructors.toSeq.asInstanceOf[mutable.Seq[Constructor[T]]]).newInstance(paraValMap.toMap)
  }

  val classStatement = mutable.Map[String, String]()
  val paraTypeMap = Map[String, String]()

  def parseNode(node: Node, nodePath: String): Unit = {
//    val newBld = new StringBuilder
    val paraNameMap = Map[String, String]()
    val paraNumMap = Map[String, Int]()

    node.nonEmptyChildren.foreach(child => {
      val childLb = child.label
      if(!childLb.equals("#PCDATA")) {
        val key = nodePath + "_" + childLb
        if(child.nonEmptyChildren.length > 1) {
          paraTypeMap.update(key, key)
          paraNameMap.update(key, childLb)
          if(paraNumMap.contains(childLb)) paraNumMap(childLb) += 1 else paraNumMap.update(childLb, 1)

          parseNode(child, key)
        }
        else if(child.nonEmptyChildren.length == 1) {
          val typeAttr = child.attribute("type").toArray
          val childType = if(typeAttr.length > 0) {
            val typeStr = typeAttr.head.toString
            typeStr.charAt(0).toUpper + typeStr.substring(1)
          } else getNodeType(child.text)
          if(paraNameMap.contains(key)) {
            if(typeWeight(childType) > typeWeight(paraTypeMap(key))) {
              paraTypeMap.update(key, childType)
            }
            paraNumMap(childLb) += 1
          }
          else {
            paraTypeMap.update(key, childType)
            paraNameMap.update(key, childLb)
            paraNumMap.update(childLb, 1)
          }
        }
      }
    })

    val classBld = new StringBuilder
    val mapConstructorBld = new StringBuilder
    classBld.append("class " + nodePath + "(")
    mapConstructorBld.append("  def this(paras: Map[String, Any]) = this(")
    paraNameMap.foreach(para => {
      val paraPath = para._1
      val paraName = para._2
      val paraType = paraTypeMap(paraPath)
      val paraNum = paraNumMap(paraName)

      if(paraNum > 1) {
        classBld.append("val ").append(paraName).append("Arr: Array[").append(paraType).append("],")
        mapConstructorBld.append(paraName).append("Arr = paras(\"").append(paraName).append("Arr\")").append(".asInstanceOf[Array[Any]].map(i => i.asInstanceOf[" + paraType + "]),")
      }
      else {
        classBld.append("val " + paraName + ": " + paraType +  ",")
        mapConstructorBld.append(s"""$paraName = paras(\"$paraName\").asInstanceOf[$paraType],""")
      }
    })
    if(mapConstructorBld.charAt(mapConstructorBld.length - 1) == ',') mapConstructorBld.deleteCharAt(mapConstructorBld.length - 1)
    mapConstructorBld.append(")\n")
    if(classBld.charAt(classBld.length - 1) == ',') classBld.deleteCharAt(classBld.length - 1)
    classBld.append("){\n").append(mapConstructorBld.toString()).append("}\n")
    classStatement.update(nodePath, classBld.toString())
  }

  private[this] val rLong = "-[0-9]{1,19}|[+]{0,1}[0-9]{1,19}"
  private[this] val rDouble = "[-+]{0,1}[0-9]+\\.[0-9]+"
  private[this] val rBoolean = "true|false"
  private[this] val typeWeight = Map[String, Int](
    "Boolean" -> 0,
    "boolean" -> 0,
    "Int" -> 0,
    "int" -> 0,
    "Long" -> 1,
    "long" -> 1,
    "Double" -> 2,
    "double" -> 2,
    "String" -> 3
  )

  def getNodeType(content: String): String = {
    if(content.matches(rLong)) {
      try {
        val l = content.toLong
        if(l >= Int.MinValue && l <= Int.MaxValue) "Int" else "Long"
      }
      catch {
        case e: Exception => "String"
      }
    }
    else if(content.matches(rDouble)) {
      "Double"
    }
    else if(content.matches(rBoolean)) {
      "Boolean"
    }
    else {
      "String"
    }
  }

  val isExcuteInJar: Boolean = {
    val clazzPath = this.getClass.getResource(this.getClass.getSimpleName + ".class").toString
    if (clazzPath.startsWith("jar:")) {
      true
    }
    else {
      false
    }
  }

}
