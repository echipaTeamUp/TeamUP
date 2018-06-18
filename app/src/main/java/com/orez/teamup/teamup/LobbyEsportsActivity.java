package com.orez.teamup.teamup;

import android.Manifest;
import android.app.Activity;
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
import android.widget.RatingBar;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.android.rides.RideRequestButtonCallback;
import com.uber.sdk.rides.client.ServerTokenSession;
import com.uber.sdk.rides.client.SessionConfiguration;
import com.uber.sdk.rides.client.error.ApiError;

import java.util.ArrayList;
import java.util.List;

public class LobbyEsportsActivity extends AppCompatActivity {
    FloatingActionButton mSendFab;
    User user;
    LobbyEsports lobby;
    ListView mChatListView;
    ArrayList<ChatMessage> data = new ArrayList<>();
    ArrayList<String> users = new ArrayList<>();
    EditText mInputMsg;
    RideRequestButton requestBtn;
    ImageButton mProfileBtn;
    TextView mLobbySport;
    ListView mUserEsportsListView;
    boolean mActiveList = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby_esports);

        user = (User) getIntent().getSerializableExtra("User");
        lobby = (LobbyEsports) getIntent().getSerializableExtra("Lobby");
        requestBtn = (RideRequestButton) findViewById(R.id.rideRequestBtn);
        mProfileBtn = (ImageButton) findViewById(R.id.menu_profileBtn);
        mLobbySport = (TextView) findViewById(R.id.lobbySportTv);
        mUserEsportsListView = (ListView) findViewById(R.id.usersEsportsListView);
        mInputMsg = (EditText) findViewById(R.id.sendMessageEt);

        mUserEsportsListView.setVisibility(View.GONE);

        mLobbySport.setText(lobby.getEsport().toString());

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Chat").child(lobby.getId());

        mSendFab = (FloatingActionButton) findViewById(R.id.sendMessageFab);
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

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    data.add(ds.getValue(ChatMessage.class));
                }

                mChatListView.setAdapter(new LobbyEsportsActivity.MyListAdapter(LobbyEsportsActivity.this, R.layout.chat_message_item, data));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users.clear();
                DataSnapshot id = dataSnapshot.child("id");
                DataSnapshot usersSnapshot = dataSnapshot.child("EsportsLobby").child(lobby.getId()).child("users");
                for (DataSnapshot ds : usersSnapshot.getChildren()){
                    users.add(id.child(ds.getValue(String.class)).getKey());
                }

                mUserEsportsListView.setAdapter(new LobbyEsportsActivity.MyUserListAdapter(LobbyEsportsActivity.this, R.layout.user_list_item, users));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(LobbyEsportsActivity.this,ProfileActivity.class);
                i.putExtra("Req_code",1);
                i.putExtra("User",user);
                startActivity(i);
            }
        });

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LobbyEsportsActivity.this);
        builder.setMessage("Are you sure you want to exit this lobby")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
            LobbyEsportsActivity.ViewHolder mainViewHolder = null;

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                LobbyEsportsActivity.ViewHolder viewHolder = new LobbyEsportsActivity.ViewHolder();

                viewHolder.mMessageTv = (TextView) convertView.findViewById(R.id.chatMessageTv);
                viewHolder.mMessageTv.setText(getItem(position).getMessageText());

                viewHolder.mTimeTv = (TextView) convertView.findViewById(R.id.chatTimeTv);
                viewHolder.mTimeTv.setText(getItem(position).getTime());

                viewHolder.mUserTv = (TextView) convertView.findViewById(R.id.chatUserTv);
                viewHolder.mUserTv.setText(getItem(position).getMessageUser() + ":");
                convertView.setTag(viewHolder);
            } else {
                mainViewHolder = (LobbyEsportsActivity.ViewHolder) convertView.getTag();
                mainViewHolder.mMessageTv.setText(getItem(position).getMessageText());
                mainViewHolder.mTimeTv.setText(getItem(position).getTime());
                mainViewHolder.mUserTv.setText(getItem(position).getMessageUser() + ":");
            }

            return convertView;
        }
    }

    public class UserViewHolder {
        TextView mUserTv;
        ImageView mProfileImage;
        Button mKickBtn;
        Button mRateBtn;
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
                viewHolder.mRateBtn=(Button) convertView.findViewById(R.id.user_rateBtn);

                final String mUserId = users.get(position);
                //Nu iti poti da rating singur
                if(mUserId.equals(FirebaseAuth.getInstance().getUid()))
                    viewHolder.mRateBtn.setVisibility(View.GONE);
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
                        Glide.with(LobbyEsportsActivity.this)
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
                        Intent intent = new Intent(LobbyEsportsActivity.this, ProfileActivity.class);
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
                viewHolder.mRateBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder rating_dialog=new AlertDialog.Builder(LobbyEsportsActivity.this);
                        View dialog_view=getLayoutInflater().inflate(R.layout.rating_dialog,null);
                        final RatingBar dialog_rating=dialog_view.findViewById(R.id.dialog_ratingBar);
                        TextView dialog_titleTv=dialog_view.findViewById(R.id.dialog_titleTV);
                        Button dialog_rateBtn=dialog_view.findViewById(R.id.dialog_rateBtn);
                        Button dialog_cancelBtn=dialog_view.findViewById(R.id.dialog_cancelBtn);
                        rating_dialog.setView(dialog_view);
                        final AlertDialog dialog=rating_dialog.create();
                        dialog_titleTv.setText("Rate "+viewHolder.mUserTv.getText().toString());
                        dialog.show();
                        dialog_cancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.cancel();
                            }
                        });
                        dialog_rateBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final float rating=dialog_rating.getRating();
                                DatabaseReference ref=FirebaseDatabase.getInstance().getReference();
                                final DatabaseReference ratingRef=ref.child("id").child(mUserId).child("rating");
                                final DatabaseReference nr_ratingsRef=ref.child("id").child(mUserId).child("number_of_ratings");
                                ratingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        float crating= dataSnapshot.getValue(float.class);
                                        ratingRef.setValue((float)crating+(float)rating);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                nr_ratingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        int nr_ratings=dataSnapshot.getValue(int.class);
                                        nr_ratingsRef.setValue(nr_ratings+1);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                dialog.cancel();
                            }
                        });

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
            }

            return convertView;
        }
    }

    public void loadUserList(View v) {
        if (!mActiveList) {
            mChatListView.setVisibility(View.GONE);
            mInputMsg.setVisibility(View.GONE);
            mSendFab.setVisibility(View.GONE);
            mUserEsportsListView.setVisibility(View.VISIBLE);

            mUserEsportsListView.setAdapter(new MyUserListAdapter(LobbyEsportsActivity.this, R.layout.user_list_item, users));
            mActiveList = true;
        } else {
            mChatListView.setVisibility(View.VISIBLE);
            mInputMsg.setVisibility(View.VISIBLE);
            mInputMsg = (EditText) findViewById(R.id.sendMessageEt);
            mSendFab.setVisibility(View.VISIBLE);
            mSendFab = (FloatingActionButton) findViewById(R.id.sendMessageFab);
            mUserEsportsListView.setVisibility(View.GONE);

            mChatListView.setAdapter(new MyListAdapter(LobbyEsportsActivity.this, R.layout.chat_message_item, data));
            mActiveList = false;
        }
    }
}
