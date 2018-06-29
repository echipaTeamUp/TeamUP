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

public class ResultsEsportsActivity extends Activity {
    User user;
    ImageButton mProfileBtn;
    ImageButton mSignoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_esports);

        mProfileBtn = (ImageButton) findViewById(R.id.menu_profileBtn);
        mSignoutBtn = (ImageButton) findViewById(R.id.menu_signoutBtn);
        user = (User) getIntent().getSerializableExtra("User");

        //Daca apesi pe profil, te duce la profil
        mProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ResultsEsportsActivity.this, ProfileActivity.class);
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
                Intent i = new Intent(ResultsEsportsActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        ArrayList<LobbyEsports> arr = (ArrayList<LobbyEsports>) getIntent().getSerializableExtra("lobbys");

        ListView mListView = (ListView) findViewById(R.id.resultsLV);
        mListView.setAdapter(new MyListAdapter(ResultsEsportsActivity.this, R.layout.results_list_item, arr));

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

    private class MyListAdapter extends ArrayAdapter<LobbyEsports> {
        private int layout;

        public MyListAdapter(@NonNull Context context, int resource, @NonNull List<LobbyEsports> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder mainViewHolder = null;

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ResultsEsportsActivity.ViewHolder viewHolder = new ResultsEsportsActivity.ViewHolder();

                viewHolder.mSportTv = (TextView) convertView.findViewById(R.id.resultsSportTV);
                viewHolder.mSportTv.setText(getItem(position).getEsport().toString());

                viewHolder.mJoinBtn = (Button) convertView.findViewById(R.id.joinLobbyBtn);

                viewHolder.mPlayersTv = (TextView) convertView.findViewById(R.id.resultsPlayersTV);
                viewHolder.mPlayersTv.setText(getItem(position).getSize() + "/" + getItem(position).getMaxSize());

                viewHolder.mTimeTv=(TextView) convertView.findViewById(R.id.results_timeTV);
                viewHolder.mTimeTv.setText(getItem(position).getDay()+"/"+(getItem(position).getMonth()+1)+"  "+getItem(position).getHour()
                        +":"+getItem(position).getMinute());

                viewHolder.mLocationTv=(TextView) convertView.findViewById(R.id.locationTV);
                viewHolder.mLocationTv.setVisibility(View.GONE);

                viewHolder.mJoinBtn = (Button) convertView.findViewById(R.id.joinLobbyBtn);
                viewHolder.mJoinBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ResultsEsportsActivity.this, LobbyEsportsActivity.class);
                        intent.putExtra("User", user);
                        LobbyEsports mlobby = (LobbyEsports) getItem(position);
                        mlobby.addUser(FirebaseAuth.getInstance().getUid());
                        intent.putExtra("Lobby", mlobby);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                        finish();
                    }
                });

//                viewHolder.mLocationTv = (TextView) convertView.findViewById(R.id.locationTV);
//                viewHolder.mLocationTv.setText(getItem(position).getLocationName());
                convertView.setTag(viewHolder);
            } else {
                mainViewHolder = (ViewHolder) convertView.getTag();
                mainViewHolder.mSportTv.setText(getItem(position).getEsport().toString());
                mainViewHolder.mPlayersTv.setText(getItem(position).getSize() + "/" + getItem(position).getMaxSize());
//                mainViewHolder.mLocationTv.setText(getItem(position).getLocationName());
            }

            return convertView;
        }
    }
}