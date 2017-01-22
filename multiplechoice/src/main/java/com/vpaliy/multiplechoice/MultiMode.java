package com.vpaliy.multiplechoice;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.StyleableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;



public class MultiMode {

    private static final String TAG=MultiMode.class.getSimpleName();

    private static final String EMPTY="";
    private static BaseAdapter adapterInstance;

    private Toolbar actionBar;
    private Activity activity;
    private Callback callback;

    private ToolbarState currentState;
    private ToolbarState prevState;

    private boolean isActivated=false;
    private boolean isColored=false;

    private Vibrator vibrator;
    private int vibrationLength=10; //be default it's 10

    public MultiMode(Builder builder) {
        this.actionBar=builder.toolbar;
        this.prevState=initPrevState(actionBar);
        this.activity=builder.activity;
        this.callback=builder.callback;
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
        currentState=new ToolbarState();
        currentState.title=builder.title;
        currentState.subTitle=EMPTY;
        currentState.menuId=builder.menuId;
        currentState.statusBarColor=builder.statusBarColor;
        currentState.toolbarColor = builder.toolbarColor;
        currentState.logo=builder.logo;
        currentState.navigationIcon=builder.navigationIcon;
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

        Set<MenuItem> menuItems;

    }


    public static class Builder {

        private static final String DEFAULT_TITLE=" items selected";

        private Toolbar toolbar;
        private String title=DEFAULT_TITLE;
        private int toolbarColor;
        private int statusBarColor;
        private Drawable navigationIcon;
        private Drawable logo;
        private int menuId=-1;

        private Callback callback;
        private Activity activity;

        public Builder(@NonNull Toolbar toolbar, @NonNull Activity activity) {
            this.toolbar=toolbar;
            this.activity=activity;
            if(toolbar.getBackground()!=null) {
                toolbarColor=((ColorDrawable)(toolbar.getBackground())).getColor();
            }
            this.navigationIcon=toolbar.getNavigationIcon();
            this.logo=toolbar.getLogo();
        }

        public Builder setMenu(int menuId, @NonNull Callback callback) {
            this.menuId = menuId;
            this.callback=callback;
            return this;
        }

        public Builder setNavigationIcon(Drawable navigationIcon) {
            if(navigationIcon!=null) {
                this.navigationIcon = navigationIcon;
            }
            return this;
        }

        public Builder setLogo(Drawable logo) {
            if (logo != null) {
                this.logo = logo;
            }
            return this;
        }

        public Builder setBackgroundColor(int color) {
            this.toolbarColor = color;
            return this;
        }

        public Builder setStatusBarColor(int statusBarColor) {
            this.statusBarColor=statusBarColor;
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

    private void savePrevMenuState() {
        Menu menu=actionBar.getMenu();
        if(menu!=null) {
            prevState.menuItems=new HashSet<>(menu.size());
            for(int index=0;index<menu.size();index++) {
                prevState.menuItems.add(menu.getItem(index));
            }
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

        actionBar.setNavigationIcon(currentState.navigationIcon);
        actionBar.setLogo(currentState.logo);

        if(currentState.menuId!=-1) {
            actionBar.post(new Runnable() {
                @Override
                public void run() {
                    savePrevMenuState();
                    actionBar.inflateMenu(currentState.menuId);
                    actionBar.setOnMenuItemClickListener(callback);
                }
            });
        }

    }

    void update(int itemCount, int vibrationLength) {
        if(vibrationLength>0) {
            this.vibrationLength=vibrationLength;
        }
        update(itemCount);
    }

    void update(int itemCount) {
        if(vibrator!=null) {
            vibrator.vibrate(vibrationLength);
        }

        actionBar.setTitle(Integer.toString(itemCount)+currentState.title);
        if(!isColored) {
            isColored = true;
            if(currentState.statusBarColor!=0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = activity.getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(currentState.statusBarColor);
                }
            }
            actionBar.setBackgroundColor(currentState.toolbarColor);
        }
    }

    void setAdapter(BaseAdapter adapter) {
        adapterInstance=adapter;
    }

    void turnOff() {
        isActivated=false;
        isColored=false;

        if(currentState.menuId!=-1) {
            Menu menu = actionBar.getMenu();
            List<MenuItem> menuItemList=new LinkedList<>();
            for (int index = 0; index < menu.size(); index++) {
                MenuItem item = menu.getItem(index);
                if (prevState.menuItems == null || !prevState.menuItems.contains(item)) {
                    menuItemList.add(item);
                }
            }

            if(!menuItemList.isEmpty()) {
                for (MenuItem item : menuItemList) {
                    menu.removeItem(item.getItemId());
                }
            }
        }

        actionBar.setNavigationIcon(prevState.navigationIcon);
        actionBar.setLogo(prevState.logo);
        if(prevState.statusBarColor!=0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = activity.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(prevState.statusBarColor);
            }
        }
        actionBar.setBackgroundColor(prevState.toolbarColor);
        actionBar.setTitle(prevState.title);
        actionBar.setSubtitle(prevState.subTitle);
    }

    boolean isActivated() {
        return isActivated;
    }

    public static abstract class Callback implements Toolbar.OnMenuItemClickListener {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            return onMenuItemClick(adapterInstance,item);
        }

        public abstract boolean onMenuItemClick(BaseAdapter adapter, MenuItem item);

    }
}
