package com.vpaliy.multiplechoice;


import android.support.v4.util.ArrayMap;
import java.util.Arrays;

class StateTracker {

    public static final int ENTER=0;
    public static final int ANIMATED=1;
    public static final int EXIT=2;
    public static final int DEFAULT=3;

    private ArrayMap<Integer,Integer> stateMap=new ArrayMap<>();

    private int checkedItemCount;



    public int getStateFor(int position) {
        if (stateMap.get(position) == null)
            stateMap.put(position, DEFAULT);
        return stateMap.get(position);
    }

    public void setStateFor(int position, int state) {
        if(stateMap.get(position)==null) {
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

    public void check(int position) {
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

    public int getCheckedItemCount() {
        return checkedItemCount;
    }


    public int[] getSelectedItemArray() {
        if(checkedItemCount==0)
            return null;
        int[] selectedItemArray = new int[stateMap.size()];
        int jIndex = 0;
        for (int index = 0; index < stateMap.size(); index++) {
            int state=stateMap.get(stateMap.keyAt(index));
            if ((state==ENTER)||(state==ANIMATED)) {
                stateMap.put(stateMap.keyAt(index),EXIT); //TODO or default ??
                selectedItemArray[jIndex++] = stateMap.keyAt(index);
            }
        }

        Arrays.sort(selectedItemArray,0,jIndex);
        int itemShift=0;
        int[] resultArray=new int[jIndex];

        for(int index=0;index<jIndex;index++,itemShift++)
            resultArray[index]=selectedItemArray[index]-itemShift;

        checkedItemCount=0;
        return resultArray;
    }




}
