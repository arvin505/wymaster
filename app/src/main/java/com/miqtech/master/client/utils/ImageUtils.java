package com.miqtech.master.client.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.ViewGroup;
import android.widget.GridView;

import com.miqtech.master.client.application.WangYuApplication;

/**
 * Created by Administrator on 2015/11/20.
 */
public class ImageUtils {
    private static Context mContext = WangYuApplication.getGlobalContext();

    public static int calculateImgHeightWithBitmap(ViewGroup viewGroup, int resid) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), resid, options);
        int bitmapHeight = options.outHeight;
        int bitmapWidth = options.outWidth;
        int viewGroupWidth = viewGroup.getWidth();
        if (viewGroup instanceof GridView) {
            GridView gridView = (GridView) viewGroup;
            int padding = gridView.getPaddingLeft() + gridView.getPaddingRight();
            int columnCount = gridView.getNumColumns();
            int viewWidth = (int) ((viewGroupWidth - padding) / columnCount + 0.5);
            return viewWidth * bitmapHeight / bitmapWidth;
        } else {
            int padding = viewGroup.getPaddingLeft() + viewGroup.getPaddingRight();
            int viewWidth = viewGroupWidth - padding;
            return viewWidth * bitmapHeight / bitmapWidth;
        }
    }

    public static int calculateImgHeight(int width, int resid) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), resid, options);
        int bitmapHeight = options.outHeight;
        int bitmapWidth = options.outWidth;
        return width * bitmapHeight / bitmapWidth;
    }

    //获得圆角图片的方法
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float x, float y) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight() , Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), (int) (bitmap.getHeight() + y * 2));
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, x, y, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);


        return output;
    }

}
