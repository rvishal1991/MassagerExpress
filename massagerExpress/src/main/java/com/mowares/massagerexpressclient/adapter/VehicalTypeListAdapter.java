
package com.mowares.massagerexpressclient.adapter;

import java.util.ArrayList;
import java.util.TreeSet;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.mowares.massagerexpressclient.R;
import com.mowares.massagerexpressclient.fragments.UberMapFragment;
import com.mowares.massagerexpressclient.models.VehicalType;

/**
 * @author Hardik A Bhalodi
 */
public class VehicalTypeListAdapter extends BaseAdapter {

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getCount()
     */
    private ArrayList<VehicalType> listVehicalType;
    private LayoutInflater inflater;
    private AQuery aQuery;
    TreeSet<Integer> selectedId;
    UberMapFragment mapfrag;
    View vi;

    public VehicalTypeListAdapter(Context context,
                                  ArrayList<VehicalType> listVehicalType, UberMapFragment mapfrag) {
        this.listVehicalType = listVehicalType;
        this.mapfrag = mapfrag;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        selectedId = new TreeSet<Integer>();
        // TODO Auto-generated constructor stub
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listVehicalType.size();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        vi = convertView;
        final ViewHolderType holder;
        if (convertView == null) {
            vi = inflater.inflate(R.layout.list_item_type, parent,
                    false);
            holder = new ViewHolderType();
            holder.tvType = (TextView) vi.findViewById(R.id.tvType);
            holder.ivIcon = (ImageView) vi.findViewById(R.id.ivIcon);
            holder.ivSelectService = (ImageView) vi
                    .findViewById(R.id.ivSelectService);

            // holder.viewSeprater = (View) convertView
            // .findViewById(R.id.seprateView);
            vi.setTag(holder);
        } else {
            holder = (ViewHolderType) vi.getTag();
        }
        aQuery = new AQuery(vi);
        holder.tvType.setText(listVehicalType.get(position).getName() + "");
        holder.ivIcon.setTag(position);
        aQuery.id(holder.ivIcon).image(listVehicalType.get(position).getIcon(),
                true, true);

        if (listVehicalType.get(position).isSelected) {
            holder.ivSelectService.setVisibility(View.VISIBLE);
            holder.ivSelectService
                    .setImageResource(R.drawable.selected_service);
        } else {
            // holder.ivSelectService.setBackgroundColor(Color.TRANSPARENT);
            holder.ivSelectService.setVisibility(View.INVISIBLE);

        }

        return vi;
    }

    public TreeSet<Integer> getSelectedId() {
        return selectedId;
    }


}

class ViewHolderType {
    TextView tvType;
    ImageView ivIcon, ivSelectService;
    // View viewSeprater;
}
