package com.mowares.massagerexpressclient;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.mowares.massagerexpressclient.component.MyFontButton;
import com.mowares.massagerexpressclient.fragments.OnBoardFragment1;
import com.mowares.massagerexpressclient.fragments.OnBoardFragment2;
import com.mowares.massagerexpressclient.fragments.OnBoardFragment3;
import com.mowares.massagerexpressclient.fragments.OnBoardFragment4;
import com.mowares.massagerexpressclient.parse.AsyncTaskCompleteListener;
import com.mowares.massagerexpressclient.parse.HttpRequester;
import com.mowares.massagerexpressclient.parse.ParseContent;
import com.mowares.massagerexpressclient.utils.AndyUtils;
import com.mowares.massagerexpressclient.utils.AppLog;
import com.mowares.massagerexpressclient.utils.Const;
import com.mowares.massagerexpressclient.utils.PreferenceHelper;
import com.splunk.mint.Mint;

import java.util.HashMap;

public class OnBoardingActivity extends AppCompatActivity implements AsyncTaskCompleteListener, Response.ErrorListener {
    static final int NUM_PAGES = 4;

    ViewPager pager;
    PagerAdapter pagerAdapter;
    LinearLayout circles;
    MyFontButton imgvNoking;
    EditText txtPhoneNumber;
    private String type = Const.MANUAL;
    private String socialId;
    private ParseContent pContent;
    public PreferenceHelper pHelper;
    private boolean isReceiverRegister;
    RelativeLayout rltop;
    private boolean pagerMoved = false;
    private static final long ANIM_VIEWPAGER_DELAY = 5000;
    private RequestQueue requestQueue;
    private Handler h = new Handler();
    private Runnable animateViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);
        //Edit by me
        String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        pHelper = new PreferenceHelper(this);
        pHelper.putDeviceToken(android_id);//done
        pContent = new ParseContent(this);
        Mint.initAndStartSession(OnBoardingActivity.this, "1a9fb841");
        requestQueue = Volley.newRequestQueue(this);
        pager = (ViewPager) findViewById(R.id.pager);
        rltop = (RelativeLayout) findViewById(R.id.rltop);
        rltop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                AndyUtils.hideSoftKeyboard(OnBoardingActivity.this);
                return false;
            }
        });
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        View touchView = findViewById(R.id.pager);
        touchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                h.removeCallbacks(animateViewPager);
                return false;
            }
        });
        animateViewPager = new Runnable() {
            public void run() {
                if (!pagerMoved) {
                    pager.setCurrentItem(pager.getCurrentItem() + 1, true);
                    h.postDelayed(animateViewPager, ANIM_VIEWPAGER_DELAY);

                }
            }
        };
        isReceiverRegister = false;
        imgvNoking = (MyFontButton) findViewById(R.id.imgvNoking);
        txtPhoneNumber = (EditText) findViewById(R.id.txtPhoneNumber);
        imgvNoking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgvNoking.setBackgroundColor(getResources().getColor(R.color.black));
                imgvNoking.setTextColor(Color.WHITE);
                Log.d("REGISTER :: :: :: ", "" + isValidate());
                if (isValidate()) {

                    register(type, socialId);

                } else {
                    imgvNoking.setBackgroundColor(getResources().getColor(R.color.black));
                }

            }
        });
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setIndicator(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        buildCircles();
        if (TextUtils.isEmpty(pHelper.getDeviceToken())) {
            isReceiverRegister = true;
        }
    }


    protected boolean isValidate() {
        String msg = null;
        if (!TextUtils.isEmpty(txtPhoneNumber.getText().toString())) {
            pHelper.putNumber(txtPhoneNumber.getText().toString());


            Log.d("NUMBER :: ", pHelper.getNumber());
            if (pHelper.getNumber().length() > 7) {
                Log.d("Valid phone No ::", "okokoko");
            } else {
                msg = getString(R.string.text_enter_no);
                txtPhoneNumber.requestFocus();
                txtPhoneNumber.setBackgroundResource(R.drawable.setborder);
                imgvNoking.setBackgroundColor(getResources().getColor(R.color.color_onselect));
                imgvNoking.setTextColor(Color.BLACK);
            }
        }
        // invaid

        if (msg != null) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            return false;

        }
        if (TextUtils.isEmpty(txtPhoneNumber.getText().toString())) {
            msg = getString(R.string.text_enter_number);
            txtPhoneNumber.requestFocus();
            txtPhoneNumber.setBackgroundResource(R.drawable.setborder);
            imgvNoking.setBackgroundColor(getResources().getColor(R.color.color_onselect));
            imgvNoking.setTextColor(Color.BLACK);
        }
        if (msg == null) {
            return true;
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        return false;
    }

    private void register(String type, String id) {
        if (!AndyUtils.isNetworkAvailable(this)) {
            AndyUtils.showToast(
                    getResources().getString(R.string.dialog_no_inter_message),
                    this);
            imgvNoking.setBackgroundColor(getResources().getColor(R.color.color_onselect));
            imgvNoking.setTextColor(Color.BLACK);
            return;
        }
        if (type.equalsIgnoreCase(Const.MANUAL)) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(Const.URL, Const.ServiceType.REGISTER);
            map.put(Const.Params.LOGIN_BY, Const.MANUAL);
            map.put(Const.Params.PHONE, txtPhoneNumber.getText().toString());
            map.put(Const.Params.DEVICE_TOKEN, this.pHelper.getDeviceToken());
            map.put(Const.Params.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID);
            AndyUtils.showCustomProgressDialog(this, getString(R.string.text_loading), true, null);
            //new MultiPartRequester(activity, map, Const.ServiceCode.REGISTER, this);
            new HttpRequester(this, map, Const.ServiceCode.REGISTER, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (h != null) {
            h.removeCallbacks(animateViewPager);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        h.postDelayed(animateViewPager, ANIM_VIEWPAGER_DELAY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pager != null) {
            pager.clearOnPageChangeListeners();
        }
        if (h != null) {
            h.removeCallbacks(animateViewPager);
        }
    }

    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            pager.setCurrentItem(pager.getCurrentItem() - 1);
        }
        /*if (h != null) {
            h.removeCallbacks(animateViewPager);
        }*/
    }

    private void buildCircles() {
        circles = LinearLayout.class.cast(findViewById(R.id.circles));

        float scale = getResources().getDisplayMetrics().density;
        int padding = (int) (4 * scale + 0.5f);
        for (int i = 0; i < NUM_PAGES; i++) {
            ImageView circle = new ImageView(this);
            circle.setImageResource(R.drawable.empty_circle);
            circle.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            circle.setAdjustViewBounds(true);
            circle.setPadding(padding, 0, padding, 0);
            circle.getLayoutParams().height = 40;
            circle.getLayoutParams().width = 40;
            circles.addView(circle);
        }
        setIndicator(0);
    }

    private void setIndicator(int index) {
        if (index < NUM_PAGES) {
            for (int i = 0; i < NUM_PAGES; i++) {
                ImageView circle = (ImageView) circles.getChildAt(i);

                if (i == index) {
                    circle.setImageResource(R.drawable.filled_circle);
                } else {

                    circle.setImageResource(R.drawable.empty_circle);
                }
            }
        }
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        switch (serviceCode) {
            case Const.ServiceCode.REGISTER:
                AndyUtils.removeCustomProgressDialog();
                AppLog.Log(" Register ", "Response" + response);
                if (pContent.isSuccessWithStoreId(response)) {
                    Log.d("register ", "response" + response);
                    Toast.makeText(this,
                            getString(R.string.toast_register_success),
                            Toast.LENGTH_SHORT).show();
                    goToVerifyFragment();

                } else if (pContent.getErrorCode(response) == Const.ALREADY_REGISTERED) {
                    Log.d("register response", response);
                    login();

                }
                /*else {
                    //Toast.makeText(this, getString(R.string.toast_register_failed), Toast.LENGTH_SHORT).show();
                    goToChooseYourNokActivity();
                    imgvNoking.setImageResource(R.drawable.screen2_1);
                }*/
                break;
            case Const.ServiceCode.LOGIN:
                AndyUtils.removeCustomProgressDialog();
                if (pContent.isSuccessWithLoginID(response))
                    goToChooseYourNokActivity();
                else {
                    goToVerifyFragment();
                }
                break;
        }
    }

    private void login() {
        if (!AndyUtils.isNetworkAvailable(this)) {
            AndyUtils.showToast(getResources().getString(R.string.no_internet),
                    this);
            return;
        }
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.LOGIN);
        map.put(Const.Params.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID);
        map.put(Const.Params.DEVICE_TOKEN, pHelper.getDeviceToken());
        map.put(Const.Params.LOGIN_BY, Const.MANUAL);
        map.put(Const.Params.PHONE, pHelper.getNumber());
        // new HttpRequester(this, map, Const.ServiceCode.LOGIN, this);
        AndyUtils.showCustomProgressDialog(this, getString(R.string.text_loading), true, null);
        //new MultiPartRequester(activity, map, Const.ServiceCode.REGISTER, this);
        new HttpRequester(this, map, Const.ServiceCode.LOGIN, this);
        // requestQueue.add(new VolleyHttpRequest(Request.Method.POST, map, Const.ServiceCode.LOGIN, this, this));
    }

    public void onErrorResponse(VolleyError error) {
        // TODO Auto-generated method stub
        AppLog.Log(Const.TAG, error.getMessage());
    }

    private void goToVerifyFragment() {
        Intent verifyotp = new Intent(OnBoardingActivity.this, VerifyOtp.class);
        startActivity(verifyotp);
    }

    private void goToChooseYourNokActivity() {
        Intent chooseyourNok = new Intent(OnBoardingActivity.this, ChooseYourNok.class);
        startActivity(chooseyourNok);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new OnBoardFragment1();
                    break;
                case 1:
                    fragment = new OnBoardFragment2();
                    break;
                case 2:
                    fragment = new OnBoardFragment3();
                    break;
                case 3:
                    fragment = new OnBoardFragment4();
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
