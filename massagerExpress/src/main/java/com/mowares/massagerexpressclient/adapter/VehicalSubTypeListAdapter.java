package com.mowares.massagerexpressclient.adapter;

import java.util.ArrayList;
import java.util.TreeSet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.mowares.massagerexpressclient.R;
import com.mowares.massagerexpressclient.fragments.UberMapFragment;
import com.mowares.massagerexpressclient.models.CategotySubType;

public class VehicalSubTypeListAdapter extends BaseAdapter {
	private ArrayList<CategotySubType> listSubType;
	private LayoutInflater inflater;
	private AQuery aQuery;
	UberMapFragment mapfrag;
	final TreeSet<Integer> selectedStrings;
	View vi;
	Context context;
	public boolean[] checkedHolder;
	public static int seletedPosition = 0;

	public VehicalSubTypeListAdapter(Context context,
			ArrayList<CategotySubType> listSubType, TreeSet<Integer> subid, UberMapFragment mapfrag) {
		 this.listSubType = listSubType;
		 this.mapfrag = mapfrag;
		 this.context = context;
		 // checkedHolder = new boolean [getCount()] ;
		 this.selectedStrings=subid;
		// createCheckedHolder();

		// itemChecked = new boolean[listSubType.size()];

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listSubType.size();
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		vi = convertView;
		final ViewHolder holder;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (convertView == null) {
			vi = inflater.inflate(R.layout.subtype_list_item, parent, false);
			holder = new ViewHolder();
			holder.tvName = (TextView) vi.findViewById(R.id.tvName);
			holder.ivIcon = (ImageView) vi.findViewById(R.id.ivIcon);
			holder.ch = (CheckBox) vi.findViewById(R.id.checkBox1);
//			holder.tvPrice = (TextView) vi.findViewById(R.id.tvPrice);
			// holder.ivSelectService = (ImageView) convertView
			// .findViewById(R.id.ivSelectService);

			// holder.viewSeprater = (View) convertView
			// .findViewById(R.id.seprateView);
			vi.setTag(holder);
		} else {
			holder = (ViewHolder) vi.getTag();
		}

		aQuery = new AQuery(vi);
		holder.tvName.setText(listSubType.get(position).getName() + "");
		holder.ivIcon.setTag(position);
		aQuery.id(holder.ivIcon).image(listSubType.get(position).getIcon(),
				true, true);
//		holder.tvPrice.setText("$ "+listSubType.get(position).getBasePrice()+"");
		// holder.ch.setChecked(false);
		// if(checkedHolder[position]){
		// holder.ch.setChecked(true);
		// }else
		// {
		// holder.ch.setChecked(false);
		// }
		//

		holder.ch
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							selectedStrings.add(listSubType.get(position)
									.getId());
							listSubType.get(position).isSelected = true;
							// checkedHolder[position] = true;
						} else {
//							selectedStrings.remove(listSubType.get(position)
//									.getId());
							listSubType.get(position).isSelected = false;
							// checkedHolder[position]=false;
						}

					}
				});
		return vi;
	}

	// private void createCheckedHolder() {
	// checkedHolder = new boolean[getCount()];
	// }
	public TreeSet<Integer> getSelectedId() {
		return selectedStrings;
		
	}
//	public boolean isValidate() {
//		boolean isSelected = false;
//		for (int i = 0; i < listSubType.size(); i++) {
//			if (listSubType.get(i).isSelected) {
//				type.add(listSubType.get(i).getId());
//
//				basePrice.add(Integer.parseInt(listSubType.get(i).getBasePrice()));
//				if (listSubType.get(i).getBasePrice().equals("")
//						|| listSubType.get(i).getBasePrice().equals("0")) {
//					Toast.makeText(registerActivity, "Please enter base price",
//
//					Toast.LENGTH_LONG).show();
//					isSelected = false;
//				} else {
//					isSelected = true;
//				}
//			}
//		}
//		return isSelected;
//	}

}

class ViewHolder {
	TextView tvName, tvPrice;
	ImageView ivIcon;
	CheckBox ch;
	// View viewSeprater;
}
