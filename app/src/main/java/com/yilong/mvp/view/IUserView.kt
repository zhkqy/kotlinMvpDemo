package com.yilong.mvp.view

/**
 */
interface IUserView {

    fun getID(): Int

    fun getUsername(): String

    fun getAge(): Int

    fun setUsername(username: String)

    fun setAge(age: Int)

    fun onSaveSuccess()
}