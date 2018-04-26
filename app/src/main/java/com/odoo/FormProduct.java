package com.odoo;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.odoo.core.support.OUser;

public class FormProduct extends AppCompatActivity {

    private EditText productName;
    private EditText productPrice;
    private Button btnAdd;

    private String uid, username, password, database, url;
    private OUser user;
    private OdooUtility odoo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_product);
        getSupportActionBar().setBackgroundDrawable(getApplicationContext()
                .getResources().getDrawable(R.color.theme_primary));

        productName = (EditText) findViewById(R.id.edtNamaProduk);
        productPrice = (EditText) findViewById(R.id.edtHargaProduk);
        btnAdd = (Button) findViewById(R.id.btnAddProduct);

        user = OUser.current(this);
        odoo = new OdooUtility(user.getHost(), "object");




    }
}
