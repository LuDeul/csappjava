package com.example.csappjava.Setting;

import static com.example.csappjava.LoginActivity.context2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.example.csappjava.Chatting.ChatMainActivity;
import com.example.csappjava.Community.CommunityActivity;
import com.example.csappjava.LoginActivity;
import com.example.csappjava.MainActivity;
import com.example.csappjava.Marketplace.MarketplaceActivity;
import com.example.csappjava.Mydata;
import com.example.csappjava.R;
import com.example.csappjava.models.ChatNicknameModel;
import com.example.csappjava.models.UserProfile;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SettingMain extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference("cs");
    private FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();
    private TextView myset,modset,nicknametv,schooltv;
    private String sch;
    private String myEmail;
    ImageView profile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_main);
        myset = findViewById(R.id.mysetting);
        modset = findViewById(R.id.modsetting);
        sch = Mydata.getMyschool();                  // ??????
        myEmail = Mydata.getMyemail();
        final DrawerLayout drawerLayout = findViewById(R.id.setlay);
        Toolbar toolbar = findViewById(R.id.settingtoolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);
        String mynickname = Mydata.getMynickname();
        String myschool = Mydata.getMyschoolkr();
        View headerView = navigationView.getHeaderView(0);              //??????????????? ?????? ??????
        nicknametv = headerView.findViewById(R.id.nicknametv);              //??????????????? ????????? ?????? ???????????? ??????
        schooltv = headerView.findViewById(R.id.schooltv);
        profile = headerView.findViewById(R.id.profile);

        nicknametv.setText(mynickname);                                     //????????? ????????? ?????????
        schooltv.setText(myschool);
        Glide.with(getApplicationContext())
                .load(Mydata.getMyprofile())
                .error(R.drawable.ic_noimage)
                .into(profile);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {    //??????????????? ?????? ????????? ???????????? ?????????
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                drawerLayout.closeDrawers();

                int id = item.getItemId();
                Intent intent;
                // ??? ?????? ????????? ???????????? ?????????
                switch (id) {
                    case R.id.nav_home:
                        intent = new Intent(SettingMain.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;

                    case R.id.nav_community:
                        intent = new Intent(SettingMain.this, CommunityActivity.class);
                        startActivity(intent);
                        finish();
                        break;

                    case R.id.nav_chatroome:
                        intent = new Intent(SettingMain.this, ChatMainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                        break;

                    case R.id.nav_marketplace:
                        intent = new Intent(SettingMain.this, MarketplaceActivity.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                        break;

                    case R.id.nav_settings:
                        Toast.makeText(SettingMain.this, "?????? ????????? ?????????.", Toast.LENGTH_LONG).show();
                        break;
                }
                return true;
            }
        });

        // ??? ???????????? ??????
        myset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingMain.this, MySetting.class);
                startActivity(intent);
            }
        });


        // ???????????? ????????? ?????? ??????????????? ????????????
        TextView btn_ask_email = findViewById(R.id.mysetting_sendemail);
        btn_ask_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.setType("plain/text");
                String[] address = {"csw10211@kangwon.ac.kr"};
                email.putExtra(Intent.EXTRA_EMAIL, address);
                email.putExtra(Intent.EXTRA_SUBJECT, "???????????????.");
                email.putExtra(Intent.EXTRA_TEXT, "?????? ????????? : " + myEmail + "\n ???????????? :");
                startActivity(email);
            }
        });
    }
}