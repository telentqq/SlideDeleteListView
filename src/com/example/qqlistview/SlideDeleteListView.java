
package com.example.qqlistview;


import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

public class SlideDeleteListView extends ListView {

    private int xDown;

    private int yDown;

    private int xMove;

    private int yMove;

    private int slide;

    private boolean isSliding;

    private PopupWindow popupWindow;

    private LayoutInflater inflater;

    private Button delButton;

    private View currentView;

    private int currentViewPos;

    private int popupWindowHeight;

    private int popupWindowWidth;
    
    private boolean clearFocus;
    
    private DelButtonClickListener listener;

    public SlideDeleteListView(Context context) {
        this(context, null);
    }

    public SlideDeleteListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        slide = ViewConfiguration.get(context).getScaledTouchSlop();
        inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.delete_btn, null);
        delButton = (Button) view.findViewById(R.id.delete_btn);
        popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.getContentView().measure(0, 0);
        popupWindowHeight = popupWindow.getContentView().getMeasuredHeight();
        popupWindowWidth = popupWindow.getContentView().getMeasuredWidth();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!popupWindow.isShowing()) {
                    xDown = x;
                    yDown = y;
                    currentViewPos = pointToPosition(xDown, yDown);
                    currentView = getChildAt(currentViewPos - getFirstVisiblePosition());
                }else {
                    clearFocus = true;
                }
     
                break;
            case MotionEvent.ACTION_MOVE:
                xMove = x;
                yMove = y;
                int dx = xMove - xDown;
                int dy = yMove - yDown;
                if (xMove < xDown && Math.abs(dx) > slide && Math.abs(dy) < slide) {
                    isSliding = true;
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (isSliding) {
            switch (action) {
                case MotionEvent.ACTION_MOVE:
                    int[] location = new int[2];
                    currentView.getLocationOnScreen(location);
                    popupWindow.setAnimationStyle(R.style.popwindow_delete_btn_anim_style);
                    popupWindow.showAtLocation(currentView, Gravity.NO_GRAVITY, location[0]
                            + currentView.getWidth(), location[1] + currentView.getHeight() / 2
                            - popupWindowHeight / 2);
                    delButton.setOnClickListener(new OnClickListener() {
                        
                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            if (listener != null) {
                                listener.clickItem(currentViewPos);
                                popupWindow.dismiss();
                            }
                        }
                    });
                    break;

                case MotionEvent.ACTION_UP:
                    isSliding = false;
                    break;
            }
            return true;
        }else if (clearFocus) {
            popupWindow.dismiss();
            currentView.clearFocus();
        }
        return super.onTouchEvent(ev);
    }
    
    
    public void setListener(DelButtonClickListener listener) {
        this.listener = listener;
    }

    public interface DelButtonClickListener {
        void clickItem(int pos);
    }
}
