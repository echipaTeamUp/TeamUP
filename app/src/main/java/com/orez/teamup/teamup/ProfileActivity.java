package com.orez.teamup.teamup;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
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
        int req_code=getIntent().getExtras().getInt("Req_code");
        //Daca vine din menu,ia userul curent
        if(req_code==1) {
            user = (User) getIntent().getSerializableExtra("User");
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        }
        //Daca vine din lobby, ia userul pe care ai apasat
        else if (req_code==2){
            getUserFromDb();
            //nu mai poti schimba imaginea de profil
            mprofileImage.setEnabled(false);}
        user_nameTv = (TextView) findViewById(R.id.profile_name);
        ref = FirebaseStorage.getInstance().getReference();
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
        if (requestCode == 1 && resultCode == RESULT_OK) {
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
                Toast.makeText(ProfileActivity.this, "Please upload a profile photo", Toast.LENGTH_SHORT).show();
            }
        });
    }
    void getUserFromDb(){
        uid=getIntent().getStringExtra("Uid");
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = database.child("id").child(uid);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}