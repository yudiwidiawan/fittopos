package com.odoo.addons.products.models;

import android.content.Context;

import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

/**
 * Created by makan on 11/07/2017.
 */

public class ProductProduct extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class).setSize(100);

    public ProductProduct(Context context, String model_name, OUser user) {
        super(context, "product.product", user);
    }
}
