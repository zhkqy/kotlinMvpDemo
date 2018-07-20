package com.yilong.mvp.test

import android.util.Log
import com.yilong.mvp.data.User

class TestOne {


    interface Base {
        fun print()
    }

    class BaseImpl(val x: Int) : Base {
        override fun print() {
//            Log.d(JTAG, "BaseImpl -> ${x.string()}")
        }
    }

    class Printer(b: Base) : Base by b

    val lazyValue: String by lazy {
        //        Log.d(JTAG, "Just run when first being used")
        "value"
    }

    fun test() {
//        Log.d(JTAG, lazyValue)
//        Log.d(JTAG, lazyValue)
    }

    var max: Int = 0
    var a: Int = 1
    var b: Int = 2


// 分支内容为代码块
//    max = if(a>b)
//    {
//
//    }else
//    {
//
//    }
//
//    log("代码块 max : ${max}")   // 代码块 max : 5

//    open class Runoob constructor(name: String) {
//        var url: String = "http://www.runoob.com"
//        open var country: String = "CN"
//        var siteName = name
//
//        init {
//            println("初始化网站名：${name}")
//        }
//
//        open fun printTest() {
//            println("我是类的函数")
//        }
//
//        constructor(name: String, age: Int) : this(name) {
//
//        }
//
//    }
//
//    class Outer : Runoob("") {
//        private val bar: Int = 1
//        var v = "成员属性"
//
//        override var country: String = ""
//
//        override fun printTest() {
//
//
//        }
//
//        /**嵌套内部类**/
//        inner class Inner {
//            fun foo() = bar  // 访问外部类成员
//            fun innerTest() {
//                var o = this@Outer //获取外部类的成员变量
//                println("内部类可以引用外部类的成员，例如：" + o.v)
//            }
//        }
//    }
//
//
//    interface MyInterface {
//
//        var str: String
//        fun bar()
//        fun foo() {
//            println("foo")
//        }
//    }
//
//
//    class child : MyInterface {
//
//        override var str: String = " "
//        override fun bar() {
//
//
//            fun test() {
//                var s = child("test")
//
//            }
//
//            fun copy(name: String = this.name, age: Int = this.age) = User1(name, age)
//
//            data class User1(val name: String, val age: Int)
//
//            fun main(args: Array<String>) {
//                val jack = User(name = "Jack", age = 1)
//                val olderJack = jack.copy(age = 2)
//                println(jack)
//                println(olderJack)
//
//            }
//
//            fun <T> abc(t: T) {
//            }
//        }
//
//
//        enum class color(i: Int) {
//            RED(),BLACK(200),BLUE(200),WHITE(200)
//        }
//
//
//    }

}