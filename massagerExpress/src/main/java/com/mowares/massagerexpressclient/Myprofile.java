package com.mowares.massagerexpressclient;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mowares.massagerexpressclient.component.MyTitleFontTextView;
import com.mowares.massagerexpressclient.utils.Const;

public class Myprofile extends BaseActivity implements View.OnClickListener {
    private ImageButton btnActionBack;
    protected MyTitleFontTextView txtTitle;
    private ImageView ivCreditCardDetail, ivHistory, ivCustomerCare, ivTos;
    private LinearLayout llCreditCardDetail, llHistory, llCustomerCare, llTos, llNok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprofile);
        Const.menu_myProfile = 0;
        Const.menu_newNok = 1;
        Const.menu_customerService = 1;
        Const.menu_getReady = 1;
        Const.menu_futureNok = 1;
        Const.menu_mynok = 1;
        InitUI();
        txtTitle.setText("My profile");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Const.menu_myProfile = 0;
        Const.menu_newNok = 1;
        Const.menu_customerService = 1;
        Const.menu_getReady = 1;
        Const.menu_futureNok = 1;
        Const.menu_mynok = 1;
        selectedPosition = -1;
    }

    private void InitUI() {
        txtTitle = (MyTitleFontTextView) findViewById(R.id.tvTitle);
        ivCreditCardDetail = (ImageView) findViewById(R.id.ivCreditCardDetail);
        ivHistory = (ImageView) findViewById(R.id.ivHistory);
        ivCustomerCare = (ImageView) findViewById(R.id.ivCustomerCare);
        ivTos = (ImageView) findViewById(R.id.ivTos);
        btnActionBack = (ImageButton) findViewById(R.id.btnActionBack);
        btnActionMenu = (ImageButton) findViewById(R.id.btnActionMenu);
        llCreditCardDetail = (LinearLayout) findViewById(R.id.llCreditCardDetail);
        llHistory = (LinearLayout) findViewById(R.id.llHistory);
        llCustomerCare = (LinearLayout) findViewById(R.id.llCustomerCare);
        llNok = (LinearLayout) findViewById(R.id.llNok);
        llTos = (LinearLayout) findViewById(R.id.llTos);
        llCreditCardDetail.setOnClickListener(this);
        llHistory.setOnClickListener(this);
        llCustomerCare.setOnClickListener(this);
        llTos.setOnClickListener(this);
        llNok.setOnClickListener(this);
        btnActionBack.setOnClickListener(this);
        btnActionMenu.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnActionBack:
                finish();
                break;
            case R.id.btnActionMenu:
                popupWindow.showAsDropDown(v, 0, 0);
                break;
            case R.id.llHistory:
                Intent history = new Intent(Myprofile.this, HistoryActivity.class);
                startActivity(history);
                break;
            case R.id.llCreditCardDetail:
                Intent paymentDetails = new Intent(Myprofile.this, UberViewPaymentActivity.class);
                startActivity(paymentDetails);
                break;
            case R.id.llCustomerCare:
                Intent customercare = new Intent(Myprofile.this, CustomerService.class);
                startActivity(customercare);
                break;
            case R.id.llTos:
                Intent termsofservice = new Intent(Myprofile.this, Termsofservice.class);
                startActivity(termsofservice);
                break;
            case R.id.llNok:
                Intent profileactivity = new Intent(Myprofile.this, ProfileActivity.class);
                startActivity(profileactivity);
                break;
            default:
                break;
        }
    }
}
