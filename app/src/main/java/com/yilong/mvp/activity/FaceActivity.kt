package com.yilong.mvp.activity

import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.Camera
import android.widget.Toast
import com.sensetime.mid.faceapi.model.FaceInfo
import com.sensetime.mid.faceapi.model.FaceSearchResult
import com.yilong.mvp.R
import com.yilong.mvp.presenter.FaceContract
import com.yilong.mvp.presenter.FacePresenter
import kotlinx.android.synthetic.main.activity_face.*

/**
 * 人脸识别页面
 */

class FaceActivity : BaseActivity(), FaceContract.View {
    override fun onSdkInitSucceed() {
        Toast.makeText(this,"onSdkInitSucceed",Toast.LENGTH_SHORT).show()
    }

    override fun onInitFailed() {
        Toast.makeText(this,"onInitFailed",Toast.LENGTH_SHORT).show()
    }

    override fun onFindFace(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onFaceRecognized(result: FaceSearchResult, avatar: Bitmap) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    //静态变量
    companion object {
        var isFaceSdkInited = false
    }

    var facePresenter: FacePresenter = FacePresenter(this, this)

    override fun setContentView() {
        setContentView(R.layout.activity_face)
    }

    override fun initPresenter() {
    }

    override fun initUI() {
    }

    override fun initListener() {
    }

    override fun drawFaces(faceInfo: Array<FaceInfo>) {
        mOverlapView.drawFaces(mCameraView, faceInfo)
    }

    override fun clearCanvas() {
        mOverlapView.clearCanvas()
    }

    override fun initData() {
        facePresenter.isPaused = false
        if (!isFaceSdkInited) {
            facePresenter.init()
            isFaceSdkInited = true
        } else {
            facePresenter.start()
        }


        mOverlapView.setZOrderOnTop(true)
        mOverlapView.holder.setFormat(PixelFormat.TRANSLUCENT)
        mOverlapView.layoutParams = mCameraView.layoutParams


        // 开启相机
        mCameraView.setPreviewCallback(
                Camera.PreviewCallback { data, camera ->
                    mCameraView.addCallbackBuffer()

                    if (facePresenter == null || facePresenter.isPaused)
                        return@PreviewCallback
                    if (facePresenter.mNv21 == null || data.size != facePresenter.mNv21.size) {
                        facePresenter.mNv21 = ByteArray(data.size)
                        facePresenter.mFaceData = ByteArray(data.size)
                    }
                    if (!facePresenter.mNv21GetReady) {
                        System.arraycopy(data, 0, facePresenter.mNv21, 0, data.size)
                        facePresenter.mNv21GetReady = true
                    }
                }
        )
        mCameraView.startPreview()
    }

    override fun onPause() {
        super.onPause()
        mCameraView.stopPreview()
    }

    override fun onDestroy() {
        super.onDestroy()
        facePresenter.stop()
//        // 清除引用
//        if (mCameraView != null) {
//            mCameraView = null
//        }
//        if (mOverlapView != null) {
//            mOverlapView = null
//        }

    }

}
