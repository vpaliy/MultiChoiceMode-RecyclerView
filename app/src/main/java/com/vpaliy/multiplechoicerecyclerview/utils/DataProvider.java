package com.vpaliy.multiplechoicerecyclerview.utils;


import com.vpaliy.multiplechoicerecyclerview.R;

public class DataProvider {

    private static DataProvider providerInstance;

    private DataProvider() {
      //  this.context=context;
    }

    //NOTE

    public int[] rawImageData() {
        return new int[]{R.drawable.eleven, R.drawable.fifteen, R.drawable.five,
                R.drawable.four, R.drawable.fourteen, R.drawable.seven, R.drawable.seventeen,
                R.drawable.six, R.drawable.sixteen, R.drawable.ten, R.drawable.thirt, R.drawable.three,
                R.drawable.two};
    }

    public int[] rawImageDataBy(int by) {
        int[] tempList=rawImageData();
        int[] rawList=new int[by*tempList.length];

        for(int index=0;index<by;index++) {
            for(int jIndex=0;jIndex<tempList.length;jIndex++) {
                rawList[jIndex+(index*tempList.length)]=tempList[jIndex];
            }
        }
        return rawList;
    }

    public static DataProvider defaultProvider() {
        if(providerInstance==null) {
            synchronized (DataProvider.class) {
                if(providerInstance==null){
                    providerInstance=new DataProvider();
                }
            }
        }
        return providerInstance;
    }

}
