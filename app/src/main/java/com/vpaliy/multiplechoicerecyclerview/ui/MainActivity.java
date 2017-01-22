package com.vpaliy.multiplechoicerecyclerview.ui;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
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


public class MainActivity extends AppCompatActivity {


    private static final String TAG=MainActivity.class.getSimpleName();
    private static final String CURRENT_EXAMPLE="example";

    private BaseAdapter adapter;
    private Toolbar actionBar;
    private RecyclerView recyclerView;
    private MultiMode mode;
    private int currentExample;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState!=null) {
            currentExample = savedInstanceState.getInt(CURRENT_EXAMPLE);
        }else {
            currentExample = R.id.animatedGallery;    //by default
        }
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


        recyclerView=(RecyclerView)(findViewById(R.id.recyclerView));
        recyclerView.setLayoutManager(new GridLayoutManager(this,getResources().
                getInteger(R.integer.span_size),GridLayoutManager.VERTICAL,false));

        //initialize using builder approach
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
                .setNavigationIcon(getResources().getDrawable(R.drawable.ic_clear_black_24dp))
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
        actionBar = (Toolbar) (findViewById(R.id.actionBar));
        actionBar.setTitle(R.string.example);
        setSupportActionBar(actionBar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setShowHideAnimationEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        actionBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initNavigation() {

        final DrawerLayout layout=(DrawerLayout)(findViewById(R.id.drawerLayout));
        NavigationView navigationView=(NavigationView)(findViewById(R.id.navigation));
        navigationView.setCheckedItem(currentExample);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
            }
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
        outState.putInt(CURRENT_EXAMPLE,currentExample);
    }
}
