package com.orez.teamup.teamup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SportsActivity extends Activity {
    private ArrayList<String> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sports);

        Resources res = getResources();
        String[] sports = res.getStringArray(R.array.Sports);

        data = new ArrayList<String>();

        ListView mListView = (ListView) findViewById(R.id.listView);
        mListView.setAdapter(new MyListAdapter(SportsActivity.this, R.layout.list_item, data));

        data.addAll(Arrays.asList(sports));
    }

    // animeaza cand apesi pe back ca sa te intorci in activitatea trecuta
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    private class MyListAdapter extends ArrayAdapter<String>{
        private int layout;

        public MyListAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder mainViewHolder = null;

            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.mSportTv = (TextView) convertView.findViewById(R.id.sportTv);
                viewHolder.mSportTv.setText(getItem(position));
                viewHolder.mEditFilterBtn = (Button) convertView.findViewById(R.id.editFilterBtn);
                viewHolder.mSportCheckBox = (CheckBox) convertView.findViewById(R.id.sportCheckBox);
                convertView.setTag(viewHolder);
            } else{
                mainViewHolder = (ViewHolder) convertView.getTag();
                mainViewHolder.mSportTv.setText(getItem(position));
            }

            return convertView;
        }
    }

    public class ViewHolder{
        TextView mSportTv;
        Button mEditFilterBtn;
        CheckBox mSportCheckBox;

    }

    public void loadFilterActivity(View view){
        Intent intent = new Intent(SportsActivity.this, FilterSportsActivity.class);
        startActivity(intent);
    }
}