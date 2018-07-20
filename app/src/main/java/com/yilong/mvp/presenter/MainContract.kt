package com.yilong.mvp.presenter

import com.yilong.mvp.base.BasePresenter
import com.yilong.mvp.base.BaseView

class MainContract {

    interface View : BaseView<Presenter> {

    }


    interface Presenter : BasePresenter {


    }

}