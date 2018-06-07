package com.orez.teamup.teamup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
    FloatingActionButton mfab;
    FloatingActionButton mSendFab;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sports);

        Resources res = getResources();
        String[] sports = res.getStringArray(R.array.Sports);
        user = (User) getIntent().getSerializableExtra("User");
        data = new ArrayList<String>();
        mfab = (FloatingActionButton) findViewById(R.id.floatingactionbutton_create);
        mSendFab = (FloatingActionButton) findViewById(R.id.floatingActionButton_send);

        ListView mListView = (ListView) findViewById(R.id.listView);
        mListView.setAdapter(new MyListAdapter(SportsActivity.this, R.layout.list_item, data));
        data.addAll(Arrays.asList(sports));

        mfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SportsActivity.this, New_lobby_activity.class);
                i.putExtra("User", user);
                startActivity(i);
            }
        });

        mSendFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SportsActivity.this, ResultsActivity.class);
                i.putExtra("User", user);
                ArrayList<LobbySports> arr = new ArrayList<>();
                LobbySports xd = new LobbySports();
                xd.addUser("123");
                arr.add(xd);
                i.putExtra("Results", arr);
                startActivity(i);
            }
        });
    }

    // animeaza cand apesi pe back ca sa te intorci in activitatea trecuta
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    private class MyListAdapter extends ArrayAdapter<String> {
        private int layout;

        public MyListAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder mainViewHolder = null;

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.mSportCheckBox = (CheckBox) convertView.findViewById(R.id.sportCheckBox);
                viewHolder.mSportCheckBox.setText(getItem(position));
                convertView.setTag(viewHolder);
            } else {
                mainViewHolder = (ViewHolder) convertView.getTag();
                mainViewHolder.mSportCheckBox.setText(getItem(position));
            }

            return convertView;
        }
    }

    public class ViewHolder {
        TextView mSportTv;
        Button mEditFilterBtn;
        CheckBox mSportCheckBox;

    }

    public void loadFilterActivity(View view) {
        Intent intent = new Intent(SportsActivity.this, FilterSportsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}