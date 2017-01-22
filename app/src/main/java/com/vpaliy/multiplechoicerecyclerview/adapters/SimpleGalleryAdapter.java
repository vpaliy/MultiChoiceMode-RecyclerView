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


public class SimpleGalleryAdapter extends BaseAdapter {

    private int[] rawList;
    private LayoutInflater inflater;


    public SimpleGalleryAdapter(Context context, MultiMode mode, DataProvider dataProvider) {
        super(mode,false);
        inflater=LayoutInflater.from(context);
        this.rawList=dataProvider.rawImageDataBy(5);
    }

    public SimpleGalleryAdapter(Context context, MultiMode mode,DataProvider dataProvider, @NonNull Bundle savedInstanceState) {
        super(mode,false,savedInstanceState);
        inflater=LayoutInflater.from(context);
        this.rawList=dataProvider.rawImageDataBy(5);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.simple_item,parent,false));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.onBindData();
    }

    public class ViewHolder extends BaseAdapter.BaseViewHolder {

        private ImageView image;
        private ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);
            image=(ImageView)(itemView.findViewById(R.id.image));
            icon=(ImageView)(itemView.findViewById(R.id.icon));
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
            if(isChecked(getAdapterPosition())) {
                icon.setVisibility(View.VISIBLE);
                image.setColorFilter(0x88000000, PorterDuff.Mode.SRC_ATOP);
            }else {
                icon.setVisibility(View.INVISIBLE);
                image.clearColorFilter();
            }
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

