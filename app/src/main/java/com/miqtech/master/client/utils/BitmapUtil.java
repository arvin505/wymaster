package com.miqtech.master.client.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2016/3/11.
 */
public class BitmapUtil {

    public static Bitmap fastblur(Bitmap sentBitmap, int radius) {

        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int temp = 256 * divsum;
        int dv[] = new int[temp];
        for (i = 0; i < temp; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
                        | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return (bitmap);
    }

    // 将Bitmap转换成InputStream
    public static InputStream Bitmap2InputStream(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        return is;
    }

    public static Bitmap cutOutBitmap(Bitmap bitmap, Context context) {
        int matchNameFontSize = (int) (context.getResources().getDimension(R.dimen.text_size_13));
        int titleMarginBottom = (int) (context.getResources().getDimension(R.dimen.invite_title_margin_bottom));
        int titleFontSize = (int) (context.getResources().getDimension(R.dimen.text_size_15));
        int titleMarginTop = (int) (context.getResources().getDimension(R.dimen.invite_title_margin_top));
        int errorMargin = 3;

        //二维码高度
        int codeHeight = 900;
        int y = matchNameFontSize + titleMarginBottom + titleFontSize + titleMarginTop + errorMargin + codeHeight / 3;
        int x = 300;
        Bitmap cutOutBitmap = Bitmap.createBitmap(bitmap, x, y, 300, 300);
        return cutOutBitmap;
    }

    /**
     * 合并两张bitmap为一张
     *
     * @param firstBitmap
     * @param secondBitmap
     * @return Bitmap
     */
    public static Bitmap mergeInviteShareBitmap(Context context, Bitmap firstBitmap, Bitmap secondBitmap, String matchName, String failureTime) {
        String wyMatchInvite = "网娱大师比赛战队邀请";

        int errorMargin = 3;
        int titleFontSize = (int) (context.getResources().getDimension(R.dimen.text_size_15));
        int matchNameFontSize = (int) (context.getResources().getDimension(R.dimen.text_size_13));
        int failureTimeFontSize = (int) (context.getResources().getDimension(R.dimen.text_size_12));

        int titleMarginTop = (int) (context.getResources().getDimension(R.dimen.invite_title_margin_top));
        int titleMarginBottom = (int) (context.getResources().getDimension(R.dimen.invite_title_margin_bottom));
        int failureTimeMarginBottom = (int) (context.getResources().getDimension(R.dimen.invite_failure_time_margin_bottom));
        //所有文字，边距的高度
        int fontHeight = titleFontSize + matchNameFontSize + failureTimeFontSize + titleMarginBottom + titleMarginBottom + failureTimeMarginBottom;

        int bitmap2Width = firstBitmap.getWidth();
        int secondBitmapWidth = secondBitmap.getWidth();
        int secondBitmapHeight = secondBitmap.getHeight();
        float scale = (float) secondBitmapHeight / secondBitmapWidth;
        float bitmap2height = bitmap2Width * scale;

        Bitmap newSecondBitmap = zoomImg(secondBitmap, bitmap2Width, (int) bitmap2height);
        Bitmap bitmap3 = Bitmap.createBitmap(firstBitmap.getWidth(),
                firstBitmap.getHeight() + newSecondBitmap.getHeight() + fontHeight + errorMargin * 3, firstBitmap.getConfig());

        Canvas canvas = new Canvas(bitmap3);
        Paint paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);
        //背景
        Rect whiteBg = new Rect(0, 0, firstBitmap.getWidth(), titleFontSize + titleMarginTop + matchNameFontSize + titleMarginBottom + errorMargin);
        paint.setColor(Color.WHITE);
        canvas.drawRect(whiteBg, paint);
        //title
        paint.setTextSize(titleFontSize);
        paint.setColor(context.getResources().getColor(R.color.black_extend_intro));
        canvas.drawText(wyMatchInvite, whiteBg.centerX(), titleFontSize + titleMarginTop, paint);
        //teamName
        paint.setColor(context.getResources().getColor(R.color.orange));
        paint.setTextSize(matchNameFontSize);
        canvas.drawText(matchName, whiteBg.centerX(), matchNameFontSize + titleMarginBottom + titleFontSize + titleMarginTop, paint);
        //第一张图
        canvas.drawBitmap(firstBitmap, 0, matchNameFontSize + titleMarginBottom + titleFontSize + titleMarginTop + errorMargin, null);
        //背景
        Rect whiteBg1 = new Rect(0, firstBitmap.getHeight() + matchNameFontSize + titleMarginBottom + titleFontSize + titleMarginTop, firstBitmap.getWidth(), firstBitmap.getHeight() + matchNameFontSize + titleMarginBottom + titleFontSize + titleMarginTop + failureTimeFontSize + failureTimeMarginBottom + errorMargin);
        paint.setColor(Color.WHITE);
        canvas.drawRect(whiteBg1, paint);
        //失效时间
        paint.setTextSize(failureTimeFontSize);
        paint.setColor(context.getResources().getColor(R.color.black_extend_intro));
        canvas.drawText(failureTime, whiteBg1.centerX(), firstBitmap.getHeight() + matchNameFontSize + titleMarginBottom + titleFontSize + titleMarginTop + failureTimeFontSize, paint);
        //第二张图
        canvas.drawBitmap(newSecondBitmap, 0, firstBitmap.getHeight() + matchNameFontSize + titleMarginBottom + titleFontSize + titleMarginTop + failureTimeFontSize + failureTimeMarginBottom - 10, null);

        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        //   FileManager.saveData(con);
        FileOutputStream out = null;
        try {
            File file = FileManager.getFile(context, FileManager.USER_FOLDER, FileManager.INVITE_CODING_NAME);
            out = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap3.compress(Bitmap.CompressFormat.PNG, 100, out);
        return bitmap3;
    }

    /**
     * @param bitmap     原图
     * @param edgeLength 希望得到的正方形部分的边长
     * @return 缩放截取正中部分后的位图。
     */
    public static Bitmap centerSquareScaleBitmap(Bitmap bitmap, int edgeLength) {
        if (null == bitmap || edgeLength <= 0) {
            return null;
        }

        Bitmap result = bitmap;
        int widthOrg = bitmap.getWidth();
        int heightOrg = bitmap.getHeight();

        //压缩到一个最小长度是edgeLength的bitmap
        int longerEdge = (int) (edgeLength * Math.max(widthOrg, heightOrg) / Math.min(widthOrg, heightOrg));
        int scaledWidth = widthOrg > heightOrg ? longerEdge : edgeLength;
        int scaledHeight = widthOrg > heightOrg ? edgeLength : longerEdge;
        Bitmap scaledBitmap;

        try {
            scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
        } catch (Exception e) {
            return null;
        }

        //从图中截取正中间的正方形部分。
        int xTopLeft = (scaledWidth - edgeLength) / 2;
        int yTopLeft = (scaledHeight - edgeLength) / 2;

        try {
            result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft, edgeLength, edgeLength);
            scaledBitmap.recycle();
        } catch (Exception e) {
            return null;
        }

        return result;
    }

    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    public static Bitmap convertToBlackWhite(Bitmap bmp) {
        int width = bmp.getWidth(); // 获取位图的宽
        int height = bmp.getHeight(); // 获取位图的高
        int[] pixels = new int[width * height]; // 通过位图的大小创建像素点数组

        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        int alpha = 0xFF << 24;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];

                int red = ((grey & 0x00FF0000) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);

                grey = (int) (red * 0.3 + green * 0.59 + blue * 0.11);
                grey = alpha | (grey << 16) | (grey << 8) | grey;
                pixels[width * i + j] = grey;
            }
        }
        Bitmap newBmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        newBmp.setPixels(pixels, 0, width, 0, 0, width, height);

        Bitmap resizeBmp = ThumbnailUtils.extractThumbnail(newBmp, bmp.getWidth(), bmp.getHeight());
        return resizeBmp;
    }

    public static Bitmap drawableToBitamp(Drawable drawable) {
        LogUtil.e("drawable", drawable.toString());
        BitmapDrawable bd = (BitmapDrawable) drawable;
        return bd.getBitmap();
    }

    /**
     * 处理图片
     *
     * @param bm        所要转换的bitmap
     * @param newWidth  新的宽
     * @param newHeight 新的高
     * @return 指定宽高的bitmap
     */
    public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }


    public static Bitmap makeRoundCorner(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int left = 0, top = 0, right = width, bottom = height;
        float roundPx = height / 2;
        if (width > height) {
            left = (width - height) / 2;
            top = 0;
            right = left + height;
            bottom = height;
        } else if (height > width) {
            left = 0;
            top = (height - width) / 2;
            right = width;
            bottom = top + width;
            roundPx = width / 2;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        int color = 0xff424242;
        Paint paint = new Paint();
        Rect rect = new Rect(left, top, right, bottom);
        RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
}
