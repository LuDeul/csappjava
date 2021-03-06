package com.example.csappjava.Community;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.csappjava.FirebaseID;
import com.example.csappjava.MainActivity;
import com.example.csappjava.MainLoadingActivity;
import com.example.csappjava.Mydata;
import com.example.csappjava.R;
import com.example.csappjava.adapters.ImageSliderAdapter;
import com.example.csappjava.adapters.MultiImageAdapter;
import com.example.csappjava.adapters.MultiImageAdapter2;
import com.example.csappjava.adapters.PostAdapterCommunity;
import com.example.csappjava.adapters.PostAdapterCommunityComment;
import com.example.csappjava.models.PostCommunityComment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommunityPostActivityRewrite extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();
    private EditText mTitle, mContents;
    private ImageView imageView, backbutton;
    private String stringurl;
    private int GALLERY_CODE = 10;
    private List<String> imglist = new ArrayList<>();
    private String postid, userid, title, contents, img, time, firstpath, secondpath;
    private String[] array;
    int count = 0;
    Uri imgUri;

    private static final String TAG = "MultiImageActivity";
    ArrayList<Uri> uriList = new ArrayList<>();     // ???????????? uri??? ?????? ArrayList ??????

    RecyclerView recyclerView;  // ???????????? ????????? ??????????????????
    private MultiImageAdapter adapter; // ????????????????????? ???????????? ?????????

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_post);

        mTitle = findViewById(R.id.community_post_title_edit);
        mContents = findViewById(R.id.community_post_contents_edit);
        imageView = findViewById(R.id.community_post_image);
        backbutton = findViewById(R.id.community_post_back_button);

        getintent();

        mTitle.setText(title);
        mContents.setText(contents);

        Button imagebt = (Button) findViewById(R.id.community_post_image_button);
        imagebt.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2222);
            }
        });

        recyclerView = findViewById(R.id.community_post_recyclerView);

        Button savebt = (Button) findViewById(R.id.community_post_save_button);
        savebt.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAuth.getCurrentUser() != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
                    String filename = sdf.format(new Date()) + ".png";//?????? ????????? ????????? ??????id??? ????????? ??????
                    String user = mAuth.getCurrentUser().getUid().toString();

                    String list = "";
                    for (int o = 0; o < imglist.size(); o++){
                        list = list + "," + imglist.get(o);
                    }
                    array = list.split(",");

                    for (int o = 0; o < imglist.size(); o++){
                        Uri u;
                        String su;

                        su = imglist.get(o).toString();
                        u = uriList.get(o);

                        StorageReference imgRef = firebaseStorage.getReference(su);
                        UploadTask uploadTask = imgRef.putFile(u);
                    }

                    Map<String, Object> data = new HashMap<>();
                    data.put(FirebaseID.postId, postid);                          //??????id
                    data.put(FirebaseID.userId, mAuth.getCurrentUser().getUid());        //id???
                    data.put(FirebaseID.title, mTitle.getText().toString());                 //??????
                    data.put(FirebaseID.contents, mContents.getText().toString());           //??????
                    if(imglist.isEmpty()){                //?????????url
                        imglist.add("null");
                        data.put(FirebaseID.img, imglist);
                    }
                    else{
                        data.put(FirebaseID.img, imglist);
                    }

                    data.put(FirebaseID.timestamp, FieldValue.serverTimestamp());           //??????
                    data.put(FirebaseID.recommendation, "null");                         //??????
                    data.put(FirebaseID.tag, "null");                                    //??????
                    data.put(FirebaseID.commentnum, "null");               //??????
                    data.put(FirebaseID.nickname, Mydata.getMynickname());           //?????????

                    mStore.collection(FirebaseID.post).document(firstpath).collection(secondpath).document(postid).set(data, SetOptions.merge());  //?????????

                    Loadingstart();
                }
            }
        });

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        adapter = new MultiImageAdapter(uriList, getApplicationContext());

        for (int o = 0; o < array.length; o++){
            String suri;
            Uri uuri;

            if(!array[o].equals("null")) {
                suri = array[o];
                uuri = Uri.parse(suri);

                uriList.add(uuri);  //uri??? list??? ?????????.
                imglist.add(suri);

                recyclerView.setAdapter(adapter);   // ????????????????????? ????????? ??????
                recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));     // ?????????????????? ?????? ????????? ??????
            }
            else{

            }

        }

        adapter.setOnLongItemClickListener(new MultiImageAdapter.OnLongItemClickListener() {
            @Override
            public void onLongItemClick(int pos) {
                commentdeleteDialog(pos);
            }
        });
    }

    // ???????????? ??????????????? ????????? ??? ???????????? ?????????
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
        String filename = sdf.format(new Date());//?????? ????????? ????????? ??????id??? ????????? ??????
        String user = mAuth.getCurrentUser().getUid().toString();


        if(data == null){   // ?????? ???????????? ???????????? ?????? ??????
            Toast.makeText(getApplicationContext(), "???????????? ???????????? ???????????????.", Toast.LENGTH_LONG).show();
        }
        else{   // ???????????? ???????????? ????????? ??????
            if(data.getClipData() == null){     // ???????????? ????????? ????????? ??????
                Log.e("single choice: ", String.valueOf(data.getData()));
                Uri imageUri = data.getData();
                uriList.add(imageUri);

                adapter = new MultiImageAdapter(uriList, getApplicationContext());
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

                stringurl = "images/community/" + user + filename + ".png";
                imglist.add(stringurl);
            }
            else{      // ???????????? ????????? ????????? ??????
                ClipData clipData = data.getClipData();
                Log.e("clipData", String.valueOf(clipData.getItemCount()));

                if(clipData.getItemCount() > 10){   // ????????? ???????????? 11??? ????????? ??????
                    Toast.makeText(getApplicationContext(), "????????? 10????????? ?????? ???????????????.", Toast.LENGTH_LONG).show();
                }
                else{   // ????????? ???????????? 1??? ?????? 10??? ????????? ??????
                    Log.e(TAG, "multiple choice");

                    for (int i = 0; i < clipData.getItemCount(); i++){
                        Uri imageUri = clipData.getItemAt(i).getUri();  // ????????? ??????????????? uri??? ????????????.
                        try {
                            uriList.add(imageUri);  //uri??? list??? ?????????.
                            stringurl = "images/community/" + user + filename + "_" + count + ".png";
                            imglist.add(stringurl);
                            count++;
                        } catch (Exception e) {
                            Log.e(TAG, "File select error", e);
                        }
                    }
                    adapter = new MultiImageAdapter(uriList, getApplicationContext());
                    recyclerView.setAdapter(adapter);   // ????????????????????? ????????? ??????
                    recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));     // ?????????????????? ?????? ????????? ??????
                }
            }
        }
    }

    void commentdeleteDialog(int pos){
        AlertDialog.Builder builder = new AlertDialog.Builder(CommunityPostActivityRewrite.this)
                .setTitle("????????? ??????")
                .setMessage("???????????? ?????????????????????????")
                .setPositiveButton("?????????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(CommunityPostActivityRewrite.this, "?????????????????????.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("???", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        uriList.remove(pos);
                        adapter.notifyItemRemoved(pos);
                        imglist.remove(pos);
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    void getintent() {
        Intent intent = getIntent();
        postid = intent.getStringExtra("postid");
        userid = intent.getStringExtra("userid");
        title = intent.getStringExtra("title");
        contents = intent.getStringExtra("contents");
        img = intent.getStringExtra("img");
        time = intent.getStringExtra("time");
        firstpath = Mydata.getFirstpath();
        secondpath = Mydata.getSecondpath();

        img = img.replace("[", "");
        img = img.replace("]", "");
        img = img.replaceAll(" ", "");
        array = img.split(",");
    }

    private void Loadingstart(){
        Handler handler=new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){
                String nextimg = "";
                for (int o = 0; o < imglist.size(); o++) {
                    nextimg = nextimg + imglist.get(o) + ",";
                }

                Intent intent = new Intent(CommunityPostActivityRewrite.this, CommunityActivity2.class);
                intent.putExtra("postid", postid);
                intent.putExtra("userid", userid);
                intent.putExtra("title", title);
                intent.putExtra("contents", contents);
                intent.putExtra("img", nextimg);
                intent.putExtra("time", time);
                startActivity(intent);
                finish();
            }
        },500);
    }
}