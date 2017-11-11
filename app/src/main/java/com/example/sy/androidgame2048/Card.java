package com.example.sy.androidgame2048;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class Card extends FrameLayout{
    private int num;
    private TextView label;

    public Card(Context context) {
        super(context);
//        Log.i("2048Blue", "one");
        initGameView(); //初始化

    }
    public Card(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        Log.i("2048Blue", "two");
        initGameView(); //初始化
    }

    public Card(Context context, AttributeSet attrs) {
        super(context, attrs);
//        Log.i("2048Blue", "three");
        initGameView();   //初始化
    }
    private void initGameView(){


        label = new TextView(getContext());
        label.setTextSize(32);
        label.setTextColor(0Xbcffffff);
        label.setBackgroundColor(0X33ffffff);
        label.setGravity(Gravity.CENTER);

        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//        Log.i("2048Blue", String.valueOf(LayoutParams.MATCH_PARENT));
        lp.setMargins(10, 10, 0, 0);
        addView(label, lp);
        label.requestLayout();

        setNum(0);
    }


    public int getNum() {
        return num;
    }


    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height= getMeasuredHeight();



        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {

                measureChild (child, widthMeasureSpec, heightMeasureSpec);
            }
        }
    }

    public void setNum(int num) {
        this.num = num;
        if(num <= 0){
            label.setText("");
        }
        else{
            label.setText(num+"");
        }
    }

    public boolean equals(Card c){
        return getNum() == c.getNum();
    }

}