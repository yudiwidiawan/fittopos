package com.odoo.addons.products.models;

import android.content.Context;
import android.net.Uri;

import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OFloat;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

/**
 * Created by makan on 11/07/2017.
 */

public class ProductTemplates extends OModel {
    public static final String TAG = ProductTemplates.class.getSimpleName();
    public static final String AUTHORITY = "com.odoo.addons.products.products_template";

    OColumn name = new OColumn("name", OVarchar.class).setSize(100);
    OColumn id = new OColumn("id", ProductProduct.class, OColumn.RelationType.OneToMany);
    OColumn price = new OColumn("list_price", OFloat.class).setSize(100);

    public ProductTemplates(Context context, String model_name, OUser user) {
        super(context, "product.template", user);
    }

    @Override
    public Uri uri() {
        return buildURI(AUTHORITY);
    }
}
