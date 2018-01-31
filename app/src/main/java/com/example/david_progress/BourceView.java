package com.example.david_progress;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by 那个谁 on 2018/1/30.
 * 奥特曼打小怪兽
 * 作用：
 */

public class BourceView extends SurfaceView implements SurfaceHolder.Callback {

    //向下状态
    public static final int STATE_DOWN = 1;
    //向上状态
    public static final int STATE_UP = 2;
    //画笔
    private Paint mPaint;
    //路径
    private Path mPath;
    private int mLineColor;
    private int mPointColor;
    private int mLineWidth;
    private int mLineHeight;
    private float mDownDistance;
    private float mUpDistance;
    //自由落体距离
    private float freeBallDistance;

    //小球向下
    private ValueAnimator downControl;
    //小球向上
    private ValueAnimator upControl;
    //自由落体（水平点到最高点）
    private ValueAnimator freeDownControl;

    private AnimatorSet mAnimatorSet;
    //绳子状态判断
    private int state;

    //小球是否脱离绳子
    private boolean isBounce = false;
    //判断是否自由落体
    private boolean isBallFreeUp = false;
    //判断是否结束向上运动
    private boolean isUpControlDied = false;
    private boolean isAnimationShowing = false;
    public static final String TAG = "dongnao";



    public BourceView(Context context) {
        this(context,null);
    }

    public BourceView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BourceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //初始化
        inti(context,attrs);

    }

    private void inti(Context context, AttributeSet attrs) {
        //初始化参数
        intiAttributes(context,attrs);
        //全局变量的初始化
        mPaint = new Paint();
        //消除锯齿
        mPaint.setAntiAlias(true);
        //线宽
        mPaint.setStrokeWidth(mLineHeight);
        //设置圆角
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mPath = new Path();

        //设置监听
        getHolder().addCallback(this);

        //动画
        intiControl();

    }

    private void intiControl() {

        //设置小球向下
        downControl = ValueAnimator.ofInt(0,1);
        downControl.setDuration(500);
        downControl.setInterpolator(new DecelerateInterpolator());
        downControl.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                mDownDistance = 50*animation.getAnimatedFraction();

                //不断重绘
                postInvalidate();
            }
        });
        downControl.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

                state = STATE_DOWN;
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        //设置小球向上
        upControl = ValueAnimator.ofInt(0,1);
        upControl.setDuration(900);
        upControl.setInterpolator(new BounceInterplator());
        upControl.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mUpDistance = 50*animation.getAnimatedFraction();
                if (mUpDistance>=50){
                    isBounce = true;
                    if (!freeDownControl.isRunning()&&!freeDownControl.isStarted()&&!isBallFreeUp){
                        //开启自由落体
                        freeDownControl.start();
                    }
                }

                postInvalidate();

            }
        });
        upControl.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

                state = STATE_UP;
            }

            @Override
            public void onAnimationEnd(Animator animation) {

                isUpControlDied = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        //自由落体
        freeDownControl = ValueAnimator.ofFloat(0,6.8f);
        freeDownControl.setDuration(600);
        freeDownControl.setInterpolator(new DecelerateInterpolator());
        freeDownControl.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                float t = animation.getAnimatedFraction();
                //模拟自由落体
                freeBallDistance = 34*t-5*t*t;
                if (isUpControlDied){
                    postInvalidate();
                }

            }
        });
        freeDownControl.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

                //标识自由落体
                isBallFreeUp = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimationShowing = false;
                startTotalAnimation();

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play(downControl).before(upControl);
        mAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


    }

    public void startTotalAnimation() {
        if (isAnimationShowing){
            return;
        }
        if (mAnimatorSet.isRunning()){
            mAnimatorSet.end();
            mAnimatorSet.cancel();
        }
        isBallFreeUp = false;
        isBounce = false;
        isUpControlDied = false;
        mAnimatorSet.start();
    }

    private void intiAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.BounceProgress);
        //线的颜色
        mLineColor = typedArray.getColor(R.styleable.BounceProgress_line_color, Color.WHITE);
        //控件的宽度
        mLineWidth = typedArray.getDimensionPixelOffset(R.styleable.BounceProgress_line_width,200);
        //线条的宽度
        mLineHeight = typedArray.getDimensionPixelOffset(R.styleable.BounceProgress_line_height,3);
        //圆点的颜色
        mPointColor = typedArray.getColor(R.styleable.BounceProgress_point_color,Color.BLACK);
        typedArray.recycle();

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        //锁住画布
        Canvas canvas = holder.lockCanvas();
        draw(canvas);
        //解锁画布
        holder.unlockCanvasAndPost(canvas);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制线
        mPaint.setColor(mLineColor);
        mPath.reset();
        //起点坐标
        mPath.moveTo(getWidth()/2-mLineWidth/2,getHeight()/2);

        //绳子向下
        if (state ==STATE_DOWN){

            //左边控制点
            mPath.quadTo((float) (getWidth()/2-mLineWidth/2+0.375*mLineWidth),getHeight()/2+mDownDistance,
                    getWidth()/2,getHeight()/2+mDownDistance);
            //右边控制点
            mPath.quadTo((float) (getWidth()/2+mLineWidth/2-0.375*mLineWidth),getHeight()/2+mDownDistance,
                    getWidth()/2+mLineWidth/2,getHeight()/2);

            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawPath(mPath,mPaint);

            //中间小球
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mPointColor);
            canvas.drawCircle(getWidth()/2,getHeight()/2+mDownDistance-10,10,mPaint);



        }else if (state == STATE_UP){
            //绳子向上
            //左边控制点
            mPath.quadTo((float) (getWidth()/2-mLineWidth/2+0.375*mLineWidth),getHeight()/2+(50-mUpDistance),
                    getWidth()/2,getHeight()/2+(50-mUpDistance));
            //右边控制点
            mPath.quadTo((float) (getWidth()/2+mLineWidth/2-0.375*mLineWidth),getHeight()/2+(50-mUpDistance),
                    getWidth()/2+mLineWidth/2,getHeight()/2);
            mPaint.setStyle(Paint.Style.STROKE);

            canvas.drawPath(mPath,mPaint);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mPointColor);

            //小球脱离绳子
            if (!isBounce){
                canvas.drawCircle(getWidth()/2,getHeight()/2+(50-mUpDistance)-10,10,mPaint);
            }else {
                //小球未脱离绳子
                canvas.drawCircle(getWidth()/2,getHeight()/2-freeBallDistance-10,10,mPaint);
            }
        }

        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        //画左边点
        canvas.drawCircle(getWidth()/2-mLineWidth/2,getHeight()/2,10,mPaint);
        //画右边点
        canvas.drawCircle(getWidth()/2+mLineWidth/2,getHeight()/2,10,mPaint);


        super.onDraw(canvas);
    }

    //自定义插值器

    class BounceInterplator implements android.view.animation.Interpolator {


        @Override
        public float getInterpolation(float input) {
            return (float) (1-Math.exp(-3*input)*Math.cos(10*input));
        }
    }
}
