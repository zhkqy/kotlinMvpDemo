package com.yilong.mvp.presenter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Rect
import android.util.Log
import com.sensetime.mid.faceapi.FaceSearch
import com.sensetime.mid.faceapi.FaceVerify
import com.sensetime.mid.faceapi.model.FaceInfo
import com.sensetime.senseguardsdk.*
import com.yilong.mvp.ZKCameraTextureView
import com.yilong.mvp.utils.BitmapUtils
import com.zkteco.android.db.orm.manager.DataManager
import com.zkteco.android.db.orm.tna.PersBiotemplate
import com.zkteco.android.db.orm.tna.PersBiotemplatedata
import com.zkteco.android.db.orm.tna.UserInfo
import java.sql.SQLException
import java.util.concurrent.Executors
import java.util.concurrent.Future

class FacePresenter(var view: FaceContract.View, var mContext: Context) : FaceContract.Presenter {

    private val TAG = "FaceController"

    // 暂停
    var isPaused = false
    private var mVerifier: Verifier? = null

    // 人脸检测
    private var mFaceDetector: FaceDetector? = null

    private var mFaceVerify: FaceVerify? = null
    private val mLock = Any()
    // 线程池
    private val mWorkerThreads = Executors.newFixedThreadPool(3)
    private val mLoadThread: Future<*>? = null
    private var mTrackThread: Future<*>? = null
    private var mRecognizeThread: Future<*>? = null

    //头像面积大小
    private val MIN_FACE_AREA = 20000//3m
    private val MIDDLE_FACE_AREA = 50000
    private val MAX_FACE_AREA = 850000


    private val mFaceInfoes = arrayOfNulls<FaceInfo>(10)

    private val cacheNV21 = ByteArray(ZKCameraTextureView.previewWidth * ZKCameraTextureView.previewHeight * 3 / 2)

    private val mirrorNV21: ByteArray? = null
    private val mirrorMatrix = Matrix()

    private var threshold: Int = 0//识别阈值
    private var isMultipleRecognizeMode: Boolean = true//是否是单人识别模式
    private var mLiveConfidence: Float = 0.toFloat()


    var mFaceGetReady = false
    var mNv21GetReady = false

    // 活体
    var mLiveDetector: LiveDetector? = null

    var mNv21 = ByteArray(ZKCameraTextureView.previewHeight * ZKCameraTextureView.previewWidth * 3 / 2)
    // nv21的数据用来track face
    var mFaceData = ByteArray(ZKCameraTextureView.previewHeight * ZKCameraTextureView.previewWidth * 3 / 2)

    override fun init() {

        SDKManager.init(mContext.getApplicationContext(), object : SDKManager.SDKInitListener {
            override fun onStart() {
            }

            override fun onSucceed() {
                Log.d(TAG, "证书校验成功")

                mVerifier = Verifier.Builder(mContext.getApplicationContext())
                        .setOrientation(Orientation.LANDSCAPE)
                        .setPreviewSize(ZKCameraTextureView.previewWidth, ZKCameraTextureView.previewHeight)
                        .build()
                mFaceDetector = FaceDetector.Builder()
                        .setOrientation(Orientation.LANDSCAPE)
                        .setPreviewSize(ZKCameraTextureView.previewWidth, ZKCameraTextureView.previewHeight)
                        .build()
                mLiveDetector = LiveDetector.Builder(mContext.getApplicationContext())
                        .setOrientation(Orientation.LANDSCAPE)
                        .setPreviewSize(ZKCameraTextureView.previewWidth, ZKCameraTextureView.previewHeight)
                        .build()
                //                FaceSearch.load(ZKFilePath.FACE_DB_PATH);
                mFaceVerify = FaceVerify()

                //                mLoadThread = mWorkerThreads.submit(new LoadTask());
                mTrackThread = mWorkerThreads.submit(TrackTask())
                mRecognizeThread = mWorkerThreads.submit(FaceRecognizeTask())

                view.onSdkInitSucceed()
                mirrorMatrix.postScale(-1f, 1f)
            }

            override fun onFailed(code: Int, message: String) {
                Log.d(TAG, "证书校验失败:$code,$message")
                view.onInitFailed()
            }
        })
    }

    inner class TrackTask : Runnable { //track 消费nv21
        override fun run() {
            while (!isPaused) {
                if (mNv21GetReady) {
                    val faceInfoes: Array<FaceInfo>

                    faceInfoes = mFaceDetector!!.detectOrderByFaceWidth(mNv21)

                    if (isMultipleRecognizeMode) {

                    }
//                    } else {
//                        faceInfoes =  arrayOf{1}
//                        faceInfoes[0] = mFaceDetector!!.detect(mNv21)
//                    }

                    if (faceInfoes == null || faceInfoes[0] == null) {
                        view.clearCanvas()
                        mNv21GetReady = false
                        continue
                    }

                    if (view.onFindFace()) {
                        view.clearCanvas()
                        continue
                    }

                    view.drawFaces(faceInfoes)
                    if (!mFaceGetReady) {
                        //                        mFaceInfos = FaceUtils.clone(faceInfoes);
                        val maxCount: Int
                        if (faceInfoes.size < mFaceInfoes.size) {
                            maxCount = faceInfoes.size
                        } else {
                            maxCount = mFaceInfoes.size
                        }
                        for (i in 0 until maxCount) {
                            val faceInfo = FaceUtils.clone(faceInfoes[i])
                            mFaceInfoes[i] = faceInfo.clone()
                        }
                        System.arraycopy(mNv21, 0, mFaceData, 0, mNv21.size)
                        mFaceGetReady = true
                    }
                    mNv21GetReady = false
                }
            }
        }
    }

    fun getVerifier(): Verifier {
        return mVerifier!!
    }

    fun getFaceVerify(): FaceVerify {
        return mFaceVerify!!
    }

    fun getFaceDetector(): FaceDetector {
        return mFaceDetector!!
    }

    fun getLiveDetector(): LiveDetector {
        return mLiveDetector!!
    }

    fun start() {
        if (mTrackThread == null) {
            mTrackThread = mWorkerThreads.submit(TrackTask())
        }
        if (mRecognizeThread == null) {
            mRecognizeThread = mWorkerThreads.submit(FaceRecognizeTask())
        }
    }

    fun setThreshold(threshold: Int) {
        this.threshold = threshold
    }

    /**
     * 设置是否是单人识别模式
     *
     * @param isSingleMode
     */
    fun setMultipleRecognizeMode(isSingleMode: Boolean) {
        isMultipleRecognizeMode = isSingleMode
    }

    fun setLiveConfidence(liveConfidence: Float) {
        this.mLiveConfidence = liveConfidence
    }


    private inner class FaceRecognizeTask : Thread() {
        override fun run() {
            Log.i(TAG, "run: FaceRecognizeTask is Running")
            while (!isPaused) {
                if (mFaceGetReady) {
                    System.arraycopy(mFaceData, 0, cacheNV21, 0, mFaceData.size)
                    if (mFaceInfoes != null) {
                        try {
                            val image = BitmapUtils.nv21ToBitmap(cacheNV21, ZKCameraTextureView.previewWidth, ZKCameraTextureView.previewHeight)

                            for (info in mFaceInfoes) {
                                if (info == null) {
                                    continue
                                }

                                if (calRectArea(info.faceRect) < MIN_FACE_AREA) {
                                    continue
                                }

                                val liveConfidence = mLiveDetector!!.detect(mFaceData, info)
                                Log.i(TAG, "run: liveConfidence=$liveConfidence")
                                if (liveConfidence < mLiveConfidence) {
                                    var feature1: ByteArray? = null
                                    synchronized(mLock) {
                                        feature1 = mVerifier!!.getFeature(mFaceData, info)
                                        val avatar = cropAvatar(image, info.faceRect)

                                        if (feature1 != null) {
                                            val result = FaceSearch.search(mFaceVerify, feature1)
                                            if (result != null) {
                                                Log.i(TAG, "run: result=" + result.toString())
                                                if (avatar is Bitmap)
                                                    view.onFaceRecognized(result, avatar)
                                            }
                                        }

                                    }
                                }

                            }
                            for (i in mFaceInfoes.indices) {
                                mFaceInfoes[i] = null
                            }
                        } catch (e: Exception) {
                            Log.i(TAG, "run: #######################################")
                            e.printStackTrace()
                        }

                    }
                    mFaceGetReady = false
                }
            }
            Log.i(TAG, "run: FaceRecognizeTask is Destroy")
        }
    }


    @Synchronized
    private fun cropAvatar(bitmap: Bitmap, rect: Rect): Bitmap? {
        var rect = rect
        if (rect.left < 0) {
            rect.left = 0 + 1
        }
        if (rect.top < 0) {
            rect.top = 0 + 1
        }
        if (rect.right > ZKCameraTextureView.previewWidth) {
            rect.right = ZKCameraTextureView.previewWidth - 1
        }
        if (rect.bottom > ZKCameraTextureView.previewHeight) {
            rect.bottom = ZKCameraTextureView.previewHeight - 1
        }


        var avatar: Bitmap? = null
        try {
            if (!bitmap.isRecycled) {
                avatar = Bitmap.createBitmap(bitmap,
                        rect.left,
                        rect.top,
                        rect.right - rect.left,
                        rect.bottom - rect.top,
                        mirrorMatrix,
                        true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "run: ##############" + e.toString())
            e.printStackTrace()
        } catch (e: OutOfMemoryError) {
            Log.e(TAG, "run: FFFFFFFFFFFFFF")
        }

        return avatar
    }

    private fun calRectArea(rect: Rect): Int {
        val width = Math.abs(rect.right - rect.left)
        val height = Math.abs(rect.bottom - rect.top)
        return width * height
    }


    fun insertFeature(userPin: String, bitmap: Bitmap) {
        synchronized(mLock) {
            var feature: ByteArray? = null
            try {
                // mVerifier.getFeature(bitmap) 有时候会出现   Calling native method failed! ResultCode : -4 Reason : run in fail inside 异常
                // 有时出现OOM
                feature = mVerifier!!.getFeature(bitmap)

            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            if (feature != null) {
                var userInfo: UserInfo? = UserInfo()
                try {
                    userInfo = userInfo!!.getQueryBuilder().where().eq("User_PIN", userPin).queryForFirst()
                    Log.i(TAG, "insertFeature ===== true")
                } catch (e: SQLException) {
                    Log.i(TAG, "insertFeature ===== err")
                    e.printStackTrace()
                }

                if (userInfo != null) {
                    try {
                        val dataManager = DataManager()
                        dataManager.open(mContext.applicationContext)
                        FaceSearch.delete(userInfo!!.getID() as Int)                      // 删除旧算法库数据
                        val ret = FaceSearch.insert(userInfo!!.getID() as Int, feature)   // 更新算法库数据
                        Log.i(TAG, "insertFeature ===== $ret")

                        // 查询人员对应的模板信息
                        var oldPersBiotemplate: PersBiotemplate? = PersBiotemplate()
                        oldPersBiotemplate = oldPersBiotemplate!!.getQueryBuilder()
                                .where()
                                .eq("User_PIN", userInfo!!.getUser_PIN())
                                .queryForFirst()

                        // 如果表中存在旧的模板，更新
                        if (oldPersBiotemplate != null) {
                            val oldPersBiotemplatedataId = oldPersBiotemplate!!.getTemplate_id()
                            val updateArguments = arrayOf<Any>(feature, 0, 0, 0, oldPersBiotemplatedataId)
                            dataManager.executeSql("ZKDB.db", "update Pers_BioTemplateData set template_data=?, CREATE_ID=?, MODIFY_TIME=?, SEND_FLAG=? WHERE ID=?", updateArguments)
                        } else {
                            // 如果没有，插入模板
                            val arguments = arrayOf<Any>(feature, 0, 0, 0)
                            dataManager.executeSql("ZKDB.db", "insert into Pers_BioTemplateData(template_data,CREATE_ID, MODIFY_TIME, SEND_FLAG) values(?,?,?,?)", arguments)
                            var persBiotemplatedata = PersBiotemplatedata()
                            persBiotemplatedata = persBiotemplatedata.getQueryBuilder().orderBy("ID", false).queryForFirst()

                            val templateId = persBiotemplatedata.getID()
                            val persBiotemplate = PersBiotemplate()
                            persBiotemplate.setTemplate_id(templateId.toInt())
                            persBiotemplate.setUser_pin(userInfo!!.getUser_PIN())
                            persBiotemplate.setBio_type(2)
                            persBiotemplate.setMajor_ver(8)
                            persBiotemplate.setTemplate_no(1)
                            persBiotemplate.create()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }
        }
    }


    fun stop() {
        Log.i("MagicK", "stop:  ssssssssssssssssssssssssssssssssss")
        isPaused = true
        if (mTrackThread != null) {
            mTrackThread!!.cancel(true)
        }

        if (mRecognizeThread != null) {
            mRecognizeThread!!.cancel(true)
        }
        mTrackThread = null
        mRecognizeThread = null
    }


}
