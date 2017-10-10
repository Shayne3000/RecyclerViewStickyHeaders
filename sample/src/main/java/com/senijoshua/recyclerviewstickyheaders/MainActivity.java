package com.senijoshua.recyclerviewstickyheaders;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    RecyclerView celebrityList;
    CelebrityListAdapter celebrityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        celebrityList = (RecyclerView) findViewById(R.id.main_rv_celebrity_list);
        celebrityList.setAdapter(celebrityAdapter);
    }
}
