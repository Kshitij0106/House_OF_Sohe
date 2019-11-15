package com.house_of_sohe.Model;

public class Users {

    String userName,userPhone,userEmail,uid, userHouseNo, userColony, userPinCode, userCity, userLandmark;

    public Users() {
    }

    public Users(String userName, String userPhone, String userEmail,
                 String uid,String userHouseNo, String userColony, String userPinCode, String userCity,
                 String userLandmark) {
        this.userName = userName;
        this.userPhone = userPhone;
        this.userEmail = userEmail;
        this.uid = uid;
        this.userHouseNo = userHouseNo;
        this.userColony = userColony;
        this.userPinCode = userPinCode;
        this.userCity = userCity;
        this.userLandmark = userLandmark;
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

    public String getUserHouseNo() {
        return userHouseNo;
    }

    public void setUserHouseNo(String userHouseNo) {
        this.userHouseNo = userHouseNo;
    }

    public String getUserColony() {
        return userColony;
    }

    public void setUserColony(String userColony) {
        this.userColony = userColony;
    }

    public String getUserPinCode() {
        return userPinCode;
    }

    public void setUserPinCode(String userPinCode) {
        this.userPinCode = userPinCode;
    }

    public String getUserCity() {
        return userCity;
    }

    public void setUserCity(String userCity) {
        this.userCity = userCity;
    }

    public String getUserLandmark() {
        return userLandmark;
    }

    public void setUserLandmark(String userLandmark) {
        this.userLandmark = userLandmark;
    }

}