package com.odoo.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.odoo.R;
import com.odoo.addons.products.models.Product;
import com.odoo.addons.products.models.Products;
import com.odoo.core.utils.BitmapUtils;


import java.util.List;

/**
 * Created by makan on 02/08/2017.
 */

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.TheViewHolder> {

    private List<Product> productsList;

    Context mContext;
    View mView;

    public class TheViewHolder extends RecyclerView.ViewHolder {
        public TextView namaProduk, hargaProduk;
        public ImageView imageProduk;

        public TheViewHolder(View itemView) {
            super(itemView);
            namaProduk = (TextView) itemView.findViewById(R.id.txvNamaProduk);
            hargaProduk = (TextView) itemView.findViewById(R.id.txvHargaProduk);
            imageProduk = (ImageView) itemView.findViewById(R.id.ivProduct);
        }
    }

    public ProductsAdapter(List<Product> productsList) {
        this.productsList = productsList;
    }

    @Override
    public TheViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product, parent, false);

        return new TheViewHolder(itemView);
    }

    public void onBindViewHolder(TheViewHolder holder, int position) {
        Product product = productsList.get(position);
        holder.namaProduk.setText(product.getNamaProduk());
        holder.hargaProduk.setText("Rp " + product.getHargaProduk());
        Bitmap gambar = BitmapUtils.getBitmapImage(mContext, product.getImageProduk());
        holder.imageProduk.setImageBitmap(gambar);
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }
}

