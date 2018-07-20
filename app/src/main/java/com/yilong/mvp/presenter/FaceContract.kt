package com.yilong.mvp.presenter

import android.content.Context
import android.graphics.Bitmap
import com.sensetime.mid.faceapi.model.FaceInfo
import com.sensetime.mid.faceapi.model.FaceSearchResult
import com.yilong.mvp.base.BasePresenter
import com.yilong.mvp.base.BaseView

interface FaceContract {

    interface View {
        fun drawFaces(faceInfo: Array<FaceInfo>)
        fun clearCanvas()

        /**
         * 注册成功
         */
        fun onSdkInitSucceed()

        /**
         * 初始化失败
         */
        fun onInitFailed()

        /**
         * 算法库检测到人脸
         */
        fun onFindFace(): Boolean

        /**
         * 算法库识别到人脸
         */
        fun onFaceRecognized(result: FaceSearchResult, avatar: Bitmap)
    }

    interface Presenter : BasePresenter {
        fun init()

    }
}