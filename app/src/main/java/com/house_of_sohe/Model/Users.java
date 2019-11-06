package com.house_of_sohe.Model;

public class Users {

    String userName,userPhone,userEmail,uid;

    public Users() {
    }

    public Users(String userName, String userPhone, String userEmail, String uid) {
        this.userName = userName;
        this.userPhone = userPhone;
        this.userEmail = userEmail;
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}