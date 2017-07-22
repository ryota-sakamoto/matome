package models

import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

abstract class Model[T](implicit tag: TypeTag[T], class_tag: ClassTag[T]) {
    val m: Mirror = runtimeMirror(getClass.getClassLoader)
    val c: ClassSymbol = typeOf[T].typeSymbol.asClass
    val cm: ClassMirror = m.reflectClass(c)
    val ms: MethodSymbol = typeOf[T].decl(termNames.CONSTRUCTOR).asMethod
    val a: MethodMirror = cm.reflectConstructor(ms)
    val data: T = a().asInstanceOf[T]
    val rc: RuntimeClass = class_tag.runtimeClass

    def setData(): Unit = {
        val field = getField()
        field.foreach{ field_name =>
            set(field_name, "Hello")
        }
    }

    def getField(): Array[String] = {
        rc.getDeclaredFields.map(_.getName)
    }

    def set(name: String, value: Any): Unit = {
        rc.getMethods.find(_.getName == name + "_$eq").get.invoke(data, value.asInstanceOf[AnyRef])
    }

    def findById(id: Int): T = {
        setData()
        data
    }
}