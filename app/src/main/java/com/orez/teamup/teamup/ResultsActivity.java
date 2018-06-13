package com.orez.teamup.teamup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ResultsActivity extends Activity {
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        user = (User) getIntent().getSerializableExtra("User");
        ArrayList<LobbySports> arr = (ArrayList<LobbySports>) getIntent().getSerializableExtra("lobbys");

        ListView mListView = (ListView) findViewById(R.id.resultsLV);
        mListView.setAdapter(new MyListAdapter(ResultsActivity.this, R.layout.results_list_item, arr));

        TextView resultsTV = (TextView) findViewById(R.id.resultsTitleTV);
        if (arr.size() == 0)
            resultsTV.setText("No results match the searching criteria");
        else if (arr.size() == 1)
            resultsTV.setText("One result found:");
        else
            resultsTV.setText(arr.size() + " results found:");
    }

    public class ViewHolder {
        TextView mSportTv;
        TextView mPlayersTv;
        TextView mLocationTv;
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
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ResultsActivity.ViewHolder mainViewHolder = null;

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ResultsActivity.ViewHolder viewHolder = new ResultsActivity.ViewHolder();

                viewHolder.mSportTv = (TextView) convertView.findViewById(R.id.resultsSportTV);
                viewHolder.mSportTv.setText(getItem(position).getSport().toString());

                viewHolder.mJoinBtn = (Button) convertView.findViewById(R.id.joinLobbyBtn);

                viewHolder.mPlayersTv = (TextView) convertView.findViewById(R.id.resultsPlayersTV);
                viewHolder.mPlayersTv.setText(getItem(position).getSize() + "/" + getItem(position).getMaxSize());

                viewHolder.mLocationTv = (TextView) convertView.findViewById(R.id.locationTV);
                viewHolder.mLocationTv.setText(getItem(position).getLocationName());

                viewHolder.mJoinBtn = (Button) convertView.findViewById(R.id.joinLobbyBtn);
                viewHolder.mJoinBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ResultsActivity.this, LobbyActivity.class);
                        intent.putExtra("User", user);
                        intent.putExtra("Lobby", getItem(position));
                        startActivity(intent);

                    }
                });
                convertView.setTag(viewHolder);
            } else {
                mainViewHolder = (ResultsActivity.ViewHolder) convertView.getTag();
                mainViewHolder.mSportTv.setText(getItem(position).getSport().toString());
                mainViewHolder.mPlayersTv.setText(getItem(position).getSize() + "/" + getItem(position).getMaxSize());
                mainViewHolder.mLocationTv.setText(getItem(position).getLocationName());
            }

            return convertView;
        }
    }
}