package com.orez.teamup.teamup;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class ProfileActivity extends Activity {
    TextView user_nameTv;
    User user;

    ImageView mprofileImage;
    Uri file;
    String uid;
    StorageReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mprofileImage = (ImageView) findViewById(R.id.profile_image);
        user = (User) getIntent().getSerializableExtra("User");
        user_nameTv = (TextView) findViewById(R.id.profile_name);
        ref = FirebaseStorage.getInstance().getReference();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        setimage();
        user_nameTv.setText(user.getFirst_name() + " " + user.getLast_name());

        mprofileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            //te trimite in galerie sa iti alegi poza
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(i, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //pentru cand vine din galerie
        if (requestCode == 1 && resultCode==RESULT_OK) {
            file = data.getData();
            ref.child(uid).putFile(file);
            setimage();
        }
    }
    //incarca imaginea in imageview
    public void setimage() {
        ref.child(uid).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //daca exista poza
                Glide.with(ProfileActivity.this)
                        .load(uri)
                        .into(mprofileImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // daca nu exista poza
                Toast.makeText(ProfileActivity.this,"Please upload a profile photo",Toast.LENGTH_SHORT).show();
            }
        });
    }
}