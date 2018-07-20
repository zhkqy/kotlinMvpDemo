package com.yilong.mvp.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;

import com.yilong.mvp.ZKApplication;


/**
 * Created by Magic on 2018/4/28.
 */

public class BitmapUtils {
    private static RenderScript rs = RenderScript.create(ZKApplication.Companion.getContext());
    private static ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));
    private static Type.Builder yuvType, rgbaType;
    private static Allocation in, out;


    public static Bitmap nv21ToBitmap(byte[] data, int width, int height) {
        if (yuvType == null) {
            yuvType = new Type.Builder(rs, Element.U8(rs)).setX(data.length);
            in = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);

            rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(width).setY(height);
            out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);
        }

        in.copyFrom(data);

        yuvToRgbIntrinsic.setInput(in);
        yuvToRgbIntrinsic.forEach(out);

        Bitmap bitmapOutput = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        out.copyTo(bitmapOutput);
        return bitmapOutput;
    }


    //镜像
    public static Bitmap convertBitmap(Bitmap srcBitmap) {
        int width = srcBitmap.getWidth();
        int height = srcBitmap.getHeight();

//        Bitmap newBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);

//        Canvas canvas = new Canvas();
        Matrix matrix = new Matrix();

        matrix.postScale(-1, 1);

        Bitmap newBitmap2 = Bitmap.createBitmap(srcBitmap, 0, 0, width, height, matrix, true);

//        canvas.drawBitmap(newBitmap2,
//                new Rect(0,0,width,height),
//                new Rect(0,0,width,height),null);

        return newBitmap2;
    }

    //将身份证图片放大，宽&高的像素均 > 150
    public static Bitmap scaledBitmap(Bitmap bitmap) {
        int WIDTH = 640;
        int width = bitmap.getWidth(), height = bitmap.getHeight();
        int dstWidth, dstHeight;
        if (width > height) {
            dstWidth = WIDTH;
            dstHeight = WIDTH * height / width;
        } else {
            dstHeight = WIDTH;
            dstWidth = WIDTH * width / height;
        }
        return Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, true);
    }

}
