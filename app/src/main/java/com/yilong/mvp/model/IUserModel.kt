package com.yilong.mvp.model

import com.yilong.mvp.data.User

/**
 * 创建一个接口
 */
interface IUserModel {
    fun setId(id: Int)
    fun setUsername(username: String)
    fun setAge(age: Int)
    fun save()
    fun load(id: Int): User
}