package com.mowares.massagerexpressclient;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.mowares.massagerexpressclient.component.MyTitleFontTextView;
import com.mowares.massagerexpressclient.utils.Const;

/**
 * Created by mobilefirst on 8/4/16.
 */
public class CustomerService extends BaseActivity implements View.OnClickListener {
    private ImageButton btnActionback, btnActionMenu;
    private MyTitleFontTextView txtTitle;
    LinearLayout llSendMail, llCallCustomerCare;
    private String Number = "035492493";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        Const.menu_myProfile = 1;
        Const.menu_newNok = 1;
        Const.menu_customerService = 0;
        Const.menu_getReady = 1;
        Const.menu_futureNok = 1;
        Const.menu_mynok = 1;
        txtTitle = (MyTitleFontTextView) findViewById(R.id.tvTitle);
        llSendMail = (LinearLayout) findViewById(R.id.llSendMail);
        llCallCustomerCare = (LinearLayout) findViewById(R.id.llCallCare);
        txtTitle.setText("Customer Service");
        btnActionback = (ImageButton) findViewById(R.id.btnActionBack);
        btnActionMenu = (ImageButton) findViewById(R.id.btnActionMenu);
        btnActionback.setOnClickListener(this);
        btnActionMenu.setOnClickListener(this);
        llCallCustomerCare.setOnClickListener(this);
        llSendMail.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Const.menu_myProfile = 1;
        Const.menu_newNok = 1;
        Const.menu_customerService = 0;
        Const.menu_getReady = 1;
        Const.menu_futureNok = 1;
        Const.menu_mynok = 1;
        selectedPosition = -1;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnActionMenu:
                popupWindow.showAsDropDown(v, 0, 0);
                break;
            case R.id.btnActionBack:
                Const.menu_myProfile = 1;
                Const.menu_newNok = 1;
                Const.menu_customerService = 1;
                Const.menu_getReady = 1;
                Const.menu_futureNok = 1;
                Const.menu_mynok = 1;
                selectedPosition = -1;
                finish();
                break;
            case R.id.llSendMail:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"arya@agiletree.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "subject");
                intent.putExtra(Intent.EXTRA_TEXT, "mail body");
                startActivity(Intent.createChooser(intent, ""));
                break;
            case R.id.llCallCare:
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"
                        + Number));
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(callIntent);
                break;
            default:
                break;
        }
    }
}
