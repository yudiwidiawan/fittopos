package com.odoo.addons.products.providers;

import com.odoo.addons.products.models.ProductTemplates;
import com.odoo.addons.projects.providers.ProjectTaskProvider;
import com.odoo.core.orm.provider.BaseModelProvider;

/**
 * Created by makan on 11/07/2017.
 */

public class ProductTaskProvider extends BaseModelProvider {
    public static final String TAG = ProjectTaskProvider.class.getSimpleName();

    @Override
    public String authority() {
        return ProductTemplates.AUTHORITY;
    }
}
