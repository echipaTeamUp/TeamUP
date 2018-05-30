package com.orez.teamup.teamup;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;


public class ProfileActivity extends Activity {
    TextView user_nameTv;
    User user;
    Button msignoutBtn;
    ImageView mprofileImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        msignoutBtn=(Button) findViewById(R.id.sign_outBtn);
        mprofileImage=(ImageView) findViewById(R.id.profile_image);
        user=(User) getIntent().getSerializableExtra("User");
        user_nameTv=(TextView) findViewById(R.id.profile_name);
        user_nameTv.setText(user.getFirst_name()+" "+user.getLast_name());

        msignoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
    }
}
