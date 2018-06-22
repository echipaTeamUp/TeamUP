package com.orez.teamup.teamup;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LobbyEsportsActivity extends AppCompatActivity {
    FloatingActionButton mSendFab;
    User user;
    LobbyEsports lobby;
    ListView mChatListView;
    ArrayList<ChatMessage> data = new ArrayList<>();
    ArrayList<String> users = new ArrayList<>();
    EditText mInputMsg;
    ImageButton mProfileBtn;
    TextView mLobbySport;
    TextView mdetailsTv;
    ListView mUserListView;
    ValueEventListener kicklistener;
    ValueEventListener userListener;
    boolean mActiveList = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby_esports);

        user = (User) getIntent().getSerializableExtra("User");
        lobby = (LobbyEsports) getIntent().getSerializableExtra("Lobby");
        mProfileBtn = (ImageButton) findViewById(R.id.menu_profileBtn);
        mLobbySport = (TextView) findViewById(R.id.lobbySportTv);
        mUserListView = (ListView) findViewById(R.id.usersEsportsListView);
        mdetailsTv = (TextView) findViewById(R.id.detailsTv);
        mSendFab = (FloatingActionButton) findViewById(R.id.sendMessageFab);
        mInputMsg = (EditText) findViewById(R.id.sendMessageEt);
        mChatListView = (ListView) findViewById(R.id.messageListView);

        mUserListView.setVisibility(View.GONE);
        mChatListView.setVisibility(View.VISIBLE);

        mLobbySport.setText(lobby.getEsport().toString());
        if(lobby.getEsport()==esports.CSGO)
            mdetailsTv.setText("Start time: " +lobby.getDay()+"/"+(lobby.getMonth()+1)+" "+lobby.getHour() + ":" + lobby.getMinute()+" |Rank: "+lobby.getCSGOrank());
        else
            mdetailsTv.setText("Start time: " +lobby.getDay()+"/"+(lobby.getMonth()+1)+" "+lobby.getHour() + ":" + lobby.getMinute()+" |Rank: "+lobby.getLoLrank());
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

        // Updateaza chatul
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

        // Verifica daca ai primit sau nu kick
        kicklistener = FirebaseDatabase.getInstance().getReference().child("id").child(FirebaseAuth.getInstance().getUid()).
                child("Lobby").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    Toast.makeText(LobbyEsportsActivity.this, "You have been kicked out the lobby", Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // updateaza obiectul lobby
        FirebaseDatabase.getInstance().getReference().child("EsportsLobby").child(lobby.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lobby = dataSnapshot.getValue(LobbyEsports.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Pune userii in lista de useri
        DatabaseReference baseRef = FirebaseDatabase.getInstance().getReference();
        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users.clear();
                DataSnapshot id = dataSnapshot.child("id");
                DataSnapshot usersSnapshot = dataSnapshot.child("EsportsLobby").child(lobby.getId()).child("users");
                for (DataSnapshot ds : usersSnapshot.getChildren()) {
                    users.add(id.child(ds.getValue(String.class)).getKey());
                }

                mUserListView.setAdapter(new LobbyEsportsActivity.MyUserListAdapter(LobbyEsportsActivity.this, R.layout.user_list_item, users));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        baseRef.addValueEventListener(userListener);

        mProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LobbyEsportsActivity.this, ProfileActivity.class);
                i.putExtra("Req_code", 1);
                i.putExtra("User", user);
                startActivity(i);
            }
        });

    }

    //Te scoate din lobby la back
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LobbyEsportsActivity.this);
        builder.setMessage("Are you sure you want to exit this lobby")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference().child("id").child(FirebaseAuth.getInstance().getUid()).
                                child("Lobby").removeEventListener(kicklistener);
                        FirebaseDatabase.getInstance().getReference().removeEventListener(userListener);

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
                mainViewHolder = (ChatViewHolder) convertView.getTag();
                mainViewHolder.mMessageTv.setText(getItem(position).getMessageText());
                mainViewHolder.mTimeTv.setText(getItem(position).getTime());
                mainViewHolder.mUserTv.setText(getItem(position).getMessageUser() + ":");
            }
            return convertView;
        }
    }

    public void showPopup(View v) {
        PopupMenu mPopup = new PopupMenu(this, v);
        MenuInflater inflater = mPopup.getMenuInflater();
        inflater.inflate(R.menu.actions, mPopup.getMenu());
        mPopup.show();

        mPopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return true;
            }
        });
    }

    public void showUserOptionsPopup(View v, final Boolean kickAvailable, final Boolean rateAvailable,
                                     final String uId, final String userName){
        PopupMenu mPopup = new PopupMenu(this, v);
        MenuInflater inflater = mPopup.getMenuInflater();
        inflater.inflate(R.menu.user_options, mPopup.getMenu());

        MenuItem kick_item = mPopup.getMenu().findItem(R.id.kick_opt);
        MenuItem rate_item = mPopup.getMenu().findItem(R.id.rate_opt);

        if (!kickAvailable)
            kick_item.setVisible(false);

        if (!rateAvailable)
            rate_item.setVisible(false);

        mPopup.show();

        mPopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().toString().equals("Kick")){
                    lobby.removeUser(uId);
                } else if (item.getTitle().toString().equals("Rate")){
                    AlertDialog.Builder rating_dialog = new AlertDialog.Builder(LobbyEsportsActivity.this);
                    View dialog_view = getLayoutInflater().inflate(R.layout.rating_dialog, null);
                    final RatingBar dialog_rating = dialog_view.findViewById(R.id.dialog_ratingBar);
                    TextView dialog_titleTv = dialog_view.findViewById(R.id.dialog_titleTV);
                    Button dialog_rateBtn = dialog_view.findViewById(R.id.dialog_rateBtn);
                    Button dialog_cancelBtn = dialog_view.findViewById(R.id.dialog_cancelBtn);
                    rating_dialog.setView(dialog_view);
                    final AlertDialog dialog = rating_dialog.create();
                    dialog_titleTv.setText("Rate " + userName);
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
                            final float rating = dialog_rating.getRating();
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                            final DatabaseReference ratingRef = ref.child("id").child(uId).child("rating");
                            final DatabaseReference nr_ratingsRef = ref.child("id").child(uId).child("number_of_ratings");
                            ratingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    float crating = dataSnapshot.getValue(float.class);
                                    ratingRef.setValue((float) crating + (float) rating);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            nr_ratingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    int nr_ratings = dataSnapshot.getValue(int.class);
                                    nr_ratingsRef.setValue(nr_ratings + 1);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            dialog.cancel();
                        }
                    });
                }
                return true;
            }
        });
    }

    public class UserViewHolder {
        TextView mUserTv;
        CircleImageView mProfileImage;
        ImageButton mImageButton;
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
                viewHolder.mProfileImage = (CircleImageView) convertView.findViewById(R.id.list_profile_image);
                viewHolder.mImageButton = (ImageButton) convertView.findViewById(R.id.userOptionsBtn);

                final String mUserId = users.get(position);
                final Boolean kickAvailable = lobby.getAdminId().equals(FirebaseAuth.getInstance().getUid()) && !mUserId.equals(FirebaseAuth.getInstance().getUid());
                final Boolean rateAvailable = !mUserId.equals(FirebaseAuth.getInstance().getUid());

                if (!kickAvailable && !rateAvailable){
                    viewHolder.mImageButton.setVisibility(View.GONE);
                }

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
                        Glide.with(LobbyEsportsActivity.this)
                                .load(R.drawable.userprofilewhitelarge)
                                .into(viewHolder.mProfileImage);
                    }
                });

                viewHolder.mImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showUserOptionsPopup(v, kickAvailable, rateAvailable, mUserId, viewHolder.mUserTv.getText().toString());
                    }
                });

                viewHolder.mUserTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(LobbyEsportsActivity.this, ProfileActivity.class);
                        if (mUserId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            intent.putExtra("User", user);
                            intent.putExtra("Req_code", 1);
                            startActivity(intent);
                        } else {
                            intent.putExtra("User", user);
                            intent.putExtra("Uid", mUserId);
                            intent.putExtra("Req_code", 2);
                            startActivity(intent);
                        }
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

            mUserListView.setAdapter(new LobbyEsportsActivity.MyUserListAdapter(LobbyEsportsActivity.this, R.layout.user_list_item, users));
            mActiveList = true;
        } else {
            mChatListView.setVisibility(View.VISIBLE);
            mInputMsg.setVisibility(View.VISIBLE);
            mSendFab.setVisibility(View.VISIBLE);
            mUserListView.setVisibility(View.GONE);

            mChatListView.setAdapter(new LobbyEsportsActivity.MyListAdapter(LobbyEsportsActivity.this, R.layout.chat_message_item, data));
            mActiveList = false;
        }
    }
}
