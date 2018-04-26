package com.odoo.addons.products.models;

/**
 * Created by makan on 02/08/2017.
 */

public class Product {
    private String namaProduk;
    private String hargaProduk;
    private String imageProduk;

    public Product() {

    }

    public Product(String namaProduk, String hargaProduk) {
        this.namaProduk = namaProduk;
        this.hargaProduk = hargaProduk;
    }

    public String getNamaProduk() {
        return namaProduk;
    }

    public String getHargaProduk() {
        return hargaProduk;
    }

    public void setNamaProduk(String namaProduk) {
        this.namaProduk = namaProduk;
    }

    public void setHargaProduk(String hargaProduk) {
        this.hargaProduk = hargaProduk;
    }

    public String getImageProduk() {
        return imageProduk;
    }

    public void setImageProduk(String imageProduk) {
        this.imageProduk = imageProduk;
    }
}
