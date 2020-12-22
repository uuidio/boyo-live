package com.kol.jumhz.common.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.LayoutAnimationController;
import android.widget.RelativeLayout;

import com.kol.jumhz.R;

/**
 * @ClassName: SwipeAnimationController
 * @Author: LanLnk
 * @CreateDate: 2020-05-06 16:15
 * @Description: 观众、主播的页面的滑动动画控制类
 */
public class SwipeAnimationController {

    private static final String TAG = SwipeAnimationController.class.getSimpleName();

    private Context mContext;
    private RelativeLayout mViewGroup;
    private float mInitX, mInitY;
    private ValueAnimator valueAnimator;
    private boolean isMoving = false;
    private float mTouchSlop;
    private float mScreenwidth;
    private LayoutAnimationController controller;

    public SwipeAnimationController(Context context) {
        this.mContext = context;

        mScreenwidth = context.getResources().getDisplayMetrics().widthPixels;
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        valueAnimator = new ValueAnimator();
        valueAnimator.setInterpolator(new AnticipateOvershootInterpolator());
        valueAnimator.setDuration(200);
        valueAnimator.addUpdateListener(animation -> {
            int x = (Integer) animation.getAnimatedValue();
            mViewGroup.setTranslationX(x);

        });
        Animation animation1 = AnimationUtils.loadAnimation(mContext, R.anim.anim_slice_in_right);
        animation1.setDuration(150);
        animation1.setInterpolator(new AccelerateDecelerateInterpolator());
        controller = new LayoutAnimationController(animation1);
        controller.setOrder(LayoutAnimationController.ORDER_REVERSE);
    }

    public void setAnimationView(RelativeLayout viewGroup) {
        this.mViewGroup = viewGroup;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public boolean processEvent(MotionEvent event) {


        if (valueAnimator.isRunning()) {
            return true;
        }

        VelocityTracker mVelTracker = VelocityTracker.obtain();

        int pointId = -1;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //记录初始位置
                mInitX = event.getRawX();
                mInitY = event.getRawY();
                mVelTracker.addMovement(event);
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = event.getRawX() - mInitX;
                float dy = event.getRawY() - mInitY;
                //根据初始位置计算移动方向与距离，判断是否为滑动手势
                if (!isMoving) {
                    if (Math.abs(dx) > mTouchSlop && Math.abs(dx) > Math.abs(dy)) {
                        isMoving = true;
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                int distance = (int) (event.getRawX() - mInitX);
                mVelTracker.addMovement(event);
                mVelTracker.computeCurrentVelocity(100);
                //获取x方向上的速度
                float velocityX = mVelTracker.getXVelocity(pointId);

                Log.d(TAG, "mVelocityX is " + velocityX);
                if (isMoving) {
                    //假如为滑动手势，启动相应动画（右滑隐藏 左滑出现）
                    if (distance >= mContext.getResources().getDisplayMetrics().widthPixels / 5 || velocityX > 1000f) {
                        if(mViewGroup != null && mViewGroup.getTranslationX() == 0){
                            valueAnimator.setIntValues(0, (int) mScreenwidth);
                            valueAnimator.start();
                        }
                    } else if(distance < 0 - mContext.getResources().getDisplayMetrics().widthPixels / 5) {
                        if(mViewGroup != null && mViewGroup.getTranslationX() > 0){

                            mViewGroup.setLayoutAnimation(null);
                            mViewGroup.setTranslationX((int) mScreenwidth);
                            mViewGroup.setLayoutAnimation(controller);
                            mViewGroup.startLayoutAnimation();
                            mViewGroup.setTranslationX(0);
                        }

                    }
                    isMoving = false;
                }

                mInitX = 0;
                mInitY = 0;

                mVelTracker.clear();
                mVelTracker.recycle();
                break;
            default:
        }
        return true;
    }
}
