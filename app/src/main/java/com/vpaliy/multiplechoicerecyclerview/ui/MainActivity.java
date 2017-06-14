package com.vpaliy.multiplechoicerecyclerview.ui;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.vpaliy.multiplechoice.BaseAdapter;
import com.vpaliy.multiplechoice.MultiMode;
import com.vpaliy.multiplechoicerecyclerview.R;
import com.vpaliy.multiplechoicerecyclerview.adapters.GalleryAdapter;
import com.vpaliy.multiplechoicerecyclerview.adapters.MixedGalleryAdapter;
import com.vpaliy.multiplechoicerecyclerview.adapters.SimpleGalleryAdapter;
import com.vpaliy.multiplechoicerecyclerview.utils.DataProvider;
import com.vpaliy.multiplechoicerecyclerview.utils.MarginDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.State;

import static butterknife.ButterKnife.findById;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.actionBar)
    protected Toolbar actionBar;

    @BindView(R.id.recyclerView)
    protected RecyclerView recyclerView;

    private BaseAdapter adapter;
    private MultiMode mode;

    @State
    protected int currentExample;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Icepick.restoreInstanceState(this,savedInstanceState);
        initUI(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null) {
            adapter.onResume();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void initUI(Bundle savedInstanceState) {

        initActionBar();
        initNavigation();

        recyclerView.addItemDecoration(new MarginDecoration(this));
        mode=new MultiMode.Builder(actionBar,this)
                .setMenu(R.menu.list_menu,new MultiMode.Callback() {
                    @Override
                    public boolean onMenuItemClick(BaseAdapter adapter, MenuItem item) {
                        if(adapter!=null) {
                            switch (item.getItemId()) {
                                case R.id.checkAll:
                                    adapter.checkAll(true);
                                    return true;
                                case R.id.unCheckAll:
                                    adapter.unCheckAll(true);
                                    return true;
                                case R.id.share:
                                    adapter.getAllChecked(true);
                                    break;
                                case R.id.delete: {
                                    int[] deleteIndices = adapter.getAllCheckedForDeletion();
                                    if(deleteIndices!=null) {
                                        for (int index : deleteIndices) {
                                            adapter.removeAt(index);
                                        }
                                    }
                                    return true;
                                }
                            }
                        }
                        return false;
                    }
                })
                .setStatusBarColor(Color.MAGENTA)
                .setBackgroundColor(Color.WHITE)
                .setNavigationIcon(ContextCompat.getDrawable(this,R.drawable.ic_clear))
                .build();


        recyclerView.setItemAnimator(null);
        adapter=provideAdapter(savedInstanceState);
        recyclerView.setAdapter(adapter);

    }

    private BaseAdapter provideAdapter(Bundle state) {

        switch (currentExample) {
            case R.id.animatedGallery:
                if (state != null) {
                    return new GalleryAdapter(this, mode, DataProvider.defaultProvider(), state);
                }
                return new GalleryAdapter(this, mode, DataProvider.defaultProvider());
            case R.id.simpleGallery:
                if (state != null) {
                    return new SimpleGalleryAdapter(this, mode, DataProvider.defaultProvider(), state);
                }
                return new SimpleGalleryAdapter(this, mode, DataProvider.defaultProvider());
            default:
                if(state!=null) {
                    return new MixedGalleryAdapter(this, mode, DataProvider.defaultProvider(), state);
                }
                return new MixedGalleryAdapter(this,mode,DataProvider.defaultProvider());
        }
    }


    private void initActionBar() {
        actionBar.setTitle(R.string.example);
        actionBar.setTitleTextColor(ContextCompat.getColor(this,R.color.colorWhite));
        setSupportActionBar(actionBar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        actionBar.setNavigationOnClickListener(click -> onBackPressed());
    }

    private void initNavigation() {
        DrawerLayout layout=findById(this,R.id.drawerLayout);
        NavigationView navigationView=findById(this,R.id.navigation);
        navigationView.setCheckedItem(currentExample);
        navigationView.setNavigationItemSelectedListener(item-> {
            adapter.unCheckAll(false);
            switch (item.getItemId()) {
                case R.id.animatedGallery:
                    currentExample=R.id.animatedGallery;
                    adapter=provideAdapter(null);
                    recyclerView.setAdapter(adapter);
                    actionBar.setTitle(R.string.animatedExample);
                    break;
                case R.id.simpleGallery:
                    currentExample=R.id.simpleGallery;
                    adapter=provideAdapter(null);
                    recyclerView.setAdapter(adapter);
                    actionBar.setTitle(R.string.galleryExample);
                    break;
                case R.id.mixedGallery:
                    currentExample=R.id.mixedGallery;
                    adapter=provideAdapter(null);
                    recyclerView.setAdapter(adapter);
                    actionBar.setTitle(R.string.mixedExample);
                    break;
            }
            layout.closeDrawers();
            return true;
        });
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


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        adapter.saveState(outState);
        Icepick.saveInstanceState(this, outState);
    }
}
