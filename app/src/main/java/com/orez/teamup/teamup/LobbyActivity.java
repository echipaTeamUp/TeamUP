package com.orez.teamup.teamup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LobbyActivity extends AppCompatActivity {
    FloatingActionButton mSendFab;
    User user;
    Lobby lobby;
    ListView mListView;
    ArrayList<ChatMessage> data = new ArrayList<>();
    EditText mInputMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        user = (User) getIntent().getSerializableExtra("User");
        lobby = (LobbySports) getIntent().getSerializableExtra("Lobby");
        
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Chat").child(lobby.getId());

        mSendFab = (FloatingActionButton) findViewById(R.id.sendMessageFab);
        mSendFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputMsg = (EditText) findViewById(R.id.sendMessageEt);
                ref.push().setValue(new ChatMessage(mInputMsg.getText().toString(),
                                user.getFirst_name()));
                mInputMsg.setText("");
            }
        });

        mListView = (ListView) findViewById(R.id.messageListView);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    data.add(ds.getValue(ChatMessage.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public class ViewHolder{
        TextView mMessageTv;
        TextView mUserTv;
        TextView mTimeTv;
    }

    private class MyListAdapter extends ArrayAdapter<ChatMessage>{
        private int layout;

        public MyListAdapter(@NonNull Context context, int resource, @NonNull List<ChatMessage> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LobbyActivity.ViewHolder mainViewHolder = null;

            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                LobbyActivity.ViewHolder viewHolder = new LobbyActivity.ViewHolder();

                viewHolder.mMessageTv = (TextView) findViewById(R.id.chatMessageTv);
                viewHolder.mMessageTv.setText(getItem(position).getMessageText());
                viewHolder.mTimeTv = (TextView) findViewById(R.id.chatTimeTv);
                viewHolder.mTimeTv.setText(getItem(position).getMessageTime());
                viewHolder.mUserTv = (TextView) findViewById(R.id.chatUserTv);
                viewHolder.mUserTv.setText(getItem(position).getMessageUser());
            }

            return convertView;
        }
    }
}
