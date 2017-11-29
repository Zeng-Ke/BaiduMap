package com.zk.baidumap;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * author: ZK.
 * date:   On 2017/11/28.
 */
public class MapProjectInfoWindowView extends FrameLayout {


    private LinearLayout mLlPopViewContent;
    private LinearLayout mLlArrowBottom;
    private LinearLayout mLlArrowTop;
    private TextView mTvName;


    public MapProjectInfoWindowView(Context context) {
        super(context);
        initView();
    }


    public MapProjectInfoWindowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MapProjectInfoWindowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MapProjectInfoWindowView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_map_pop_window, this, true);
        mLlPopViewContent = view.findViewById(R.id.ll_pop_view_content);
        mLlArrowBottom = view.findViewById(R.id.ll_arrow_bottom);
        mLlArrowTop = view.findViewById(R.id.ll_arrow_top);
        mTvName = view.findViewById(R.id.tv_name);
    }


    public void setData(final String name) {
        mTvName.setText(name);
    }


    public View getPopContentView() {
        return mLlPopViewContent;
    }

    public View getArrowBottomView() {
        return mLlArrowBottom;
    }

    public View getArrowTopView() {
        return mLlArrowTop;
    }

    public void setViewDirection(boolean up) {
        LayoutParams layoutParams = (LayoutParams) mLlPopViewContent.getLayoutParams();
        if (up) {
            mLlArrowTop.setVisibility(VISIBLE);
            mLlArrowBottom.setVisibility(GONE);
            layoutParams.bottomMargin = 0;
            layoutParams.topMargin = fromDip(45);
        } else {
            mLlArrowTop.setVisibility(GONE);
            mLlArrowBottom.setVisibility(VISIBLE);
            layoutParams.topMargin = 0;
            layoutParams.bottomMargin = fromDip(45);
        }
        mLlPopViewContent.setLayoutParams(layoutParams);
    }


    public FrameLayout.LayoutParams getPopViewContentLayoutParams() {
        return (FrameLayout.LayoutParams) mLlPopViewContent.getLayoutParams();
    }

    public void restoryPopViewContentLayoutParams() {
        LayoutParams layoutParams = getPopViewContentLayoutParams();
        layoutParams.leftMargin = 0;
        layoutParams.rightMargin = 0;
        setPopViewContentLayoutParams(layoutParams);
    }

    public void restoryPopViewTranslation() {
        setTranslationX(0);
        setTranslationY(0);
        mLlArrowBottom.setTranslationX(0);
        mLlArrowBottom.setTranslationY(0);
        mLlArrowTop.setTranslationX(0);
        mLlArrowTop.setTranslationY(0);

    }


    public void setPopViewContentLayoutParams(FrameLayout.LayoutParams layoutParams) {
        mLlPopViewContent.setLayoutParams(layoutParams);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }

    public static int fromDip(float dip) {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, displayMetrics);
    }


}
