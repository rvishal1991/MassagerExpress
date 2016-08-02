package com.mowares.massagerexpressclient.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mowares.massagerexpressclient.R;
import com.mowares.massagerexpressclient.models.TypeInvoice;

public class TypeInvoiceAdapter extends BaseAdapter {
    final ArrayList<TypeInvoice> listTypeInvoice;
    Context context;
    private ViewHolder holder;
    private LayoutInflater inflater;
    final TypeInvoice typeInvoiceReffaral = new TypeInvoice();
    final TypeInvoice typeInvoicePromo = new TypeInvoice();

    public TypeInvoiceAdapter(Context context,
                              final ArrayList<TypeInvoice> listTypeInvoice, double reffaralBonus,
                              double promoBonus) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.listTypeInvoice = listTypeInvoice;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        typeInvoiceReffaral.setName(context.getResources().getString(
                R.string.text_referral_bonus));
        typeInvoiceReffaral.setBasePrice(reffaralBonus);


        typeInvoicePromo.setName(context.getResources().getString(
                R.string.text_promo_bonus));
        typeInvoicePromo.setBasePrice(promoBonus);


    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listTypeInvoice.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_type_invoice, parent,
                    false);
            holder = new ViewHolder();
            holder.tvTypeInvoiceName = (TextView) convertView
                    .findViewById(R.id.tvTypeInvoiceName);
            holder.tvTypeInvoicePrice = (TextView) convertView
                    .findViewById(R.id.tvTypeInvoicePrice);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        TypeInvoice typeInvoice = listTypeInvoice.get(position);
        holder.tvTypeInvoiceName.setText(typeInvoice.getName());
        holder.tvTypeInvoicePrice.setText(typeInvoice.getBasePrice() + "");
        return convertView;
    }

    private class ViewHolder {
        TextView tvTypeInvoiceName, tvTypeInvoicePrice;
    }

}
