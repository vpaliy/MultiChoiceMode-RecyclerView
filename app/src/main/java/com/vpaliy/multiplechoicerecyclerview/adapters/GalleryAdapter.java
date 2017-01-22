package com.vpaliy.multiplechoicerecyclerview.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.vpaliy.multiplechoice.BaseAdapter;
import com.vpaliy.multiplechoice.MultiMode;
import com.vpaliy.multiplechoicerecyclerview.R;
import com.vpaliy.multiplechoicerecyclerview.utils.DataProvider;

public class GalleryAdapter extends BaseAdapter {

    protected int[] rawList;
    protected LayoutInflater inflater;

    private static final float SCALE_F=0.85f;

    public GalleryAdapter(Context context, MultiMode mode, DataProvider dataProvider) {
        super(mode,true);
        inflater=LayoutInflater.from(context);
        this.rawList=dataProvider.rawImageDataBy(5);
    }

    public GalleryAdapter(Context context, MultiMode mode,DataProvider dataProvider, @NonNull Bundle savedInstanceState) {
        super(mode,true,savedInstanceState);
        inflater=LayoutInflater.from(context);
        this.rawList=dataProvider.rawImageDataBy(5);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item,parent,false));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.onBindData();
    }

    public class ViewHolder extends BaseAdapter.BaseViewHolder {

        private ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            image=(ImageView)(itemView);
        }

        @Override
        public void onBindData() {
            Glide.with(itemView.getContext())
                    .load(rawList[getAdapterPosition() % rawList.length])
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .thumbnail(0.5f)
                    .centerCrop().into(image);
            determineState();
        }

        @Override
        public void updateBackground() {
            //
        }

        @Override
        public void enterState() {
            super.enterState();
            itemView.animate()
                    .scaleX(SCALE_F)
                    .scaleY(SCALE_F)
                    .setDuration(180);
        }

        @Override
        public void animatedState() {
            super.animatedState();
            itemView.setScaleX(SCALE_F);
            itemView.setScaleY(SCALE_F);
        }

        @Override
        public void exitState() {
            super.exitState();
            if(itemView.getScaleX()<1.f) {
                itemView.animate()
                        .scaleX(1.f)
                        .scaleY(1.f)
                        .setDuration(180);
            }

        }

        @Override
        public void defaultState() {
            super.defaultState();
            itemView.setScaleX(1.f);
            itemView.setScaleY(1.f);
        }
    }

    public void removeAt(int index) {
        int[] temp=new int[rawList.length-1];
        int currIndex=0;
        for(int jIndex=0;jIndex<rawList.length;jIndex++){
            if(jIndex==index)
                continue;
            temp[currIndex++]=rawList[jIndex];
        }
        rawList=temp;
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return rawList.length;
    }
}

