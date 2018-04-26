package com.odoo.addons.projects.services;

import android.content.Context;
import android.os.Bundle;

import com.odoo.addons.projects.models.ProjectTask;
import com.odoo.core.service.OSyncAdapter;
import com.odoo.core.service.OSyncService;
import com.odoo.core.support.OUser;

/**
 * Created by makan on 12/06/2017.
 */

public class ProjectSyncService extends OSyncService {
    public static final String TAG = ProjectSyncService.class.getSimpleName();

    @Override
    public OSyncAdapter getSyncAdapter(OSyncService service, Context context) {
        return new OSyncAdapter(getApplicationContext(), ProjectTask.class, this, true);
    }

    @Override
    public void performDataSync(OSyncAdapter adapter, Bundle extras, OUser user) {
        adapter.syncDataLimit(80);
    }
}
