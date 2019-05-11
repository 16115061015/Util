package com.hzy.cnn.CustomView.Ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hzy.cnn.waveview.R;

/**
 * 此刷新框架只能有一个子控件
 * Created by 胡泽宇 on 2018/8/3.
 */
//下拉刷新控件
public class FreshView extends LinearLayout {
    //下拉多少长度刷新
    private float lenghtofpulldown = 200;
    //上拉头部
    private View headView;
    //下拉加载底部
    private View DownView;
    //刷新框的大小
    private float FreshViewHeight = 200;
    //已经下拉或上拉的高度
    private float HighOfMove = 0;


    //是否需要刷新
    private boolean freshable = true;
    //是否需要加载
    private boolean loadable = true;

    private boolean canfresh=false;
    private boolean canload=false;
    //刷新的子控件类
    private View vchild;
    //上一次滑动的x,y
    float lastXIntercept, lastYIntercept, lastXtouch, lastYtouch;

    //在上滑还是下拉
    private boolean UpOrDownTF=false;
    //监听
    FreshViewListener freshVieListener;

    //ViewGroup的大小
    private float VGHeight, VGWidth;

    public void setFreshable(boolean freshable) {
        this.freshable = freshable;
    }

    public void setLoadable(boolean loadable) {
        this.loadable = loadable;
    }

    public void setFreshVieListener(FreshViewListener freshVieListener) {
        this.freshVieListener = freshVieListener;
    }

    public void setHeadView(View headView) {
        this.headView = headView;
    }

    public void setDownView(View downView) {
        DownView = downView;
    }

    public FreshView(Context context) {
        this(context, null);
    }

    public FreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        //初始参数
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FreshView);
        lenghtofpulldown = typedArray.getDimension(R.styleable.FreshView_LengthOfPullDown, 200);
        FreshViewHeight = typedArray.getDimension(R.styleable.FreshView_HeightOfView, 200);
        int id = typedArray.getResourceId(R.styleable.FreshView_HeadRefreshView, -1);
        int id2 = typedArray.getResourceId(R.styleable.FreshView_DownRefreshView, -1);


        this.setOrientation(VERTICAL);
        //默认刷新加载部位
        TextView tvhead = new TextView(context);
        tvhead.setGravity(Gravity.CENTER);
        tvhead.setText("松开刷新");
        TextView tvDown = new TextView(context);
        tvDown.setText("加载中");
        tvDown.setGravity(Gravity.CENTER);
        //如果没有指定属性给定的顶部和底部的话就用默认的
        if (headView == null) {
            if (id == -1) {
                headView = tvhead;
            } else {
                headView = View.inflate(context, id, null);
            }
        }
        if(DownView==null) {
            if (id2 == -1) {
                DownView = tvDown;
            } else {
                DownView = View.inflate(context, id2, null);
            }
        }

        //添加至容器
        this.addView(headView);
        this.addView(DownView);

        this.setClickable(true);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = false;
        float x = ev.getX();
        float y = ev.getY();
        switch (ev.getAction()) {
            /*如果拦截了Down事件,则子类不会拿到这个事件序列*/
            case MotionEvent.ACTION_DOWN:
                lastXtouch = x;
                lastYtouch = y;
                lastXIntercept = x;
                lastYIntercept = y;
                intercepted = false;
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaX = x - lastXIntercept;
                final float deltaY = y - lastYIntercept;
                /*根据条件判断是否拦截该事件*/
                //此处条件为Y轴滑动
                intercepted=false;
                if (Math.abs(deltaX) < Math.abs(deltaY)) {
                    //此处进行判断
                    //如果子控件不为viewgroup的子类的话拦截改操作
                    if (!(vchild instanceof ViewGroup)) {
                      //  Log.i("滑动拦截(子控件为不可滑动类)", "onInterceptTouchEvent: +true");
                        intercepted = true;
                        canfresh=true;
                        canload=true;
                    }
                    //TODO 判断子控件是否达到了滑动边界判断是否拦截事件

                    if(vchild instanceof ScrollView){
                        int scrollY=((ScrollView)vchild).getScrollY();
                        Log.i("scrollview", "onInterceptTouchEvent: Y:"+scrollY+" height:"+((ScrollView)vchild).getHeight()+" MeasureHeight:"+((ScrollView)vchild).getChildAt(0).getHeight());
                        if(deltaY>0){
                            //下滑判断
                            if (scrollY==0){
                                //滑动到了顶部，可以刷新
                            //    Log.i("顶部可以拦截", "onInterceptTouchEvent: ");
                                intercepted=true;
                                canfresh=true;
                            }
                        }else{
                            //滑动的距离加上展示的高度等于整个View所需要的高度
                            if((scrollY+((ScrollView)vchild).getHeight())>=((ScrollView)vchild).getChildAt(0).getHeight()){
                                //滑到了底部,可以加载
                            //    Log.i("底部可以拦截", "onInterceptTouchEvent: ");
                                intercepted=true;
                                canload=true;
                            }
                        }
                    }
                    if(vchild instanceof AbsListView){
                        Log.i("高度", "onInterceptTouchEvent: "+((AbsListView) vchild).getMeasuredHeight());
                         if(deltaY>0) {
                            if (((AbsListView) vchild).getFirstVisiblePosition() == 0&&((AbsListView) vchild).getChildAt(0).getTop()==0) {
                                //可以刷新
                                intercepted = true;
                                canfresh = true;
                            }
                        }else {
                            int lastPosition = ((AbsListView) vchild).getLastVisiblePosition();
                            int count = ((AbsListView) vchild).getCount();
                            if (lastPosition == count - 1&&((AbsListView) vchild).getChildAt(((AbsListView) vchild).getChildCount()-1).getBottom()==((AbsListView) vchild).getHeight()||((AbsListView) vchild).getChildAt(((AbsListView) vchild).getChildCount()-1).getBottom()<((AbsListView) vchild).getHeight()){
                                intercepted = true;
                                canload = true;
                            }
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                intercepted = false;
                break;
        }

        lastXIntercept = x;
        lastYIntercept = y;
        return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX(), y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (lastYtouch > y) {
                    //向上滑动
                    UpOrDownTF=true;
                    Log.i("向上滑动", "onTouchEvent: ");
                    if(loadable&&canload) {
                        HighOfMove += Math.abs((lastYtouch - y));
                        if (HighOfMove > FreshViewHeight) {
                            HighOfMove = FreshViewHeight;
                        }
                        this.scrollTo(0, (int) HighOfMove);
                    }
                } else {
                    UpOrDownTF=false;
                    //向下滑动
                    if(freshable&&canfresh) {
                        Log.i("向下滑动", "onTouchEvent: ");
                        HighOfMove += Math.abs((lastYtouch - y));
                        if (HighOfMove > FreshViewHeight) {
                            HighOfMove = FreshViewHeight;
                        }
                        this.scrollTo(0, (int) -HighOfMove);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (HighOfMove >= FreshViewHeight) {
                    //刷新
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(4000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } finally {
                                FreshView.this.scrollTo(0, 0);
                                //通知刷新完成
                                if (freshVieListener != null) {
                                    if (UpOrDownTF) {
                                        freshVieListener.LoadFinish();
                                    }else{
                                        freshVieListener.FreshFinish();
                                    }
                                }
                            }
                        }
                    }).start();
                } else {
                    //隐藏
                    this.scrollTo(0, 0);

                }
                canfresh=false;
                canload=false;
                HighOfMove = 0;
                lastYtouch = 0;
                Log.i("UP事件", "UP ");
                break;
        }
        lastXtouch = x;
        lastYtouch = y;
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        //TODO
        Log.i("位置", "onLayout: " + l + " " + t + " " + r + " " + b);
        this.getChildAt(0).layout(0, (int) -FreshViewHeight, getMeasuredWidth(), 0);
        this.getChildAt(2).layout(l, t, r, b);
        this.getChildAt(1).layout(0, getMeasuredHeight(), getMeasuredWidth(), (int) (getMeasuredHeight() + FreshViewHeight));
        //获取子控件
        vchild = this.getChildAt(2);
        //设置为可触控才可以消费掉down事件，在拦截事件时才会进入move
        vchild.setClickable(true);
        //需要设置为可触摸才能捕捉到UP和move事件
        if (vchild == null || this.getChildCount() > 3) {
            Log.e("错误", "子控件数量错误");
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        VGHeight = MeasureSpec.getSize(heightMeasureSpec);
        VGWidth = MeasureSpec.getSize(widthMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public interface FreshViewListener {
        public void FreshFinish();
        public void LoadFinish();
    }
}
