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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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
    ListView mChatListView;
    ArrayList<ChatMessage> data = new ArrayList<>();
    ArrayList<String> users = new ArrayList<>();
    EditText mInputMsg;
    Button view_on_mapBtn;
    RideRequestButton requestBtn;
    ImageButton mProfileBtn;
    TextView mLobbySport;
    TextView mdetailsTv;
    ListView mUserListView;
    ValueEventListener kicklistener;
    boolean mActiveList = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        user = (User) getIntent().getSerializableExtra("User");
        lobby = (LobbySports) getIntent().getSerializableExtra("Lobby");
        view_on_mapBtn = (Button) findViewById(R.id.view_on_mapBtn);
        requestBtn = (RideRequestButton) findViewById(R.id.rideRequestBtn);
        mProfileBtn = (ImageButton) findViewById(R.id.menu_profileBtn);
        mLobbySport = (TextView) findViewById(R.id.lobbySportTv);
        mUserListView = (ListView) findViewById(R.id.usersListView);
        mdetailsTv = (TextView) findViewById(R.id.detailsTv);
        mSendFab = (FloatingActionButton) findViewById(R.id.sendMessageFab);
        mInputMsg = (EditText) findViewById(R.id.sendMessageEt);

        mUserListView.setVisibility(View.GONE);

        //Pentru Uber
        initialize_uber();
        mLobbySport.setText(lobby.getSport().toString());
        mdetailsTv.setText(lobby.getLocationName() + " " + lobby.getHour() + ":" + lobby.getMinute());
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Chat").child(lobby.getId());
        mSendFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mInputMsg.getText().toString().trim();
                if (!message.equals("")) {
                    long xd = System.currentTimeMillis() / 1000L;
                    ChatMessage chatMessage = new ChatMessage(message, user.getFirst_name() + " " + user.getLast_name(), xd);
                    ref.child(Long.toString(xd)).setValue(chatMessage);
                }
                mInputMsg.setText("");
            }
        });

        mChatListView = (ListView) findViewById(R.id.messageListView);
        //Updateaza chatul
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    data.add(ds.getValue(ChatMessage.class));
                }

                mChatListView.setAdapter(new LobbyActivity.MyListAdapter(LobbyActivity.this, R.layout.chat_message_item, data));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //Verifica daca ai primit sau nu kick
        kicklistener=FirebaseDatabase.getInstance().getReference().child("id").child(FirebaseAuth.getInstance().getUid()).
                child("Lobby").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()==null)
                {
                    Toast.makeText(LobbyActivity.this,"You have been kicked out the lobby",Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //Pune userii in lista de useri
        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users.clear();
                DataSnapshot id = dataSnapshot.child("id");
                DataSnapshot usersSnapshot = dataSnapshot.child("SportsLobby").child(lobby.getId()).child("users");
                for (DataSnapshot ds : usersSnapshot.getChildren()) {
                    users.add(id.child(ds.getValue(String.class)).getKey());
                }

                mUserListView.setAdapter(new LobbyActivity.MyUserListAdapter(LobbyActivity.this, R.layout.user_list_item, users));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LobbyActivity.this, ProfileActivity.class);
                i.putExtra("Req_code", 1);
                i.putExtra("User", user);
                startActivity(i);
            }
        });

    }
    //Te scoate din lobby la back
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LobbyActivity.this);
        builder.setMessage("Are you sure you want to exit this lobby")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference().child("id").child(FirebaseAuth.getInstance().getUid()).
                                child("Lobby").removeEventListener(kicklistener);
                        lobby.removeUser(FirebaseAuth.getInstance().getUid());
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    public class ChatViewHolder {
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
            ChatViewHolder mainViewHolder = null;

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ChatViewHolder viewHolder = new ChatViewHolder();

                viewHolder.mMessageTv = (TextView) convertView.findViewById(R.id.chatMessageTv);
                viewHolder.mMessageTv.setText(getItem(position).getMessageText());

                viewHolder.mTimeTv = (TextView) convertView.findViewById(R.id.chatTimeTv);
                viewHolder.mTimeTv.setText(getItem(position).getTime());

                viewHolder.mUserTv = (TextView) convertView.findViewById(R.id.chatUserTv);
                viewHolder.mUserTv.setText(getItem(position).getMessageUser() + ":");
                convertView.setTag(viewHolder);
            } else {
                mainViewHolder = (ChatViewHolder) convertView.getTag();
                mainViewHolder.mMessageTv.setText(getItem(position).getMessageText());
                mainViewHolder.mTimeTv.setText(getItem(position).getTime());
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

    public void showPopup(View v) {
        PopupMenu mPopup = new PopupMenu(this, v);
        MenuInflater inflater = mPopup.getMenuInflater();
        inflater.inflate(R.menu.actions, mPopup.getMenu());
        mPopup.show();

        mPopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //arata locatia in Gmaps
                if (item.getTitle().toString().equals("View on map")) {
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
                return true;
            }
        });
    }

    public class UserViewHolder {
        TextView mUserTv;
        ImageView mProfileImage;
        Button mKickBtn;
    }

    private class MyUserListAdapter extends ArrayAdapter<String> {
        private int layout;

        public MyUserListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<String> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            UserViewHolder mainViewHolder = null;

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                final UserViewHolder viewHolder = new UserViewHolder();

                viewHolder.mUserTv = (TextView) convertView.findViewById(R.id.userTv);
                viewHolder.mProfileImage = (ImageView) convertView.findViewById(R.id.list_profile_image);
                viewHolder.mKickBtn=(Button) convertView.findViewById(R.id.kickBtn);
                final String mUserId = users.get(position);
                //Nu poti sa dai kick daca nu esti admin si nu iti poti da kick singur
                if(!lobby.getAdminId().equals(FirebaseAuth.getInstance().getUid()) || mUserId.equals(FirebaseAuth.getInstance().getUid()))
                    viewHolder.mKickBtn.setVisibility(View.GONE);

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("id").child(mUserId);
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String mUserFirstName = dataSnapshot.child("first_name").getValue().toString();
                        viewHolder.mUserTv.setText(mUserFirstName);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                StorageReference sref = FirebaseStorage.getInstance().getReference();
                sref.child(mUserId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //daca exista poza
                        Glide.with(LobbyActivity.this)
                                .load(uri)
                                .into(viewHolder.mProfileImage);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // daca nu exista poza
                    }
                });

                viewHolder.mUserTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(LobbyActivity.this, ProfileActivity.class);
                        if(mUserId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            intent.putExtra("User", user);
                            intent.putExtra("Req_code", 1);
                            startActivity(intent);
                        } else{
                            intent.putExtra("User", user);
                            intent.putExtra("Uid", mUserId);
                            intent.putExtra("Req_code", 2);
                            startActivity(intent);
                        }
                    }
                });
                viewHolder.mKickBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lobby.removeUser(mUserId);
                    }
                });
                convertView.setTag(viewHolder);
            } else {
                mainViewHolder = (UserViewHolder) convertView.getTag();
                final String mUserId = users.get(position);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("id").child(mUserId);
                final UserViewHolder finalMainViewHolder = mainViewHolder;
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String mUserFirstName = dataSnapshot.child("first_name").getValue().toString();
                        finalMainViewHolder.mUserTv.setText(mUserFirstName);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });


                //mainViewHolder.mUserTv.setText(mUserFirstName);
                //mainViewHolder.mProfileImage.set
            }

            return convertView;
        }
    }

    public void loadUserList(View v) {
        if (!mActiveList) {
            mChatListView.setVisibility(View.GONE);
            mInputMsg.setVisibility(View.GONE);
            mSendFab.setVisibility(View.GONE);
            mUserListView.setVisibility(View.VISIBLE);

            mUserListView.setAdapter(new LobbyActivity.MyUserListAdapter(LobbyActivity.this, R.layout.user_list_item, users));
            mActiveList = true;
        } else {
            mChatListView.setVisibility(View.VISIBLE);
            mInputMsg.setVisibility(View.VISIBLE);
            mSendFab.setVisibility(View.VISIBLE);
            mUserListView.setVisibility(View.GONE);

            mChatListView.setAdapter(new LobbyActivity.MyListAdapter(LobbyActivity.this, R.layout.chat_message_item, data));
            mActiveList = false;
        }
    }
}
