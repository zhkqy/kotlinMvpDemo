package com.yilong.mvp.view

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.SurfaceView
import com.sensetime.mid.faceapi.model.FaceInfo
import com.sensetime.mid.faceapi.util.FaceDrawUtil
import com.yilong.mvp.ZKCameraTextureView
import kotlinx.android.synthetic.main.activity_face.view.*

class FaceSurfaceView : SurfaceView {

    var mPaint = Paint() // 绘制人脸框和点的画笔

    constructor(context: Context) : super(context) {
        initPaint()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initPaint()
    }

    fun initPaint() {
        mPaint.color = Color.rgb(0, 255, 0)
        mPaint.style = Paint.Style.STROKE
    }

    fun drawFaces(mCameraView: ZKCameraTextureView, faceInfo: Array<FaceInfo>) {
        if (faceInfo != null) {
            val canvas = getHolder().lockCanvas() ?: return
            canvas.drawColor(0, PorterDuff.Mode.CLEAR)
            canvas.save()
            canvas.setMatrix(mCameraView.getMatrix())

            for (info in faceInfo) {
                // 如果需要用到属性提取、认证功能，建议使用cvface.clone()方法复制一份数据进行旋转操作和绘制，可以不改变原始数据
                //                FaceInfo face = FaceUtils.clone(info);
                //                FaceUtils.rotateFace(face, mCameraView.mPreviewWidth, mCameraView.mPreviewHeight,
                //                        mCameraView.isFrontCamera(), mCameraView.mDegrees);
                //                FaceUtils.drawFaces(canvas, faceInfo, mPaint);
                FaceDrawUtil.drawFaceRect(canvas, info.faceRect, mPaint)
            }
            canvas.restore()
            getHolder().unlockCanvasAndPost(canvas)
        } else {
            clearCanvas()
        }
    }

    fun clearCanvas() {
        val canvas = mOverlapView.holder.lockCanvas()
        if (canvas != null) {
            canvas.drawColor(0, PorterDuff.Mode.CLEAR)
            mOverlapView.holder.unlockCanvasAndPost(canvas)
        }

    }


}
