package com.yilong.mvp.model

import com.yilong.mvp.data.User
import android.util.SparseArray

/**
 * IUserModel的实现类
 */
class UserModel: IUserModel {

    private var mId: Int = 0
    private var mUsername: String = ""
    private var mAge: Int = 0
        private val mUserArray = SparseArray<User>()

    override fun setId(id: Int) {
        mId = id
    }

    override fun setUsername(username: String) {
        mUsername = username
    }

    override fun setAge(age: Int) {
        mAge = age
    }

    override fun save() {
        mUserArray.append(mId, User(mUsername, mAge))
    }

    override fun load(id: Int): User {
        mId = id
        return mUserArray.get(mId, User(mUsername, mAge))
    }
}