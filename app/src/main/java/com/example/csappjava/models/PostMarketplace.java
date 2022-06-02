package com.example.csappjava.models;

import java.util.Date;

public class PostMarketplace {

    private String postId;      //게시물id
    private String userId; //게시물 올린사람
    private String title; //제목
    private String contents;    //내용
    private String img; //이미지
    private String time; //시간
    private String price;   //가격
    private String nick;
    private Date date;
    private String transaction; //거래 완료 유무


    public PostMarketplace() {
    }

    public PostMarketplace(String postId, String userId, String title, String contents, String img, String price, String nick, String time, String transaction) {
        this.postId = postId;
        this.userId = userId;
        this.title = title;
        this.contents = contents;
        this.img = img;
        this.time = time;
        this.price = price;
        this.date = date;
        this.nick = nick;
        this.transaction = transaction;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    @Override
    public String toString() {
        return "PostMarketplace{" +
                "postId='" + postId + '\'' +
                ", userId='" + userId + '\'' +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", img='" + img + '\'' +
                ", time='" + time + '\'' +
                ", price='" + price + '\'' +
                ", price='" + nick + '\'' +
                ", transaction='" + transaction + '\'' +
                ", date=" + date +
                '}';
    }
}

