/**
 * Odoo, Open Source Management Solution
 * Copyright (C) 2012-today Odoo SA (<http:www.odoo.com>)
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http:www.gnu.org/licenses/>
 * <p>
 * Created on 9/1/15 11:54 AM
 */
package com.odoo.core.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.odoo.MenuAwalActivity;
import com.odoo.OdooActivity;
import com.odoo.R;
import com.odoo.core.auth.OdooAccountManager;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.support.OUser;
import com.odoo.core.utils.BitmapUtils;
import com.odoo.core.utils.OAppBarUtils;
import com.odoo.core.utils.OResource;
import com.odoo.core.utils.OStringColorUtil;

import odoo.controls.OForm;

public class Profile extends AppCompatActivity implements View.OnClickListener{
    public static final String TAG = Profile.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_profile);
        OAppBarUtils.setAppBar(this, true);
        OUser user = OUser.current(this);
        OForm form = (OForm) findViewById(R.id.profileDetails);
        int color = OStringColorUtil.getStringColor(this, user.getName());
        form.setIconTintColor(color);
        ODataRow userData = new ODataRow();
        userData.put("name", user.getName());
        userData.put("user_login", user.getUsername());
        userData.put("server_url", user.getHost());
        userData.put("database", user.getDatabase());
        userData.put("version", user.getOdooVersion().getServerSerie());
        userData.put("timezone", user.getTimezone());
        form.initForm(userData);

        CollapsingToolbarLayout collapsing_toolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsing_toolbar.setTitle(userData.getString("name"));
        setTitle(userData.getString("name"));
        Bitmap avatar;
        if (user.getAvatar().equals("false")) {
            avatar = BitmapUtils.getAlphabetImage(this, user.getName());
        } else {
            avatar = BitmapUtils.getBitmapImage(this, user.getAvatar());
        }
        ImageView imageView = (ImageView) findViewById(R.id.image);
        imageView.setImageBitmap(avatar);
        findViewById(R.id.btnProfileLogout).setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(this, MenuAwalActivity.class));
                finish();
                break;
            case R.id.logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.title_confirm);
                builder.setMessage(R.string.toast_are_you_sure_logout);
                builder.setPositiveButton(R.string.label_logout, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AccountManager accountManager = (AccountManager) getApplicationContext().getSystemService(ACCOUNT_SERVICE);
                        // loop through all accounts to remove them
                        Account[] accounts = accountManager.getAccounts();
                        for (int index = 0; index < accounts.length; index++) {
                            if (accounts[index].type.intern() == "com.odoo.auth") {
                                accountManager.removeAccount(accounts[index], null, null);
                                Intent intent = new Intent(getApplicationContext(), OdooLogin.class);
                                startActivity(intent);
                                finish();
                                break;
                            }
                        }
                    }
                });
                builder.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnProfileLogout:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.title_confirm);
                builder.setMessage(R.string.toast_are_you_sure_logout);
                builder.setPositiveButton(R.string.label_logout, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AccountManager accountManager = (AccountManager) getApplicationContext().getSystemService(ACCOUNT_SERVICE);

                        // loop through all accounts to remove them
                        Account[] accounts = accountManager.getAccounts();
                        for (int index = 0; index < accounts.length; index++) {
                            if (accounts[index].type.intern() == "com.odoo.auth") {
                                accountManager.removeAccount(accounts[index], null, null);
                                Intent intent = new Intent(getApplicationContext(), OdooLogin.class);
                                startActivity(intent);
                                finish();
                                break;
                            }
                        }

                    }
                });
                builder.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MenuAwalActivity.class));
        finish();
    }
}

