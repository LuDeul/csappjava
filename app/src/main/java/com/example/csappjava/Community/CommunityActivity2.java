package com.example.csappjava.Community;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.csappjava.FirebaseID;
import com.example.csappjava.Mydata;
import com.example.csappjava.R;
import com.example.csappjava.adapters.PostAdapterCommunity;
import com.example.csappjava.adapters.PostAdapterCommunityComment;
import com.example.csappjava.models.PostCommunity;
import com.example.csappjava.models.PostCommunityComment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
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

public class CommunityActivity2 extends AppCompatActivity {

    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();

    private RecyclerView mPostRecyclerView;
    private ImageView imageView,backView;
    private String stringurl;
    Uri imgUri;

    private PostAdapterCommunity mAdapter;
    private List<PostCommunity> mDatas;
    private PostAdapterCommunityComment mAdaptercomment;
    private List<PostCommunityComment> mDatascomment;

    private String commentpostId;
    private String deleteuser;
    private String postid,userid,title,contents,img,time,firstpath,secondpath;

    String myemail, myimg, mynickname, mypoint, myschool;

    Dialog dialogreport;    //신고
    String reportitem;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community2);

        mPostRecyclerView = findViewById(R.id.community_recycleview2);

        Button commentbt = findViewById(R.id.commentbt);
        EditText commenttv = findViewById(R.id.comment);

        getintent();

        Toolbar toolbar = findViewById (R.id.commenttoolbar);
        setSupportActionBar (toolbar); //액티비티의 앱바(App Bar)로 지정
        ActionBar actionBar = getSupportActionBar (); //앱바 제어를 위해 툴바 액세스
        actionBar.setDisplayHomeAsUpEnabled (true);
        actionBar.setTitle(secondpath);

        Log.d("LOGTEST",  "mydata :  " + Mydata.getFirstpath());

        TextView Ttitle = findViewById(R.id.title2);
        Ttitle.setText(title);

        TextView Tcontents = findViewById(R.id.content2);
        Tcontents.setText(contents);

        if(img == "null"){

        }
        else{
            imageView = findViewById(R.id.community2_image);
        }

        FirebaseStorage storage = FirebaseStorage.getInstance("gs://csapp-a3fce.appspot.com/");             //파이어베이스 스토리지 경로지정
        StorageReference storageRef = storage.getReference();
        //storageRef.child("images/test.png")
        //storageRef.child("images/"+img+".png")
        storageRef.child(img).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //이미지 로드 성공시

                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(imageView);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //이미지 로드 실패시
                Toast.makeText(getApplicationContext(), "이미지가 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        commentbt.setOnClickListener(new Button.OnClickListener() {                         //댓글 작성
            @Override
            public void onClick(View view) {
                if(commenttv.getText().toString().length() == 0){
                    Toast.makeText(getApplicationContext(), "빈칸입니다.", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (mAuth.getCurrentUser() != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
                        String filename = sdf.format(new Date()) + ".png";//현재 시간과 사용자 고유id로 파일명 지정
                        String user = mAuth.getCurrentUser().getUid().toString();

                        if (imageView.isEnabled()) {     //이미지 없을 때
                            stringurl = "null";
                        } else {       //이미지가 있을 때
                            StorageReference imgRef = firebaseStorage.getReference("/images/comment/" + user + filename);

                            UploadTask uploadTask = imgRef.putFile(imgUri);
                            //stringurl = imgRef.getDownloadUrl().toString();
                            stringurl = "/images/comment/" + user + filename;
                        }

                        String commentId = mStore.collection(FirebaseID.post).document(firstpath).collection(secondpath).document(userid).collection(FirebaseID.comment).document().getId(); //중복 방지
                        commentpostId = commentId;
                        Map<String, Object> data = new HashMap<>();
                        data.put(FirebaseID.userId, mAuth.getCurrentUser().getUid());        //id값//제목
                        data.put(FirebaseID.nickname, mynickname.toString());               //닉네임
                        data.put(FirebaseID.contents, commenttv.getText().toString());           //내용
                        data.put(FirebaseID.img, stringurl);                                     //이미지url
                        data.put(FirebaseID.timestamp, FieldValue.serverTimestamp());           //타임
                        data.put(FirebaseID.commentId,commentId.toString());                //댓글 id

                        mStore.collection(FirebaseID.post).document(firstpath).collection(secondpath).document(postid).collection(FirebaseID.comment).document(commentId).set(data, SetOptions.merge());  //값넣기
                        commenttv.setText("");
                    }
                }
            }
        });

        dialogreport = new Dialog(CommunityActivity2.this);       // Dialog 초기화
        dialogreport.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        dialogreport.setContentView(R.layout.dialog_report);             // xml 레이아웃 파일과 연결

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        String uid = mAuth.getCurrentUser().getUid();

        if(userid.equals(uid)){                                             //자기가 쓴 게시글일경우 수정,삭제 메뉴 불러오기
            getMenuInflater().inflate (R.menu.mainoption, menu);
        }
        else{                                                               //자기가 쓴 게시글이 아닐경우 신고하기 버튼만 있는 메뉴 불러오기
            getMenuInflater().inflate (R.menu.mainoption2, menu);
        }
        return true;
    }

    //앱바(App Bar)에 표시된 액션 또는 오버플로우 메뉴가 선택되면
    //액티비티의 onOptionsItemSelected() 메서드가 호출
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId ()) {
            case android.R.id.home:
                finish ();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                return true;
            case R.id.option_delete:
                //Toast.makeText(getApplicationContext(), "삭제", Toast.LENGTH_SHORT).show();
                deleteDialog();
                return true;
            case R.id.option_report:
                showDialogreport();
                //Toast.makeText(getApplicationContext(), "신고", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.option_rewrite:
                //Log.d("LOGTEST", "postid : " + postid);
                Toast.makeText(getApplicationContext(), "수정", Toast.LENGTH_SHORT).show();
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

        mDatascomment = new ArrayList<>();

        mStore.collection(FirebaseID.post).document(firstpath).collection(secondpath).document(postid).collection(FirebaseID.comment)
                .orderBy(FirebaseID.timestamp, Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {   //파이어베이스에서 목록을 실시간으로 불러오는 이벤트
                        if (queryDocumentSnapshots != null) {
                            mDatascomment.clear();
                            for (DocumentSnapshot snap : queryDocumentSnapshots.getDocuments()) {
                                Map<String, Object> shot = snap.getData();
                                //String nickname = String.valueOf((shot.get(FirebaseID.nickname)));
                                String commentId = String.valueOf((shot.get(FirebaseID.commentId)));
                                String userId = String.valueOf((shot.get(FirebaseID.userId)));
                                String contents = String.valueOf(shot.get(FirebaseID.contents));
                                String img = String.valueOf(shot.get(FirebaseID.img));
                                String nickname = String.valueOf(shot.get(FirebaseID.nickname));
                                //String time = String.valueOf(shot.get(clock()));
                                //String time = String.valueOf(shot.get(FirebaseID.timestamp));
                                //Date date = Date.valueOf(shot.get(FirebaseID.timestamp));
                                //data.put(FirebaseID.timestamp, FieldValue.serverTimestamp());

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

                            mPostRecyclerView.setAdapter(mAdaptercomment);
                        }
                    }

                });

    }

    void deleteDialog(){
        FirebaseUser user = mAuth.getInstance().getCurrentUser();
        //String uid = user.getUid();
        String uid = mAuth.getCurrentUser().getUid();


        AlertDialog.Builder builder = new AlertDialog.Builder(CommunityActivity2.this)
                .setTitle("삭제")
                .setMessage("삭제하시겠습니까?")
                .setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(CommunityActivity2.this, "취소하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(userid.equals(uid)){

                            mStore.collection(FirebaseID.post).document(firstpath).collection(secondpath).document(postid).collection("comment").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() { //댓글 삭제 작업
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.getResult().size() > 0){
                                        for(QueryDocumentSnapshot ds : task.getResult()){
                                            ds.getReference().delete();
                                        }
                                    }
                                }
                            });
                            mStore.collection(FirebaseID.post).document(firstpath).collection(secondpath).document(postid)             //게시글 삭제 작업
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(CommunityActivity2.this, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(CommunityActivity2.this, "삭제 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                        else{
                            Toast.makeText(CommunityActivity2.this, "글 작성자가 아닙니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    void commentdeleteDialog(){
        Intent intent = getIntent();
        String postid = intent.getStringExtra("postid");
        String firstpath = Mydata.getFirstpath();
        String secondpath = Mydata.getSecondpath();

        //FirebaseUser user = mAuth.getInstance().getCurrentUser();
        //String uid = user.getUid();
        String uid = mAuth.getCurrentUser().getUid();

        AlertDialog.Builder builder = new AlertDialog.Builder(CommunityActivity2.this)
                .setTitle("삭제")
                .setMessage("삭제하시겠습니까?")
                .setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(CommunityActivity2.this, "취소하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(deleteuser.equals(uid)){
                            mStore.collection(FirebaseID.post).document(firstpath).collection(secondpath).document(postid).collection("comment").document(commentpostId)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(CommunityActivity2.this, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(CommunityActivity2.this, "삭제 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                        else{
                            Toast.makeText(CommunityActivity2.this, " 글 작성자가 아닙니다. ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void showDialogreport(){
        dialogreport.show(); // 다이얼로그 띄우기


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

        /* 이 함수 안에 원하는 디자인과 기능을 구현하면 된다. */

        // 위젯 연결 방식은 각자 취향대로~
        // '아래 아니오 버튼'처럼 일반적인 방법대로 연결하면 재사용에 용이하고,
        // '아래 네 버튼'처럼 바로 연결하면 일회성으로 사용하기 편함.
        // *주의할 점: findViewById()를 쓸 때는 -> 앞에 반드시 다이얼로그 이름을 붙여야 한다.

        // 취소 버튼
        Button noBtn = dialogreport.findViewById(R.id.noBtn);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 원하는 기능 구현
                dialogreport.dismiss(); // 다이얼로그 닫기
                Toast.makeText(CommunityActivity2.this, " 신고취소 ", Toast.LENGTH_SHORT).show();
            }
        });
        // 신고하기 버튼
        dialogreport.findViewById(R.id.yesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et = dialogreport.findViewById(R.id.report_edittext);
                String reportconetnt = et.getText().toString();

                //String postId = mStore.collection("report").document(firstpath).collection(secondpath).document().getId(); //중복 방지
                String postId = mStore.collection("report").document().getId(); //중복 방지
                Map<String, Object> data = new HashMap<>();
                data.put("1_1_신고글ID",postId);                          //신고글 id
                data.put("1_2_신고자ID",mAuth.getCurrentUser().getUid()); //신고자 id
                data.put("1_3_신고사유",reportitem);                //내용
                data.put("1_4_신고내용",reportconetnt);                //내용

                data.put("2_1_게시글ID",postid);                          //신고게시글 id
                data.put("2_2_게시글작성자ID",userid);                          //신고게시글 유저id
                data.put("2_3_게시글제목",title);                           //신고 게시물 제목

                data.put("3_1_경로1","community");
                data.put("3_2_경로2",firstpath);
                data.put("3_3_경로3",secondpath);
                data.put(FirebaseID.timestamp, FieldValue.serverTimestamp());    //타임

                //mStore.collection("report").document(firstpath).collection(secondpath).document(postId).set(data, SetOptions.merge());  //값넣기
                mStore.collection("report").document(postId).set(data, SetOptions.merge());  //값넣기
                Toast.makeText(CommunityActivity2.this, " 신고완료 ", Toast.LENGTH_SHORT).show();
                dialogreport.dismiss(); // 다이얼로그 닫기
                // 원하는 기능 구현
                //finish();           // 앱 종료
            }
        });
    }

    void getintent(){
        Intent intent = getIntent();
        postid = intent.getStringExtra("postid");
        userid = intent.getStringExtra("userid");
        title = intent.getStringExtra("title");
        contents = intent.getStringExtra("contents");
        img = intent.getStringExtra("img");
        time = intent.getStringExtra("time");
        firstpath = Mydata.getFirstpath();
        secondpath = Mydata.getSecondpath();
        myemail = Mydata.getMyemail();
        myimg = Mydata.getMyprofile();
        mynickname = Mydata.getMynickname();
        myschool = Mydata.getMyschool();
    }
}