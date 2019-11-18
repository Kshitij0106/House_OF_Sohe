package com.house_of_sohe.Model;

public class Products {
    private String prodImg, prodName, prodPrice, prodDesc, prodQty, prodSize, prodCode, prodDate, OID, prodInfo;

    public Products() {
    }

    public Products(String prodImg, String prodName, String prodPrice, String prodDesc, String prodQty, String prodSize, String prodCode, String dateOfOrder, String OID, String prodInfo){
        this.prodImg = prodImg;
        this.prodName = prodName;
        this.prodPrice = prodPrice;
        this.prodDesc = prodDesc;
        this.prodQty = prodQty;
        this.prodSize = prodSize;
        this.prodCode = prodCode;
        this.prodDate = dateOfOrder;
        this.OID = OID;
        this.prodInfo = prodInfo;
    }

    public Products(String prodImg, String prodName, String prodPrice, String prodQty, String prodSize, String prodDate, String OID) { // orders
        this.prodImg = prodImg;
        this.prodName = prodName;
        this.prodPrice = prodPrice;
        this.prodQty = prodQty;
        this.prodSize = prodSize;
        this.prodDate = prodDate;
        this.OID = OID;
    }

    public String getProdImg() {
        return prodImg;
    }

    public void setProdImg(String prodImg) {
        this.prodImg = prodImg;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getProdPrice() {
        return prodPrice;
    }

    public void setProdPrice(String prodPrice) {
        this.prodPrice = prodPrice;
    }

    public String getProdDesc() {
        return prodDesc;
    }

    public void setProdDesc(String prodDesc) {
        this.prodDesc = prodDesc;
    }

    public String getProdQty() {
        return prodQty;
    }

    public void setProdQty(String prodQty) {
        this.prodQty = prodQty;
    }

    public String getProdSize() {
        return prodSize;
    }

    public void setProdSize(String prodSize) {
        this.prodSize = prodSize;
    }

    public String getProdCode() {
        return prodCode;
    }

    public void setProdCode(String prodCode) {
        this.prodCode = prodCode;
    }

    public String getProdDate() {
        return prodDate;
    }

    public void setProdDate(String prodDate) {
        this.prodDate = prodDate;
    }

    public String getOID() {
        return OID;
    }

    public void setOID(String OID) {
        this.OID = OID;
    }

    public String getProdInfo() {
        return prodInfo;
    }

    public void setProdInfo(String prodInfo) {
        this.prodInfo = prodInfo;
    }
}