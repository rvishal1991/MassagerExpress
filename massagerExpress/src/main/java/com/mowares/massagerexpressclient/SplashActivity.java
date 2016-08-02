package com.mowares.massagerexpressclient;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mowares.massagerexpressclient.utils.AndyUtils;
import com.mowares.massagerexpressclient.utils.PreferenceHelper;

import java.io.IOException;

import io.fabric.sdk.android.Fabric;

public class SplashActivity extends AppCompatActivity {
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1000;
    private PreferenceHelper preferenceHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* Fabric.with(this, new Crashlytics());*/
        setContentView(R.layout.activity_splash);
        preferenceHelper = new PreferenceHelper(this);
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    givePerMisson();
                } else {
                    requestIntent();
                }
              /*  if (preferenceHelper.isLoggedIn()) {
                    gotoChooseYourNok();
                } else {
                    gotoBoardingScreen();
                }*/
            }

        }, SPLASH_TIME_OUT);
    }

    public void requestIntent() {
        if (preferenceHelper.isLoggedIn()) {
            gotoChooseYourNok();
        } else {
            gotoBoardingScreen();
        }

    }
    public void gotoChooseYourNok() {
        Intent i = new Intent(SplashActivity.this, ChooseYourNok.class);
        startActivity(i);
        // close this activity
        finish();
    }

    public void gotoBoardingScreen() {
        Intent i = new Intent(SplashActivity.this, OnBoardingActivity.class);
        startActivity(i);
        // close this activity
        finish();
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void givePerMisson() {
        if ((AndyUtils.checkPermission(SplashActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) &&
                AndyUtils.checkPermission(SplashActivity.this, Manifest.permission.CALL_PHONE) &&
                AndyUtils.checkPermission(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                AndyUtils.checkPermission(SplashActivity.this, Manifest.permission.GET_ACCOUNTS) &&
                AndyUtils.checkPermission(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) &&
                AndyUtils.checkPermission(SplashActivity.this, Manifest.permission.CAMERA)
        )) {
            requestIntent();
        } else {
            AndyUtils.givePermisson(SplashActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION, AndyUtils.PERMISSOIN);
            AndyUtils.givePermisson(SplashActivity.this, Manifest.permission.CALL_PHONE, AndyUtils.PERMISSOIN);
            AndyUtils.givePermisson(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, AndyUtils.PERMISSOIN);
            AndyUtils.givePermisson(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION, AndyUtils.PERMISSOIN);
            AndyUtils.givePermisson(SplashActivity.this, Manifest.permission.GET_ACCOUNTS, AndyUtils.PERMISSOIN);
            AndyUtils.givePermisson(SplashActivity.this, Manifest.permission.CAMERA, AndyUtils.PERMISSOIN);
            if (!AndyUtils.permissionsList.isEmpty()) {
                requestPermissions(AndyUtils.permissionsList.toArray(new String[AndyUtils.permissionsList.size()]), AndyUtils.PERMISSOIN_CODE);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AndyUtils.PERMISSOIN_CODE: {
                if ((AndyUtils.checkPermission(SplashActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) &&
                        AndyUtils.checkPermission(SplashActivity.this, Manifest.permission.CALL_PHONE) &&
                        AndyUtils.checkPermission(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                        AndyUtils.checkPermission(SplashActivity.this, Manifest.permission.GET_ACCOUNTS) &&
                        AndyUtils.checkPermission(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) &&
                        AndyUtils.checkPermission(SplashActivity.this, Manifest.permission.CAMERA)
                )) {
                    requestIntent();
                } else {
                    finish();
                }
            }
        }
    }
}