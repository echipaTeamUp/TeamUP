package com.orez.teamup.teamup;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FilterSportsActivity extends Activity {

    List<String> headers = new ArrayList<String>();
    HashMap<String,List<String>> headeritems = new HashMap<String, List<String>>();

    ExpandableListView mFilterExpList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_sports);

        mFilterExpList = findViewById(R.id.expandable_list_view);
        MyExpandableListAdapter mExpListAdapter = new MyExpandableListAdapter(this, headers, headeritems);

        headers.add("Age");
        headers.add("Distance");
        headeritems.put(headers.get(0), null);
        headeritems.put(headers.get(1), null);

        mFilterExpList.setAdapter(mExpListAdapter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }
}
