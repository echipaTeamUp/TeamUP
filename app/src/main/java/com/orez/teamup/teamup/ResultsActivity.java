package com.orez.teamup.teamup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;


public class ResultsActivity extends Activity {
    User user;
    ImageButton mProfileBtn;
    ImageButton mSignoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        mProfileBtn = (ImageButton) findViewById(R.id.menu_profileBtn);
        mSignoutBtn = (ImageButton) findViewById(R.id.menu_signoutBtn);
        user = (User) getIntent().getSerializableExtra("User");

        //Daca apesi pe profil, te duce la profil
        mProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ResultsActivity.this, ProfileActivity.class);
                i.putExtra("User", user);
                i.putExtra("Req_code", 1);
                startActivity(i);
            }
        });

        //Signout
        mSignoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                setResult(Activity.RESULT_OK);
                Intent i = new Intent(ResultsActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        ArrayList<LobbySports> arr = (ArrayList<LobbySports>) getIntent().getSerializableExtra("lobbys");

        ListView mListView = (ListView) findViewById(R.id.resultsLV);
        mListView.setAdapter(new MyListAdapter(ResultsActivity.this, R.layout.results_list_item, arr));

        TextView resultsTV = (TextView) findViewById(R.id.resultsTitleTV);
        if (arr.size() == 0)
            resultsTV.setText("No results match the search");
        else if (arr.size() == 1)
            resultsTV.setText("One result found:");
        else
            resultsTV.setText(arr.size() + " results found:");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public class ViewHolder {
        TextView mSportTv;
        TextView mPlayersTv;
        TextView mLocationTv;
        TextView mTimeTv;
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

                viewHolder.mTimeTv=(TextView) convertView.findViewById(R.id.results_timeTV);
                viewHolder.mTimeTv.setText(getItem(position).getDay()+"/"+(getItem(position).getMonth()+1)+"  "+getItem(position).getHour()
                +":"+getItem(position).getMinute());

                viewHolder.mJoinBtn = (Button) convertView.findViewById(R.id.joinLobbyBtn);
                viewHolder.mJoinBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ResultsActivity.this, LobbyActivity.class);
                        intent.putExtra("User", user);
                        LobbySports mlobby=(LobbySports)getItem(position);
                        mlobby.addUser(FirebaseAuth.getInstance().getUid());
                        intent.putExtra("Lobby", mlobby);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                        finish();
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