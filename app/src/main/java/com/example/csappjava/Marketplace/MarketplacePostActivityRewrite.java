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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.csappjava.Community.CommunityActivity;
import com.example.csappjava.Community.CommunityActivity2;
import com.example.csappjava.Community.CommunityPostActivity;
import com.example.csappjava.FirebaseID;
import com.example.csappjava.Mydata;
import com.example.csappjava.R;
import com.example.csappjava.adapters.MultiImageAdapter;
import com.example.csappjava.adapters.PostAdapterCommunity;
import com.example.csappjava.adapters.PostAdapterMarketplacesearch;
import com.example.csappjava.models.DateConverter;
import com.example.csappjava.models.PostCommunity;
import com.example.csappjava.models.PostMarketplace;
import com.example.csappjava.models.PostMarketplaceSearch;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarketplacePostActivityRewrite extends AppCompatActivity {

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
    private String postid,userid,title,contents,img,time,firstpath,secondpath,price,transaction,b1,b2,b3;
    private String[] array;

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
    private ImageView backbutton;

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
    private String bookname,bookprice,department,lecture,professor,bookpublisher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketplace_post);

        mTitle = findViewById(R.id.market_post_title_edit);
        mContents = findViewById(R.id.market_post_contents_edit);
        imageView = findViewById(R.id.marketplace_image);
        mPrice = findViewById(R.id.market_post_price);
        mPrice.addTextChangedListener(watcher);
        bname = findViewById(R.id.bookname);
        bpublisher = findViewById(R.id.bookpublisher);
        bprice = findViewById(R.id.bookprice);
        setlesson = findViewById(R.id.lesson);
        setprofessor = findViewById(R.id.lesson2);
        searchposttv = findViewById(R.id.post_searchtv);
        backbutton = findViewById(R.id.market_post_back_button);

        mydata();

        getintent();

        mTitle.setText(title);
        mContents.setText(contents);
        mPrice.setText(price);
        bname.setText(bookname);
        bprice.setText(bookprice);
        bpublisher.setText(bookpublisher);
        searchposttv.setText(department);
        setlesson.setText(lecture);
        setprofessor.setText(professor);



        FirebaseUser user = mAuth.getCurrentUser();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("schooldata");
        mDatabaseRef2 = FirebaseDatabase.getInstance().getReference("lesson");

        dialogsearch = new Dialog(MarketplacePostActivityRewrite.this);       // Dialog ?????????
        dialogsearch.requestWindowFeature(Window.FEATURE_NO_TITLE); // ????????? ??????
        dialogsearch.setContentView(R.layout.activity_marketplace_post_search1);             // xml ???????????? ????????? ??????
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogsearch.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        Window window = dialogsearch.getWindow();
        window.setAttributes(lp);

        dialogsearch2 = new Dialog(MarketplacePostActivityRewrite.this);       // Dialog ?????????
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

                    Map<String, Object> data = new HashMap<>();
                    data.put(FirebaseID.postId,postid);                          //??????id
                    data.put(FirebaseID.userId,mAuth.getCurrentUser().getUid());        //id???
                    data.put(FirebaseID.title,mTitle.getText().toString());                 //??????
                    data.put(FirebaseID.price,mPrice.getText().toString());                 //??????
                    data.put(FirebaseID.contents,mContents.getText().toString());           //??????
                    data.put("????????????_?????????",adapterlecture);
                    data.put("????????????_?????????",adapterprofessor);
                    data.put("????????????_?????????",adapterdepartment);
                    data.put("????????????_??????",adapteryear);
                    data.put("????????????_??????",adaptermonth);

                    if(imglist.isEmpty()){                //?????????url
                        imglist.add("null");
                        data.put(FirebaseID.img, imglist);
                    }
                    else{
                        data.put(FirebaseID.img, imglist);
                    }

                    mStore.collection(FirebaseID.postMarket).document(firstpath).collection(secondpath).document(postid).set(data, SetOptions.merge());  //?????????
                    Intent intent = new Intent(MarketplacePostActivityRewrite.this, MarketplaceActivity.class);
                    finish();
                    startActivity(intent);

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
                //Log.d("LOGTEST", "arrayfor : " + array[o]);
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

        ImageView searchpostbt = (ImageView) findViewById(R.id.searchpostbt) ;
        searchpostbt.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogsearch();
            }
        }) ;

        ImageView searchpostbt2 = (ImageView) findViewById(R.id.searchpostbt2) ;
        searchpostbt2.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!searchposttv.getText().equals("??????")){
                    showDialogsearch2();
                }
                else{
                    Toast.makeText(getApplicationContext(), "???????????? ??????????????????", Toast.LENGTH_LONG).show();
                }
            }
        }) ;

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
        AlertDialog.Builder builder = new AlertDialog.Builder(MarketplacePostActivityRewrite.this)
                .setTitle("????????? ??????")
                .setMessage("???????????? ?????????????????????????")
                .setPositiveButton("?????????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MarketplacePostActivityRewrite.this, "?????????????????????.", Toast.LENGTH_SHORT).show();
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

    public void showDialogsearch(){
        dialogsearch.show(); // ??????????????? ?????????

        // ???????????? ????????????.
        list = new ArrayList<String>();

        searchbt = dialogsearch.findViewById(R.id.list_seach);
        searchtv = dialogsearch.findViewById(R.id.tv_search);


        searchtv.setText("?????? ??????");

        // ???????????? ????????? ?????????(??????)??? ????????????.
        settingList();

        final AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) dialogsearch.findViewById(R.id.autoCompleteTextView);

        // AutoCompleteTextView ??? ???????????? ????????????.
        autoCompleteTextView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,  list ));

        searchbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchet = autoCompleteTextView.getText().toString();
                if(list.contains(searchet)){
                    Toast.makeText(MarketplacePostActivityRewrite.this,"???????????? ????????????.",Toast.LENGTH_SHORT).show();
                    searchposttv.setText(searchet);
                    dialogsearch.dismiss();
                }
                else{
                    Toast.makeText(MarketplacePostActivityRewrite.this,"???????????? ????????????.",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void settingList(){
        mDatabaseRef.child(Mydata.getMyschool()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    //---------------------
                    mDatabaseRef.child(Mydata.getMyschool()).child(snapshot.getKey()).child("????????????").addValueEventListener(new ValueEventListener() {        //????????? ?????? ??????
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshots) {
                            String value = snapshots.getValue(String.class);
                            //Log.d("LOGVALUE", "ValueEventListener : " + snapshot.getKey() + "value" + value);
                            if(value != null){
                                if(value.equals(Mydata.getMycampus())) {               //???????????????
                                    list.add(snapshot.getKey());
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void showDialogsearch2(){
        dialogsearch2.show(); // ??????????????? ?????????


        // ???????????? ????????????.
        list2 = new ArrayList<String>();



        //---------------???????????? ?????? ???????????? ??????
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy");
        String getyear = dateFormat1.format(date);                          //?????? ????????? ????????? ??????
        int numInt = Integer.parseInt(getyear);                             //int??? string??? ???????????? ??????
        //Log.d("LOGTEST", "int num : " + numInt);
        for (int i = numInt; i > 2010; i--) {                               //???????????? 2010???????????? ???????????? ???????????? ??????
            String numStr2 = String.valueOf(i);                             //string??? int??? ???????????? ??????
            spinnerArray.add(numStr2+"???");                                      //???????????? ????????? ????????????
        }
        ArrayAdapter<String> spadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
        spadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sItems1 = (Spinner) dialogsearch2.findViewById(R.id.spinner_year);
        sItems1.setAdapter(spadapter);
        sItems2 = (Spinner) dialogsearch2.findViewById(R.id.spinner_month);


        list_seach2 = dialogsearch2.findViewById(R.id.list_seach2);
        searchtv2 = dialogsearch2.findViewById(R.id.tv_search2);
        addtv2 = dialogsearch2.findViewById(R.id.list_add);


        test1_1 = dialogsearch2.findViewById(R.id.test1_1);
        test2_1 = dialogsearch2.findViewById(R.id.test2_1);
        test1_2 = dialogsearch2.findViewById(R.id.test1_2);
        test2_2 = dialogsearch2.findViewById(R.id.test2_2);
        addlesson = dialogsearch2.findViewById(R.id.addlesson);
        dialog_search_tv = dialogsearch2.findViewById(R.id.dialog_search_tv);

        searchtv2.setText(searchposttv.getText().toString() + " ?????? ??????");

        test1_1.setVisibility(View.GONE);
        test2_1.setVisibility(View.GONE);
        test1_2.setVisibility(View.GONE);
        test2_2.setVisibility(View.GONE);
        addlesson.setVisibility(View.GONE);
        sItems1.setVisibility(View.GONE);
        sItems2.setVisibility(View.GONE);

        radioGroup = dialogsearch2.findViewById(R.id.radioGroup);
        radioGroup.check(R.id.rg_btn1);
        settingList2("??????");

        list_seach2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchdata(dialog_search_tv.getText().toString());
            }
        });

        addtv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(test1_1.getVisibility() == View.GONE){
                    test1_1.setVisibility(View.VISIBLE);
                    test2_1.setVisibility(View.VISIBLE);
                    test1_2.setVisibility(View.VISIBLE);
                    test2_2.setVisibility(View.VISIBLE);
                    addlesson.setVisibility(View.VISIBLE);
                    sItems1.setVisibility(View.VISIBLE);
                    sItems2.setVisibility(View.VISIBLE);
                }
                else{
                    test1_2.setText(null);
                    test2_2.setText(null);
                    test1_1.setVisibility(View.GONE);
                    test2_1.setVisibility(View.GONE);
                    test1_2.setVisibility(View.GONE);
                    test2_2.setVisibility(View.GONE);
                    addlesson.setVisibility(View.GONE);
                    sItems1.setVisibility(View.GONE);
                    sItems2.setVisibility(View.GONE);
                }

            }
        });

        addlesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() != null) {
                    adddata(sItems1.getSelectedItem().toString(),sItems2.getSelectedItem().toString());
                }
            }
        });
    }


    private void settingList2(String name){
        mDatabaseRef2.child(Mydata.getMyschool()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    mDatabaseRef2.child(Mydata.getMyschool()).child(snapshot.getKey()).child("????????????").addValueEventListener(new ValueEventListener() {        //????????? ?????? ??????
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshots) {
                            String value = snapshots.getValue(String.class);
                            //Log.d("LOGVALUE", "ValueEventListener : " + snapshot.getKey() + "value" + value);
                            if(value != null){
                                if(value.equals(Mydata.getMycampus())) {
                                    Log.d("LOGVALUE", "name" + name);
                                    mDatabaseRef2.child(Mydata.getMyschool()).child(snapshot.getKey()).child(name).addValueEventListener(new ValueEventListener() {

                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot sshot) {
                                            String value2 = sshot.getValue(String.class);
                                            list2.add(value2);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void addDialog(String sItems11,String sItems22){

        AlertDialog.Builder builder = new AlertDialog.Builder(MarketplacePostActivityRewrite.this)
                .setTitle("??????")
                .setMessage("????????? : " + test1_2.getText().toString() + "\n" + "????????? : " + test2_2.getText().toString() + "\n??? ?????????????")
                .setPositiveButton("?????????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton("???", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("LOGTEST", "yes");
                        String postId = mStore.collection("Lecture").document(Mydata.getMyschool()).collection(searchposttv.getText().toString()).document().getId();
                        Map<String, Object> data = new HashMap<>();                             // Lecture - ????????? ?????? - ????????? ????????? ?????? ???????????? ??????
                        data.put(FirebaseID.postId, postId.toString());                          //??????id
                        data.put(FirebaseID.userId, mAuth.getCurrentUser().getUid());            //???????????? ????????? id
                        data.put("?????????", test1_2.getText().toString());                 //??????
                        data.put("?????????", test2_2.getText().toString());                 //??????
                        data.put("????????????", Mydata.getMycampus());                       //??????
                        data.put("?????????", searchposttv.getText().toString());            //??????
                        data.put("??????", sItems11);           //??????
                        data.put("??????", sItems22);           //??????
                        data.put(FirebaseID.timestamp, FieldValue.serverTimestamp());           //??????

                        mStore.collection("Lecture").document(Mydata.getMyschool()).collection(searchposttv.getText().toString()).document(postId).set(data, SetOptions.merge());

                        Toast.makeText(getApplicationContext(),"???????????? ?????????????????????.",Toast.LENGTH_SHORT).show();                   //??????????????? ??????
                        dialogsearch2.dismiss();


                        test1_2.setText(null);
                        test2_2.setText(null);
                        test1_1.setVisibility(View.GONE);
                        test2_1.setVisibility(View.GONE);
                        test1_2.setVisibility(View.GONE);
                        test2_2.setVisibility(View.GONE);
                        addlesson.setVisibility(View.GONE);
                        sItems1.setVisibility(View.GONE);
                        sItems2.setVisibility(View.GONE);
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    String tf;
    void adddata(String sItems11,String sItems22){
        tf = "";
        Log.d("LOGTEST", Mydata.getMyschool());

        mStore.collection("Lecture").document(Mydata.getMyschool()).collection(searchposttv.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> shot = document.getData();
                                String lecture = String.valueOf((shot.get("?????????")));
                                String professor = String.valueOf((shot.get("?????????")));
                                String campus = String.valueOf((shot.get("????????????")));
                                String department = String.valueOf((shot.get("?????????")));
                                String year = String.valueOf((shot.get("??????")));
                                String month = String.valueOf((shot.get("??????")));
                                //Log.d("LOGTEST", "tf " + tf);
                                if(!tf.equals("t")){
                                    if(lecture.equals(test1_2.getText().toString()) && professor.equals(test2_2.getText().toString())                       //???????????? ???????????? ????????? ??????
                                            && campus.equals(Mydata.getMycampus()) && department.equals(searchposttv.getText().toString())
                                            && year.equals(sItems1) && month.equals(sItems2)) {
                                        tf = "t";
                                    }
                                    else{
                                        tf = "f";
                                    }
                                }
                            }
                            if(!tf.equals("t")){
                                addDialog(sItems11,sItems22);
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"?????? ?????????????????? ???????????????.",Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    void searchdata(String sc){
        RadioButton rdoButton = dialogsearch2.findViewById( radioGroup.getCheckedRadioButtonId() );
        String strPgmId = rdoButton.getText().toString().toUpperCase();
        Log.d("LOGTEST", "???????????????" + strPgmId);
        Log.d("LOGTEST", searchposttv.getText().toString());
        Log.d("LOGTEST", Mydata.getMyschool());
        mDatas = new ArrayList<>();

        mStore.collection("Lecture").document(myschool).collection(searchposttv.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(!mDatas.isEmpty()){
                                mDatas.clear();
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> shot = document.getData();
                                String lecture = String.valueOf((shot.get("?????????")));
                                String professor = String.valueOf((shot.get("?????????")));
                                String campus = String.valueOf((shot.get("????????????")));
                                String department = String.valueOf((shot.get("?????????")));
                                String year = String.valueOf((shot.get("??????")));
                                String month = String.valueOf((shot.get("??????")));
                                if(strPgmId.equals("??????????????? ??????")){
                                    if(lecture.contains(sc)){
                                        PostMarketplaceSearch data = new PostMarketplaceSearch(lecture, professor, campus, department, year, month);
                                        mDatas.add(data);
                                    }
                                }
                                else if(strPgmId.equals("??????????????? ??????")) {
                                    if (professor.contains(sc)) {
                                        PostMarketplaceSearch data = new PostMarketplaceSearch(lecture, professor, campus, department, year, month);
                                        mDatas.add(data);
                                    }
                                }
                            }

                            mAdapter = new PostAdapterMarketplacesearch(mDatas);

                            mAdapter.setOnItemClickListener(new PostAdapterMarketplacesearch.OnItemClickListener() {         //?????????????????? ????????? ????????? ????????? ????????????2??????????????? ???????????? ???????????? ???????????? ?????????
                                @Override
                                public void onItemClick(int pos) {
                                    //Toast.makeText(getApplicationContext(), "onItemClick position : " + pos, Toast.LENGTH_SHORT).show();

                                    PostMarketplaceSearch hm = mDatas.get(pos);
                                    setlesson.setText(hm.getLecture());
                                    adapterlecture = hm.getLecture();
                                    setprofessor.setText(hm.getProfessor());
                                    adapterprofessor = hm.getProfessor();
                                    adapterdepartment = hm.getDepartment();
                                    adapteryear = hm.getYear();
                                    adaptermonth = hm.getMonth();
                                    Log.d("LOGTEST", setlesson.toString());

                                    dialogsearch2.dismiss();
                                    //Toast.makeText(getApplicationContext(), hm.getContents() + "," + hm.getTitle(), Toast.LENGTH_SHORT).show();
                                }
                            });





                            mPostRecyclerView.setAdapter(mAdapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }

                });
    }

    void mydata(){
        myschool = Mydata.getMyschool();
    }

    void getintent(){
        Intent intent = getIntent();
        postid = intent.getStringExtra("postid");
        userid = intent.getStringExtra("userid");
        title = intent.getStringExtra("title");
        contents = intent.getStringExtra("contents");
        img = intent.getStringExtra("img");
        time = intent.getStringExtra("time");
        price = intent.getStringExtra("price");
        transaction = intent.getStringExtra("transaction");
        firstpath = Mydata.getFirstpath();
        secondpath = Mydata.getSecondpath();
        bookname = intent.getStringExtra("bookname");
        bookprice = intent.getStringExtra("bookprice");
        department = intent.getStringExtra("department");
        adapterdepartment = intent.getStringExtra("department");
        lecture = intent.getStringExtra("lecture");
        adapterlecture = intent.getStringExtra("lecture");
        professor = intent.getStringExtra("professor");
        adapterprofessor = intent.getStringExtra("professor");
        bookpublisher = intent.getStringExtra("bookpublisher");
        adapteryear = intent.getStringExtra("adapteryear");
        adaptermonth = intent.getStringExtra("adaptermonth");

        img = img.replace("[","");
        img = img.replace("]","");
        img = img.replaceAll(" ","");
        array = img.split(",");
    }
}