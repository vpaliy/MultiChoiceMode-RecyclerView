package com.vpaliy.multiplechoice;


import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import java.util.Arrays;
import java.util.Map;

class StateTracker implements Parcelable {

    static final int ENTER=0;
    static final int ANIMATED=1;
    static final int EXIT=2;
    static final int DEFAULT=3;

    private ArrayMap<Integer,Integer> stateMap=new ArrayMap<>();

    private int checkedItemCount;

    StateTracker(){}

    public StateTracker(Parcel in) {
        int size=in.readInt();
        if(size>0) {
            stateMap=new ArrayMap<>(size);
            for(int index=0;index<size;index++) {
                stateMap.put(in.readInt(),in.readInt());
            }
        }
    }

    int getStateFor(int position) {
        if (stateMap.get(position) == null)
            stateMap.put(position, DEFAULT);
        return stateMap.get(position);
    }

    void setStateFor(int position, int state) {
        if(stateMap.get(position)==null) {
            if(state==EXIT||state==DEFAULT) {
                return; //must be a mistake, so go back
            }
            stateMap.put(position, state);
            checkedItemCount++;
        }else {
            int tempState=stateMap.get(position);
            if(tempState!=state) {
                if(tempState==ANIMATED||tempState==ENTER) {
                    if(state==EXIT||state==DEFAULT) {
                        checkedItemCount--;
                    }
                }else if((state==ENTER)||(state==ANIMATED)) {
                    checkedItemCount++;
                }
                stateMap.put(position,state);
            }
        }
    }

    void check(int position) {
        if(stateMap.get(position)==null) {
            stateMap.put(position, ENTER);
            checkedItemCount++;
        }else {
            int state=stateMap.get(position);
            if(state==EXIT||state==DEFAULT) {
                stateMap.put(position, ENTER);
                checkedItemCount++;
            }else {
                stateMap.put(position, EXIT);
                checkedItemCount--;
            }
        }
    }

    int getCheckedItemCount() {
        return checkedItemCount;
    }


    int[] getSelectedItemArray(boolean cancel) {
        if(checkedItemCount==0)
            return null;
        int[] selectedItemArray = new int[stateMap.size()];
        int jIndex = 0;
        for (int index = 0; index < stateMap.size(); index++) {
            int state=stateMap.get(stateMap.keyAt(index));
            if ((state==ENTER)||(state==ANIMATED)) {
                if(cancel) {
                     stateMap.put(stateMap.keyAt(index),EXIT); //TODO EXIT only for visible items on the screen
                }
                selectedItemArray[jIndex++] = stateMap.keyAt(index);
            }
        }

        if(cancel) {
            checkedItemCount = 0;
        }
        Arrays.sort(selectedItemArray,0,jIndex);
        return Arrays.copyOfRange(selectedItemArray, 0, jIndex);
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeInt(stateMap.size());
        for(Map.Entry<Integer,Integer> entry:stateMap.entrySet()) {
            out.writeInt(entry.getKey());
            out.writeInt(entry.getValue());
        }
    }

    public static final Parcelable.Creator<StateTracker> CREATOR=new Creator<StateTracker>() {
        @Override
        public StateTracker createFromParcel(Parcel parcel) {
            return new StateTracker(parcel);
        }

        @Override
        public StateTracker[] newArray(int size) {
            return new StateTracker[size];
        }
    };

    public void saveState(String key, @NonNull Bundle outState) {
        outState.putParcelable(key,this);
    }
}
