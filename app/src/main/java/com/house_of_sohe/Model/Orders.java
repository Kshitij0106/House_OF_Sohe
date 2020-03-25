package com.house_of_sohe.Model;

import java.util.List;

public class Orders {

    private String OrdersID, userEmail,totalPrice;
    private List<Products> productsList;

    public Orders() {
    }

    public Orders(String userEmail, List<Products> productsList,String totalPrice) {
        this.userEmail = userEmail;
        this.productsList = productsList;
        this.totalPrice = totalPrice;
    }

    public String getOrdersID() {
        return OrdersID;
    }

    public void setOrdersID(String ordersID) {
        OrdersID = ordersID;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public List<Products> getProductsList() {
        return productsList;
    }

    public void setProductsList(List<Products> productsList) {
        this.productsList = productsList;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }
}
