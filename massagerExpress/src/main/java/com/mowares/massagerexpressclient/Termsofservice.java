package com.mowares.massagerexpressclient;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.mowares.massagerexpressclient.component.MyTitleFontTextView;

public class Termsofservice extends BaseActivity implements View.OnClickListener {
    private ImageButton btnActionBack, btnActionMenu;
    protected MyTitleFontTextView txtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termsofservice);
        getWindow().setBackgroundDrawableResource(R.drawable.terms_of_service);
        txtTitle = (MyTitleFontTextView) findViewById(R.id.tvTitle);
        txtTitle.setText("My profile");
        btnActionBack = (ImageButton) findViewById(R.id.btnActionBack);
        btnActionMenu = (ImageButton) findViewById(R.id.btnActionMenu);
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
            default:
                break;
        }
    }
}
