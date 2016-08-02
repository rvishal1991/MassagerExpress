package com.mowares.massagerexpressclient.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.mowares.massagerexpressclient.ListService;
import com.mowares.massagerexpressclient.R;
import com.mowares.massagerexpressclient.models.MyThings;
import com.mowares.massagerexpressclient.utils.AppLog;
import com.mowares.massagerexpressclient.utils.PreferenceHelper;

import java.util.ArrayList;

/**
 * Created by mobilefirst on 12/3/16.
 */
public class ListServiceAdapter extends BaseAdapter {
    protected LayoutInflater layoutInflater;
    protected ViewHolder holder;
    protected Context mContext;
    private ArrayList<MyThings> subTypeList;
    private int mSelectedPosition = -1;
    private RadioButton mSelectedRB;
    private PreferenceHelper pHelper;

    public ListServiceAdapter(ListService listService, ArrayList<MyThings> subtypeList) {
        this.mContext = listService;
        this.subTypeList = subtypeList;
        pHelper = new PreferenceHelper(mContext);
        layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return subTypeList.size();
    }

    @Override
    public Object getItem(int position) {
        return subTypeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.raw_list_service, parent, false);
            holder.rbService = (RadioButton) convertView.findViewById(R.id.rbService);
            holder.llChooseService = (LinearLayout) convertView.findViewById(R.id.llChooseService);
            holder.rbService.setText(subTypeList.get(position).SUBTYPE_NAME);
            /*Typeface font = Typeface.createFromAsset(mContext.getAssets(), "ArbelHagilda_Regular.otf");
            holder.rbService.setTypeface(font);*/
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.rbService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position != mSelectedPosition && mSelectedRB != null) {
                    mSelectedRB.setChecked(false);
                }

                mSelectedPosition = position;
                mSelectedRB = (RadioButton) view;
                notifyDataSetChanged();
                //AppLog.Log("selected radion Button position", "" + mSelectedPosition);
            }
        });
        if (mSelectedPosition != position) {
            holder.rbService.setChecked(false);
            holder.llChooseService.setBackgroundColor(mContext.getResources().getColor(R.color.black));
            holder.rbService.setTextColor(Color.WHITE);
            // AppLog.Log("selected radion Button position", "" + mSelectedPosition);
        } else {
            holder.rbService.setChecked(true);
            holder.llChooseService.setBackgroundColor(mContext.getResources().getColor(R.color.color_onselect));
            holder.rbService.setTextColor(Color.BLACK);
            mSelectedRB = holder.rbService;
            // AppLog.Log("selected radion Button position", "" + mSelectedPosition);
        }

        return convertView;

    }

    public int getSelectedRadioButtonPosition() {
        AppLog.Log("selected radion Button position", "" + mSelectedPosition);
        if (mSelectedPosition > -1) {
            AppLog.Log("selected subtype", "from list" + subTypeList.get(mSelectedPosition).SUBTYPE_ID);
            pHelper.putSubtypeId(subTypeList.get(mSelectedPosition).SUBTYPE_ID);
        }
        return mSelectedPosition;
    }

    class ViewHolder {
        protected RadioButton rbService;
        private LinearLayout llChooseService;
    }
}
