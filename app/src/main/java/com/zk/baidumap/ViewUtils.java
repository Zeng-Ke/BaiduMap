package com.zk.baidumap;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * author: ZK.
 * date:   On 2017/11/28.
 */

public class ViewUtils {


    public static int getViewHeightOrWidth(View view, boolean isHeight) {
        int result;
        if (view == null) return 0;
        if (isHeight) {
            int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            view.measure(0, h);
            result = view.getMeasuredHeight();
        } else {
            int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            view.measure(w, 0);
            result = view.getMeasuredWidth();
        }
        return result;
    }


    public static int getViewHeight(View view) {
        return getViewHeightOrWidth(view, true);
    }

    public static int getViewWidth(View view) {
        return getViewHeightOrWidth(view, false);
    }


    public static void setViewWidthOrHeight(View view, int value, boolean isSetHeight) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (isSetHeight)
            layoutParams.height = value;
        else
            layoutParams.width = value;
        view.setLayoutParams(layoutParams);
    }

    public static void setViewHeight(View view, int value) {
        setViewWidthOrHeight(view, value, true);
    }

    public static void setViewWidth(View view, int value) {
        setViewWidthOrHeight(view, value, false);
    }


}
