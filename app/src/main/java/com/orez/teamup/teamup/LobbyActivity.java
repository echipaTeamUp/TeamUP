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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LobbyActivity extends AppCompatActivity {
    FloatingActionButton mSendFab;
    User user;
    Lobby lobby;
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        user = (User) getIntent().getSerializableExtra("User");
        lobby = (Lobby) getIntent().getSerializableExtra("Lobby");

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Chat").child(lobby.getId());

        mSendFab = (FloatingActionButton) findViewById(R.id.sendMessageFab);
        mSendFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText mInputMsg = (EditText) findViewById(R.id.messageEt);

                ref.push().setValue(new ChatMessage(mInputMsg.getText().toString(),
                                user.getFirst_name()));

                mInputMsg.setText("");
            }
        });

        mListView = (ListView) findViewById(R.id.messageListView);

    }

    public class ViewHolder{
        TextView mMessageTv;
        TextView mUserTv;
        TextView mTimeTv;
    }

    private class MyListAdapter extends ArrayAdapter<String>{
        private int layout;

        public MyListAdapter(@NonNull Context context, int resource, @NonNull String[] objects) {
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
                //aici m-am blocat
                //viewHolder.mMessageTv.setText();
                viewHolder.mTimeTv = (TextView) findViewById(R.id.chatTimeTv);
                viewHolder.mUserTv = (TextView) findViewById(R.id.chatUserTv);

            }

            return convertView;
        }
    }
}
