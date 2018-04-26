package com.odoo.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.odoo.R;

/**
 * Created by makan on 14/07/2017.
 */

public class InvoicingFragment extends Fragment {

    public static InvoicingFragment newInstance() {

        Bundle args = new Bundle();

        InvoicingFragment fragment = new InvoicingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_invoice, container, false);
    }
}
