package com.example.csappjava.Chatting;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.csappjava.Mydata;
import com.example.csappjava.R;
import com.example.csappjava.adapters.ChatAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

public class ChatMainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    List<String> list;
    String username,Nickname;
    RecyclerView userList;
    ChatAdapter userAdapter;
    String sch;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatmain);


        //---------------
        username = mAuth.getCurrentUser().getUid();

        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();
        list = new ArrayList<>();
        userList = (RecyclerView)findViewById(R.id.userList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(ChatMainActivity.this,1);
        userList.setLayoutManager(layoutManager);
        userAdapter = new ChatAdapter(ChatMainActivity.this,list, ChatMainActivity.this, username, Nickname);
        userList.setAdapter(userAdapter);

        sch = Mydata.getMyschool();

        list();

    }


        //----------------------
    public void list (){
        reference.child("cs").child(sch).child("Chat").child(username).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull  DataSnapshot snapshot, @Nullable  String previousChildName) {
                if(!snapshot.getKey().equals(username)){
                    list.add(snapshot.getKey());
                    userAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable  String previousChildName) { }
            @Override
            public void onChildRemoved(@NonNull  DataSnapshot snapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable  String previousChildName) { }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}