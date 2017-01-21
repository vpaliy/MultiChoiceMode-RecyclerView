package com.vpaliy.multiplechoicerecyclerview;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.vpaliy.multiplechoice.BaseAdapter;
import com.vpaliy.multiplechoice.MultiMode;

public class MainActivity extends AppCompatActivity {

    private Toolbar actionBar;
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();

    }

    private void initUI() {

        if(getSupportActionBar()==null) {
            actionBar = (Toolbar) (findViewById(R.id.actionBar));
            setSupportActionBar(actionBar);
        }

        int[] rawData=new int[]{R.drawable.eleven, R.drawable.fifteen, R.drawable.five,
                R.drawable.four, R.drawable.fourteen, R.drawable.seven, R.drawable.seventeen,
                R.drawable.six, R.drawable.sixteen, R.drawable.ten, R.drawable.thirt, R.drawable.three,
                R.drawable.two};

        RecyclerView recyclerView=(RecyclerView)(findViewById(R.id.recyclerView));
        recyclerView.setLayoutManager(new GridLayoutManager(this,getResources().
            getInteger(R.integer.span_size),GridLayoutManager.VERTICAL,false));
        MultiMode mode=new MultiMode.Builder(actionBar,R.menu.list_menu)
                        .setColor(Color.WHITE)
                        .build();
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter=new Adapter(this,mode,rawData));

    }

    @Override
    public void onBackPressed() {
        if(adapter!=null) {
            if(adapter.isMultiModeActivated()) {
                adapter.unCheckAll(true);
                return;
            }
        }
        super.onBackPressed();
    }

    public class Adapter extends BaseAdapter {

        private int[] arrayData;
        private LayoutInflater inflater;

        private static final float SCALE_F=0.85f;

        public Adapter(Context context,MultiMode mode, int[] arrayData) {
            super(mode,true);
            inflater=LayoutInflater.from(context);
            this.arrayData=arrayData;
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
                    .load(arrayData[getAdapterPosition()%arrayData.length])
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .thumbnail(0.5f)
                    .centerCrop().into(image);
                determineState();
            }

            @Override
            public void updateBackground() {
                //set background of image
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

        @Override
        public int getItemCount() {
            return arrayData.length;
        }
    }
}
