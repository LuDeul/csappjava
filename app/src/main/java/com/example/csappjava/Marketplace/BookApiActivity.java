package com.example.csappjava.Marketplace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.csappjava.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class BookApiActivity extends AppCompatActivity {

    EditText et1;
    TextView tv1,tv2,tv3;
    Button bt1;
    String result;
    public String b1,b2,b3,b4,b5,b6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookapi);

        Intent intent = getIntent();
        result = intent.getStringExtra("result");

        tv1 = findViewById(R.id.tv1);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String keyword = result;
                    String str = getNaverSearch(keyword);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            TextView searchResult2 = (TextView) findViewById(R.id.tv1);
                            searchResult2.setText(str);

                            Intent intent = new Intent(BookApiActivity.this, MarketplacePostActivity.class);
                            intent.putExtra("b1", b1);
                            intent.putExtra("b2", b2);
                            intent.putExtra("b3", b3);
                            intent.putExtra("b4", b4);
                            intent.putExtra("b5", b5);
                            intent.putExtra("b6", b6);
                            startActivity(intent);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });thread.start();
    }

    public String getNaverSearch(String keyword) {

        String clientID = "c3ayYRBzgCA6JrnacZoA";
        String clientSecret = "nSjcZhnIfz";
        StringBuffer sb = new StringBuffer();
        String booktitle, bookprice, bookcontent,publisher, image, author, link;

        try {
            String text = URLEncoder.encode(keyword, "UTF-8");

            String apiURL = "https://openapi.naver.com/v1/search/book.xml?query=" + text + "&display=10" + "&start=1";

            URL url = new URL(apiURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-Naver-Client-Id", clientID);
            conn.setRequestProperty("X-Naver-Client-Secret", clientSecret);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            String tag;
            //inputStream???????????? xml??? ??????
            xpp.setInput(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            xpp.next();
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tag = xpp.getName(); //?????? ?????? ????????????

                        if (tag.equals("item")) ; //????????? ?????? ??????
                        else if (tag.equals("title")) {

                            xpp.next();
                            booktitle = xpp.getText().replaceAll("<(/)?([a-zA-Z]*)(\\\\s[a-zA-Z]*=[^>]*)?(\\\\s)*(/)?>", "");
                            bname(booktitle);
                            Log.d("LOGBOOKTEST1",  "?????? : " + booktitle);

                        } else if (tag.equals("description")) {
                            xpp.next();
                            bookcontent = xpp.getText().replaceAll("<(/)?([a-zA-Z]*)(\\\\s[a-zA-Z]*=[^>]*)?(\\\\s)*(/)?>", "");
                            Log.d("LOGBOOKTEST2",  "?????? : " + bookcontent);
                        }
                        else if (tag.equals("price")) {
                            xpp.next();
                            bookprice = xpp.getText().replaceAll("<(/)?([a-zA-Z]*)(\\\\s[a-zA-Z]*=[^>]*)?(\\\\s)*(/)?>", "");
                            bprice(bookprice);
                            Log.d("LOGBOOKTEST3",  "?????? : " + bookprice);
                        }
                        else if (tag.equals("publisher")) {
                            xpp.next();
                            publisher = xpp.getText().replaceAll("<(/)?([a-zA-Z]*)(\\\\s[a-zA-Z]*=[^>]*)?(\\\\s)*(/)?>", "");
                            bpublisher(publisher);
                            Log.d("LOGBOOKTEST4",  "??????????????? : " + publisher);
                        }
                        else if (tag.equals("image")) {
                            xpp.next();
                            image = xpp.getText().replaceAll("<(/)?([a-zA-Z]*)(\\\\s[a-zA-Z]*=[^>]*)?(\\\\s)*(/)?>", "");
                            bimg(image);
                        }
                        else if (tag.equals("author")) {
                            xpp.next();
                            author = xpp.getText().replaceAll("<(/)?([a-zA-Z]*)(\\\\s[a-zA-Z]*=[^>]*)?(\\\\s)*(/)?>", "");
                            bauthor(author);
                        }
                        else if (tag.equals("link")) {
                            xpp.next();
                            link = xpp.getText().replaceAll("<(/)?([a-zA-Z]*)(\\\\s[a-zA-Z]*=[^>]*)?(\\\\s)*(/)?>", "");
                            blink(link);
                        }
                        break;
                }
                eventType = xpp.next();
            }
        } catch (Exception e) {
            return e.toString();
        }
        return sb.toString();
    }

    void bprice(String p){
        b1 = p;
    }
    void bname(String n){
        b2 = n;
    }
    void bpublisher(String p){
        b3 = p;
    }
    void bimg(String i){
        b4 = i;
    }
    void bauthor(String a){
        b5 = a;
    }
    void blink(String l){
        b6 = l;
    }
}