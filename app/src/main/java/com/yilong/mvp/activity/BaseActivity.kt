package com.yilong.mvp.activity

import android.app.Activity
import android.os.Bundle

abstract class BaseActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView()
        initPresenter()
        initListener()
        initData()
    }

    abstract fun setContentView()

    abstract fun initPresenter()

    abstract fun initUI()

    abstract fun initListener()

    abstract fun initData()

}