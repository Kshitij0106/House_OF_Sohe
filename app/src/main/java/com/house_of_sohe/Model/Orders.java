package com.house_of_sohe.Model;

import java.util.List;

public class Orders {

    private String OrdersID, userEmail;
    private List<Products> productsList;

    public Orders() {
    }

    public Orders(String userEmail, List<Products> productsList) {
        this.userEmail = userEmail;
        this.productsList = productsList;
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
}
