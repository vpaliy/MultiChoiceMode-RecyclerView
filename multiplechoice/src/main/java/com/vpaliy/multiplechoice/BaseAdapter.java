package com.vpaliy.multiplechoice;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

public abstract class BaseAdapter extends RecyclerView.Adapter<BaseAdapter.BaseViewHolder> {

    private static final String TAG=BaseAdapter.class.getSimpleName();

    private MultiMode mode;
    private final StateTracker tracker=new StateTracker();

    private boolean isAnimationEnabled=false;


    public BaseAdapter(@NonNull MultiMode mode, boolean isAnimationEnabled) {
        this.mode=mode;
        this.isAnimationEnabled=isAnimationEnabled;
    }

    public abstract class BaseViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener{

        public BaseViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        public final void determineState() {

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
            // animationState=ANIMATED;
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
                determineState();
                mode.update(tracker.getCheckedItemCount());
                if(tracker.getCheckedItemCount()==0) {
                    mode.turnOff();
                }
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
            mode.turnOff(); //not supposed to happen
        }
    }

    public int[] getAllChecked() {
        return tracker.getSelectedItemArray();
    }

}
