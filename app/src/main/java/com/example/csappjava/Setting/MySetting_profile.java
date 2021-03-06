package com.example.csappjava.Setting;

import static com.example.csappjava.LoginActivity.context2;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.csappjava.FirebaseID;
import com.example.csappjava.LoginActivity;
import com.example.csappjava.Mydata;
import com.example.csappjava.ProgressDialog;
import com.example.csappjava.R;
import com.example.csappjava.models.UserProfile;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class MySetting_profile extends AppCompatActivity {

    private ImageView profile;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference("cs");
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    ProgressDialog progressDialog;

    private String userUid,sch,times;
    Uri imgUri;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mysetting_profile);

        progressDialog = new ProgressDialog(this);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        sch = Mydata.getMyschool();
        userUid = mAuth.getCurrentUser().getUid();
        times = String.valueOf(System.currentTimeMillis());
        profile = findViewById(R.id.myprofile_photo);

        Toolbar toolbar = findViewById (R.id.setoolbar);
        setSupportActionBar (toolbar); //??????????????? ??????(App Bar)??? ??????
        ActionBar actionBar = getSupportActionBar (); //?????? ????????? ?????? ?????? ?????????
        actionBar.setDisplayHomeAsUpEnabled (true);


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MySetting_profile.this) // TestActivity ???????????? ?????? Activity??? ?????? ??????.
                        .setMessage("???????????????")     // ?????? ?????? (?????? ??????)
                        .setPositiveButton("?????????", new DialogInterface.OnClickListener() {      // ??????1 (?????? ??????)
                            public void onClick(DialogInterface dialog, int which){

                                Intent gallery= new Intent();
                                gallery.setAction(gallery.ACTION_GET_CONTENT);
                                gallery.setType("image/*");
                                startActivityForResult(gallery,10);
                                profile.setEnabled(false);

                            }
                        })

                        .setNegativeButton("??????", new DialogInterface.OnClickListener() {     // ??????2 (?????? ??????)
                            public void onClick(DialogInterface dialog, int which){
                                Toast.makeText(getApplicationContext(), "??????", Toast.LENGTH_SHORT).show(); // ????????? ??????
                            }
                        })
                        .show();
            }
        });

        Button btn_upload_profile = findViewById(R.id.mysetting_profile_upload);
        btn_upload_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imgUri!=null){
                    progressDialog.show();
                    uploadImage(imgUri);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate (R.menu.mainoption, menu);

        return true;
    }

    //??????(App Bar)??? ????????? ?????? ?????? ??????????????? ????????? ????????????
    //??????????????? onOptionsItemSelected() ???????????? ??????
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId ()) {
            case android.R.id.home:
                finish ();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                return true;

            default:
                return super.onOptionsItemSelected (item);
        }
    }

    private void uploadImage(Uri uri){
        StorageReference fileRef = storageReference.child("/profile/profile" + userUid+ times+".png");
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        reference.child(sch+"/user").child(userUid).child("profile").setValue("profile/profile"+userUid+times+".png");
                        Map<String, Object> data = new HashMap<>();
                        data.put(FirebaseID.img, uri.toString());                          //??????id
                        mStore.collection(FirebaseID.user).document(mAuth.getCurrentUser().getUid()).set(data, SetOptions.merge());
                        Log.d("LOGTEST",  "????????? : " + uri + ", " + "profile/profile"+userUid+times+".png");
                        Toast.makeText(MySetting_profile.this,"?????? ??????",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        finish();
                    }
                });

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {                      //??????????????? ????????? ????????????
            case 10:
                if (resultCode == RESULT_OK) {
                    //????????? ???????????? ?????? ????????????
                    imgUri = data.getData();
                    Glide.with(this).load(imgUri).into(profile);
                }
                break;
        }
    }

}
