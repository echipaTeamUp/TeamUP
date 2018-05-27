package com.orez.teamup.teamup;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.widget.ImageView;

public class MenuActivity extends Activity {

    ImageView profile_image;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        //primeste obiectul user din activitatea de login
        Intent i=getIntent();
        user=(User) i.getSerializableExtra("User");
        profile_image=(ImageView) findViewById(R.id.menu_profile_image);
        //Daca apesi pe imagine, te duce la profil
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MenuActivity.this,ProfileActivity.class);
                i.putExtra("User",user);
                startActivityForResult(i,1);
            }
        });
    }

    public void loadSportsActivity(View view) {
        Intent i = new Intent(MenuActivity.this, SportsActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Daca vine din profile
        if(requestCode==1 && resultCode==Activity.RESULT_OK){
            setResult(Activity.RESULT_OK);
            finish();
        }
    }
}
