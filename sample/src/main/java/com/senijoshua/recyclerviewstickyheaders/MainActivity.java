package com.senijoshua.recyclerviewstickyheaders;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.senijoshua.library.NestedLayoutManager;

public class MainActivity extends AppCompatActivity {
    RecyclerView celebrityList;
    CelebAdapter celebrityAdapter;
    NestedLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        celebrityList = (RecyclerView) findViewById(R.id.main_rv_celebrity_list);
        celebrityAdapter = new CelebAdapter();
        mLayoutManager = new NestedLayoutManager();
//        mLayoutManager.setParentHeaderPositionChangedCallback(new NestedStickyHeadersLayoutManager.ParentHeaderPositionChangedCallback() {
//            @Override
//            public void onParentHeaderPositionChanged(int sectionIndex, View header, NestedStickyHeadersLayoutManager.ParentHeaderPosition oldPosition, NestedStickyHeadersLayoutManager.ParentHeaderPosition newPosition) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    boolean elevated = newPosition == NestedStickyHeadersLayoutManager.ParentHeaderPosition.STICKY;
//                    header.setElevation(elevated ? 8 : 0);
//                }
//            }
//        });
        
        celebrityAdapter.setCelebrities(CelebrityUtility.loadCelebrities());
        celebrityList.setLayoutManager(mLayoutManager);
        celebrityList.setAdapter(celebrityAdapter);

    }
}
