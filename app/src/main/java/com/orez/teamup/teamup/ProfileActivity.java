package com.orez.teamup.teamup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    TextView user_nameTv;
    User user;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        user=new User();
        user_nameTv=(TextView) findViewById(R.id.profile_name);
    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference myRef = database.child("id").child(uid);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                Toast.makeText(ProfileActivity.this,"a intrat",Toast.LENGTH_LONG).show();
                user_nameTv.setText(user.getFirst_name());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this,databaseError.toException().toString(),Toast.LENGTH_LONG).show();
            }

        });
    }
}
