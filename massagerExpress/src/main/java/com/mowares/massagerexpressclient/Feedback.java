package com.mowares.massagerexpressclient;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.androidquery.AQuery;
import com.mowares.massagerexpressclient.adapter.TypeInvoiceAdapter;
import com.mowares.massagerexpressclient.component.MyFontButton;
import com.mowares.massagerexpressclient.component.MyFontEdittextView;
import com.mowares.massagerexpressclient.component.MyFontTextView;
import com.mowares.massagerexpressclient.component.MyTitleFontTextView;
import com.mowares.massagerexpressclient.models.Driver;
import com.mowares.massagerexpressclient.models.TypeInvoice;
import com.mowares.massagerexpressclient.parse.AsyncTaskCompleteListener;
import com.mowares.massagerexpressclient.parse.ParseContent;
import com.mowares.massagerexpressclient.parse.VolleyHttpRequest;
import com.mowares.massagerexpressclient.utils.AndyUtils;
import com.mowares.massagerexpressclient.utils.AppLog;
import com.mowares.massagerexpressclient.utils.Const;
import com.mowares.massagerexpressclient.utils.PreferenceHelper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mobilefirst on 16/5/16.
 */
public class Feedback extends BaseActivity implements View.OnClickListener, Response.ErrorListener, AsyncTaskCompleteListener {
    protected MyTitleFontTextView txtTitle;
    private MyFontEdittextView etComment;
    private RatingBar rtBar;
    private MyFontButton btnSubmit;
    private ImageView ivDriverImage;
    private Driver driver;
    private PreferenceHelper pHelper;
    private ParseContent pContent;
    private MyFontTextView tvDistance, tvTime, tvClientName;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);
        InitUI();
      /*  txtTitle.setText(getString(R.string.text_feedback));*/
        Bundle mydata = getIntent().getExtras();
        driver = (Driver) mydata.getSerializable(Const.DRIVER);
        if (driver != null) {
            if (driver.getPicture() != null)
                new AQuery(this).id(ivDriverImage).progress(R.id.pBar)
                        .image(driver.getPicture());
            tvClientName.setText(driver.getFirstName() + " "
                    + driver.getLastName());
            tvDistance.setText(driver.getLastDistance());
            tvDistance.setText(driver.getBill().getDistance() + " "
                    + driver.getBill().getUnit());

            tvTime.setText((int) (Double.parseDouble(driver.getBill().getTime()))
                    + " " + getString(R.string.text_mins));
            // tvFeedbackAmount.setText(getString(R.string.text_price_unit)
            // + Double.parseDouble(driver.getBill().getTotal()));
       /* if (!TextUtils.isEmpty(driver.getLastTime()))*/
          /*  tvTime.setText(driver.getLastTime());*/
            showBillDialog(driver.getBill().getListTypeInvoice(),
                    driver.getBill().getTotal(), driver.getBill()
                            .getPromoBouns(), driver.getBill()
                            .getReferralBouns(),
                    getString(R.string.text_confirm));
            AppLog.Log("listTypeInvoice", ""
                    + driver.getBill().getListTypeInvoice());
        }
    }

    protected boolean isValidate() {
        if (rtBar.getRating() <= 0)
            return false;
        return true;
    }

    private void InitUI() {
       /* txtTitle = (MyTitleFontTextView) findViewById(R.id.tvTitle);*/
        tvClientName = (MyFontTextView) findViewById(R.id.tvClientName);
        etComment = (MyFontEdittextView) findViewById(R.id.etComment);
        rtBar = (RatingBar) findViewById(R.id.ratingBar);
        btnSubmit = (MyFontButton) findViewById(R.id.btnSubmit);
        ivDriverImage = (ImageView) findViewById(R.id.ivDriverImage);
        tvDistance = (MyFontTextView) findViewById(R.id.tvDistance);
        tvTime = (MyFontTextView) findViewById(R.id.tvTime);
        // tvFeedbackAmount = (TextView)
        // view.findViewById(R.id.tvFeedbackAmount);

        //need to work on This
        requestQueue = Volley.newRequestQueue(this);
        pHelper = new PreferenceHelper(this);
        pContent = new ParseContent(this);
        //	activity.btnNotification.setVisibility(View.GONE);
        btnSubmit.setOnClickListener(this);
    }

    public void showBillDialog(ArrayList<TypeInvoice> listTypeInvoice,
                               String total, String promoBouns, String referralBouns,
                               String btnTitle) {
        final Dialog mDialog = new Dialog(this,
                android.R.style.Theme_Translucent_NoTitleBar);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        mDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setContentView(R.layout.bill_layout);
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        DecimalFormat perHourFormat = new DecimalFormat("0.0");
        String totalTmp = String.valueOf(decimalFormat.format(Double
                .parseDouble(total)));

        ListView lvSelectedType = (ListView) mDialog
                .findViewById(R.id.lvSelectedType);
        TypeInvoiceAdapter adapter = new TypeInvoiceAdapter(this,
                listTypeInvoice, Double.parseDouble(decimalFormat.format(Double
                .parseDouble(referralBouns))),
                Double.parseDouble(decimalFormat.format(Double
                        .parseDouble(promoBouns))));
        lvSelectedType.setAdapter(adapter);


        ((MyTitleFontTextView) mDialog.findViewById(R.id.tvTotal1)).setText(totalTmp);

        MyFontButton btnConfirm = (MyFontButton) mDialog
                .findViewById(R.id.btnBillDialogClose);
        if (!TextUtils.isEmpty(btnTitle)) {
            btnConfirm.setText(btnTitle);
        }
        btnConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        mDialog.setCancelable(true);
        mDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSubmit:
                if (isValidate()) {
                    rating();
                } else
                    AndyUtils.showToast(
                            this.getString(R.string.error_empty_rating),
                            this);
                break;
            default:
                break;
        }
    }

    private void rating() {
        AndyUtils.showCustomProgressDialog(this,
                getString(R.string.text_rating), false, null);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.RATING);
        map.put(Const.Params.TOKEN, pHelper.getSessionToken());
        map.put(Const.Params.ID, new PreferenceHelper(this).getUserId());
        map.put(Const.Params.COMMENT, etComment.getText().toString());
        map.put(Const.Params.RATING, String.valueOf(((int) rtBar.getRating())));
        map.put(Const.Params.REQUEST_ID,
                String.valueOf(pHelper.getRequestId()));
        // new HttpRequester(activity, map, Const.ServiceCode.RATING, this);
        requestQueue.add(new VolleyHttpRequest(Request.Method.POST, map,
                Const.ServiceCode.RATING, this, this));
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {

    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        switch (serviceCode) {
            case Const.ServiceCode.RATING:
                AndyUtils.removeCustomProgressDialog();
                if (pContent.isSuccess(response)) {
                    pHelper.clearRequestData();
                    AndyUtils.showToast(
                            getString(R.string.text_feedback_completed), this);
                    gotoChooseYourNok();
                }
                break;
        }
    }

    public void gotoChooseYourNok() {
        Intent i = new Intent(Feedback.this, ChooseYourNok.class);
        startActivity(i);
        // close this activity
        finish();
    }
}
