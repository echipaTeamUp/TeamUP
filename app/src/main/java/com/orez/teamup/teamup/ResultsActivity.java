package com.orez.teamup.teamup;

import android.app.Activity;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ResultsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        /*
        ref.addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                String val = dataSnapshot.getValue(String.class);
                Toast.makeText(ResultsActivity.this, val,
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError error){
                Toast.makeText(ResultsActivity.this, "failed",
                        Toast.LENGTH_LONG);
            }
        });
        */
    }
}
