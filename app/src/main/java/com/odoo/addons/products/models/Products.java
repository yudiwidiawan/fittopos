package com.odoo.addons.products.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.odoo.OdooUtility;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by makan on 02/08/2017.
 */

public class Products extends SQLiteOpenHelper {
    private Integer id;
    private String name;
    private Double priceUnit;
    private String image;
    public static final String DATABASE_NAME = "odoo.db";

    public Products(Context context) {
        super(context, DATABASE_NAME, null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table product " +
                        "(id integer, name text, " +
                        "priceUnit float, image_medium text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS product");
        onCreate(db);
    }

    public boolean insert (Integer id, String name, Double priceUnit, String image)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", id);
        contentValues.put("name", name);
        contentValues.put("priceUnit", priceUnit);
        contentValues.put("image_medium", image);
        db.insert("product", null, contentValues);
        return true;
    }

    public  void addToDb(){
        insert(id, name, priceUnit, image);
    }


    public ArrayList<Product> getAllProducts()
    {
        ArrayList<Product> array_list = new ArrayList<Product>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from product", null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            Product product = new Product();
            product.setNamaProduk(res.getString(res.getColumnIndex("name")));
            product.setHargaProduk(res.getString(res.getColumnIndex("priceUnit")));
            product.setImageProduk(res.getString(res.getColumnIndex("image_medium")));
            array_list.add(product);
            res.moveToNext();
        }
        return array_list;
    }

    public Cursor getById(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from product where id="
                + id + "", null);
        return res;
    }

    public Integer getIdByName(String name){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from product where name='"
                + name + "'", null);
        res.moveToFirst();
        Integer i=0;
        if(res.getCount()  > 0)
            i = res.getInt( res.getColumnIndex("id") );

        return i;
    }

    public void deleteAll(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from product");
    }

    public void setData(Map<String,Object> classObj){
        setId((Integer) classObj.get("id"));
        setName(OdooUtility.getString(classObj, "display_name"));
        setPriceUnit(OdooUtility.getDouble(classObj, "list_price"));
        setImage(OdooUtility.getString(classObj,"image_medium"));
    }

    public void setById(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from product where id=" + id + "", null);
        res.moveToFirst();

        if(res.getCount()>0){
            setPriceUnit( res.getDouble( res.getColumnIndex("priceUnit")));
            setName(res.getString(res.getColumnIndex("name")));
            setId(res.getInt( res.getColumnIndex("id")));
        }
    }

    public void setImage(String image) {
        this.image = image;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPriceUnit() {
        return priceUnit;
    }

    public void setPriceUnit(Double priceUnit) {
        this.priceUnit = priceUnit;
    }

}
