package com.example.csappjava.Setting;

import static com.example.csappjava.LoginActivity.context2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

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
    private Button myset,modset;
    private String sch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_main);
        myset = findViewById(R.id.mysetting);
        modset = findViewById(R.id.modsetting);
        sch = Mydata.getMyschool();                  // 학교

        final DrawerLayout drawerLayout = findViewById(R.id.setlay);
        Toolbar toolbar = findViewById(R.id.settingtoolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("설정");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {    //네비게이션 메뉴 누를때 발생하는 이벤트
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                drawerLayout.closeDrawers();

                int id = item.getItemId();
                Intent intent;
                // 각 메뉴 클릭시 이뤄지는 이벤트
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
                        Toast.makeText(SettingMain.this, "이미 설정창 입니다.", Toast.LENGTH_LONG).show();
                        finish();
                        break;
                }
                return true;
            }
        });

        // 내 설정으로 이동
        myset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingMain.this, MySetting.class);
                startActivity(intent);
            }
        });


        // 모드설정으로 이동
//        modset.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(SettingMain.this, ModSetting.class);
//                startActivity(intent);
//            }
//        });


        // 문의하기 이메일 보냄 조성우에게 ㅅㄱㅂㅇ
        Button btn_ask_email = findViewById(R.id.mysetting_sendemail);
        btn_ask_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.setType("plain/text");
                String[] address = {"csw10211@kangwon.ac.kr"};
                email.putExtra(Intent.EXTRA_EMAIL, address);
                email.putExtra(Intent.EXTRA_SUBJECT, "test@test");
                email.putExtra(Intent.EXTRA_TEXT, "회원 이메일 : ");
                startActivity(email);
            }
        });
    }
}