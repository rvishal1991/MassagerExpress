package com.mowares.massagerexpressclient;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mowares.massagerexpressclient.adapter.ChooseYourNokAdapter;
import com.mowares.massagerexpressclient.component.DividerItemDecoration;
import com.mowares.massagerexpressclient.component.MyFontTextView;
import com.mowares.massagerexpressclient.component.MyTitleFontTextView;
import com.mowares.massagerexpressclient.models.MyThings;
import com.mowares.massagerexpressclient.parse.AsyncTaskCompleteListener;
import com.mowares.massagerexpressclient.parse.HttpRequester;
import com.mowares.massagerexpressclient.parse.ParseContent;
import com.mowares.massagerexpressclient.parse.VolleyHttpRequest;
import com.mowares.massagerexpressclient.utils.AndyUtils;
import com.mowares.massagerexpressclient.utils.AppLog;
import com.mowares.massagerexpressclient.utils.Const;
import com.mowares.massagerexpressclient.utils.PreferenceHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ChooseYourNok extends BaseActivity implements AsyncTaskCompleteListener, Response.ErrorListener, View.OnClickListener {
    protected RecyclerView rvBanners;
    protected MyTitleFontTextView txtTitle;
    protected MyFontTextView txtNoProviders;
    private ImageButton btnActionBack, btnActionMenu;
    private RequestQueue requestQueue;
    private PreferenceHelper pHelper;
    public GoogleCloudMessaging gcm;
    public String regid;
    private ParseContent pContent;
    private ArrayList<MyThings> subTypeMakeUp = new ArrayList<>();
    private ArrayList<MyThings> subTypeHairs = new ArrayList<>();
    private ArrayList<MyThings> subTypeNails = new ArrayList<>();
    private ArrayList<MyThings> listWalkerMakeup = new ArrayList<>();
    private ArrayList<MyThings> listWalkerHairs = new ArrayList<>();
    private ArrayList<MyThings> listWalkerNails = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_your_nok);
        Const.menu_myProfile = 1;
        Const.menu_newNok = 0;
        Const.menu_customerService = 1;
        Const.menu_getReady = 1;
        Const.menu_futureNok = 1;
        Const.menu_mynok = 1;
        txtTitle = (MyTitleFontTextView) findViewById(R.id.tvTitle);
        txtTitle.setText("What's your NOK?");
        requestQueue = Volley.newRequestQueue(this);
        pHelper = new PreferenceHelper(this);
        pContent = new ParseContent(this);
        rvBanners = (RecyclerView) findViewById(R.id.rvBanners);
        btnActionBack = (ImageButton) findViewById(R.id.btnActionBack);
        btnActionMenu = (ImageButton) findViewById(R.id.btnActionMenu);
        btnActionBack.setVisibility(View.GONE);
        btnActionMenu.setOnClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvBanners.setLayoutManager(layoutManager);
      //  rvBanners.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        getRegId();
        getTypes();
    }
    public void getRegId() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String gcm_token = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(ChooseYourNok.this);
                    }
                    // InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
                    regid = gcm.register(CommonUtilities.SENDER_ID);

                    //   String GcmToken = instanceID.getToken(Config.GOOGLE_PROJECT_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    gcm_token = "" + regid;
                    Log.i("GCM", gcm_token);
                    //  Log.i("GCMASDF", GcmToken);

                    /*AndroidDeviceID = msg;*/

                } catch (IOException ex) {
                    Log.e("ErrorGCM", ex.getMessage());

                }
                return gcm_token;
            }

            @Override
            protected void onPostExecute(String msg) {
                login();
            }
        }.execute(null, null, null);
    }

    public void login() {
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
        map.put("gcm_token",regid);
        // new HttpRequester(this, map, Const.ServiceCode.LOGIN, this);
       /* AndyUtils.showCustomProgressDialog(this, getString(R.string.text_loading), true, null);*/
        //new MultiPartRequester(activity, map, Const.ServiceCode.REGISTER, this);
        new HttpRequester(this, map, Const.ServiceCode.LOGIN, this);
        // requestQueue.add(new VolleyHttpRequest(Request.Method.POST, map, Const.ServiceCode.LOGIN, this, this));
    }

 /*   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_custom_indicator:
                *//*MenuInflater menuInflater = getMenuInflater();
                menuInflater.inflate(R.menu.main, menu);
                return super.onCreateOptionsMenu(menu);*//*
                //showPopup(item);
                return true;
            default:
                break;
        }
        return true;
    }*/

    @Override
    protected void onResume() {
        Const.menu_myProfile = 1;
        Const.menu_newNok = 0;
        Const.menu_customerService = 1;
        Const.menu_getReady = 1;
        Const.menu_futureNok = 1;
        Const.menu_mynok = 1;
        selectedPosition = -1;
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        selectedPosition = -1;
    }

    @Override
    public void onBackPressed() {
        exitAppMethod();
    }

   /* public void showPopup(View V) {
        View menuItemView = findViewById(R.id.action_custom_indicator);
        PopupMenu popup = new PopupMenu(this, V);
        MenuInflater inflate = popup.getMenuInflater();
        inflate.inflate(R.menu.main, popup.getMenu());
        popup.show();

    }*/

    public void exitAppMethod() {
        selectedPosition = -1;
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        AppLog.Log(Const.TAG, volleyError.getMessage());
    }

    /* private void getData() {
         if (!AndyUtils.isNetworkAvailable(this)) {
             AndyUtils.showToast(
                     getResources().getString(R.string.dialog_no_inter_message),
                     this);
             return;
         }
         HashMap<String, String> map = new HashMap<String, String>();
         map.put(Const.URL, Const.ServiceType.GET_PROVIDERSALL);
         map.put(Const.Params.ID, id);
         map.put(Const.Params.TOKEN, token);
         // String.valueOf(curretLatLng.latitude)String.valueOf(curretLatLng.longitude)39.92826080,  54.63038410
         map.put(Const.Params.LATITUDE, String.valueOf(curretLatLng.latitude));
         map.put(Const.Params.LONGITUDE, String.valueOf(curretLatLng.longitude));
         AndyUtils.showCustomProgressDialog(this,
                 getString(R.string.progress_loading), false, null);
         new HttpRequester(this, map, Const.ServiceCode.GET_PROVIDERS_LIST, this);
     }*/
    private void getTypes() {
        if (!AndyUtils.isNetworkAvailable(this)) {
            AndyUtils.showToast(
                    getResources().getString(R.string.dialog_no_inter_message),
                    this);
            return;
        }
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.GET_TYPE);
        AndyUtils.showCustomProgressDialog(this,
                getString(R.string.progress_loading), false, null);
        requestQueue.add(new VolleyHttpRequest(Request.Method.GET, map,
                Const.ServiceCode.GET_TYPE, this, this));

    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        switch (serviceCode) {

            case Const.ServiceCode.GET_PROVIDERS_LIST:

                AppLog.Log("response", "" + response);
                try {
                    if (!TextUtils.isEmpty(response)) {
                        if (new JSONObject(response).getBoolean("success")) {
                            JSONObject jObject = new JSONObject(response);
                            JSONArray jArray = jObject.getJSONArray("walkers");

                            MyThings beanConversationMakeup = new MyThings();
                            MyThings beanConversationHairs = new MyThings();
                            MyThings beanConversationNails = new MyThings();


                            for (int i = 0; i < jArray.length(); i++) {
                                JSONObject innerjObject = jArray.getJSONObject(i);
                                if (innerjObject.has("type")) {
                                    if (innerjObject.getString("type").equals("Makeup")) {
                                        beanConversationMakeup.WALKER_ID = innerjObject.getInt("id");
                                        beanConversationMakeup.WALKER_BGIMAGE = innerjObject.getString("service_image_1");
                                        beanConversationMakeup.WALKER_FGIMAGE = innerjObject.getString("service_image_2");
                                        beanConversationMakeup.WALKER_TYPE = innerjObject.getString("type");
                                        JSONArray subtypemakeup = innerjObject.getJSONArray("sub_type");
                                        if (subtypemakeup.length() > 0) {
                                            for (int j = 0; j < subtypemakeup.length(); j++) {
                                                JSONObject subtypes = subtypemakeup.getJSONObject(j);
                                                MyThings beanMakeupSubtype = new MyThings();
                                                beanMakeupSubtype.WALKER_ID = innerjObject.getInt("id");
                                                beanMakeupSubtype.WALKER_LATITUDE = innerjObject.getDouble("latitude");
                                                beanMakeupSubtype.WALKER_LONGITUDE = innerjObject.getDouble("longitude");
                                                beanMakeupSubtype.SUBTYPE_ID = subtypes.getInt("sub_id");
                                                beanMakeupSubtype.SUBTYPE_NAME = subtypes.getString("name");
                                                beanMakeupSubtype.SUBTYPE_ICON = subtypes.getString("icon");
                                                beanMakeupSubtype.BASE_DISTANCE = subtypes.getInt("base_distance");
                                                beanMakeupSubtype.BASE_PRICE = subtypes.getInt("base_price");
                                                beanMakeupSubtype.MAIN_TYPE = subtypes.getString("main_type");
                                                subTypeMakeUp.add(beanMakeupSubtype);
                                               /* AppLog.Log("listmakeup ","latitude"+beanMakeupSubtype.WALKER_LATITUDE);
                                                AppLog.Log("listmakeup ", "longitude" + beanMakeupSubtype.WALKER_LONGITUDE);*/
                                            }
                                        }
                                        listWalkerMakeup.add(beanConversationMakeup);
                                    } else if (innerjObject.getString("type").equals("Hairs")) {
                                        beanConversationHairs.WALKER_ID = innerjObject.getInt("id");
                                        beanConversationHairs.WALKER_BGIMAGE = innerjObject.getString("service_image_1");
                                        beanConversationHairs.WALKER_FGIMAGE = innerjObject.getString("service_image_2");
                                        beanConversationHairs.WALKER_TYPE = innerjObject.getString("type");
                                        JSONArray subtypeHairs = innerjObject.getJSONArray("sub_type");
                                        if (subtypeHairs.length() > 0) {
                                            for (int j = 0; j < subtypeHairs.length(); j++) {
                                                JSONObject subtypes = subtypeHairs.getJSONObject(j);
                                                MyThings beanHairsSubtype = new MyThings();
                                                beanHairsSubtype.WALKER_ID = innerjObject.getInt("id");
                                                beanHairsSubtype.WALKER_LATITUDE = innerjObject.getDouble("latitude");
                                                beanHairsSubtype.WALKER_LONGITUDE = innerjObject.getDouble("longitude");
                                                beanHairsSubtype.SUBTYPE_ID = subtypes.getInt("sub_id");
                                                beanHairsSubtype.SUBTYPE_NAME = subtypes.getString("name");
                                                beanHairsSubtype.SUBTYPE_ICON = subtypes.getString("icon");
                                                beanHairsSubtype.BASE_DISTANCE = subtypes.getInt("base_distance");
                                                beanHairsSubtype.BASE_PRICE = subtypes.getInt("base_price");
                                                beanHairsSubtype.MAIN_TYPE = subtypes.getString("main_type");
                                                subTypeHairs.add(beanHairsSubtype);
                                            }
                                        }
                                        listWalkerHairs.add(beanConversationHairs);
                                    } else if (innerjObject.getString("type").equals("Nails")) {
                                        beanConversationNails.WALKER_ID = innerjObject.getInt("id");
                                        beanConversationNails.WALKER_BGIMAGE = innerjObject.getString("service_image_1");
                                        beanConversationNails.WALKER_FGIMAGE = innerjObject.getString("service_image_2");
                                        beanConversationNails.WALKER_TYPE = innerjObject.getString("type");
                                        JSONArray subtypeNails = innerjObject.getJSONArray("sub_type");
                                        if (subtypeNails.length() > 0) {
                                            for (int j = 0; j < subtypeNails.length(); j++) {
                                                JSONObject subtypes = subtypeNails.getJSONObject(j);
                                                MyThings beanNailsSubtype = new MyThings();
                                                beanNailsSubtype.WALKER_ID = innerjObject.getInt("id");
                                                beanNailsSubtype.WALKER_LATITUDE = innerjObject.getDouble("latitude");
                                                beanNailsSubtype.WALKER_LONGITUDE = innerjObject.getDouble("longitude");
                                                beanNailsSubtype.SUBTYPE_ID = subtypes.getInt("sub_id");
                                                beanNailsSubtype.SUBTYPE_NAME = subtypes.getString("name");
                                                beanNailsSubtype.SUBTYPE_ICON = subtypes.getString("icon");
                                                beanNailsSubtype.BASE_DISTANCE = subtypes.getInt("base_distance");
                                                beanNailsSubtype.BASE_PRICE = subtypes.getInt("base_price");
                                                beanNailsSubtype.MAIN_TYPE = subtypes.getString("main_type");
                                                subTypeNails.add(beanNailsSubtype);
                                            }
                                        }
                                        listWalkerNails.add(beanConversationNails);
                                    }

                                }
                            }
                            int i = 0;
                            if (listWalkerNails.size() > 0) {
                                i = i + 1;
                            }
                            if (listWalkerMakeup.size() > 0) {
                                i = i + 1;
                            }
                            if (listWalkerHairs.size() > 0) {
                                i = i + 1;
                            }
                            int AdapterLength = i;
                            ChooseYourNokAdapter recyclerViewAdapter = new ChooseYourNokAdapter(ChooseYourNok.this,
                                    listWalkerMakeup, listWalkerHairs, listWalkerNails,
                                    subTypeMakeUp, subTypeHairs, subTypeNails, AdapterLength);
                            AndyUtils.removeCustomProgressDialog();
                            rvBanners.setAdapter(recyclerViewAdapter);
                        }/* else if (pContent.getErrorCode(response) == Const.INVALID_TOKEN) {
                            AndyUtils.showCustomProgressDialog(this, getString(R.string.text_loading), true, null);
                            login();
                        } */ else {
                            AndyUtils.removeCustomProgressDialog();
                            txtNoProviders = (MyFontTextView) findViewById(R.id.txtNoProviders);
                            rvBanners.setVisibility(View.GONE);
                            txtNoProviders.setVisibility(View.VISIBLE);
                        }
                    } else {
                        AndyUtils.removeCustomProgressDialog();
                        Toast.makeText(getApplicationContext(), "Internet is slow Retry again", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                break;
            case Const.ServiceCode.LOGIN:
                AppLog.Log("login", "res" + response);
                if (pContent.isSuccessWithLoginID(response)) {

                } else {
                    Intent OnboardinScreen = new Intent(ChooseYourNok.this, OnBoardingActivity.class);
                    startActivity(OnboardinScreen);
                }
                break;
            case Const.ServiceCode.GET_TYPE:
                AppLog.Log("response", "" + response);
                try {
                    if (!TextUtils.isEmpty(response)) {
                        if (new JSONObject(response).getBoolean("success")) {
                            JSONObject jObject = new JSONObject(response);
                            JSONArray jArray = jObject.getJSONArray("result");

                            MyThings beanConversationMakeup = new MyThings();
                            MyThings beanConversationHairs = new MyThings();
                            MyThings beanConversationNails = new MyThings();


                            for (int i = 0; i < jArray.length(); i++) {
                                JSONObject innerjObject = jArray.getJSONObject(i);
                                if (innerjObject.has("name")) {
                                    if (innerjObject.getString("name").equals("Makeup")) {
                                        beanConversationMakeup.TYPE_ID = innerjObject.getInt("id");
                                        beanConversationMakeup.WALKER_BGIMAGE = innerjObject.getString("service_image_1");
                                        beanConversationMakeup.WALKER_FGIMAGE = innerjObject.getString("service_image_2");
                                        beanConversationMakeup.TYPE_NAME = innerjObject.getString("name");
                                        JSONArray subtypemakeup = innerjObject.getJSONArray("sub_type");
                                        if (subtypemakeup.length() > 0) {
                                            for (int j = 0; j < subtypemakeup.length(); j++) {
                                                JSONObject subtypes = subtypemakeup.getJSONObject(j);
                                                MyThings beanMakeupSubtype = new MyThings();
                                               /* beanMakeupSubtype.WALKER_ID = innerjObject.getInt("id");
                                                beanMakeupSubtype.WALKER_LATITUDE = innerjObject.getDouble("latitude");
                                                beanMakeupSubtype.WALKER_LONGITUDE = innerjObject.getDouble("longitude");*/
                                                beanMakeupSubtype.SUBTYPE_ID = subtypes.getInt("sub_id");
                                                beanMakeupSubtype.SUBTYPE_NAME = subtypes.getString("name");
                                                beanMakeupSubtype.SUBTYPE_ICON = subtypes.getString("icon");
                                                beanMakeupSubtype.BASE_DISTANCE = subtypes.getInt("base_distance");
                                                beanMakeupSubtype.BASE_PRICE = subtypes.getInt("base_price");
                                                beanMakeupSubtype.MAIN_TYPE = subtypes.getString("main_type");
                                                subTypeMakeUp.add(beanMakeupSubtype);
                                               /* AppLog.Log("listmakeup ","latitude"+beanMakeupSubtype.WALKER_LATITUDE);
                                                AppLog.Log("listmakeup ", "longitude" + beanMakeupSubtype.WALKER_LONGITUDE);*/
                                            }
                                        }
                                        listWalkerMakeup.add(beanConversationMakeup);
                                    } else if (innerjObject.getString("name").equals("Hairs")) {
                                        beanConversationHairs.TYPE_ID = innerjObject.getInt("id");
                                        beanConversationHairs.WALKER_BGIMAGE = innerjObject.getString("service_image_1");
                                        beanConversationHairs.WALKER_FGIMAGE = innerjObject.getString("service_image_2");
                                        beanConversationHairs.TYPE_NAME = innerjObject.getString("name");
                                        JSONArray subtypeHairs = innerjObject.getJSONArray("sub_type");
                                        if (subtypeHairs.length() > 0) {
                                            for (int j = 0; j < subtypeHairs.length(); j++) {
                                                JSONObject subtypes = subtypeHairs.getJSONObject(j);
                                                MyThings beanHairsSubtype = new MyThings();
                                                /*beanHairsSubtype.WALKER_ID = innerjObject.getInt("id");
                                                beanHairsSubtype.WALKER_LATITUDE = innerjObject.getDouble("latitude");
                                                beanHairsSubtype.WALKER_LONGITUDE = innerjObject.getDouble("longitude");*/
                                                beanHairsSubtype.SUBTYPE_ID = subtypes.getInt("sub_id");
                                                beanHairsSubtype.SUBTYPE_NAME = subtypes.getString("name");
                                                beanHairsSubtype.SUBTYPE_ICON = subtypes.getString("icon");
                                                beanHairsSubtype.BASE_DISTANCE = subtypes.getInt("base_distance");
                                                beanHairsSubtype.BASE_PRICE = subtypes.getInt("base_price");
                                                beanHairsSubtype.MAIN_TYPE = subtypes.getString("main_type");
                                                subTypeHairs.add(beanHairsSubtype);
                                            }
                                        }
                                        listWalkerHairs.add(beanConversationHairs);
                                    } else if (innerjObject.getString("name").equals("Nails")) {
                                        beanConversationNails.TYPE_ID = innerjObject.getInt("id");
                                        beanConversationNails.WALKER_BGIMAGE = innerjObject.getString("service_image_1");
                                        beanConversationNails.WALKER_FGIMAGE = innerjObject.getString("service_image_2");
                                        beanConversationNails.TYPE_NAME = innerjObject.getString("name");
                                        JSONArray subtypeNails = innerjObject.getJSONArray("sub_type");
                                        if (subtypeNails.length() > 0) {
                                            for (int j = 0; j < subtypeNails.length(); j++) {
                                                JSONObject subtypes = subtypeNails.getJSONObject(j);
                                                MyThings beanNailsSubtype = new MyThings();
                                                /*beanNailsSubtype.WALKER_ID = innerjObject.getInt("id");
                                                beanNailsSubtype.WALKER_LATITUDE = innerjObject.getDouble("latitude");
                                                beanNailsSubtype.WALKER_LONGITUDE = innerjObject.getDouble("longitude");*/
                                                beanNailsSubtype.SUBTYPE_ID = subtypes.getInt("sub_id");
                                                beanNailsSubtype.SUBTYPE_NAME = subtypes.getString("name");
                                                beanNailsSubtype.SUBTYPE_ICON = subtypes.getString("icon");
                                                beanNailsSubtype.BASE_DISTANCE = subtypes.getInt("base_distance");
                                                beanNailsSubtype.BASE_PRICE = subtypes.getInt("base_price");
                                                beanNailsSubtype.MAIN_TYPE = subtypes.getString("main_type");
                                                subTypeNails.add(beanNailsSubtype);
                                            }
                                        }
                                        listWalkerNails.add(beanConversationNails);
                                    }

                                }
                            }
                            int i = 0;
                            if (listWalkerNails.size() > 0) {
                                i = i + 1;
                            }
                            if (listWalkerMakeup.size() > 0) {
                                i = i + 1;
                            }
                            if (listWalkerHairs.size() > 0) {
                                i = i + 1;
                            }
                            int AdapterLength = i;
                            ChooseYourNokAdapter recyclerViewAdapter = new ChooseYourNokAdapter(ChooseYourNok.this,
                                    listWalkerMakeup, listWalkerHairs, listWalkerNails,
                                    subTypeMakeUp, subTypeHairs, subTypeNails, AdapterLength);
                            AndyUtils.removeCustomProgressDialog();
                            rvBanners.setAdapter(recyclerViewAdapter);
                        } /*else if (pContent.getErrorCode(response) == Const.INVALID_TOKEN) {
                            AndyUtils.showCustomProgressDialog(this, getString(R.string.text_loading), true, null);
                            login();
                        }*/
                    } else {
                        AndyUtils.removeCustomProgressDialog();
                        Toast.makeText(getApplicationContext(), "Internet is slow Retry again", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnActionMenu:
                popupWindow.showAsDropDown(v, 0, 0);
                return;
        }
    }
}