package com.yilong.mvp.utils;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.util.SparseIntArray;

import com.yilong.mvp.R;
import com.yilong.mvp.ZKApplication;

public final class SoundHelper {
    private static SparseIntArray mRawSounds = new SparseIntArray();
    private static LoadCompleteListener mListener = new LoadCompleteListener();

    private static long mPlayingTimeMillis = -1;

    private static AudioManager mAudioManager;


    @SuppressWarnings("deprecation")
    private static SoundPool sSoundPool = new SoundPool(1,
            AudioManager.STREAM_MUSIC, 0);

    static {
        sSoundPool.setOnLoadCompleteListener(mListener);
    }

    private SoundHelper() {
    }

    private static class LoadCompleteListener implements OnLoadCompleteListener {

        @Override
        public void onLoadComplete(final SoundPool soundPool,
                                   final int sampleId, final int status) {
            soundPool.play(sampleId, 1.0f, 1.0f, 0, 0, 1.0f);
        }
    }

    private static int loadSound(final Context context, final int resId) {
        final Integer sound = sSoundPool.load(context, resId, 1);
        mRawSounds.put(resId, sound);
        return sound;
    }

    public static void playSound(final Context context, final int soundID) {
        if (context == null) {
            return;
        }
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int ringer_mode = mAudioManager.getRingerMode();
        if (ringer_mode == AudioManager.RINGER_MODE_SILENT) {  //静音
            return;
        } else if (ringer_mode == AudioManager.RINGER_MODE_NORMAL) { //标准
            synchronized (sSoundPool) {
                int sound = mRawSounds.get(soundID);
                if (sound == 0) {
                    sound = loadSound(ZKApplication.Companion.getContext(), soundID);
                } else {
                    long currentTimeMillis = System.currentTimeMillis();

                    // 如果当前播放的语音是“请将手掌放在绿色区域。。。"，判断是否播放完毕，没有播放完毕，直接返回
                    if (soundID == R.raw.s_75) {
                        // 判断3秒内是否播放完毕，如果已播放完毕直接返回
                        if ((currentTimeMillis - mPlayingTimeMillis) > 3000) {
                            mPlayingTimeMillis = currentTimeMillis;
                            sSoundPool.play(sound, 1.0f, 1.0f, 0, 0, 1.0f);
                        }
                    } else {
                        sSoundPool.play(sound, 1.0f, 1.0f, 0, 0, 1.0f);
                    }
                }
            }
        }

    }

    // �ȴ���ʾ���������
    public static boolean waitForPlayComplete(Activity activity, long millisSeconds) {
        // 如果是”请将手掌置于绿色区域。。。“，不过播放延迟
        if (activity == null || activity.isFinishing()) {
            return false;
        }

        try {
            Thread.sleep(millisSeconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return !activity.isFinishing();
    }


    public static boolean waitForPlayComplete(long millisSeconds) {
        try {
            Thread.sleep(millisSeconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return true;
    }
}