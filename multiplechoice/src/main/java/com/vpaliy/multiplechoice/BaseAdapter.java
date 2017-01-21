package com.vpaliy.multiplechoice;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

public abstract class BaseAdapter extends RecyclerView.Adapter<BaseAdapter.BaseViewHolder> {

    private static final String TAG=BaseAdapter.class.getSimpleName();
    private static final String KEY="baseAdapter:stateTracker";

    private MultiMode mode;
    private final StateTracker tracker;

    private boolean isScreenRotation=false;
    private boolean isAnimationEnabled=false;


    public BaseAdapter(@NonNull MultiMode mode, boolean isAnimationEnabled) {
        this.mode=mode;
        this.isAnimationEnabled=isAnimationEnabled;
        this.tracker=new StateTracker();
    }

    //This constructor must be  called only to restore previous state
    public BaseAdapter(@NonNull MultiMode mode, boolean isAnimationEnabled, @NonNull Bundle savedInstanceState) {
        this.mode=mode;
        this.isAnimationEnabled=isAnimationEnabled;
        tracker=savedInstanceState.getParcelable(KEY);
        if(tracker==null) {
            throw new IllegalArgumentException();
        }

        isScreenRotation=true;

        if(tracker.getCheckedItemCount()>0) {
            mode.turnOn();
        }else {
            isScreenRotation = false;
        }

        notifyDataSetChanged();
    }

    public abstract class BaseViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener{

        public BaseViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        public final void determineState() {
            if(isScreenRotation) {
                isScreenRotation=false;
                if(mode.isActivated()) {
                    mode.update(tracker.getCheckedItemCount());
                }
            }

            if(isAnimationEnabled) {
                switch (tracker.getStateFor(getAdapterPosition())) {
                    case StateTracker.ENTER:
                        enterState();
                        break;
                    case StateTracker.ANIMATED:
                        animatedState();
                        break;
                    case StateTracker.EXIT:
                        exitState();
                        break;
                    default:
                        defaultState();
                }
            }
            updateBackground();
        }

        public abstract void updateBackground();

        @CallSuper
        public void enterState() {
            tracker.setStateFor(getAdapterPosition(),StateTracker.ANIMATED);
        }


        public void animatedState() {

        }

        @CallSuper
        public void exitState() {
            tracker.setStateFor(getAdapterPosition(),StateTracker.DEFAULT);
        }

        public void defaultState() {

        }

        @Override
        @CallSuper
        public void onClick(View view) {
            if(mode.isActivated()) {
                tracker.check(getAdapterPosition());
                mode.update(tracker.getCheckedItemCount());
                if (tracker.getCheckedItemCount() == 0) {
                    mode.turnOff();
                }
                determineState();
            }
        }

        public abstract void onBindData();

        @Override
        @CallSuper
        public  boolean onLongClick(View view) {
            if(!mode.isActivated()) {
                mode.turnOn();
            }
            return false;
        }
    }

    public boolean isMultiModeActivated() {
        return mode.isActivated();
    }

    public boolean isChecked(int position) {
        int state=tracker.getStateFor(position);
        return state==StateTracker.ENTER || state==StateTracker.ANIMATED;
    }


    public void checkAll(boolean animate) {
        if(!mode.isActivated()) {
            mode.turnOn();
        }
        for(int index=0;index<getItemCount();index++) {
            tracker.setStateFor(index,animate?StateTracker.ENTER:StateTracker.ANIMATED);
            notifyItemChanged(index);
        }
    }


    public void unCheckAll(boolean animate) {
        for(int index=0;index<getItemCount();index++) {
            if(isChecked(index)) {
                tracker.setStateFor(index, animate ? StateTracker.EXIT : StateTracker.DEFAULT);
                notifyItemChanged(index);
            }
        }
        if(mode.isActivated()) {
            Log.d(TAG,"After canceling:"+Integer.toString(tracker.getCheckedItemCount()));
            mode.turnOff();
        }
    }

    public int[] getAllChecked() {
        return tracker.getSelectedItemArray();

    }

    public void saveState(@NonNull Bundle outState) {
        if(mode.isActivated()) {
            mode.turnOff();
        }
        tracker.saveState(KEY,outState);
    }
}
