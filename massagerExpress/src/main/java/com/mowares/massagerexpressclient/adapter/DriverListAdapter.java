package com.mowares.massagerexpressclient.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.mowares.massagerexpressclient.MapActivity;
import com.mowares.massagerexpressclient.R;
import com.mowares.massagerexpressclient.component.MyTitleFontTextView;
import com.mowares.massagerexpressclient.models.Driver;

import java.util.ArrayList;

/**
 * Created by mobilefirst on 1/4/16.
 */

public class DriverListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ViewHolder holder;
    private ArrayList<Driver> listDriver;
    private Context context;
    private ImageOptions imageOptions;
    private AQuery aQuery;
    private int selectedPostion = -1;

    public DriverListAdapter(Context context, ArrayList<Driver> listDriver) {
        this.context = context;
        this.listDriver = listDriver;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageOptions = new ImageOptions();
        imageOptions.fileCache = true;
        imageOptions.memCache = true;
        imageOptions.fallback = R.drawable.default_user;


    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        return listDriver.size();
    }


    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        return listDriver.get(position);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.dialog_walker_info, parent, false);
            aQuery = new AQuery(context);
            holder = new ViewHolder();
            holder.etName = (MyTitleFontTextView) convertView
                    .findViewById(R.id.etName);
            holder.etDetail = (MyTitleFontTextView) convertView
                    .findViewById(R.id.etdetail);
            holder.etDesc = (MyTitleFontTextView) convertView
                    .findViewById(R.id.etdesc);
            holder.rtbProviderRating = (RatingBar) convertView
                    .findViewById(R.id.rtbProviderRating);
            holder.imgProviderPic = (ImageView) convertView
                    .findViewById(R.id.imgProviderPic);
            holder.llNOK = (LinearLayout) convertView.findViewById(R.id.llNOK);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Driver driver = listDriver.get(position);
        holder.etName.setText(driver.getFirstName() + "" + driver.getLastName());
        holder.etDesc.setText(driver.getBio());
        holder.etDetail.setText(driver.getPhone());
        holder.rtbProviderRating.setRating((float) driver.getRating());
        aQuery.id(holder.imgProviderPic).progress(R.id.pBar)
                .image(driver.getPicture(), imageOptions);
        holder.llNOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPostion = position;
//                pickMeUp();
            }
        });
        return convertView;
    }

    private class ViewHolder {
        MyTitleFontTextView etName, etDesc, etDetail;
        RatingBar rtbProviderRating;
        ImageView imgProviderPic;
        LinearLayout llNOK;
    }

}
