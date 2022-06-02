package com.example.csappjava;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.csappjava.Community.CommunityActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainLoadingActivity extends AppCompatActivity {
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Mydata mdata = (Mydata)getApplication();
        mdata.Init();

        String uid = mAuth.getCurrentUser().getUid();                                   //자신의 정보 불러오기
        DocumentReference docRef = mStore.collection(FirebaseID.user).document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Mydata.setMyemail(document.getData().get("email").toString());
                        Mydata.setMyprofile(document.getData().get("img").toString());
                        Mydata.setMynickname(document.getData().get("nickname").toString());
                        Mydata.setMyschool(document.getData().get("school").toString());
                        Mydata.setMyschoolkr(document.getData().get("schoolKR").toString());
                        Mydata.setMycampus(document.getData().get("campus").toString());
                        Mydata.setMydepartment(document.getData().get("department").toString());
                        Mydata.setMyaffiliation(document.getData().get("affiliation").toString());
                    } else {

                    }
                } else {

                }
            }
        });
        //로딩화면 시작.
        Loadingstart();
    }
    private void Loadingstart(){
        Handler handler=new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){
                Intent intent=new Intent(getApplicationContext(), CommunityActivity.class);
                startActivity(intent);
                finish();
            }
        },500);
    }
}