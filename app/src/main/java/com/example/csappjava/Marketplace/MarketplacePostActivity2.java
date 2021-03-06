package com.example.csappjava.Marketplace;

import android.app.Dialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.csappjava.FirebaseID;
import com.example.csappjava.Mydata;
import com.example.csappjava.R;
import com.example.csappjava.adapters.MultiImageAdapter;
import com.example.csappjava.adapters.PostAdapterMarketplacesearch;
import com.example.csappjava.models.PostMarketplaceSearch;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarketplacePostActivity2 extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseFirestore mStore2 = FirebaseFirestore.getInstance();
    private FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();
    private DatabaseReference mDatabaseRef;     // ????????? ??????????????????
    private DatabaseReference mDatabaseRef2;     // ????????? ??????????????????
    private EditText mTitle, mContents, mPrice;
    private ImageView imageView;
    private String stringurl;
    private int GALLERY_CODE = 10;
    private List<String> imglist = new ArrayList<>();
    private TextView bname,bprice,bpublisher;
    private List<String> spinnerArray =  new ArrayList<String>();
    int count = 0;
    private Spinner sItems1, sItems2;
    private PostAdapterMarketplacesearch mAdapter;
    private List<PostMarketplaceSearch> mDatas;
    private RecyclerView mPostRecyclerView;

    private DecimalFormat decimalFormat = new DecimalFormat("#,###");       //?????? ?????? ??????
    private String result="";
    Uri imgUri;

    private static final String TAG = "MultiImageActivity";
    ArrayList<Uri> uriList = new ArrayList<>();     // ???????????? uri??? ?????? ArrayList ??????

    RecyclerView recyclerView;  // ???????????? ????????? ??????????????????
    private MultiImageAdapter adapter; // ????????????????????? ???????????? ?????????

    private List<String> list;          // ???????????? ?????? ???????????????
    private Button searchbt;
    private TextView searchtv, searchposttv;

    private List<String> list2;          // ???????????? ?????? ???????????????
    private Button searchbt2, list_seach2;
    private TextView searchtv2, addtv2;
    private RadioGroup radioGroup;

    private Button addlesson;
    private TextView test1_1, test2_1, setlesson, setprofessor;
    private EditText test1_2, test2_2,dialog_search_tv;

    Dialog dialogsearch;    //??????
    Dialog dialogsearch2;    //??????

    private String myschool, adapterlecture, adapterprofessor, adapterdepartment, adapteryear, adaptermonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketplace_post2);

        mTitle = findViewById(R.id.market_post_title_edit);
        mContents = findViewById(R.id.market_post_contents_edit);
        imageView = findViewById(R.id.marketplace_image);
        mPrice = findViewById(R.id.market_post_price);
        mPrice.addTextChangedListener(watcher);

        mydata();

        Intent intent = getIntent();
        String firstpath = Mydata.getFirstpath();
        String secondpath = Mydata.getSecondpath();

        FirebaseUser user = mAuth.getCurrentUser();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("schooldata");
        mDatabaseRef2 = FirebaseDatabase.getInstance().getReference("lesson");

        dialogsearch = new Dialog(MarketplacePostActivity2.this);       // Dialog ?????????
        dialogsearch.requestWindowFeature(Window.FEATURE_NO_TITLE); // ????????? ??????
        dialogsearch.setContentView(R.layout.activity_marketplace_post_search1);             // xml ???????????? ????????? ??????
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogsearch.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        Window window = dialogsearch.getWindow();
        window.setAttributes(lp);

        dialogsearch2 = new Dialog(MarketplacePostActivity2.this);       // Dialog ?????????
        dialogsearch2.requestWindowFeature(Window.FEATURE_NO_TITLE); // ????????? ??????
        dialogsearch2.setContentView(R.layout.activity_marketplace_post_search2);             // xml ???????????? ????????? ??????
        WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
        lp2.copyFrom(dialogsearch.getWindow().getAttributes());
        lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp2.height = WindowManager.LayoutParams.WRAP_CONTENT;
        Window window2 = dialogsearch2.getWindow();
        window2.setAttributes(lp2);

        mPostRecyclerView = dialogsearch2.findViewById(R.id.list_search_recycleview);

        Button imagebt = (Button) findViewById(R.id.market_post_image_button);
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

        recyclerView = findViewById(R.id.market_post_recyclerView);

        Button savebt = (Button) findViewById(R.id.market_post_save_button) ;
        savebt.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAuth.getCurrentUser() != null){
                    SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMddhhmmss");
                    String filename= sdf.format(new Date())+ ".png";//?????? ????????? ????????? ??????id??? ????????? ??????
                    String user = mAuth.getCurrentUser().getUid().toString();

                    String list = "";
                    for (int o = 0; o < imglist.size(); o++){
                        list = list + "," + imglist.get(o);
                    }
                    String[] array = list.split(",");

                    for (int o = 0; o < imglist.size(); o++){
                        Uri u;
                        String su;

                        su = imglist.get(o).toString();
                        u = uriList.get(o);

                        StorageReference imgRef = firebaseStorage.getReference(su);
                        UploadTask uploadTask = imgRef.putFile(u);
                        //Log.d("LOGTEST", "array : " + array[o]);
                    }

                    String postId = mStore.collection(FirebaseID.postMarket).document(firstpath).collection
                            (secondpath+"??????").document().getId(); //?????? ??????
                    Map<String, Object> data = new HashMap<>();
                    data.put(FirebaseID.postId,postId.toString());                          //??????id
                    data.put(FirebaseID.userId,mAuth.getCurrentUser().getUid());        //id???
                    data.put(FirebaseID.title,mTitle.getText().toString());                 //??????
                    data.put(FirebaseID.price,mPrice.getText().toString());                 //??????
                    data.put(FirebaseID.contents,mContents.getText().toString());           //??????

                    if(imglist.isEmpty()){                //?????????url
                        imglist.add("null");
                        data.put(FirebaseID.img, imglist);
                    }
                    else{
                        data.put(FirebaseID.img, imglist);
                    }
                    data.put(FirebaseID.timestamp, FieldValue.serverTimestamp());           //??????
                    data.put(FirebaseID.nickname, Mydata.getMynickname());           //??????
                    data.put(FirebaseID.transaction, "false");           //???????????? ??????

                    mStore.collection(FirebaseID.postMarket).document(firstpath).collection(secondpath+"??????").document(postId).set(data, SetOptions.merge());  //?????????
                    Intent intent = new Intent(MarketplacePostActivity2.this, MarketplaceActivity.class);
                    finish();
                    startActivity(intent);

                }
            }
        });
    }

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(!TextUtils.isEmpty(charSequence.toString()) && !charSequence.toString().equals(result)){
                result = decimalFormat.format(Double.parseDouble(charSequence.toString().replaceAll(",","")));
                mPrice.setText(result);
                mPrice.setSelection(result.length());
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }


    };

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

                //-------------------------------

                //StorageReference imgRef = firebaseStorage.getReference("images/community/" + user + filename + ".png");
                //UploadTask uploadTask = imgRef.putFile(imageUri);
                //stringurl = imgRef.getDownloadUrl().toString();
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
                            //StorageReference imgRef = firebaseStorage.getReference("images/community/" + user + filename + ".png");
                            //UploadTask uploadTask = imgRef.putFile(imageUri);
                            //stringurl = imgRef.getDownloadUrl().toString();
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

                    adapter.setOnLongItemClickListener(new MultiImageAdapter.OnLongItemClickListener() {
                        @Override
                        public void onLongItemClick(int pos) {
                            commentdeleteDialog(pos);
                        }
                    });
                }
            }
        }
    }

    void commentdeleteDialog(int pos){
        AlertDialog.Builder builder = new AlertDialog.Builder(MarketplacePostActivity2.this)
                .setTitle("????????? ??????")
                .setMessage("???????????? ?????????????????????????")
                .setPositiveButton("?????????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MarketplacePostActivity2.this, "?????????????????????.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("???", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        uriList.remove(pos);
                        adapter.notifyItemRemoved(pos);
                        imglist.remove(pos);
                        //Log.d("LOGTEST", "imglist2 : " + imglist);
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    void mydata(){
        myschool = Mydata.getMyschool();
    }
}