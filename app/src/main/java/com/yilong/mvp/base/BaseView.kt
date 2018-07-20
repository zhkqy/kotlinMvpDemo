package com.yilong.mvp.base

interface BaseView<T : BasePresenter> {
    fun setPresenter(presenter: T)
}
