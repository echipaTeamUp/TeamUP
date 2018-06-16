package com.orez.teamup.teamup;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.android.rides.RideRequestButtonCallback;
import com.uber.sdk.rides.client.ServerTokenSession;
import com.uber.sdk.rides.client.SessionConfiguration;
import com.uber.sdk.rides.client.error.ApiError;

import java.util.ArrayList;
import java.util.List;

public class LobbyActivity extends AppCompatActivity {
    FloatingActionButton mSendFab;
    User user;
    LobbySports lobby;
    ListView mListView;
    ArrayList<ChatMessage> data = new ArrayList<>();
    EditText mInputMsg;
    Button get_directionsBtn;
    Button view_on_mapBtn;
    RideRequestButton requestBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        user = (User) getIntent().getSerializableExtra("User");
        lobby = (LobbySports) getIntent().getSerializableExtra("Lobby");
        get_directionsBtn = (Button) findViewById(R.id.get_directionsBtn);
        view_on_mapBtn = (Button) findViewById(R.id.view_on_mapBtn);
        requestBtn = (RideRequestButton) findViewById(R.id.rideRequestBtn);

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Chat").child(lobby.getId());

        mSendFab = (FloatingActionButton) findViewById(R.id.sendMessageFab);
        //Pentru Uber
        initialize_uber();

        mSendFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputMsg = (EditText) findViewById(R.id.sendMessageEt);
                String message = mInputMsg.getText().toString();
                if (!message.equals("")) {
                    long xd = System.currentTimeMillis() / 1000L;
                    ChatMessage chatMessage = new ChatMessage(message, user.getFirst_name(), xd);
                    ref.child(Long.toString(xd)).setValue(chatMessage);
                    mInputMsg.setText("");
                }
            }
        });

        mListView = (ListView) findViewById(R.id.messageListView);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    data.add(ds.getValue(ChatMessage.class));
                }

                mListView.setAdapter(new LobbyActivity.MyListAdapter(LobbyActivity.this, R.layout.chat_message_item, data));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //arata locatia in Google Maps
        view_on_mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double longitude = lobby.getLongitude();
                double latitude = lobby.getLatitude();
                Toast.makeText(LobbyActivity.this, latitude + "", Toast.LENGTH_SHORT).show();
                Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude + "(Lobby+location)");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LobbyActivity.this);
        builder.setMessage("Are you sure you want to exit this lobby")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        lobby.removeUser(FirebaseAuth.getInstance().getUid());
                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    public class ViewHolder {
        TextView mMessageTv;
        TextView mUserTv;
        TextView mTimeTv;
    }

    private class MyListAdapter extends ArrayAdapter<ChatMessage> {
        private int layout;

        public MyListAdapter(@NonNull Context context, int resource, @NonNull List<ChatMessage> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LobbyActivity.ViewHolder mainViewHolder = null;

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                LobbyActivity.ViewHolder viewHolder = new LobbyActivity.ViewHolder();

                viewHolder.mMessageTv = (TextView) convertView.findViewById(R.id.chatMessageTv);
                viewHolder.mMessageTv.setText(getItem(position).getMessageText());
                viewHolder.mTimeTv = (TextView) convertView.findViewById(R.id.chatTimeTv);
                viewHolder.mTimeTv.setText(getItem(position).getTime());
                viewHolder.mUserTv = (TextView) convertView.findViewById(R.id.chatUserTv);
                viewHolder.mUserTv.setText(getItem(position).getMessageUser() + ":");
                convertView.setTag(viewHolder);
            } else {
                mainViewHolder = (LobbyActivity.ViewHolder) convertView.getTag();
                mainViewHolder.mMessageTv.setText(getItem(position).getMessageText());
                mainViewHolder.mTimeTv.setText(getItem(position).getTime());
                mainViewHolder.mUserTv.setText(getItem(position).getMessageUser() + ":");
                mainViewHolder.mUserTv.setText(getItem(position).getMessageUser() + ":");
            }

            return convertView;
        }
    }

    LocationManager mLocationManager;
    LocationListener mLocationListener;

    void initialize_uber() {
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLocationManager.removeUpdates(mLocationListener);
                mLocationManager = null;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000,
                300, mLocationListener);
        SessionConfiguration config = new SessionConfiguration.Builder()
                .setClientId("FyEMGUBPhsd3tdZpQF3S2spa1W5nYifH")
                .setServerToken("SEN3_I3iKJCKoQfoYJF6WD2YbceHRt_rMf_zmka3")
                .setRedirectUri("teamup.com/redirecturi")
                .setEnvironment(SessionConfiguration.Environment.SANDBOX)
                .build();
        UberSdk.initialize(config);
        RideParameters rideParams = new RideParameters.Builder()
                .setDropoffLocation(
                        lobby.getLatitude(), lobby.getLongitude(), "Lobby location", lobby.getLocationName())
                .setPickupLocation(mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude(),
                        mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude(),
                        "Your location", "")
                .build();
        requestBtn.setRideParameters(rideParams);
        ServerTokenSession session = new ServerTokenSession(config);
        requestBtn.setSession(session);
        requestBtn.loadRideInformation();
        RideRequestButtonCallback callback = new RideRequestButtonCallback() {

            @Override
            public void onRideInformationLoaded() {
                // react to the displayed estimates
            }

            @Override
            public void onError(ApiError apiError) {
                // API error details: /docs/riders/references/api#section-errors
            }

            @Override
            public void onError(Throwable throwable) {
                // Unexpected error, very likely an IOException
            }
        };
        requestBtn.setCallback(callback);
    }
}
