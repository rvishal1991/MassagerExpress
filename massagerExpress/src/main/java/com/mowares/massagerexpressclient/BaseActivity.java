package com.mowares.massagerexpressclient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.mowares.massagerexpressclient.component.MyFontButton;
import com.mowares.massagerexpressclient.component.MyFontTextView;
import com.mowares.massagerexpressclient.fragments.OnBoardFragment1;
import com.mowares.massagerexpressclient.models.History;
import com.mowares.massagerexpressclient.utils.Const;
import com.mowares.massagerexpressclient.utils.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mobilefirst on 8/4/16.
 */
public class BaseActivity extends ActionBarActivity {
    public String popUpContents[];
    public PopupWindow popupWindow;
    public ImageButton btnActionMenu;
    rawFile rawFileAdapter;
    public List<String> contentList;
    public int selectedPosition = -1;
    private PreferenceHelper pHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* LayoutInflater inflater = (LayoutInflater) actionBar.getThemedContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View customActionBarView = inflater.inflate(R.layout.customactionbar,
                null);*/
        setContentView(R.layout.customactionbar);
        pHelper = new PreferenceHelper(this);
        btnActionMenu = (ImageButton) findViewById(R.id.btnActionMenu);
        contentList = new ArrayList<String>();
        contentList.add(getResources().getString(R.string.text_new_nok));
        contentList.add(getResources().getString(R.string.text_future_nok));
        contentList.add(getResources().getString(R.string.text_my_nok));
        contentList.add(getResources().getString(R.string.text_customerservice));
        contentList.add(getResources().getString(R.string.text_get_ready));

        contentList.add(getResources().getString(R.string.text_myprofile));
        contentList.add(getResources().getString(R.string.text_logout));
 /*  contentList.add("Prices");
        contentList.add("Settings");*/
// convert to simple array
        popUpContents = new String[contentList.size()];
        contentList.toArray(popUpContents);

/*
* initialize pop up window
*/
        popupWindow = popupWindowDogs();
        btnActionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                popupWindowDogs.setElevation(10);
                popupWindow.showAsDropDown(v, 0, 0);
            }
        });
    }

    public PopupWindow popupWindowDogs() {

        // initialize a pop up window type
        PopupWindow popupWindow = new PopupWindow(this);

        // the drop down list is a list view
        ListView listView = new ListView(this);
        listView.setBackgroundColor(0xff000000);    //Black
        // set our adapter and pass our pop up window contents
        rawFileAdapter = new rawFile(this, contentList);
        listView.setAdapter(rawFileAdapter);
        listView.setDivider(new ColorDrawable(0xffffffff));   //Gray 0xff888888
        listView.setDividerHeight(2);
        //listView.setAdapter(dogsAdapter(popUpContents));
        // set the item click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View v, final int position,
                                    long arg3) {
                // TODO Auto-generated method stub
                Context mContext = v.getContext();
                selectedPosition = position;
                rawFileAdapter.notifyDataSetChanged();
                BaseActivity mainActivity = ((BaseActivity) mContext);
                // add some animation when a list item was clicked
                Animation fadeInAnimation = AnimationUtils.loadAnimation(v.getContext(), android.R.anim.fade_in);
                fadeInAnimation.setDuration(10);
                v.startAnimation(fadeInAnimation);
                // dismiss the pop up
                mainActivity.popupWindow.dismiss();
                // get the text and set it as the button text
                String val = (String) arg0.getItemAtPosition(position);
                if (val.equals(getResources().getString(R.string.text_get_ready))) {
                    if (Const.menu_getReady == 1) {
                        if (pHelper.getRequestId() > 0) {
                            if (pHelper.getARRAYTYPE() == 1) {
                                Intent myNok = new Intent(BaseActivity.this, GetReadyToNok.class);
                                myNok.putExtra("ArrayType", 1);
                                startActivity(myNok);
                            } else if (pHelper.getARRAYTYPE() == 2) {
                                Intent myNok = new Intent(BaseActivity.this, GetReadyToNok.class);
                                myNok.putExtra("ArrayType", 2);
                                startActivity(myNok);
                            } else if (pHelper.getARRAYTYPE() == 3) {
                                Intent myNok = new Intent(BaseActivity.this, GetReadyToNok.class);
                                myNok.putExtra("ArrayType", 3);
                                startActivity(myNok);
                            }
                        } else {
                            Toast.makeText(mContext, "Request Not Found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else if (val.equals(getResources().getString(R.string.text_customerservice))) {
                    if (Const.menu_customerService == 1) {
                        Intent customerService = new Intent(BaseActivity.this, CustomerService.class);
                        startActivity(customerService);
                    }
                } else if (val.equals(getResources().getString(R.string.text_new_nok))) {
                    if (Const.menu_newNok == 1) {
                        Intent chooseYourNok = new Intent(BaseActivity.this, ChooseYourNok.class);
                        startActivity(chooseYourNok);
                    }
                } else if (val.equals(getResources().getString(R.string.text_myprofile))) {
                    if (Const.menu_myProfile == 1) {
                        Intent myProfile = new Intent(BaseActivity.this, Myprofile.class);
                        startActivity(myProfile);
                    }
                } else if (val.equals(getResources().getString(R.string.text_my_nok))) {
                    if (Const.menu_mynok == 1) {
                        Intent myNok = new Intent(BaseActivity.this, HistoryActivity.class);
                        myNok.putExtra("My NOKS", 1);
                        startActivity(myNok);
                    }

                } else if (val.equals(getResources().getString(R.string.text_future_nok))) {
                    if (Const.menu_futureNok == 1) {
                        Intent myNok = new Intent(BaseActivity.this, HistoryActivity.class);
                        myNok.putExtra("Future NOKS", 1);
                        startActivity(myNok);
                    }

                } else if (val.equals(getResources().getString(R.string.text_logout))) {
                    pHelper.Logout();
                    Intent gotoOnBoardingIntent = new Intent(BaseActivity.this, OnBoardingActivity.class);
                    finish();
                    startActivity(gotoOnBoardingIntent);
                }

                /*Toast.makeText(mContext, val, Toast.LENGTH_SHORT).show();*/
            }
        });
// some other visual settings
        popupWindow.setFocusable(true);
        // popupWindow.setElevation(10);330
        int width = (int) (getDeviceWidth() / 2);
        int height = (WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        popupWindow.setWidth(width);
        //  popupWindow.setOutsideTouchable(true)
        // ;
        //  WindowManager.LayoutParams.WRAP_CONTENT
        popupWindow.setHeight(height);
// set the list view as pop up window content
        popupWindow.setContentView(listView);

        return popupWindow;
    }

    @SuppressLint("NewApi")
    public int getDeviceWidth() {
        int deviceWidth = 0;

        Point size = new Point();
        WindowManager windowManager = getWindowManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            windowManager.getDefaultDisplay().getSize(size);
            deviceWidth = size.x;
        } else {
            Display display = windowManager.getDefaultDisplay();
            deviceWidth = display.getWidth();
        }
        return deviceWidth;
    }

    @SuppressLint("NewApi")
    public int getDeviceHeight() {
        int deviceHeight = 0;

        Point size = new Point();
        WindowManager windowManager = getWindowManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            windowManager.getDefaultDisplay().getSize(size);
            deviceHeight = size.y;
        } else {
            Display display = windowManager.getDefaultDisplay();
            deviceHeight = display.getHeight();
        }
        return deviceHeight;
    }

    class rawFile extends BaseAdapter {
        Context context;
        List<String> content;
        protected LayoutInflater layoutInflater;
        protected ViewHolder holder;

        public rawFile(Context c, List<String> myArray) {
            context = c;
            content = myArray;
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return content.size();
        }

        @Override
        public Object getItem(int position) {
            return content.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.activity_menu_raw, parent, false);
                holder.llContainer = (LinearLayout) convertView.findViewById(R.id.llContainer);
                holder.textView = (MyFontTextView) convertView.findViewById(R.id.textView);
                holder.textView2 = (MyFontTextView) convertView.findViewById(R.id.textView2);
                holder.textView.setText(content.get(position).toString());
              /*  holder.textView2.setText("Hello");*/
               /* holder.textView.setPadding(20, 14, 20, 14);*/
               /* holder.textView2.setPadding(20, 3, 20, 7);*/
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (selectedPosition > -1) {
                if (position == selectedPosition) {
                    // set your color
                    holder.llContainer.setBackgroundColor(context.getResources().getColor(R.color.color_onselect));
                    holder.textView.setTextColor(getResources().getColor(R.color.black));
                    holder.textView2.setTextColor(getResources().getColor(R.color.black));
                } else {
                    holder.llContainer.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
                    holder.textView.setTextColor(getResources().getColor(R.color.white));
                    holder.textView2.setTextColor(getResources().getColor(R.color.white));
                }
            } else {
                holder.llContainer.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
                holder.textView.setTextColor(getResources().getColor(R.color.white));
                holder.textView2.setTextColor(getResources().getColor(R.color.white));
            }
            notifyDataSetChanged();
            return convertView;
        }

        class ViewHolder {
            MyFontTextView textView, textView2;
            LinearLayout llContainer;
        }
    }
}


