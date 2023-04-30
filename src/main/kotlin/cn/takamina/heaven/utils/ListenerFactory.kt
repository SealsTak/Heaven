package cn.takamina.heaven.utils

import javassist.*
import javassist.bytecode.AnnotationsAttribute
import javassist.bytecode.SignatureAttribute
import javassist.bytecode.annotation.Annotation
import org.bukkit.event.Event
import org.bukkit.event.Listener
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.function.console
import taboolib.module.lang.sendError
import java.lang.reflect.InvocationTargetException
import java.util.function.Consumer

@RuntimeDependency("org.javassist:javassist:3.29.2-GA")
object ListenerFactory {

    val listenerClasses: MutableMap<String, CtClass> by lazy { HashMap() }
    val pool: ClassPool by lazy { ClassPool.getDefault() }

    /**
     * 创建监听器类
     * @param eventClass 监听的事件类
     */
    fun createListenerClass(eventClass: Class<out Event?>) {
        try {
            var listener: CtClass
            try {
                listener =
                    pool.getCtClass(ListenerFactory::class.java.name + ".dynamic_listener." + eventClass.simpleName)
            } catch (err: NotFoundException) {
                listener =
                    pool.makeClass(ListenerFactory::class.java.name + ".dynamic_listener." + eventClass.simpleName)
                //实现bukkit的Listener接口
                val listenerI = pool.getCtClass("org.bukkit.event.Listener")
                listener.addInterface(listenerI)
                //泛型Consumer<event>
                val cs = SignatureAttribute.ClassSignature(
                    arrayOf(
                        SignatureAttribute.TypeParameter(eventClass.name)
                    )
                )
                val eventFunction = pool.getCtClass("java.util.function.Consumer")
                eventFunction.genericSignature = cs.encode()
                //添加Consumer<event> method字段
                val methodF = CtField(eventFunction, "method", listener)
                listener.addField(methodF)
                //添加构造函数
                val constructor = CtConstructor(arrayOf(eventFunction), listener)
                constructor.setBody(
                    "{\n"
                            + "$0.method = $1;\n"
                            + "}"
                )
                listener.addConstructor(constructor)
                //添加@EventHandler注解
                val constpool = listener.classFile.constPool
                val methodAttr = AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag)
                val eventHandlerA = Annotation("org.bukkit.event.EventHandler", constpool)
                methodAttr.addAnnotation(eventHandlerA)
                //添加方法体
                val onEventM = CtMethod(
                    CtClass.voidType,
                    "on" + eventClass.simpleName,
                    arrayOf(pool.getCtClass(eventClass.name)),
                    listener
                )
                onEventM.setBody("{$0.method.accept($1);}")
                onEventM.methodInfo.addAttribute(methodAttr)
                listener.addMethod(onEventM)
            }
            listener.detach()
            try {
                listener.toClass()
            } catch (ignored: CannotCompileException) {
            }
            listenerClasses[eventClass.simpleName] = listener
        } catch (e: NotFoundException) {
            e.printStackTrace()
        } catch (e: CannotCompileException) {
            e.printStackTrace()
        }
    }

    /**
     * 获取事件的监听类
     * @param eventClass 监听的事件
     * @return 事件的监听类
     */
    fun getListenerClass(eventClass: Class<out Event?>): Class<*>? {
        if (eventClass.isInterface) {
            console().sendError("Script-Listen-Event-Interface-Error", eventClass.name)
            return null
        }
        if (!listenerClasses.containsKey(eventClass.simpleName)) {
            createListenerClass(eventClass)
        }
        return try {
            Class.forName(listenerClasses[eventClass.simpleName]!!.name)
        } catch (err: ClassNotFoundException) {
            err.printStackTrace()
            null
        }
    }

    /**
     * 获取事件的监听器(未注册)
     * @param event 坚挺的事件类
     * @param method 监听器主体
     * @return 监听器(未注册)
     */
    fun getListener(event: Class<out Event?>?, method: Consumer<out Event?>?): Listener? {
        return try {
            getListenerClass(event!!)!!.declaredConstructors[0].newInstance(method) as Listener
        } catch (e: InstantiationException) {
            e.printStackTrace()
            null
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
            null
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
            null
        }
    }
}