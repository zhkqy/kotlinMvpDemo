package com.yilong.mvp.activity

import android.content.Intent
import android.view.View
import com.yilong.mvp.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), View.OnClickListener {


    override fun setContentView() {
        setContentView(R.layout.activity_main)
    }

    override fun initPresenter() {
    }

    override fun initUI() {
    }

    override fun initListener() {
        facebtn.setOnClickListener(this)
        loginbtn.setOnClickListener(this)
    }

    override fun initData() {

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.facebtn -> startActivity(Intent(this, FaceActivity::class.java))
            R.id.loginbtn -> startActivity(Intent(this, LoginActivity::class.java))
        }
    }


}
