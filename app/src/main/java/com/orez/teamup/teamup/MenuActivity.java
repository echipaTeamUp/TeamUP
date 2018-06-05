package com.orez.teamup.teamup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;

public class MenuActivity extends Activity {

    ImageButton mprofileBtn;
    ImageButton msignoutBtn;
    User user;
    Button mWorkBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Intent i = getIntent();
        user = (User) i.getSerializableExtra("User");
        mprofileBtn = (ImageButton) findViewById(R.id.menu_profileBtn);
        msignoutBtn = (ImageButton) findViewById(R.id.menu_signoutBtn);
        mWorkBtn = (Button) findViewById(R.id.workBtn);
        //Daca apesi pe profil, te duce la profil
        mprofileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuActivity.this, ProfileActivity.class);
                i.putExtra("User", user);
                startActivity(i);
            }
        });

        msignoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                setResult(Activity.RESULT_OK);
                finish();
                Intent i = new Intent(MenuActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }

    public void loadSportsActivity(View view) {
        Intent i = new Intent(MenuActivity.this, SportsActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

}
