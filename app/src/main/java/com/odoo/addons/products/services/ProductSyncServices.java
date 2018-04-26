package com.odoo.addons.products.services;

import android.content.Context;
import android.os.Bundle;

import com.odoo.addons.products.models.ProductTemplates;
import com.odoo.addons.projects.services.ProjectSyncService;
import com.odoo.core.service.OSyncAdapter;
import com.odoo.core.service.OSyncService;
import com.odoo.core.support.OUser;

/**
 * Created by makan on 11/07/2017.
 */

public class ProductSyncServices extends OSyncService {
    public static final String TAG = ProductSyncServices.class.getSimpleName();

    @Override
    public OSyncAdapter getSyncAdapter(OSyncService service, Context context) {
        return new OSyncAdapter(getApplicationContext(), ProductTemplates.class, this, true);
    }

    @Override
    public void performDataSync(OSyncAdapter adapter, Bundle extras, OUser user) {
        adapter.syncDataLimit(80);
    }
}
