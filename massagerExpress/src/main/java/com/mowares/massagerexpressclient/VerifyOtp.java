package com.mowares.massagerexpressclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.mowares.massagerexpressclient.component.MyFontEdittextView;
import com.mowares.massagerexpressclient.parse.ParseContent;
import com.mowares.massagerexpressclient.parse.VolleyHttpRequest;
import com.mowares.massagerexpressclient.utils.AndyUtils;
import com.mowares.massagerexpressclient.utils.AppLog;
import com.mowares.massagerexpressclient.utils.Const;

import java.util.HashMap;

/**
 * Created by mobilefirst on 27/2/16.
 */
public class VerifyOtp extends OnBoardingActivity implements View.OnClickListener, Response.ErrorListener {

    private RequestQueue requestQueue;
    private ParseContent parseContent;
    private String token, id;
    private MyFontEdittextView txtVerifyotp;
    ImageView imgvVerifyOtp;
    private int is_skip = 0;
    LinearLayout lltop;
    RelativeLayout rlTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(this);
       /* Bundle bundle = getIntent().getExtras();*/
        token = pHelper.getSessionToken();
        id = pHelper.getUserId();
        setContentView(R.layout.otp_verification);
        txtVerifyotp = (MyFontEdittextView) findViewById(R.id.txtVerifyotp);
        lltop = (LinearLayout) findViewById(R.id.llTop);
        rlTop = (RelativeLayout) findViewById(R.id.rlTop);
        rlTop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                AndyUtils.hideSoftKeyboard(VerifyOtp.this);
                return false;
            }
        });
        imgvVerifyOtp = (ImageView) findViewById(R.id.imgvVerifyOtp);
        imgvVerifyOtp.setOnClickListener(this);
        parseContent = new ParseContent(this);
    }

    @Override
    protected boolean isValidate() {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgvVerifyOtp:
                imgvVerifyOtp.setImageResource(R.drawable.imgok);
                if (txtVerifyotp.getText().length() == 0) {
                    AndyUtils.showToast(
                            getResources().getString(
                                    R.string.text_blank_verify_code), this);
                    return;
                } else {
                    if (!AndyUtils.isNetworkAvailable(this)) {
                        AndyUtils
                                .showToast(
                                        getResources().getString(
                                                R.string.dialog_no_inter_message),
                                        this);
                        imgvVerifyOtp.setImageResource(R.drawable.imgok_clicked);
                        return;
                    }
                    is_skip = 0;
                    applyVerifyCode(true);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        imgvVerifyOtp.setImageResource(R.drawable.imgok_clicked);
    }

    public void exitAppMethod() {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        exitAppMethod();
    }

    private void applyVerifyCode(boolean isShowLoader) {
        if (isShowLoader)
            AndyUtils.showCustomProgressDialog(this,
                    getString(R.string.progress_loading), false, null);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.VERIFY_CODE);
        map.put(Const.Params.VERIFY_CODE, txtVerifyotp.getText().toString());
       /* map.put(Const.Params.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID);*/
        map.put(Const.Params.DEVICE_TOKEN, pHelper.getDeviceToken());
        map.put(Const.Params.PHONE, pHelper.getNumber());
        map.put(Const.Params.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID);
        // new HttpRequester(activity, map,
        // Const.ServiceCode.APPLY_REFFRAL_CODE,
        // this);
        requestQueue.add(new VolleyHttpRequest(Request.Method.POST, map,
                Const.ServiceCode.VERIFY_CODE, this, this));
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        // TODO Auto-generated method stub
        AndyUtils.removeCustomProgressDialog();
        AppLog.Log(Const.TAG, "VerifyCode Response ::: " + response);
        switch (serviceCode) {
            case Const.ServiceCode.VERIFY_CODE:
                if (parseContent.isSuccess(response)) {
                    // parseContent.parseUserAndStoreToDb(response);
                    pHelper.putIsVerify(true);
                    pHelper.putIsLoogedIn(true);
                    /*gotoRegistrationPage(pHelper.getUserId(),
                            pHelper.getSessionToken());*/
                    goToChooseYourNok();
                    AppLog.Log("onTaskCompleted",
                            "id:" + pHelper.getUserId());

                    AppLog.Log("onTaskCompleted",
                            "token:" + pHelper.getSessionToken());
                } else {
                    imgvVerifyOtp.setImageResource(R.drawable.imgok_clicked);
                    txtVerifyotp.requestFocus();
                    txtVerifyotp.setBackgroundResource(R.drawable.setborder);
                }
                break;

            default:
                break;
        }
    }

    private void goToChooseYourNok() {
        Intent registrationActivity = new Intent(VerifyOtp.this, ChooseYourNok.class);
        startActivity(registrationActivity);
    }

    private void gotoRegistrationPage(String id, String token) {
        Intent registrationActivity = new Intent(VerifyOtp.this, registration.class);
        /*registrationActivity.putExtra(Const.Params.TOKEN, token);
        registrationActivity.putExtra(Const.Params.ID, id);*/
        startActivity(registrationActivity);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        AppLog.Log(Const.TAG, error.getMessage());

    }
}
