package com.xiyuan.template.util

import java.io.{File, FileFilter, IOException}
import java.lang
import java.lang.reflect.{Method, Modifier}
import java.net.{JarURLConnection, URL, URLDecoder}
import java.util.jar.{JarEntry, JarFile}

import org.objectweb.asm._

import scala.collection.mutable

/**
  * Created by xiyuan_fengyu on 2016/7/13.
  */
object ClassUtil {

  private val fileTag = "file:"

  val isExcuteInJar: Boolean = {
    val path = this.getClass.getResource(this.getClass.getSimpleName + ".class").getPath
    if (path.startsWith(fileTag)) true
    else false
  }


  private val classPath = "/" + this.getClass.getPackage.getName.replaceAll("\\.", "/") + "/" + this.getClass.getSimpleName + ".class"

  val classRoot: String = {
    val path = this.getClass.getResource(this.getClass.getSimpleName + ".class").getPath
    if (path.startsWith(fileTag)) {
      val jarPath = path.substring(fileTag.length, path.indexOf(classPath))
      jarPath.substring(0, jarPath.lastIndexOf("/"))
    }
    else path.substring(0, path.indexOf(classPath))
  }

  private def sameType(types: Array[Type], clazzes: Array[Class[_]]): Boolean = {
    // 个数不同
    if (types.length != clazzes.length) {
      return false
    }

    for (i <- types.indices) {
      if(!Type.getType(clazzes(i)).equals(types(i))) {
        return false
      }
    }
    true
  }

  /**
    * 获取一个方法的参数列表（名字和类型）
    *
    * @param m
    * @return
    */
  def getMethodParam(m: Method): Array[(String, Class[_])] = {
    val params = new Array[(String, Class[_])](m.getParameterTypes.length)
    val n = m.getDeclaringClass.getName
    val cw = new ClassWriter(ClassWriter.COMPUTE_MAXS)
    var cr: ClassReader = null
    try {
      cr = new ClassReader(n)
    } catch {
      case e: Exception =>
        e.printStackTrace()
    }

    cr.accept(new ClassVisitor(Opcodes.ASM4, cw) {

      override def visitMethod(access: Int, name: String, desc: String, signature: String, exceptions: Array[String]): MethodVisitor = {
        val paramTypes = Type.getArgumentTypes(desc)
        val paramClasses = m.getParameterTypes
        // 方法名相同并且参数个数相同
        if (name.equals(m.getName)
          && sameType(paramTypes, paramClasses)) {
          val v = cv.visitMethod(access, name, desc, signature,
            exceptions)
          new MethodVisitor(Opcodes.ASM4, v) {

            override def visitLocalVariable(name: String, desc: String, signature: String, start: Label, end: Label, index: Int): Unit = {
              var i = index - 1
              // 如果是静态方法，则第一就是参数
              // 如果不是静态方法，则第一个是"this"，然后才是方法的参数
              if(Modifier.isStatic(m.getModifiers)) {
                i = index
              }
              if (i >= 0 && i < params.length) {
                params(i) = (name, paramClasses(i))
              }
              super.visitLocalVariable(name, desc, signature, start,
                end, index)
            }

          }
        }
        else {
          super.visitMethod(access, name, desc, signature,
            exceptions)
        }
      }

    }, 0)
    params
  }

  /**
    * 从包package中获取所有的Class
    *
    * @param pack
    * @return
    */
  def getClasses (pack: String): mutable.HashSet[Class[_]] = {
    val classes  = new mutable.HashSet[Class[_]]
    val recursive: Boolean = true
    var packageName: String = pack
    val packageDirName: String = packageName.replace('.', '/')
    var dirs: java.util.Enumeration[URL] = null
    try {
      dirs = Thread.currentThread.getContextClassLoader.getResources(packageDirName)
      while (dirs.hasMoreElements) {
        val url: URL = dirs.nextElement
        val protocol: String = url.getProtocol
        if ("file" == protocol) {
          val filePath: String = URLDecoder.decode(url.getFile, "UTF-8")
          findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes)
        }
        else if ("jar" == protocol) {
          var jar: JarFile = null
          try {
            jar = (url.openConnection.asInstanceOf[JarURLConnection]).getJarFile
            val entries: java.util.Enumeration[JarEntry] = jar.entries
            while (entries.hasMoreElements) {
              val entry: JarEntry = entries.nextElement
              var name: String = entry.getName
              if (name.charAt(0) == '/') {
                name = name.substring(1)
              }
              if (name.startsWith(packageDirName)) {
                val idx: Int = name.lastIndexOf('/')
                if (idx != -1) {
                  packageName = name.substring(0, idx).replace('/', '.')
                }
                if ((idx != -1) || recursive) {
                  if (name.endsWith(".class") && !entry.isDirectory) {
                    val className: String = name.substring(packageName.length + 1, name.length - 6)
                    try {
                      classes.add(Class.forName(packageName + '.' + className))
                    }
                    catch {
                      case e: ClassNotFoundException =>
                        e.printStackTrace()
                    }
                  }
                }
              }
            }
          }
          catch {
            case e: IOException =>
              e.printStackTrace()
          }
        }
      }
    }
    catch {
      case e: IOException =>
        e.printStackTrace()
    }
    classes
  }

  /**
    * 以文件的形式来获取包下的所有Class
    *
    * @param packageName
    * @param packagePath
    * @param recursive
    * @param classes
    */
  private def findAndAddClassesInPackageByFile(packageName: String, packagePath: String, recursive: Boolean, classes: mutable.HashSet[Class[_]]) {
    val dir: File = new File(packagePath)
    if (!dir.exists || !dir.isDirectory) {
      return
    }
    val dirfiles: Array[File] = dir.listFiles(new FileFilter() {
      def accept(file: File): Boolean = {
        (recursive && file.isDirectory) || file.getName.endsWith(".class")
      }
    })

    for (file <- dirfiles) {
      if (file.isDirectory) {
        findAndAddClassesInPackageByFile(packageName + "." + file.getName, file.getAbsolutePath, recursive, classes)
      }
      else {
        val className: String = file.getName.substring(0, file.getName.length - 6)
        try {
          classes.add(Thread.currentThread.getContextClassLoader.loadClass(packageName + '.' + className))
        }
        catch {
          case e: ClassNotFoundException =>
            e.printStackTrace()
        }
      }
    }
  }

  def valueTypeToJavaObj(value: String, clazz: Class[_]): AnyRef = {
    try {
      if (clazz == classOf[Int]) {
        new Integer(value)
      }
      else if (clazz == classOf[Long]) {
        new lang.Long(value)
      }
      else if (clazz == classOf[Double]) {
        new lang.Double(value)
      }
      else if (clazz == classOf[Float]) {
        new lang.Float(value)
      }
      else if (clazz == classOf[Short]) {
        new lang.Short(value)
      }
      else if (clazz == classOf[Boolean]) {
        new lang.Boolean(value)
      }
      else if (clazz == classOf[String]) {
        value
      }
      else null
    }
    catch {
      case e: Exception =>
        e.printStackTrace()
        null
    }
  }

}
