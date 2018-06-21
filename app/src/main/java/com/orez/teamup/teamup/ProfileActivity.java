package com.orez.teamup.teamup;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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


public class ProfileActivity extends Activity {
    TextView user_nameTv;
    TextView birthdayTv;
    TextView strikesTv;
    TextView mProfileEmail;
    ImageButton edit_image;
    User user;
    ImageView mProfileImage;
    ImageView mfullProfileImage;
    Uri file;
    String uid;
    StorageReference ref;
    RatingBar mratingBar;
    ImageButton mSignoutBtn;
    ImageButton mProfileBtn;

    int req_code = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user_nameTv = (TextView) findViewById(R.id.profile_name);
        birthdayTv=(TextView) findViewById(R.id.profile_birthdayTV);
        mProfileImage = (ImageView) findViewById(R.id.profile_image);
        mratingBar=(RatingBar) findViewById(R.id.ratingBar2);
        strikesTv=(TextView) findViewById(R.id.profile_strikesTV);
        edit_image=(ImageButton) findViewById(R.id.edit_profile_image_ImgBtn);
        mSignoutBtn = (ImageButton) findViewById(R.id.menu_signoutBtn);
        mProfileBtn = (ImageButton) findViewById(R.id.menu_profileBtn);
        mProfileEmail = (TextView) findViewById(R.id.profile_email);
        mfullProfileImage=(ImageView) findViewById(R.id.profile_full_ImageView);

        req_code=getIntent().getExtras().getInt("Req_code");
        //Daca vine din menu,ia userul curent
        //Extras intent: Req_code==1, User==user
        user = (User) getIntent().getSerializableExtra("User");
        if(req_code==1) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            loadData();
            getUserFromDb();
        }
        //Daca vine din lobby, ia userul pe care ai apasat
        // Extras intent: Req_code==2, Uid==user id
        else if (req_code==2){
            uid=getIntent().getStringExtra("Uid");
            getUserFromDb();
            //nu mai poti schimba imaginea de profil

            edit_image.setVisibility(View.GONE);
            mSignoutBtn.setVisibility(View.GONE);
            mProfileBtn.setVisibility(View.GONE);
            mProfileEmail.setVisibility(View.GONE);
        }

        ref = FirebaseStorage.getInstance().getReference();
        setimage();


        edit_image.setOnClickListener(new View.OnClickListener() {
            @Override
            //te trimite in galerie sa iti alegi poza
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(i, 1);
            }
        });

        //te duce la profilul tau
        mProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, ProfileActivity.class);
                i.putExtra("User", user);
                i.putExtra("Req_code", 1);
                startActivity(i);
                //finish();
            }
        });

        //Signout
        mSignoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                setResult(Activity.RESULT_OK);
                Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ref.child(uid).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //daca exista poza
                        mfullProfileImage.setVisibility(View.VISIBLE);
                        Glide.with(ProfileActivity.this)
                                .load(uri)
                                .into(mfullProfileImage);
                    }
                });
            }
        });
        mfullProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mfullProfileImage.setVisibility(View.GONE);
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
                        .into(mProfileImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // daca nu exista poza
                if(req_code == 1)
                    Toast.makeText(ProfileActivity.this, "Please upload a profile photo", Toast.LENGTH_SHORT).show();
                Glide.with(ProfileActivity.this)
                        .load(R.drawable.userprofilewhitelarge)
                        .into(mProfileImage);
            }
        });
    }
    void getUserFromDb(){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = database.child("id").child(uid);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                loadData();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    //Incarca datele in TV
    public void loadData(){
            mSignoutBtn.setVisibility(View.VISIBLE);
            user_nameTv.setText(user.getFirst_name() + " " + user.getLast_name());
            if (user.getNumber_of_ratings() > 0)
                mratingBar.setRating(user.getRating() / user.getNumber_of_ratings());
            birthdayTv.setText("Birthday: " + user.getBirthday());
            strikesTv.setText("Strikes: " + user.getStrikes());
            mProfileEmail.setText(user.getEmail());
    }
}