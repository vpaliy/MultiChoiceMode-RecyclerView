package com.vpaliy.multiplechoice;


import android.Manifest;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.StyleableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;

public class MultiMode {

    private static final String TAG=MultiMode.class.getSimpleName();

    private static final String EMPTY="";

    private Toolbar actionBar;

    private ToolbarState currentState;
    private ToolbarState prevState;

    private boolean isActivated=false;
    private boolean isColored=false;
    private Vibrator vibrator;




    public MultiMode(Builder builder) {
        this.actionBar=builder.toolbar;
        this.prevState=initPrevState(actionBar);
        initCurrentState(builder);
        initVibrator();
    }

    private void initVibrator() {
        Context context=actionBar.getContext();
        if (ContextCompat.checkSelfPermission(actionBar.getContext(),
                Manifest.permission.VIBRATE) == PermissionChecker.PERMISSION_GRANTED) {
            vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        }

    }

    private void initCurrentState(Builder builder) {
        this.currentState=new ToolbarState();
        currentState.title=builder.title;
        currentState.subTitle=EMPTY;
        currentState.menuId=builder.menuId;
        currentState.toolbarColor = builder.toolbarColor;
    }

    private ToolbarState initPrevState(Toolbar toolbar) {
        ToolbarState prevState=new ToolbarState();
        if(toolbar.getBackground()!=null) {
            prevState.toolbarColor=((ColorDrawable)(toolbar.getBackground())).getColor();
        }
        Context context=toolbar.getContext();
        TypedValue typedValue = new TypedValue();
        TypedArray attr = context.obtainStyledAttributes(typedValue.data,
                new int[] {R.attr.colorPrimary, R.attr.colorPrimaryDark});
        if(attr!=null) {
            if (prevState.toolbarColor > 0) {
                prevState.toolbarColor = attr.getColor(0, 0);
            }
            @StyleableRes int index = 1;
            prevState.statusBarColor = attr.getColor(index, prevState.toolbarColor);
            attr.recycle();
        }

        if(toolbar.getTitle()!=null) {
            prevState.title=toolbar.getTitle().toString();
        }

        if(toolbar.getSubtitle()!=null) {
            prevState.subTitle=toolbar.getSubtitle().toString();
        }

        prevState.title=prevState.title!=null?prevState.title:EMPTY;
        prevState.subTitle=prevState.subTitle!=null?prevState.subTitle:EMPTY;

        prevState.logo=toolbar.getLogo();
        prevState.navigationIcon=toolbar.getNavigationIcon();

        return prevState;
    }

    private class ToolbarState {

        int menuId;
        int statusBarColor;
        int toolbarColor=-1;

        Drawable logo;
        Drawable navigationIcon;

        String title;
        String subTitle;

    }


    public static class Builder {

        private static final String DEFAULT_TITLE=" items selected";

        private Toolbar toolbar;
        private String title=DEFAULT_TITLE;
        private int toolbarColor=-1;
        private int icon;
        private int menuId;

        public Builder(@NonNull Toolbar toolbar, int menuId) {
            this.toolbar=toolbar;
            this.menuId=menuId;
            if(toolbar.getBackground()!=null) {
                toolbarColor=((ColorDrawable)(toolbar.getBackground())).getColor();
            }
        }

        public Builder setMenuId(int menuId) {
            this.menuId=menuId;
            return this;
        }

        public Builder setIcon(int icon) {
            if(icon>0) {
                this.icon = icon;
            }
            return this;
        }

        public Builder setColor(int color) {
            this.toolbarColor = color;
            return this;
        }

        public Builder setTitle(String title) {
            this.title=title;
            return this;
        }


        public MultiMode build() {
            return new MultiMode(this);
        }
    }


    void turnOn() {
        isActivated=true;

        if(actionBar.getTranslationX()!=0.f||actionBar.getTranslationY()!=0.f) {
            actionBar.setTranslationX(0.f);
            actionBar.setTranslationY(0.f);
        }

        if(actionBar.getVisibility()!= View.VISIBLE) {
            actionBar.setVisibility(View.VISIBLE);
        }

        if(actionBar.getAlpha()<1.f) {
            actionBar.setAlpha(1.f);
        }

        actionBar.inflateMenu(currentState.menuId);
    }

    void update(int itemCount) {
        if(vibrator!=null) {
            vibrator.vibrate(10);
        }

        actionBar.setTitle(Integer.toString(itemCount)+currentState.title);
        if(!isColored) {
            isColored = true;
            actionBar.setBackgroundColor(currentState.toolbarColor);
        }
    }

    void turnOff() {
        isActivated=false;
        isColored=false;
        actionBar.setBackgroundColor(prevState.toolbarColor);
        actionBar.setTitle(prevState.title);
        actionBar.setSubtitle(prevState.subTitle);
    }

    boolean isActivated() {
        return isActivated;
    }
}
