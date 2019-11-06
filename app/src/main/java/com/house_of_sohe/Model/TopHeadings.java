package com.house_of_sohe.Model;

import java.util.ArrayList;

public class TopHeadings {

    private String title;
    private ArrayList<Products> products;

    public TopHeadings() {
    }

    public TopHeadings(String title, ArrayList<Products> products) {
        this.title = title;
        this.products = products;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Products> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Products> products) {
        this.products = products;
    }

}