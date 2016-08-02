package com.mowares.massagerexpressclient;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.mowares.massagerexpressclient.adapter.ListServiceAdapter;
import com.mowares.massagerexpressclient.component.DividerItemDecoration;
import com.mowares.massagerexpressclient.component.MyFontButton;
import com.mowares.massagerexpressclient.component.MyTitleFontTextView;
import com.mowares.massagerexpressclient.models.Driver;
import com.mowares.massagerexpressclient.models.MyThings;
import com.mowares.massagerexpressclient.parse.AsyncTaskCompleteListener;
import com.mowares.massagerexpressclient.parse.ParseContent;
import com.mowares.massagerexpressclient.parse.VolleyHttpRequest;
import com.mowares.massagerexpressclient.utils.AndyUtils;
import com.mowares.massagerexpressclient.utils.AppLog;
import com.mowares.massagerexpressclient.utils.Const;
import com.mowares.massagerexpressclient.utils.LocationHelper;
import com.mowares.massagerexpressclient.utils.PreferenceHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class ListService extends BaseActivity implements Response.ErrorListener, AsyncTaskCompleteListener, LocationHelper.OnLocationReceived {

    protected MyTitleFontTextView txtTitle;
    protected MyFontButton btnWhenWhere;
    private LinearLayout llBackground;
    protected ListView lvServices;
    private RequestQueue requestQueue;
    public ParseContent pContent;
    PreferenceHelper preferenceHelper;
    protected ListServiceAdapter listServiceAdapter;
    protected ArrayList<MyThings> listMakeup;
    protected ArrayList<MyThings> listNails;
    protected ArrayList<MyThings> listHairs;
    private int ArrayType;
    private ArrayList<Driver> listDriver = new ArrayList<Driver>();
    private ImageButton btnActionBack;
    private int position;
    private AlertDialog locationAlertDialog;
    Context context;
    private LocationHelper locHelper;
    private Location myLocation;
    private LatLng curretLatLng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = ListService.this;
        setContentView(R.layout.activity_list_service);
        txtTitle = (MyTitleFontTextView) findViewById(R.id.tvTitle);
        preferenceHelper = new PreferenceHelper(this);
        pContent = new ParseContent(this);
        // socialId = preferenceHelper.getSocialId();
        //loginType = preferenceHelper.getLoginBy();
        requestQueue = Volley.newRequestQueue(this);
        locHelper = new LocationHelper(this);
        locHelper.setLocationReceivedLister(this);
        // token = preferenceHelper.getSessionToken();
        // id = preferenceHelper.getUserId();
        llBackground = (LinearLayout) findViewById(R.id.llBackground);
        btnActionBack = (ImageButton) findViewById(R.id.btnActionBack);
        btnWhenWhere = (MyFontButton) findViewById(R.id.btn_when_where);
        btnActionMenu = (ImageButton) findViewById(R.id.btnActionMenu);
        btnActionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.showAsDropDown(v, 0, 0);
            }
        });
        txtTitle.setText("What's your NOK?");
        Bundle bundle = getIntent().getExtras();
        ArrayType = bundle.getInt("check_array");

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showLocationOffDialog();
        }
        if (ArrayType == 1) {
            listMakeup = (ArrayList<MyThings>) bundle.getSerializable("subTypeMakeup");
            listServiceAdapter = new ListServiceAdapter(ListService.this, listMakeup);

            btnWhenWhere.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    position = listServiceAdapter.getSelectedRadioButtonPosition();
                    if (position > -1) {
                        getProvidersSubtype(curretLatLng, Const.ServiceCode.GET_PROVIDERS_SUBTYPE);
                        btnWhenWhere.setBackgroundColor(Color.BLACK);
                        btnWhenWhere.setTextColor(Color.WHITE);
                       /* Intent mapActivity = new Intent(ListService.this, MapActivity.class);
                        mapActivity.putExtra("subTypeMakeup", listMakeup);
                        mapActivity.putExtra("check_array", 1);
                        startActivity(mapActivity);*/
                    } else if (position == -1) {
                        Toast.makeText(context, "Choose one Service", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        if (ArrayType == 2) {
            llBackground.setBackgroundResource(R.drawable.choose_your_nailtreat);
            listNails = (ArrayList<MyThings>) bundle.getSerializable("subTypeNails");
            listServiceAdapter = new ListServiceAdapter(ListService.this, listNails);

            btnWhenWhere.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    position = listServiceAdapter.getSelectedRadioButtonPosition();
                    if (position > -1) {
                        getProvidersSubtype(curretLatLng, Const.ServiceCode.GET_PROVIDERS_SUBTYPE);
                        btnWhenWhere.setBackgroundColor(Color.BLACK);
                        btnWhenWhere.setTextColor(Color.WHITE);
                        /*Intent mapActivity = new Intent(ListService.this, MapActivity.class);
                        mapActivity.putExtra("subTypeNails", listNails);
                        mapActivity.putExtra("check_array", 2);
                        startActivity(mapActivity);*/
                    } else if (position == -1) {
                        Toast.makeText(context, "Choose one Service", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else if (ArrayType == 3) {
            llBackground.setBackgroundResource(R.drawable.choose_your_hairstyle);
            listHairs = (ArrayList<MyThings>) bundle.getSerializable("subTypeHairs");
            listServiceAdapter = new ListServiceAdapter(ListService.this, listHairs);
            btnWhenWhere.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    position = listServiceAdapter.getSelectedRadioButtonPosition();
                    if (position > -1) {
                        getProvidersSubtype(curretLatLng, Const.ServiceCode.GET_PROVIDERS_SUBTYPE);
                        btnWhenWhere.setBackgroundColor(Color.BLACK);
                        btnWhenWhere.setTextColor(Color.WHITE);
                        /*Intent mapActivity = new Intent(ListService.this, MapActivity.class);
                        mapActivity.putExtra("subTypeHairs", listHairs);
                        mapActivity.putExtra("check_array", 3);
                        startActivity(mapActivity);*/
                    } else if (position == -1) {
                        Toast.makeText(context, "Choose one Service", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        lvServices = (ListView) findViewById(R.id.lvServices);
        lvServices.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        lvServices.setAdapter(listServiceAdapter);
        lvServices.setDivider(new ColorDrawable(0xffffffff));
        lvServices.setDividerHeight(2);
        btnActionBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void showLocationOffDialog() {

        AlertDialog.Builder gpsBuilder = new AlertDialog.Builder(
                ListService.this);
        gpsBuilder.setCancelable(false);
        gpsBuilder
                .setTitle(getString(R.string.dialog_no_location_service_title))
                .setMessage(getString(R.string.dialog_no_location_service))
                .setPositiveButton(
                        getString(R.string.dialog_enable_location_service),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // continue with delete
                                dialog.dismiss();
                                Intent viewIntent = new Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(viewIntent);

                            }
                        })

                .setNegativeButton(getString(R.string.dialog_exit),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // do nothing
                                dialog.dismiss();
                                finish();
                            }
                        });
        locationAlertDialog = gpsBuilder.create();
        locationAlertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectedPosition = -1;
        Const.menu_myProfile = 1;
        Const.menu_newNok = 1;
        Const.menu_customerService = 1;
        Const.menu_getReady = 1;
        Const.menu_futureNok = 1;
        Const.menu_mynok = 1;
        btnWhenWhere.setBackgroundColor(getResources().getColor(R.color.color_onselect));
        btnWhenWhere.setTextColor(Color.BLACK);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        selectedPosition = -1;
        finish();
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
        map.put(Const.Params.DEVICE_TOKEN, preferenceHelper.getDeviceToken());
        map.put(Const.Params.LOGIN_BY, Const.MANUAL);
        map.put(Const.Params.PHONE, preferenceHelper.getNumber());
        // new HttpRequester(this, map, Const.ServiceCode.LOGIN, this);
        requestQueue.add(new VolleyHttpRequest(Request.Method.POST, map, Const.ServiceCode.LOGIN, this, this));
    }

    protected void getprovidersList(ArrayList<Driver> listProvider) {
        this.listDriver = listProvider;
        Intent mapActivity = new Intent(ListService.this, MapActivity.class);
        mapActivity.putExtra("listDriver", this.listDriver);
        if (ArrayType == 1) {
            mapActivity.putExtra("subTypeMakeup", listMakeup);
            mapActivity.putExtra("check_array", 1);
        }
        if (ArrayType == 2) {
            mapActivity.putExtra("subTypeNails", listNails);
            mapActivity.putExtra("check_array", 2);
        }
        if (ArrayType == 3) {
            mapActivity.putExtra("subTypeHairs", listHairs);
            mapActivity.putExtra("check_array", 3);
        }
        startActivity(mapActivity);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {

    }

    @Override
    public void onLocationReceived(LatLng latlong) {

    }

    @Override
    public void onLocationReceived(Location location) {
        if (location != null) {
            // drawTrip(latlong);
            myLocation = location;
        }
    }

    @Override
    public void onConntected(Bundle bundle) {

    }

    @Override
    public void onConntected(Location location) {
        if (location != null) {

            myLocation = location;

            // isLocationEnable = true;
            LatLng latLang = new LatLng(location.getLatitude(),
                    location.getLongitude());
            curretLatLng = latLang;
            //animateCameraToMarker(latLang, false);
        } else {
            showLocationOffDialog();
        }
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        switch (serviceCode) {
            case Const.ServiceCode.GET_PROVIDERS_SUBTYPE:
                AppLog.Log("subtype", "response" + response);
                if (pContent.isSuccess(response)) {
                    getprovidersList(pContent.getDriverList(response));

                } else if (pContent.getErrorCode(response) == Const.INVALID_TOKEN) {
                    AndyUtils.showCustomProgressDialog(this, getString(R.string.text_loading), true, null);
                    new ChooseYourNok().getRegId();
                } else {
                    Toast.makeText(this,
                            "No Providers found",
                            Toast.LENGTH_SHORT).show();
                }
                AndyUtils.removeCustomProgressDialog();
                break;

            case Const.ServiceCode.LOGIN:
                AppLog.Log("Login", "response" + response);
                if (pContent.isSuccessWithLoginID(response)) {
                    AndyUtils.removeCustomProgressDialog();
                    //*  getData();*//*
                    getProvidersSubtype(curretLatLng, Const.ServiceCode.GET_PROVIDERS_SUBTYPE);
                } else {
                    AndyUtils.removeCustomProgressDialog();
                    Intent OnboardinScreen = new Intent(ListService.this, OnBoardingActivity.class);
                    startActivity(OnboardinScreen);
                }
                break;
        }
    }

    private void getProvidersSubtype(LatLng latlang, int serviceCode) {
        try {

            if (!AndyUtils.isNetworkAvailable(this)) {
                AndyUtils.showToast(
                        getResources().getString(R.string.no_internet),
                        this);
                return;
            } else if (latlang == null) {
                Toast.makeText(this,
                        getString(R.string.text_location_not_found),
                        Toast.LENGTH_SHORT).show();
                return;
            }
            AndyUtils.showCustomProgressDialog(this,
                    getString(R.string.text_loading), false, null);
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(Const.URL, Const.ServiceType.GET_PROVIDERS_SUBTYPE);
            map.put(Const.Params.ID,
                    String.valueOf(preferenceHelper.getUserId()));
            AppLog.Log("TAG", "ID : " + String.valueOf(preferenceHelper.getUserId()));
            map.put(Const.Params.TOKEN,
                    String.valueOf(preferenceHelper.getSessionToken()));
            AppLog.Log("TAG", "token : " + String.valueOf(preferenceHelper.getSessionToken()));
            map.put(Const.Params.USER_LATITUDE,
                    String.valueOf(latlang.latitude));
            AppLog.Log("TAG", "latitude: " + String.valueOf(latlang.latitude));
            map.put(Const.Params.USER_LONGITUDE,
                    String.valueOf(latlang.longitude));
            AppLog.Log("TAG", "longitude : " + String.valueOf(latlang.longitude));
            map.put(Const.Params.SUB_TYPE,
                    String.valueOf(preferenceHelper.getSubtypeId()));
            AppLog.Log("TAG", "subtype: " + String.valueOf(preferenceHelper.getSubtypeId()));
            // new HttpRequester(activity, map, Const.ServiceCode.GET_PROVIDERS,
            // this);
            requestQueue.add(new VolleyHttpRequest(Request.Method.POST, map,
                    serviceCode, this, this));
        } catch (Exception e) {
            AppLog.Log("TAG", "getAllProviderException : " + e);
        }
    }

}
