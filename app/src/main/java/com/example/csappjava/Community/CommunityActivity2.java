package com.example.csappjava.Community;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.csappjava.FirebaseID;
import com.example.csappjava.Mydata;
import com.example.csappjava.ProgressDialog;
import com.example.csappjava.R;
import com.example.csappjava.adapters.MultiImageAdapter2;
import com.example.csappjava.adapters.PostAdapterCommunity;
import com.example.csappjava.adapters.PostAdapterCommunityComment;
import com.example.csappjava.models.PostCommunity;
import com.example.csappjava.models.PostCommunityComment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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

import uk.co.senab.photoview.PhotoViewAttacher;

public class CommunityActivity2 extends AppCompatActivity {

    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();
    private RecyclerView mPostRecyclerView, mPostRecyclerView2;
    private ImageView imageView,backView;
    private String stringurl;
    private String[] array;
    ArrayList<Uri> uriList = new ArrayList<>();     // ???????????? uri??? ?????? ArrayList ??????
    Uri imgUri;
    private PostAdapterCommunity mAdapter;
    private List<PostCommunity> mDatas;
    private PostAdapterCommunityComment mAdaptercomment;
    private List<PostCommunityComment> mDatascomment;
    private String commentpostId;
    private String deleteuser;
    private String postid,userid,title,contents,img,time,firstpath,secondpath;
    private MultiImageAdapter2 adapter; // ????????????????????? ???????????? ?????????
    String myemail, myimg, mynickname, mypoint, myschool;
    Dialog dialogreport, dialogimg;    //??????
    String reportitem;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community2);

        progressDialog = new ProgressDialog(this);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        //progressDialog.show();
        Mydata.setCount(1);

        mPostRecyclerView = findViewById(R.id.community2_recyclerView);
        mPostRecyclerView2 = findViewById(R.id.community_recycleview2);

        Button commentbt = findViewById(R.id.commentbt);
        EditText commenttv = findViewById(R.id.comment);

        getintent();

        Toolbar toolbar = findViewById (R.id.commenttoolbar);
        setSupportActionBar (toolbar); //??????????????? ??????(App Bar)??? ??????
        ActionBar actionBar = getSupportActionBar (); //?????? ????????? ?????? ?????? ?????????
        actionBar.setDisplayHomeAsUpEnabled (true);
        actionBar.setTitle(secondpath);

        TextView Ttitle = findViewById(R.id.title2);
        Ttitle.setText(title);

        TextView Tcontents = findViewById(R.id.content2);
        Tcontents.setText(contents);

        if(img == "null"){

        }
        else{
            imageView = findViewById(R.id.community2_image);
        }

        commentbt.setOnClickListener(new Button.OnClickListener() {                         //?????? ??????
            @Override
            public void onClick(View view) {
                if(commenttv.getText().toString().length() == 0){
                    Toast.makeText(getApplicationContext(), "???????????????.", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (mAuth.getCurrentUser() != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
                        String filename = sdf.format(new Date()) + ".png";//?????? ????????? ????????? ??????id??? ????????? ??????
                        String user = mAuth.getCurrentUser().getUid().toString();

                        if (imageView.isEnabled()) {     //????????? ?????? ???
                            stringurl = "null";
                        } else {       //???????????? ?????? ???
                            StorageReference imgRef = firebaseStorage.getReference("/images/comment/" + user + filename);
                            UploadTask uploadTask = imgRef.putFile(imgUri);
                            stringurl = "/images/comment/" + user + filename;
                        }

                        String commentId = mStore.collection(FirebaseID.post).document(firstpath).collection(secondpath).document(userid).collection(FirebaseID.comment).document().getId(); //?????? ??????
                        commentpostId = commentId;
                        Map<String, Object> data = new HashMap<>();
                        data.put(FirebaseID.userId, mAuth.getCurrentUser().getUid());        //id???//??????
                        data.put(FirebaseID.nickname, mynickname.toString());               //?????????
                        data.put(FirebaseID.contents, commenttv.getText().toString());           //??????
                        data.put(FirebaseID.img, stringurl);                                     //?????????url
                        data.put(FirebaseID.timestamp, FieldValue.serverTimestamp());           //??????
                        data.put(FirebaseID.commentId,commentId.toString());                //?????? id


                        mStore.collection(FirebaseID.post).document(firstpath).collection(secondpath).document(postid).collection(FirebaseID.comment).document(commentId).set(data, SetOptions.merge());  //?????????
                        commenttv.setText("");
                    }
                }
            }
        });

        dialogreport = new Dialog(CommunityActivity2.this);       // Dialog ?????????
        dialogreport.requestWindowFeature(Window.FEATURE_NO_TITLE); // ????????? ??????
        dialogreport.setContentView(R.layout.dialog_report);             // xml ???????????? ????????? ??????

        dialogimg = new Dialog(CommunityActivity2.this);       // Dialog ?????????
        dialogimg.requestWindowFeature(Window.FEATURE_NO_TITLE); // ????????? ??????
        dialogimg.setContentView(R.layout.dialog_photo);             // xml ???????????? ????????? ??????
        imgdialog = dialogimg.findViewById(R.id.imagedialog);
        dialogimg.setCancelable(false);

        if(!array[0].equals("null")){
            for (int o = 0; o < array.length; o++){
                Loadingstart();
                String suri = array[o];
                Uri uuri = Uri.parse(suri);

                uriList.add(uuri);  //uri??? list??? ?????????.
            }
            adapter = new MultiImageAdapter2(uriList, getApplicationContext());
            mPostRecyclerView.setAdapter(adapter);   // ????????????????????? ????????? ??????
            mPostRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));     // ?????????????????? ?????? ????????? ??????

            adapter.setOnItemClickListener(new MultiImageAdapter2.OnItemClickListener() {
                @Override
                public void onItemClick(int pos) {
                    showdialogimg(pos);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        String uid = mAuth.getCurrentUser().getUid();
        if(userid.equals(uid)){                                             //????????? ??? ?????????????????? ??????,?????? ?????? ????????????
            getMenuInflater().inflate (R.menu.mainoption, menu);
        }
        else{                                                               //????????? ??? ???????????? ???????????? ???????????? ????????? ?????? ?????? ????????????
            getMenuInflater().inflate (R.menu.mainoption2, menu);
        }
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
            case R.id.option_delete:
                deleteDialog();
                return true;
            case R.id.option_report:
                showDialogreport();
                return true;
            case R.id.option_rewrite:
                Intent intent = new Intent(CommunityActivity2.this, CommunityPostActivityRewrite.class);
                intent.putExtra("postid", postid);
                intent.putExtra("userid", userid);
                intent.putExtra("title", title);
                intent.putExtra("contents", contents);
                intent.putExtra("img", img);
                intent.putExtra("time", time);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected (item);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(Mydata.getCount() >= array.length){
            progressDialog.dismiss();
        }
        mDatascomment = new ArrayList<>();
        mStore.collection(FirebaseID.post).document(firstpath).collection(secondpath).document(postid).collection(FirebaseID.comment)
                .orderBy(FirebaseID.timestamp, Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {   //???????????????????????? ????????? ??????????????? ???????????? ?????????
                        if (queryDocumentSnapshots != null) {
                            mDatascomment.clear();
                            for (DocumentSnapshot snap : queryDocumentSnapshots.getDocuments()) {
                                Map<String, Object> shot = snap.getData();
                                String commentId = String.valueOf((shot.get(FirebaseID.commentId)));
                                String userId = String.valueOf((shot.get(FirebaseID.userId)));
                                String contents = String.valueOf(shot.get(FirebaseID.contents));
                                String img = String.valueOf(shot.get(FirebaseID.img));
                                String nickname = String.valueOf(shot.get(FirebaseID.nickname));

                                PostCommunityComment data = new PostCommunityComment(userId, commentId, contents, img , nickname);
                                mDatascomment.add(data);
                            }

                            mAdaptercomment = new PostAdapterCommunityComment(mDatascomment);

                            mAdaptercomment.setOnLongItemClickListener(new PostAdapterCommunityComment.OnLongItemClickListener() {
                                @Override
                                public void onLongItemClick(int pos) {

                                    PostCommunityComment hm = mDatascomment.get(pos);
                                    commentpostId = hm.getPostid();
                                    deleteuser = hm.getUserId();

                                    commentdeleteDialog();
                                }
                            });
                            mPostRecyclerView2.setAdapter(mAdaptercomment);
                        }
                    }
                });
    }

    void deleteDialog(){                                            //????????? ?????? ???????????????
        FirebaseUser user = mAuth.getInstance().getCurrentUser();
        String uid = mAuth.getCurrentUser().getUid();

        AlertDialog.Builder builder = new AlertDialog.Builder(CommunityActivity2.this)
                .setTitle("??????")
                .setMessage("?????????????????????????")
                .setPositiveButton("?????????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(CommunityActivity2.this, "?????????????????????.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("???", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(userid.equals(uid)){
                            StorageReference storageReference = firebaseStorage.getReference();
                            if(!array[0].equals("null")){
                                for (int o = 0; o < array.length; o++){
                                String suri = array[o];
                                StorageReference desertRef = storageReference.child(suri);
                                desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                    }
                                });
                                }
                            }

                            mStore.collection(FirebaseID.post).document(firstpath).collection(secondpath).document(postid).collection("comment").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() { //?????? ?????? ??????
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.getResult().size() > 0){
                                        for(QueryDocumentSnapshot ds : task.getResult()){
                                            ds.getReference().delete();
                                        }
                                    }
                                }
                            });
                            mStore.collection(FirebaseID.post).document(firstpath).collection(secondpath).document(postid)             //????????? ?????? ??????
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(CommunityActivity2.this, "?????????????????????.", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(CommunityActivity2.this, "?????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                        else{
                            Toast.makeText(CommunityActivity2.this, "??? ???????????? ????????????.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    void commentdeleteDialog(){                         //?????? ?????? ???????????????
        String uid = mAuth.getCurrentUser().getUid();

        AlertDialog.Builder builder = new AlertDialog.Builder(CommunityActivity2.this)
                .setTitle("??????")
                .setMessage("?????????????????????????")
                .setPositiveButton("?????????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(CommunityActivity2.this, "?????????????????????.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("???", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(deleteuser.equals(uid)){
                            mStore.collection(FirebaseID.post).document(firstpath).collection(secondpath).document(postid).collection("comment").document(commentpostId)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(CommunityActivity2.this, "?????????????????????.", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(CommunityActivity2.this, "?????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                        else{
                            Toast.makeText(CommunityActivity2.this, " ??? ???????????? ????????????. ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void showDialogreport(){                 //???????????? ???????????????
        dialogreport.show(); // ??????????????? ?????????

        Spinner reportspinner = dialogreport.findViewById(R.id.spinner_report);

        reportspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                reportitem = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button noBtn = dialogreport.findViewById(R.id.noBtn);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogreport.dismiss(); // ??????????????? ??????
                Toast.makeText(CommunityActivity2.this, " ???????????? ", Toast.LENGTH_SHORT).show();
            }
        });
        // ???????????? ??????
        dialogreport.findViewById(R.id.yesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et = dialogreport.findViewById(R.id.report_edittext);
                String reportconetnt = et.getText().toString();

                //String postId = mStore.collection("report").document(firstpath).collection(secondpath).document().getId(); //?????? ??????
                String postId = mStore.collection("report").document().getId(); //?????? ??????
                Map<String, Object> data = new HashMap<>();
                data.put("1_1_?????????ID",postId);                          //????????? id
                data.put("1_2_?????????ID",mAuth.getCurrentUser().getUid()); //????????? id
                data.put("1_3_????????????",reportitem);                //??????
                data.put("1_4_????????????",reportconetnt);                //??????

                data.put("2_1_?????????ID",postid);                          //??????????????? id
                data.put("2_2_??????????????????ID",userid);                          //??????????????? ??????id
                data.put("2_3_???????????????",title);                           //?????? ????????? ??????

                data.put("3_1_??????1","community");
                data.put("3_2_??????2",firstpath);
                data.put("3_3_??????3",secondpath);
                data.put(FirebaseID.timestamp, FieldValue.serverTimestamp());    //??????

                mStore.collection("report").document(postId).set(data, SetOptions.merge());  //?????????
                Toast.makeText(CommunityActivity2.this, " ???????????? ", Toast.LENGTH_SHORT).show();
                dialogreport.dismiss(); // ??????????????? ??????
            }
        });
    }

    private ImageView imgdialog;
    private PhotoViewAttacher mAttacher;    //????????? ???????????? ??????

    void showdialogimg(int pos){
        dialogimg.show(); // ??????????????? ?????????
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://csapp-a3fce.appspot.com/");             //?????????????????? ???????????? ????????????
        StorageReference storageRef = storage.getReference();
        mAttacher = new PhotoViewAttacher(imgdialog);

        storageRef.child(array[pos]).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //????????? ?????? ?????????
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(imgdialog);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //????????? ?????? ?????????
            }
        });
        dialogimg.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogimg.dismiss();
            }
        });
    }

    private void Loadingstart(){
        Handler handler=new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){
                progressDialog.show();

                if(Mydata.getCount() >= array.length){
                    progressDialog.dismiss();
                }
                else{
                    Loadingstart();
                }
            }
        },100);
    }

    void getintent(){
        Intent intent = getIntent();
        postid = intent.getStringExtra("postid");
        userid = intent.getStringExtra("userid");
        title = intent.getStringExtra("title");
        contents = intent.getStringExtra("contents");
        img = intent.getStringExtra("img");
        time = intent.getStringExtra("time");
        firstpath = Mydata.getPostpath1();
        secondpath = Mydata.getPostpath2();
        myemail = Mydata.getMyemail();
        myimg = Mydata.getMyprofile();
        mynickname = Mydata.getMynickname();
        myschool = Mydata.getMyschool();

        img = img.replace("[","");
        img = img.replace("]","");
        img = img.replaceAll(" ","");
        array = img.split(",");
    }
}