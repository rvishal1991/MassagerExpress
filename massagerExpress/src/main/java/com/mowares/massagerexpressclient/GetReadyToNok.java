package com.mowares.massagerexpressclient;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.mowares.massagerexpressclient.component.MyTitleFontTextView;
import com.mowares.massagerexpressclient.models.Driver;
import com.mowares.massagerexpressclient.parse.AsyncTaskCompleteListener;
import com.mowares.massagerexpressclient.parse.ParseContent;
import com.mowares.massagerexpressclient.parse.VolleyHttpRequest;
import com.mowares.massagerexpressclient.utils.AndyUtils;
import com.mowares.massagerexpressclient.utils.AppLog;
import com.mowares.massagerexpressclient.utils.Const;
import com.mowares.massagerexpressclient.utils.PreferenceHelper;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class GetReadyToNok extends BaseActivity implements Response.ErrorListener, AsyncTaskCompleteListener {
    protected MyTitleFontTextView txtTitle;
    private ImageView imgReadyToNok;
    private int ArrayType;
    private ImageButton btnActionBack, btnActionMenu;
    private ParseContent pContent;
    private PreferenceHelper pHelper;
    private boolean isContinueRequest;
    private Timer timer;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_ready_to_nok);
        Const.menu_myProfile = 1;
        Const.menu_newNok = 1;
        Const.menu_customerService = 1;
        Const.menu_getReady = 0;
        Const.menu_futureNok = 1;
        Const.menu_mynok = 1;
        Bundle bundle = getIntent().getExtras();
        InitUI();
        if (bundle != null) {
            ArrayType = bundle.getInt("ArrayType");
            imgReadyToNok = (ImageView) findViewById(R.id.imgReadyToNok);
            if (ArrayType == 1) {
                imgReadyToNok.setImageResource(R.drawable.get_ready_makeup);
            }
            if (ArrayType == 2) {
                imgReadyToNok.setImageResource(R.drawable.get_ready_nails);
            } else if (ArrayType == 3) {

                imgReadyToNok.setImageResource(R.drawable.get_ready_hairstyle);
            }
        }
        btnActionBack = (ImageButton) findViewById(R.id.btnActionBack);
        btnActionMenu = (ImageButton) findViewById(R.id.btnActionMenu);
        btnActionBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnActionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.showAsDropDown(v, 0, 0);
            }
        });
        startCheckingStatusUpdate();
    }

    private void InitUI() {
        txtTitle = (MyTitleFontTextView) findViewById(R.id.tvTitle);
        txtTitle.setText("Ready to NOK");
        pHelper = new PreferenceHelper(this);
        pContent = new ParseContent(this);
        requestQueue = Volley.newRequestQueue(this);
    }

    private void startCheckingStatusUpdate() {
        stopCheckingStatusUpdate();
        if (pHelper.getRequestId() != Const.NO_REQUEST) {
            isContinueRequest = true;
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerRequestStatus(), Const.DELAY,
                    Const.TIME_SCHEDULE);
        }
    }

    private void stopCheckingStatusUpdate() {
        isContinueRequest = false;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {

    }

    private void gotoRateFragment(Driver driver) {
        try {

            Intent gotoFeedback = new Intent(GetReadyToNok.this, Feedback.class);
            gotoFeedback.putExtra(Const.DRIVER, driver);
            startActivity(gotoFeedback);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        switch (serviceCode) {
            case Const.ServiceCode.GET_REQUEST_STATUS:
                AppLog.Log(Const.TAG, "Get Request Response::::" + response);
                AndyUtils.removeCustomProgressRequestDialog();
                if (pContent.isSuccess(response)) {
                    switch (pContent.checkRequestStatus(response)) {
                        case Const.IS_COMPLETED:
                            AppLog.Log("complete", response);
                            stopCheckingStatusUpdate();
                            gotoRateFragment(pContent.getDriverDetail(response));
                            break;
                        case Const.NO_REQUEST:
//					AndyUtils.removeCustomProgressDialog();
                           /* if (!isGettingVehicalType) {

                                startUpdateProvidersLocation();
                            }*/
                            stopCheckingStatusUpdate();
                            break;
                    }

                } else if (pContent.getErrorCode(response) == Const.REQUEST_ID_NOT_FOUND) {
                    AndyUtils.removeCustomProgressDialog();
                    pHelper.clearRequestData();
                    isContinueRequest = false;
                } else if (pContent.getErrorCode(response) == Const.INVALID_TOKEN) {
                   /* if (pHelper.getLoginBy()
                            .equalsIgnoreCase(Const.MANUAL))
                        login();*/

                } else {
                    isContinueRequest = true;
                }
                break;
        }
    }

    private class TimerRequestStatus extends TimerTask {
        @Override
        public void run() {
            if (isContinueRequest) {
                isContinueRequest = false;
                getRequestStatus(String.valueOf(pHelper.getRequestId()));
            }
        }
    }

    private void getRequestStatus(String requestId) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL,
                Const.ServiceType.GET_REQUEST_STATUS + Const.Params.ID + "="
                        + pHelper.getUserId() + "&"
                        + Const.Params.TOKEN + "="
                        + pHelper.getSessionToken()
                        + "&" + Const.Params.REQUEST_ID + "=" + requestId);

        // new HttpRequester(activity, map,
        // Const.ServiceCode.GET_REQUEST_STATUS,
        // true, this);
        requestQueue.add(new VolleyHttpRequest(Request.Method.GET, map,
                Const.ServiceCode.GET_REQUEST_STATUS, this, this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCheckingStatusUpdate();
        selectedPosition = -1;
        Const.menu_myProfile = 1;
        Const.menu_newNok = 1;
        Const.menu_customerService = 1;
        Const.menu_getReady = 0;
        Const.menu_futureNok = 1;
        Const.menu_mynok = 1;
    }
}
