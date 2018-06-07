package com.orez.teamup.teamup;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Debug;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ResultsActivity extends Activity {

    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        User user = (User) getIntent().getSerializableExtra("User");
        ArrayList<LobbySports> arr = (ArrayList<LobbySports>) getIntent().getSerializableExtra("Results");
        mListView = (ListView) findViewById(R.id.resultsLV);
        mListView.setAdapter(new MyListAdapter(ResultsActivity.this, R.layout.results_list_item, arr));
    }

    public class ViewHolder {
        TextView mSportTv;
        TextView mPlayersTv;
        Button mJoinBtn;
    }

    private class MyListAdapter extends ArrayAdapter<LobbySports> {
        private int layout;

        public MyListAdapter(@NonNull Context context, int resource, @NonNull List<LobbySports> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ResultsActivity.ViewHolder mainViewHolder = null;

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ResultsActivity.ViewHolder viewHolder = new ResultsActivity.ViewHolder();

                viewHolder.mSportTv = (TextView) findViewById(R.id.resultsSportTV);
                //viewHolder.mSportTv.setText(getItem(position).getSport().toString());
                viewHolder.mJoinBtn = (Button) findViewById(R.id.joinLobbyBtn);
                viewHolder.mPlayersTv = (TextView) findViewById(R.id.resultsPlayersTV);
                //viewHolder.mPlayersTv.setText(Integer.toString(getItem(position).getSize()));
                convertView.setTag(viewHolder);
            } else {
                mainViewHolder = (ResultsActivity.ViewHolder) convertView.getTag();
                //mainViewHolder.mSportTv.setText(getItem(position).getSport().toString());
                //mainViewHolder.mPlayersTv.setText(Integer.toString(getItem(position).users.size()));
            }

            return convertView;
        }
    }
}