package com.example.sy.androidgame2048;

import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.widget.GridLayout;
import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.util.DisplayMetrics;


public class GameView extends GridLayout{
    public GameView(Context context) {
        super(context);
        initGameView();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initGameView(); //初始化
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGameView();   //初始化

    }

    //初始化Game界面
    private void initGameView(){
        setColumnCount(4);
        setBackgroundColor(Color.parseColor("#507886"));
        //setBackgroundColor(0Xffbbada0);
        //OXffbbada0
        addCards(GetCardWidth(),GetCardWidth());
        setOnTouchListener(new OnTouchListener() {
            // 定义变量
            private float startX,startY,offsetX,offsetY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {                  //得到用户操作
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        offsetX = event.getX() - startX;
                        offsetY = event.getY() - startY;

                        if(Math.abs(offsetX) > Math.abs(offsetY)){
                            if(offsetX < -5){
//							System.out.println("left");
                                slipLeft();
                            }
                            else if(offsetX > 5){
//							System.out.println("right");
                                slipRight();
                            }
                        }
                        else{
                            if(offsetY < -5){
//							System.out.println("up");
                                slipUp();
                            }
                            else if(offsetY > 5){
//							System.out.println("down");
                                slipDown();
                            }
                        }

                        break;

                }
                return true;
            }
        });

    }
    private int GetCardWidth()
    {

        //屏幕信息的对象
        DisplayMetrics displayMetrics;
        displayMetrics = getResources().getDisplayMetrics();

        //获取屏幕信息
        int cardWidth;
        cardWidth = displayMetrics.widthPixels;

        //一行有四个卡片，每个卡片占屏幕的四分之一
        return ( cardWidth - 10 ) / 4;

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int cardWidth = (Math.min(w, h)-10)/4;
//        try {
//            addCards(cardWidth,cardWidth);
//        }catch (NullPointerException e){
//
//        }


        startGame();
    }

    private void addCards(int cardWidth, int cardHeight){
        Card c;
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                c = new Card(getContext());
                c.setNum(0);
                cardsMap[x][y] = c;

                addView(c, cardWidth, cardHeight);
                c.requestLayout();

            }

        }

    }

    private void startGame(){
        MainActivity.getMainActivity().clearScore();
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                cardsMap[x][y].setNum(0);
            }
        }

        addRandomNum();
        addRandomNum();
    }

    //添加随机数
    private void addRandomNum(){

        emptyPoints.clear();

        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                if(cardsMap[x][y].getNum() <= 0){
                    emptyPoints.add(new Point(x,y));
                }

            }

        }

        Point p = emptyPoints.remove((int)(Math.random()*emptyPoints.size()));	//随机移除一个点
        cardsMap[p.x][p.y].setNum(Math.random()>0.1?2:4);

    }

    private void checkComplete(){
        boolean complete = true;

        ALL:
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                if(cardsMap[x][y].getNum() == 0 ||
                        (x>0 && cardsMap[x][y].equals(cardsMap[x-1][y])) ||
                        (x<3 && cardsMap[x][y].equals(cardsMap[x+1][y])) ||
                        (y>0 && cardsMap[x][y].equals(cardsMap[x][y-1])) ||
                        (y<3 && cardsMap[x][y].equals(cardsMap[x][y+1]))){

                    complete = false;
                    break ALL;
                }

            }

        }

        if(complete){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("哎呦，不好了").setMessage("游戏被你玩完了哦。");
            builder.setPositiveButton(R.string.play_again, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startGame();
                }
            });
            builder.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity.getMainActivity().quitGame();
                }
            });
            builder.create().show();
        }
    }

    private void slipLeft(){
        boolean merge = false;

        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                for (int x1 = x+1; x1 < 4; x1++) { //横轴上比较
                    if(cardsMap[x1][y].getNum() > 0){ //只对有数的进行操作

                        if(cardsMap[x][y].getNum() <= 0){ //有空格，则把右边的放到左边来
                            cardsMap[x][y].setNum(cardsMap[x1][y].getNum());
                            cardsMap[x1][y].setNum(0);
                            merge = true;
                            x--;
                        }
                        else if(cardsMap[x][y].equals(cardsMap[x1][y])){ // 如果两个相等，则合并
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
                            cardsMap[x1][y].setNum(0);
                            merge = true;
                            MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());
                        }
                        break;
                    }

                }
            }

        }
        if(merge){
            addRandomNum();
            checkComplete();
        }

    }
    private void slipRight(){
        boolean merge = false;

        for (int y = 0; y < 4; y++) {
            for (int x = 3; x >= 0; x--) {
                for (int x1 = x-1; x1 >= 0; x1--) {
                    if(cardsMap[x1][y].getNum() > 0){

                        if(cardsMap[x][y].getNum() <= 0){
                            cardsMap[x][y].setNum(cardsMap[x1][y].getNum());
                            cardsMap[x1][y].setNum(0);
                            merge = true;
                            x++;
                        }
                        else if(cardsMap[x][y].equals(cardsMap[x1][y])){
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
                            cardsMap[x1][y].setNum(0);
                            merge = true;
                            MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());
                        }
                        break;
                    }

                }
            }

        }
        if(merge){
            addRandomNum();
            checkComplete();
        }

    }
    private void slipUp(){
        boolean merge = false;

        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                for (int y1 = y+1; y1 < 4; y1++) {
                    if(cardsMap[x][y1].getNum() > 0){

                        if(cardsMap[x][y].getNum() <= 0){
                            cardsMap[x][y].setNum(cardsMap[x][y1].getNum());
                            cardsMap[x][y1].setNum(0);
                            merge = true;
                            y--;
                        }
                        else if(cardsMap[x][y].equals(cardsMap[x][y1])){
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
                            cardsMap[x][y1].setNum(0);
                            merge = true;
                            MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());
                        }
                        break;
                    }

                }
            }

        }
        if(merge){
            addRandomNum();
            checkComplete();
        }

    }
    private void slipDown(){
        boolean merge = false;

        for (int x = 0; x < 4; x++) {
            for (int y = 3; y >= 0; y--) {
                for (int y1 = y-1; y1 >=0; y1--) {
                    if(cardsMap[x][y1].getNum() > 0){

                        if(cardsMap[x][y].getNum() <= 0){
                            cardsMap[x][y].setNum(cardsMap[x][y1].getNum());
                            cardsMap[x][y1].setNum(0);
                            merge = true;
                            y++;
                        }
                        else if(cardsMap[x][y].equals(cardsMap[x][y1])){
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
                            cardsMap[x][y1].setNum(0);
                            merge = true;
                            MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());
                        }
                        break;
                    }

                }
            }

        }
        if(merge){
            addRandomNum();
            checkComplete();
        }

    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        int height= getMeasuredHeight();
//        Log.i("2048Blue","getMeasure(Father)" +String.valueOf(height));
        final int count = getChildCount();
//        Log.i("2048Blue","getMeasure(Count(" +String.valueOf(count));
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                //Make or work out measurements for children here (MeasureSpec.make...)
                measureChild (child, widthSpec, heightSpec);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        Log.i("2048Blue","onDraw");
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
//        Log.i("2048Blue","onLayout");
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);

//        Log.i("2048Blue","onWindowsFoucusChanged");
    }

    private Card[][] cardsMap = new Card[4][4];
    private List<Point> emptyPoints = new ArrayList<Point>();
}