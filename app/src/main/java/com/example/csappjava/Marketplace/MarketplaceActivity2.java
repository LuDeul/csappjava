package com.example.csappjava.Marketplace;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.csappjava.Chatting.ChatActivity;
import com.example.csappjava.Chatting.ChatActivity_2;
import com.example.csappjava.Community.CommunityActivity2;
import com.example.csappjava.Community.CommunityPostActivityRewrite;
import com.example.csappjava.FirebaseID;
import com.example.csappjava.Mydata;
import com.example.csappjava.ProgressDialog;
import com.example.csappjava.R;
import com.example.csappjava.adapters.ImageSliderAdapter;
import com.example.csappjava.adapters.MultiImageAdapter2;
import com.example.csappjava.models.PostMarketplace;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

import uk.co.senab.photoview.PhotoViewAttacher;

public class MarketplaceActivity2 extends AppCompatActivity {

    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();
    private Button btn_chatstart, postend;
    private String postid,userid,title,contents,img,time,firstpath,secondpath,price,transaction;
    private String lecture,professor,department,adapteryear,adaptermonth,bookname,bookpublisher,bookimg,author,booklink,bookprice;
    private TextView place2_department,place2_lecture,place2_professor,place2_year,place2_month,place2_bookname,place2_bookprice,place2_bookpublisher,place2_bookauthor;
    private ImageView place2_bookimg;
    private Button linkbutton;

    private ViewPager2 sliderViewPager;
    private String[] array;
    private LinearLayout layoutIndicator;
    private ImageSliderAdapter adapter; // ????????????????????? ???????????? ?????????
    TextView Tprice3, Tprice2, Tprice;


    ProgressDialog progressDialog;
    Dialog dialogreport, dialogimg;    //??????
    String reportitem;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketplace2);

        Toolbar toolbar = findViewById (R.id.commenttoolbar);
        setSupportActionBar (toolbar); //??????????????? ??????(App Bar)??? ??????
        ActionBar actionBar = getSupportActionBar (); //?????? ????????? ?????? ?????? ?????????
        actionBar.setDisplayHomeAsUpEnabled (true);
        actionBar.setTitle("????????????");

        place2_department = findViewById(R.id.place2_department);
        place2_lecture = findViewById(R.id.place2_lecture);
        place2_professor = findViewById(R.id.place2_professor);
        place2_year = findViewById(R.id.place2_year);
        place2_month = findViewById(R.id.place2_month);
        place2_bookname = findViewById(R.id.place2_bookname);
        place2_bookprice = findViewById(R.id.place2_bookprice);
        place2_bookpublisher = findViewById(R.id.place2_bookpublisher);
        place2_bookauthor = findViewById(R.id.place2_bookauthor);
        place2_bookimg = findViewById(R.id.place2_bookimg);
        linkbutton = findViewById(R.id.linkbutton);
        Mydata.setCount(1);
        getintent();

        TextView Ttitle = findViewById(R.id.market_title2);
        Ttitle.setText(title);

        TextView Tcontents = findViewById(R.id.market_content2);
        Tcontents.setText(contents);

        Tprice3 = findViewById(R.id.market_price3);
        Tprice2 = findViewById(R.id.market_price2);
        Tprice = findViewById(R.id.market_price);
        Tprice2.setPaintFlags(Ttitle.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        discount();

        place2_department.setText(department);
        place2_lecture.setText(lecture);
        place2_professor.setText(professor);
        place2_year.setText(adapteryear);
        place2_month.setText(adaptermonth);
        place2_bookname.setText(bookname);
        place2_bookprice.setText(bookprice);
        place2_bookpublisher.setText(bookpublisher);
        place2_bookauthor.setText(author);
        Glide.with(getApplicationContext())
                .load(bookimg)
                .into(place2_bookimg);
        linkbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(booklink));
                startActivity(myIntent);
            }
        });

        if(place2_lecture.getText().equals("null")&&place2_department.getText().equals("null")){
            TextView ll = findViewById(R.id.ll);
            LinearLayout ll2 = findViewById(R.id.ll2);
            ll.setVisibility(View.GONE);
            ll2.setVisibility(View.GONE);
        }

        btn_chatstart = findViewById(R.id.btn_chatstart);
        sliderViewPager = findViewById(R.id.sliderViewPager);
        layoutIndicator = findViewById(R.id.layoutIndicators);
        progressDialog = new ProgressDialog(this);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        if(transaction.equals("true")){
            Ttitle.setPaintFlags(Ttitle.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
            Tcontents.setPaintFlags(Tcontents.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
            Tprice.setPaintFlags(Tprice.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
            btn_chatstart.setText("????????? ????????? ??????????????????.");
        }

        FirebaseStorage storage = FirebaseStorage.getInstance("gs://csapp-a3fce.appspot.com/");             //?????????????????? ???????????? ????????????
        StorageReference storageRef = storage.getReference();

        if(!transaction.equals("true")){
            btn_chatstart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MarketplaceActivity2.this, ChatActivity_2.class);
                    intent.putExtra("userid",userid);
                    startActivity(intent);
                }
            });
        }
        else{
            Toast.makeText(getApplicationContext(), "????????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
        }

        postend = findViewById(R.id.btn_postend);
        postend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postendDialog();
            }
        });

        if(mAuth.getCurrentUser().getUid().equals(userid)){             //????????? ??????????????? ????????????
            btn_chatstart.setVisibility(View.GONE);
            postend.setVisibility(View.VISIBLE);
        }
        else{
            postend.setVisibility(View.GONE);
            btn_chatstart.setVisibility(View.VISIBLE);
        }

        dialogreport = new Dialog(MarketplaceActivity2.this);       // Dialog ?????????
        dialogreport.requestWindowFeature(Window.FEATURE_NO_TITLE); // ????????? ??????
        dialogreport.setContentView(R.layout.dialog_report);             // xml ???????????? ????????? ??????

        dialogimg = new Dialog(MarketplaceActivity2.this);       // Dialog ?????????
        dialogimg.requestWindowFeature(Window.FEATURE_NO_TITLE); // ????????? ??????
        dialogimg.setContentView(R.layout.dialog_photo);             // xml ???????????? ????????? ??????
        imgdialog = dialogimg.findViewById(R.id.imagedialog);
        dialogimg.setCancelable(false);

        if(!array[0].equals("null")) {
            sliderViewPager.setOffscreenPageLimit(1);
            adapter = new ImageSliderAdapter(this, array);
            sliderViewPager.setAdapter(adapter);

            sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    setCurrentIndicator(position);
                }
            });

            setupIndicators(array.length);

            adapter.setOnItemClickListener(new ImageSliderAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int pos) {
                    showdialogimg(pos);
                }
            });
            Loadingstart();
        }
        else{
            View v = findViewById(R.id.viewFadingEdge);
            sliderViewPager.setVisibility(View.GONE);
            v.setVisibility(View.GONE);
        }
    }

    private void setupIndicators(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.setMargins(16, 8, 16, 8);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(this);
            indicators[i].setImageDrawable(ContextCompat.getDrawable(this,
                    R.drawable.bg_indicator_inactive));
            indicators[i].setLayoutParams(params);
            layoutIndicator.addView(indicators[i]);
        }
        setCurrentIndicator(0);
    }

    private void setCurrentIndicator(int position) {
        int childCount = layoutIndicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutIndicator.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        this,
                        R.drawable.bg_indicator_active
                ));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        this,
                        R.drawable.bg_indicator_inactive
                ));
            }
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
                overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                return true;
            case R.id.option_delete:
                //Toast.makeText(getApplicationContext(), "??????", Toast.LENGTH_SHORT).show();
                deleteDialog();
                return true;
            case R.id.option_report:
                showDialogreport();
                //Toast.makeText(getApplicationContext(), "??????", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.option_rewrite:
                Toast.makeText(getApplicationContext(), "??????", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MarketplaceActivity2.this, MarketplacePostActivityRewrite.class);
                intent.putExtra("postid", postid);
                intent.putExtra("userid", userid);
                intent.putExtra("title", title);
                intent.putExtra("contents", contents);
                intent.putExtra("img", img);
                intent.putExtra("time", time);
                intent.putExtra("price", price);
                intent.putExtra("bookname", bookname);
                intent.putExtra("bookprice",bookprice);
                intent.putExtra("department",department);
                intent.putExtra("lecture",lecture);
                intent.putExtra("professor",professor);
                intent.putExtra("bookpublisher",bookpublisher);
                intent.putExtra("adapteryear",adapteryear);
                intent.putExtra("adaptermonth",adaptermonth);

                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected (item);
        }
    }

    void deleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MarketplaceActivity2.this)
                .setTitle("??????")
                .setMessage("?????????????????????????")
                .setPositiveButton("?????????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MarketplaceActivity2.this, "?????????????????????.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("???", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(userid.equals(mAuth.getCurrentUser().getUid())){
                            mStore.collection(FirebaseID.postMarket).document(firstpath).collection(secondpath).document(postid)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(MarketplaceActivity2.this, "?????????????????????.", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(MarketplaceActivity2.this, "?????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                        else{
                            Toast.makeText(MarketplaceActivity2.this, "??? ???????????? ????????????.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void showDialogreport(){
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
                // ????????? ?????? ??????
                dialogreport.dismiss(); // ??????????????? ??????
                Toast.makeText(MarketplaceActivity2.this, " ???????????? ", Toast.LENGTH_SHORT).show();
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

                data.put("3_1_??????1","marketplace");
                data.put("3_2_??????2",firstpath);
                data.put("3_3_??????3",secondpath);
                data.put(FirebaseID.timestamp, FieldValue.serverTimestamp());    //??????

                //mStore.collection("report").document(firstpath).collection(secondpath).document(postId).set(data, SetOptions.merge());  //?????????
                mStore.collection("report").document(postId).set(data, SetOptions.merge());  //?????????
                Toast.makeText(MarketplaceActivity2.this, " ???????????? ", Toast.LENGTH_SHORT).show();
                dialogreport.dismiss(); // ??????????????? ??????
                // ????????? ?????? ??????
                //finish();           // ??? ??????
            }
        });
    }

    void postendDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MarketplaceActivity2.this)
                .setTitle("?????? ??????")
                .setMessage("?????? ??????")
                .setPositiveButton("?????????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MarketplaceActivity2.this, "?????????????????????.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("???", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(userid.equals(mAuth.getCurrentUser().getUid())){
                            Map<String, Object> data = new HashMap<>();
                            data.put("transaction","true");
                            mStore.collection(FirebaseID.postMarket).document(firstpath).collection(secondpath).document(postid).set(data, SetOptions.merge());  //?????????
                            Toast.makeText(MarketplaceActivity2.this, "??????.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(MarketplaceActivity2.this, "??? ???????????? ????????????.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private ImageView imgdialog;

    private PhotoViewAttacher mAttacher;    //????????? ???????????? ??????

    void showdialogimg(int pos){
        dialogimg.show(); // ??????????????? ?????????
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://csapp-a3fce.appspot.com/");             //?????????????????? ???????????? ????????????
        StorageReference storageRef = storage.getReference();
        mAttacher = new PhotoViewAttacher(imgdialog);

        dialogimg.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogimg.dismiss();
            }
        });

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

    }

    private void Loadingstart(){
        Handler handler=new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){
                progressDialog.show();
                if(Mydata.getCount() >= array.length-1){
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
        price = intent.getStringExtra("price");
        transaction = intent.getStringExtra("transaction");
        lecture = intent.getStringExtra("lecture");
        professor = intent.getStringExtra("professor");
        department = intent.getStringExtra("department");
        adapteryear = intent.getStringExtra("adapteryear");
        adaptermonth = intent.getStringExtra("adaptermonth");
        bookname = intent.getStringExtra("bookname");
        bookpublisher = intent.getStringExtra("bookpublisher");
        bookimg = intent.getStringExtra("bookimg");
        author = intent.getStringExtra("author");
        booklink = intent.getStringExtra("booklink");
        bookprice = intent.getStringExtra("bookprice");

        firstpath = Mydata.getFirstpath();
        secondpath = Mydata.getSecondpath();

        img = img.replace("[","");
        img = img.replace("]","");
        img = img.replaceAll(" ","");
        array = img.split(",");

    }

    void discount() {
        String numString1 = bookprice.replace(",","");
        String numString2 = price.replace(",","");

        int numInt1 = Integer.parseInt(numString1);
        int numInt2 = Integer.parseInt(numString2);

        double disacountint =  Math.floor(100-(numInt2*100/numInt1));


        Tprice3.setText(Math.round(disacountint) + "% ??????");
        Tprice2.setText(bookprice + "???");
        Tprice.setText(price + "???");


    }


}