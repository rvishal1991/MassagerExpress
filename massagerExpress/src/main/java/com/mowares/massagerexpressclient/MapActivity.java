package com.mowares.massagerexpressclient;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mowares.massagerexpressclient.adapter.DriverListAdapter;
import com.mowares.massagerexpressclient.adapter.PlacesAutoCompleteAdapter;
import com.mowares.massagerexpressclient.component.MyFontButton;
import com.mowares.massagerexpressclient.component.MyFontTextView;
import com.mowares.massagerexpressclient.component.MyStyleButton;
import com.mowares.massagerexpressclient.component.MyTitleFontTextView;
import com.mowares.massagerexpressclient.interfaces.OnProgressCancelListener;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MapActivity extends BaseActivity implements View.OnClickListener, LocationHelper.OnLocationReceived, Response.ErrorListener, AsyncTaskCompleteListener, OnProgressCancelListener {
    protected MyTitleFontTextView txtTitle;
    private boolean isLocationFound;
    public static boolean isMapTouched = false;
    private ImageButton btnActionBack, btnActionMenu;
    private FrameLayout mapFrameLayout;
    private GoogleMap map;
    private LocationHelper locHelper;
    private Location myLocation;
    private float currentZoom = -1;
    private LatLng curretLatLng;
    public int HoursSelectedIndex = -1;
    private MainDrawerActivity activity;
    private PlacesAutoCompleteAdapter adapter;
    private AutoCompleteTextView etSource, etFilterByName;
    private String strAddress = null;
    private boolean isContinueRequest;
    private boolean isLater = false;
    private ArrayList<String> stringArrayList = new ArrayList<String>();
    private Timer timer;
    /*private WalkerStatusReceiver walkerReceiver;*/
    private MyTitleFontTextView etName, etDesc, etdetail;
    protected ArrayList<MyThings> listMakeup;
    protected ArrayList<MyThings> listNails;
    protected ArrayList<MyThings> listHairs;
    private int ArrayType;
    private DriverListLaterAdapter adapterDriver;
    private Driverlist_Filterable adapterFilterDriver;
    private ArrayList<Driver> listDriver = new ArrayList<Driver>();
    private ArrayList<Driver> listDriverLater = new ArrayList<Driver>();
    private ArrayList<String> slotsid = new ArrayList<>();
    protected Bundle bundle;
    private ListView lvProviders;
    private RequestQueue requestQueue;
    private PreferenceHelper pHelper;
    private ParseContent pContent;
    private MyStyleButton btn_NokNow, btn_NokLater, btn_FindYourNok, btn_bookLater;
    private LinearLayout llProviderDetail, llProviderList, llFilterByName, llSetHourDate, llHour, llDate, llNok;
    private RatingBar rtbProviderRating;
    private ImageView ImgProviderPic;
    private ImageOptions imageOptions;
    private int listposition = -1;
    private AQuery aQuery;
    private int day, month, year;
    Calendar cal = Calendar.getInstance();
    private MyFontTextView etHour, etDate;
    private DatePickerDialog NoklaterDatePicker;
    CharSequence[] ArrayYears;
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  mBundle = savedInstanceState;
        setContentView(R.layout.activity_map);
        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
        }
        StrictMode.setThreadPolicy(policy);
        isLocationFound = false;
        bundle = getIntent().getExtras();
        IntentFilter filter = new IntentFilter(Const.INTENT_WALKER_STATUS);
        /*walkerReceiver = new WalkerStatusReceiver();
        LocalBroadcastManager.getInstance(MapActivity.this).registerReceiver(
                walkerReceiver, filter);*/
        ArrayType = bundle.getInt("check_array");
        InitUI();
        btnActionBack.setOnClickListener(this);
        txtTitle.setText("When & Where");
        mapFrameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN | MotionEvent.ACTION_MOVE:
                        MapActivity.isMapTouched = true;
                        // layoutMarker.setVisibility(View.GONE);
                        Log.e("Map", "Touch sdf -------------------------");
                        break;

                    case MotionEvent.ACTION_UP:
                        MapActivity.isMapTouched = false;
                        Log.e("Map", "NoTouch sdf -------------------------");
                        break;
                }
                return true;
            }
        });
        //  mMapView = (MapView) findViewById(R.id.map);
        //  mMapView.onCreate(mBundle);
        /*if (ArrayType == 1)
        {
            listMakeup = (ArrayList<MyThings>) bundle.getSerializable("subTypeMakeup");
            for (int i = 0; i < listMakeup.size(); i++) {
                subtype_id = listMakeup.get(i).SUBTYPE_ID;
            }
        }*/
        adapter = new PlacesAutoCompleteAdapter(getApplicationContext(),
                R.layout.autocomplete_list_text);
        etSource.setAdapter(adapter);
        locHelper = new LocationHelper(this);
        locHelper.setLocationReceivedLister(this);
        setUpMapIfNeeded();
        locHelper.onStart();
        etSource.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                final String selectedDestPlace = adapter.getItem(arg2);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final LatLng latlng = getLocationFromAddress(selectedDestPlace);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                isMapTouched = true;
                                curretLatLng = latlng;
                                // setMarker(curretLatLng, isSource);
                                // setMarkerOnRoad(curretLatLng, curretLatLng);
                                getAllProviders(curretLatLng, Const.ServiceCode.GET_PROVIDERS_LIST);
                                animateCameraToMarker(curretLatLng, true);
                                //stopUpdateProvidersLoaction();

                            }
                        });
                    }
                }).start();
            }
        });
        showMarker();//shows markers by parsing intent data
        imageOptions = new ImageOptions();
        imageOptions.fileCache = true;
        imageOptions.memCache = true;
        imageOptions.fallback = R.drawable.default_user;

        etFilterByName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                if (listDriver.size() >= 1) {
                    AppLog.Log("constriant", "" + cs);
                    adapterFilterDriver.getFilter().filter(cs);
                    llProviderList.setVisibility(View.VISIBLE);
                    lvProviders.setAdapter(adapterFilterDriver);
                    adapterFilterDriver.notifyDataSetChanged();
                    if (!TextUtils.isEmpty(cs)) {
                    } else {

                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        llProviderDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent who_is_noking = new Intent(MapActivity.this, who_is_noking.class);
                who_is_noking.putExtra("position", listposition);
                who_is_noking.putExtra("is_taped", true);
                who_is_noking.putExtra("listDriver", listDriver);
                if (ArrayType == 1) {
                    who_is_noking.putExtra("ArrayType", 1);
                } else if (ArrayType == 2) {
                    who_is_noking.putExtra("ArrayType", 2);
                } else if (ArrayType == 3) {
                    who_is_noking.putExtra("ArrayType", 3);
                }
                startActivity(who_is_noking);
            }
        });
        if (pHelper.getRequestId() > -1) {
            startCheckingStatusUpdate();
        }
        // lvProviders.setAdapter(adapterDriver);
    }

    private void sendData() {
        AndyUtils.showCustomProgressDialog(MapActivity.this,
                getString(R.string.text_loading), false, null);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.CREATE_REQUEST_PROVIDER_LATER);
        map.put(Const.Params.TOKEN,
                pHelper.getSessionToken());
        map.put(Const.Params.ID, pHelper.getUserId());
        map.put(Const.Params.LATITUDE, String.valueOf(curretLatLng.latitude));
        map.put(Const.Params.LONGITUDE, String.valueOf(curretLatLng.longitude));
        map.put(Const.Params.PAYMENT_MODE, String.valueOf(pHelper.getPaymentMode()));
        if (ArrayType == 1) {
            pHelper.putArrayType(1);
            map.put(Const.Params.TYPE, "Makeup");
        } else if (ArrayType == 2) {
            pHelper.putArrayType(2);
            map.put(Const.Params.TYPE,
                    "Nails");
        } else if (ArrayType == 3) {
            pHelper.putArrayType(3);
            map.put(Const.Params.TYPE,
                    "Hairs");
        }
        map.put(Const.Params.SUB_TYPE, String.valueOf(pHelper.getSubtypeId()));
        pHelper.putSelectedId(listposition);
        map.put(Const.Params.PROVIDER_ID,
                String.valueOf(listDriverLater.get(listposition).getDriverId()));
        map.put("slot_id", slotsid.get(HoursSelectedIndex));
        map.put("later_date", etDate.getText().toString());
        requestQueue.add(new VolleyHttpRequest(Request.Method.POST, map,
                Const.ServiceCode.CREATE_REQUEST_PROVIDER_LATER, this, this));
    }

    public void alertMessage() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        if (etDate.getText().length() > 0 && etHour.getText().length() > 0)
                            sendData();
                           /* Toast.makeText(MapActivity.this, "Working on this. :)",
                                    Toast.LENGTH_LONG).show();*/
                        else Toast.makeText(MapActivity.this, "choose Date and time",
                                Toast.LENGTH_LONG).show();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        // do nothing

                        break;
                }
            }
        };
        if (listposition > -1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("Your selected provider is " + listDriver.get(listposition).getFirstName() + " " + listDriver.get(listposition).getLastName())
                    .setPositiveButton("Ok", dialogClickListener)
                    .setNegativeButton("Cancel", dialogClickListener).show();
        } else {
            Toast.makeText(MapActivity.this, "Choose provider first.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        map.clear();
      /*  LocalBroadcastManager.getInstance(MapActivity.this).unregisterReceiver(
                walkerReceiver);*/
        // activity.tvTitle.setVisibility(View.VISIBLE);
        // etSource.setVisibility(View.GONE);
    }

    private void InitUI() {
        btnActionBack = (ImageButton) findViewById(R.id.btnActionBack);
        btnActionMenu = (ImageButton) findViewById(R.id.btnActionMenu);
        btnActionMenu.setOnClickListener(this);
        mapFrameLayout = (FrameLayout) findViewById(R.id.mapFrameLayout);
        txtTitle = (MyTitleFontTextView) findViewById(R.id.tvTitle);
        etSource = (AutoCompleteTextView) findViewById(R.id.etEnterSouce);
        etName = (MyTitleFontTextView) findViewById(R.id.etName);
        etDesc = (MyTitleFontTextView) findViewById(R.id.etdesc);
        etdetail = (MyTitleFontTextView) findViewById(R.id.etdetail);
        etDate = (MyFontTextView) findViewById(R.id.etDate);
        etHour = (MyFontTextView) findViewById(R.id.etHour);
        etFilterByName = (AutoCompleteTextView) findViewById(R.id.etFilterByName);
        ImgProviderPic = (ImageView) findViewById(R.id.imgProviderPic);

        llProviderDetail = (LinearLayout) findViewById(R.id.llProviderDetail);
        llProviderList = (LinearLayout) findViewById(R.id.llProviderList);
        llFilterByName = (LinearLayout) findViewById(R.id.llFilterByName);
        llSetHourDate = (LinearLayout) findViewById(R.id.llSetHourDate);
        llHour = (LinearLayout) findViewById(R.id.llHour);
        llDate = (LinearLayout) findViewById(R.id.llDate);
        llNok = (LinearLayout) findViewById(R.id.llNOK);
        lvProviders = (ListView) findViewById(R.id.lvProviders);

        btn_FindYourNok = (MyStyleButton) findViewById(R.id.btn_find_your_nok);
        btn_NokLater = (MyStyleButton) findViewById(R.id.btn_nok_later);
        btn_bookLater = (MyStyleButton) findViewById(R.id.btn_bookLater);
        btn_NokNow = (MyStyleButton) findViewById(R.id.btn_nok_now);
        rtbProviderRating = (RatingBar) findViewById(R.id.rtbProviderRating);

        pHelper = new PreferenceHelper(this);
        pContent = new ParseContent(this);
        aQuery = new AQuery(this);
        requestQueue = Volley.newRequestQueue(this);
        btn_FindYourNok.setOnClickListener(this);
        btn_NokLater.setOnClickListener(this);
        btn_NokNow.setOnClickListener(this);
        llHour.setOnClickListener(this);
        llDate.setOnClickListener(this);
        btn_bookLater.setOnClickListener(this);
    }


    private void getAllProviders(LatLng latlang, int serviceCode) {
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
            AppLog.Log("TAG", "Provider lat : " + latlang.latitude + " Long :"
                    + latlang.longitude);
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(Const.URL, Const.ServiceType.GET_PROVIDERS);
            map.put(Const.Params.ID,
                    String.valueOf(pHelper.getUserId()));
            AppLog.Log("TAG", "ID : " + String.valueOf(pHelper.getUserId()));
            map.put(Const.Params.TOKEN,
                    String.valueOf(pHelper.getSessionToken()));
            AppLog.Log("TAG", "token : " + String.valueOf(pHelper.getSessionToken()));
            map.put(Const.Params.USER_LATITUDE,
                    String.valueOf(latlang.latitude));
            AppLog.Log("TAG", "latitude: " + String.valueOf(latlang.latitude));
            map.put(Const.Params.USER_LONGITUDE,
                    String.valueOf(latlang.longitude));
            AppLog.Log("TAG", "longitude : " + String.valueOf(latlang.longitude));
            map.put(Const.Params.SUB_TYPE,
                    String.valueOf(pHelper.getSubtypeId()));
            AppLog.Log("TAG", "subtype: " + String.valueOf(pHelper.getSubtypeId()));
            // new HttpRequester(activity, map, Const.ServiceCode.GET_PROVIDERS,
            // this);
            requestQueue.add(new VolleyHttpRequest(Request.Method.POST, map,
                    serviceCode, this, this));
        } catch (Exception e) {
            AppLog.Log("TAG", "getAllProviderException : " + e);
        }
    }

    protected Bitmap getImagefromurl(String path) {
        try {
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;//etFilterByName.setAdapter(adapterFilterDriver);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    protected void showMarker() {
        //  this.listDriver = listProvider;
        listDriver = (ArrayList<Driver>) bundle.getSerializable("listDriver");
        adapterFilterDriver = new Driverlist_Filterable(MapActivity.this, listDriver);
        adapterDriver = new DriverListLaterAdapter(MapActivity.this, listDriver);

        // etFilterByName.setAdapter(adapterFilterDriver);
        if (ArrayType == 1) {
            listMakeup = (ArrayList<MyThings>) bundle.getSerializable("subTypeMakeup");
            Bitmap image = getImagefromurl(listMakeup.get(0).SUBTYPE_ICON);
            for (int i = 0; i < listDriver.size(); i++) {

                AppLog.Log("provider", "lat" + listDriver.get(i).getLatitude());
                AppLog.Log("provider", "long" + listDriver.get(i).getLongitude());
                createMarker(listDriver.get(i).getLatitude(), listDriver.get(i).getLongitude(), image);
            }
            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    String markerId = marker.getId();
                    final int j = Integer.parseInt(markerId.substring(1));
                    listposition = j;
                    AppLog.Log("Selected position:", "" + listposition);
                    if (llFilterByName.getVisibility() == View.VISIBLE) {
                        llFilterByName.setVisibility(View.GONE);
                    }
                    if (llSetHourDate.getVisibility() == View.VISIBLE) {
                        llSetHourDate.setVisibility(View.GONE);
                        btn_bookLater.setVisibility(View.GONE);
                    }
                    if (llProviderList.getVisibility() == View.VISIBLE) {
                        llProviderList.setVisibility(View.GONE);
                    }
                    llProviderDetail.setVisibility(View.VISIBLE);
                    etName.setText(listDriver.get(j).getFirstName() + " " + listDriver.get(j).getLastName());
                    etDesc.setText(listDriver.get(j).getBio());
                    rtbProviderRating.setRating((float) listDriver.get(j).getRating());
                    aQuery.id(ImgProviderPic).progress(R.id.pBar)
                            .image(listDriver.get(j).getPicture(), imageOptions);
                    etdetail.setText(listDriver.get(j).getPhone());
                    llNok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listposition = j;
                            Intent who_is_noking = new Intent(MapActivity.this, who_is_noking.class);
                            who_is_noking.putExtra("position", listposition);
                            who_is_noking.putExtra("is_taped", true);
                            who_is_noking.putExtra("listDriver", listDriver);
                            if (ArrayType == 1) {
                                who_is_noking.putExtra("ArrayType", 1);
                            } else if (ArrayType == 2) {
                                who_is_noking.putExtra("ArrayType", 2);
                            } else if (ArrayType == 3) {
                                who_is_noking.putExtra("ArrayType", 3);
                            }
                            startActivity(who_is_noking);
                        }
                    });
                    return true;
                }
            });
            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if (llProviderDetail.getVisibility() == View.VISIBLE) {
                        llProviderDetail.setVisibility(View.GONE);
                    }
                    if (llProviderList.getVisibility() == View.VISIBLE) {
                        llProviderList.setVisibility(View.GONE);
                    }
                    if (llFilterByName.getVisibility() == View.VISIBLE) {
                        llFilterByName.setVisibility(View.GONE);
                        etFilterByName.setText("");
                    }
                    if (llSetHourDate.getVisibility() == View.VISIBLE) {
                        llSetHourDate.setVisibility(View.GONE);
                        btn_bookLater.setVisibility(View.GONE);
                    }
                    //AndyUtils.hideSoftKeyboard(MapActivity.this);
                    hideKeyboard(mapFrameLayout);
                }
            });


        } else if (ArrayType == 2) {
            listNails = (ArrayList<MyThings>) bundle.getSerializable("subTypeNails");
            Bitmap image = getImagefromurl(listNails.get(0).SUBTYPE_ICON);
            for (int i = 0; i < listDriver.size(); i++) {

                AppLog.Log("provider", "lat" + listDriver.get(i).getLatitude());
                AppLog.Log("provider", "long" + listDriver.get(i).getLongitude());
                createMarker(listDriver.get(i).getLatitude(), listDriver.get(i).getLongitude(), image);
                /*AppLog.Log("listNails ","latitude"+listNails.get(i).WALKER_LATITUDE);
                AppLog.Log("listNails ","longitude"+listNails.get(i).WALKER_LONGITUDE);*/
            }
            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    String markerId = marker.getId();
                    final int j = Integer.parseInt(markerId.substring(1));
                    listposition = j;
                    AppLog.Log("Selected position:", "" + listposition);
                    if (llFilterByName.getVisibility() == View.VISIBLE) {
                        llFilterByName.setVisibility(View.GONE);
                    }
                    if (llSetHourDate.getVisibility() == View.VISIBLE) {
                        llSetHourDate.setVisibility(View.GONE);
                        btn_bookLater.setVisibility(View.GONE);
                    }
                    if (llProviderList.getVisibility() == View.VISIBLE) {
                        llProviderList.setVisibility(View.GONE);
                    }
                    llProviderDetail.setVisibility(View.VISIBLE);
                    etName.setText(listDriver.get(j).getFirstName() + " " + listDriver.get(j).getLastName());
                    etDesc.setText(listDriver.get(j).getBio());
                    rtbProviderRating.setRating((float) listDriver.get(j).getRating());
                    etdetail.setText(listDriver.get(j).getPhone());
                    aQuery.id(ImgProviderPic).progress(R.id.pBar)
                            .image(listDriver.get(j).getPicture(), imageOptions);
                    llNok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listposition = j;
                            Intent who_is_noking = new Intent(MapActivity.this, who_is_noking.class);
                            who_is_noking.putExtra("position", listposition);
                            who_is_noking.putExtra("is_taped", true);
                            who_is_noking.putExtra("listDriver", listDriver);
                            if (ArrayType == 1) {
                                who_is_noking.putExtra("ArrayType", 1);
                            } else if (ArrayType == 2) {
                                who_is_noking.putExtra("ArrayType", 2);
                            } else if (ArrayType == 3) {
                                who_is_noking.putExtra("ArrayType", 3);
                            }
                            startActivity(who_is_noking);
                        }
                    });
                        /*dialog.show();*/
                    return true;
                }
            });
            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if (llProviderDetail.getVisibility() == View.VISIBLE) {
                        llProviderDetail.setVisibility(View.GONE);
                    }
                    if (llProviderList.getVisibility() == View.VISIBLE) {
                        llProviderList.setVisibility(View.GONE);
                    }
                    if (llFilterByName.getVisibility() == View.VISIBLE) {
                        llFilterByName.setVisibility(View.GONE);
                    }
                    if (llSetHourDate.getVisibility() == View.VISIBLE) {
                        llSetHourDate.setVisibility(View.GONE);
                        btn_bookLater.setVisibility(View.GONE);
                    }
                    //AndyUtils.hideSoftKeyboard(MapActivity.this);
                    hideKeyboard(mapFrameLayout);
                }
            });


        } else if (ArrayType == 3) {
            listHairs = (ArrayList<MyThings>) bundle.getSerializable("subTypeHairs");
            Bitmap image = getImagefromurl(listHairs.get(0).SUBTYPE_ICON);
            for (int i = 0; i < listDriver.size(); i++) {
                AppLog.Log("provider", "lat" + listDriver.get(i).getLatitude());
                AppLog.Log("provider", "long" + listDriver.get(i).getLongitude());
                createMarker(listDriver.get(i).getLatitude(), listDriver.get(i).getLongitude(), image);
            }
              /*  AppLog.Log("listHairs ","latitude"+listHairs.get(i).WALKER_LATITUDE);
                AppLog.Log("listHairs ","longitude"+listHairs.get(i).WALKER_LONGITUDE);*/

            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    String markerId = marker.getId();
                    final int j = Integer.parseInt(markerId.substring(1));
                    listposition = j;
                    AppLog.Log("Selected position:", "" + listposition);
                    if (llFilterByName.getVisibility() == View.VISIBLE) {
                        llFilterByName.setVisibility(View.GONE);
                    }
                    if (llSetHourDate.getVisibility() == View.VISIBLE) {
                        llSetHourDate.setVisibility(View.GONE);
                        btn_bookLater.setVisibility(View.GONE);
                    }
                    if (llProviderList.getVisibility() == View.VISIBLE) {
                        llProviderList.setVisibility(View.GONE);
                    }
                    llProviderDetail.setVisibility(View.VISIBLE);
                    etName.setText(listDriver.get(j).getFirstName() + " " + listDriver.get(j).getLastName());
                    etDesc.setText(listDriver.get(j).getBio());
                    rtbProviderRating.setRating((float) listDriver.get(j).getRating());
                    etdetail.setText(listDriver.get(j).getPhone());
                    aQuery.id(ImgProviderPic).progress(R.id.pBar)
                            .image(listDriver.get(j).getPicture(), imageOptions);
                    llNok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listposition = j;
                            Intent who_is_noking = new Intent(MapActivity.this, who_is_noking.class);
                            who_is_noking.putExtra("position", listposition);
                            who_is_noking.putExtra("is_taped", true);
                            who_is_noking.putExtra("listDriver", listDriver);
                            if (ArrayType == 1) {
                                who_is_noking.putExtra("ArrayType", 1);
                            } else if (ArrayType == 2) {
                                who_is_noking.putExtra("ArrayType", 2);
                            } else if (ArrayType == 3) {
                                who_is_noking.putExtra("ArrayType", 3);
                            }
                            startActivity(who_is_noking);
                        }
                    });
                        /*dialog.show();*/
                    return true;
                }
            });
            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if (llProviderDetail.getVisibility() == View.VISIBLE) {
                        llProviderDetail.setVisibility(View.GONE);
                    }
                    if (llProviderList.getVisibility() == View.VISIBLE) {
                        llProviderList.setVisibility(View.GONE);
                    }
                    if (llFilterByName.getVisibility() == View.VISIBLE) {
                        llFilterByName.setVisibility(View.GONE);
                    }
                    if (llSetHourDate.getVisibility() == View.VISIBLE) {
                        llSetHourDate.setVisibility(View.GONE);
                        btn_bookLater.setVisibility(View.GONE);
                    }
                    //AndyUtils.hideSoftKeyboard(MapActivity.this);
                    hideKeyboard(mapFrameLayout);
                }
            });

        }

    }

    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void pickMeUp() {
        if (!AndyUtils.isNetworkAvailable(this)) {
            AndyUtils.showToast(getResources().getString(R.string.no_internet),
                    this);
            return;

        } /*else if (pHelper.getDefaultCard() == 0
                && paymentMode == Const.CREDIT) {
            AndyUtils.showToast(getResources().getString(R.string.no_card),
                    this);
            return;
        }*/
        if (!pHelper.isRegistered()) {
            Intent registrationActivity = new Intent(MapActivity.this, registration.class);
            startActivity(registrationActivity);
            return;
        }
        pHelper.putPaymentMode(0);
        AppLog.Log("List Driver Provider", "" + listDriver.get(listposition).getDriverId());
        AppLog.Log("Selected position:", "when requesting" + listposition);
        AndyUtils.showCustomProgressRequestDialog(this,
                getString(R.string.text_creating_request), true, null);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.CREATE_REQUEST_PROVIDER);
        map.put(Const.Params.TOKEN,
                pHelper.getSessionToken());
        map.put(Const.Params.ID, pHelper.getUserId());
        AppLog.Log("Latitude:", "when requesting" + String.valueOf(curretLatLng.latitude));
        AppLog.Log("longitude:", "when requesting" + String.valueOf(curretLatLng.longitude));
        map.put(Const.Params.LATITUDE, String.valueOf(curretLatLng.latitude));
        map.put(Const.Params.LONGITUDE, String.valueOf(curretLatLng.longitude));
        //	map.put(Const.Params.LATITUDE, "22.2904875");
        //map.put(Const.Params.LONGITUDE, "70.7751974");

        map.put(Const.Params.SUB_TYPE, String.valueOf(pHelper.getSubtypeId()));

        pHelper.putSelectedId(listposition);
        map.put(Const.Params.PROVIDER_ID,
                String.valueOf(listDriver.get(listposition).getDriverId()));
        map.put(Const.Params.PAYMENT_MODE,
                String.valueOf(pHelper.getPaymentMode()));
        if (ArrayType == 1) {
            map.put(Const.Params.TYPE, "Makeup");
        } else if (ArrayType == 2) {
            map.put(Const.Params.TYPE,
                    "Nails");
        } else if (ArrayType == 3) {
            map.put(Const.Params.TYPE,
                    "Hairs");
        }

//		map.put(Const.Params.CARD_ID,
//				String.valueOf(new PreferenceHelper(activity).getDefaultCard()));
//		map.put(Const.Params.DISTANCE, "1");
//		if (markerDestination != null) {
//			final LatLng dest = markerDestination.getPosition();
//
//			map.put(Const.Params.DESTI_LATITUDE, String.valueOf(dest.latitude));
//			map.put(Const.Params.DESTI_LONGITUDE,
//					String.valueOf(dest.longitude));
//		}
        // new HttpRequester(activity, map, Const.ServiceCode.CREATE_REQUEST,
        // this);
        requestQueue.add(new VolleyHttpRequest(Request.Method.POST, map,
                Const.ServiceCode.CREATE_REQUEST_PROVIDER, this, this));
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
        if (pHelper.getRequestId() > -1) {
            startCheckingStatusUpdate();
        }
    }

    protected void createMarker(double latitude, double longitude, Bitmap iconResID) {

        map.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                       /* .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.pin_client_org))*/
                .icon(BitmapDescriptorFactory.fromBitmap(iconResID)));
                       /* .title(title)
                       .anchor(0.5f, 0.5f)
                        .snippet(snippet)*/

    }

    private LatLng getLocationFromAddress(final String place) {
        LatLng loc = null;
        Geocoder gCoder = new Geocoder(getApplicationContext());
        try {
            final List<Address> list = gCoder.getFromLocationName(place, 1);
            if (list != null && list.size() > 0) {
                loc = new LatLng(list.get(0).getLatitude(), list.get(0)
                        .getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loc;
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
            case R.id.btn_find_your_nok:
                if (llProviderDetail.getVisibility() == View.VISIBLE) {
                    llProviderDetail.setVisibility(View.GONE);
                }
                if (llSetHourDate.getVisibility() == View.VISIBLE) {
                    llSetHourDate.setVisibility(View.GONE);
                    btn_bookLater.setVisibility(View.GONE);
                }
                if (llProviderList.getVisibility() == View.VISIBLE)
                    llProviderList.setVisibility(View.GONE);
                llFilterByName.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_nok_later:
                getSlots();
                if (llHour.getVisibility() == View.GONE)
                    llHour.setVisibility(View.VISIBLE);
                if (llDate.getVisibility() == View.GONE)
                    llDate.setVisibility(View.VISIBLE);
                if (llProviderDetail.getVisibility() == View.VISIBLE) {
                    llProviderDetail.setVisibility(View.GONE);
                }
                if (llFilterByName.getVisibility() == View.VISIBLE) {
                    llFilterByName.setVisibility(View.GONE);
                }
                if (llProviderList.getVisibility() == View.VISIBLE)
                    llProviderList.setVisibility(View.GONE);
                llSetHourDate.setVisibility(View.VISIBLE);
                btn_bookLater.setVisibility(View.VISIBLE);

                break;
            case R.id.btn_nok_now:
                if (llFilterByName.getVisibility() == View.VISIBLE) {
                    llFilterByName.setVisibility(View.GONE);
                }
                if (llSetHourDate.getVisibility() == View.VISIBLE) {
                    llSetHourDate.setVisibility(View.GONE);
                    btn_bookLater.setVisibility(View.GONE);
                }
                if (llProviderDetail.getVisibility() == View.VISIBLE) {
                    pickMeUp();
                } else {
                    if (listDriver.size() >= 1) {
                        if (llProviderList.getVisibility() == View.GONE)
                            llProviderList.setVisibility(View.VISIBLE);
                        llProviderList.setVisibility(View.VISIBLE);
                        lvProviders.setAdapter(adapterFilterDriver);
                        adapterFilterDriver.notifyDataSetChanged();

                    }
                }
                break;
            case R.id.llHour:
              /*  hour = cal.get(Calendar.HOUR_OF_DAY);
                minute = cal.get(Calendar.MINUTE);
                NoklaterTimePicker = new TimePickerDialog(this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                // Display Selected time in textbox
                                etHour.setText(hourOfDay + ":" + minute);
                            }
                        }, hour, minute, false);
                NoklaterTimePicker.show();*/
                AlertDialog.Builder dialogYears = new AlertDialog.Builder(MapActivity.this);
                dialogYears.setSingleChoiceItems(ArrayYears, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {
                        HoursSelectedIndex = position;
                        etHour.setText(ArrayYears[position]);
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialogYears = dialogYears.create();
                alertDialogYears.show();
                break;
            case R.id.llDate:
                day = cal.get(Calendar.DAY_OF_MONTH);
                month = cal.get(Calendar.MONTH);
                year = cal.get(Calendar.YEAR);
                NoklaterDatePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // Display Selected date in textbox
                        if (monthOfYear < 9) {
                            etDate.setText(year + "-0"
                                    + (monthOfYear + 1) + "-" + dayOfMonth);
                        } else {
                            etDate.setText(year + "-"
                                    + (monthOfYear + 1) + "-" + dayOfMonth);

                        }
                    }
                }, year, month, day);
                NoklaterDatePicker.show();
                break;
            case R.id.btn_bookLater:
                /*alertMessage();*/
                getProvidersLater(curretLatLng, Const.ServiceCode.GET_PROVIDERS_LATER);
                break;
        }

    }

    private void getProvidersLater(LatLng latlang, int serviceCode) {
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
            if (etDate.getText().length() > 0 && etHour.getText().length() > 0) {
                AndyUtils.showCustomProgressDialog(this,
                        getString(R.string.text_loading), false, null);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(Const.URL, Const.ServiceType.GET_PROVIDERS_LATER);
                map.put(Const.Params.ID,
                        String.valueOf(pHelper.getUserId()));
                AppLog.Log("TAG", "ID : " + String.valueOf(pHelper.getUserId()));
                map.put(Const.Params.TOKEN,
                        String.valueOf(pHelper.getSessionToken()));
                AppLog.Log("TAG", "token : " + String.valueOf(pHelper.getSessionToken()));
                map.put(Const.Params.USER_LATITUDE,
                        String.valueOf(latlang.latitude));
                AppLog.Log("TAG", "latitude: " + String.valueOf(latlang.latitude));
                map.put(Const.Params.USER_LONGITUDE,
                        String.valueOf(latlang.longitude));
                AppLog.Log("TAG", "longitude : " + String.valueOf(latlang.longitude));
                map.put(Const.Params.SUB_TYPE,
                        String.valueOf(pHelper.getSubtypeId()));
                AppLog.Log("TAG", "subtype: " + String.valueOf(pHelper.getSubtypeId()));
                map.put("slot_id", slotsid.get(HoursSelectedIndex));
                map.put("slot_date", etDate.getText().toString());
                // new HttpRequester(activity, map, Const.ServiceCode.GET_PROVIDERS,
                // this);
                requestQueue.add(new VolleyHttpRequest(Request.Method.POST, map,
                        serviceCode, this, this));
            }
        } catch (Exception e) {
            AppLog.Log("TAG", "getAllProviderException : " + e);
        }
    }

    private void getSlots() {
        AndyUtils.showCustomProgressRequestDialog(this,
                getString(R.string.text_loading), true, null);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.SLOTS);
        AppLog.Log("where", Const.URL);
        requestQueue.add(new VolleyHttpRequest(Request.Method.GET, map,
                Const.ServiceCode.SLOTS, this, this));

    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the
        // map.
        if (map == null) {
            // map = ((SupportMapFragment) activity.getSupportFragmentManager()
            // .findFragmentById(R.id.map)).getMap();
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            //map = ((MapView) findViewById(R.id.map)).getMap();
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(false);
            map.getUiSettings().setZoomControlsEnabled(false);
            map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location loc) {

                }
            });


            map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

                public void onCameraChange(CameraPosition camPos) {
                    if (currentZoom == -1) {
                        currentZoom = camPos.zoom;
                    } else if (camPos.zoom != currentZoom) {
                        currentZoom = camPos.zoom;
                        return;
                    }

                    if (!isMapTouched) {
                        // curretLatLng = camPos.target;
                        /*if (!isAddDestination) {
                            layoutMarker.setVisibility(LinearLayout.VISIBLE);
                            if (listType.size() > 0) {
                                stopUpdateProvidersLoaction();
                                // getAllProviders(curretLatLng,
                                // Const.ServiceCode.GET_PROVIDERS);
                            }*/
                        // getAddressFromLocation(camPos.target, etSource);

                    }
                    isMapTouched = false;
                    // setMarker(camPos.target);
                }

            });
            if (map != null) {
                // Log.i("Map", "Map Fragment");
            }
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see com.automated.taxinow.utils.LocationHelper.OnLocationReceived#
     * onLocationReceived(com.google.android.gms.maps.model.LatLng)
     */
    @Override
    public void onLocationReceived(LatLng latlong) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see com.automated.taxinow.utils.LocationHelper.OnLocationReceived#
     * onLocationReceived(android.location.Location)
     */
    @Override
    public void onLocationReceived(Location location) {
        // TODO Auto-generated method stub

        if (location != null) {
            // drawTrip(latlong);
            myLocation = location;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.automated.taxinow.utils.LocationHelper.OnLocationReceived#onConntected
     * (android.os.Bundle)
     */
    @Override
    public void onConntected(Bundle bundle) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.automated.taxinow.utils.LocationHelper.OnLocationReceived#onConntected
     * (android.location.Location)
     */
    @Override
    public void onConntected(Location location) {
        // TODO Auto-generated method stub

        if (location != null) {

            myLocation = location;

            // isLocationEnable = true;
            LatLng latLang = new LatLng(location.getLatitude(),
                    location.getLongitude());
            curretLatLng = latLang;
            //  getAllProviders(curretLatLng, Const.ServiceCode.GET_PROVIDERS);
            animateCameraToMarker(latLang, false);
        } else {
            activity.showLocationOffDialog();
        }
    }

    private void animateCameraToMarker(LatLng latLng, boolean isAnimate) {
        try {
           /* etSource.setFocusable(false);
            etSource.setFocusableInTouchMode(false);*/
            CameraUpdate cameraUpdate = null;

            cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            if (cameraUpdate != null && map != null) {
                if (isAnimate)
                    map.animateCamera(cameraUpdate);
                else
                    map.moveCamera(cameraUpdate);
            }
            etSource.setFocusable(true);
            etSource.setFocusableInTouchMode(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onErrorResponse(VolleyError volleyError) {

    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        switch (serviceCode) {
            case Const.ServiceCode.SLOTS:
                AndyUtils.removeCustomProgressDialog();
                if (response.length() > 0) {
                    stringArrayList.clear();
                    try {
                        JSONObject dateobject = new JSONObject(response);
                        if (dateobject.getBoolean("success")) {
                            JSONObject jsonObject = dateobject.getJSONObject("result");
                            JSONArray data = jsonObject.getJSONArray("1");
                            for (int j = 0; j < data.length(); j++) {
                                JSONObject object = data.getJSONObject(j);
                                Iterator<String> iter = object.keys();
                                while (iter.hasNext()) {
                                    String key = iter.next();

                                    try {
                                        Object value = object.get(key);
                                        stringArrayList.add(value.toString());
                                        ArrayYears = stringArrayList.toArray(new CharSequence[stringArrayList.size()]);
                                        slotsid.add(key);
                                    } catch (JSONException e) {
                                        // Something went wrong!

                                    }
                                }
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case Const.ServiceCode.GET_PROVIDERS_LIST:
                AppLog.Log("Get Provider Response::::", "Providers:::\n" + response);
                if (pContent.isSuccess(response)) {

                    // activity.gotoDriverListFragment(activity.pContent
                    // .getDriverList(response));

                    // listDriver.clear();
                    // listDriver.addAll(activity.pContent.getDriverList(response));
                    // AppLog.Log("ListDriver", "" + listDriver);
                    // AppLog.Log("response", ""+response);
                    // AppLog.Log("getDriverDetail", "" +
                    // activity.pContent.getDriverList(response));
                    showDriverListDialog(pContent.getDriverList(response));
//                    adapterDriver.notifyDataSetChanged();
                } else {
                    Toast.makeText(this,
                            "No Providers found",
                            Toast.LENGTH_SHORT).show();
                    if (llProviderDetail.getVisibility() == View.VISIBLE) {
                        llProviderDetail.setVisibility(View.GONE);
                    }
                }
                AndyUtils.removeCustomProgressDialog();
                break;
            case Const.ServiceCode.GET_PROVIDERS_LATER:
                AndyUtils.removeCustomProgressDialog();
                if (pContent.isSuccess(response)) {
                    isLater = true;
                    listDriverLater = pContent.getDriverList(response);
                    adapterDriver = new DriverListLaterAdapter(MapActivity.this, listDriverLater);
                    if (llProviderList.getVisibility() == View.GONE)
                        llProviderList.setVisibility(View.VISIBLE);
                    if (btn_bookLater.getVisibility() == View.VISIBLE)
                        btn_bookLater.setVisibility(View.GONE);
                    if (llHour.getVisibility() == View.VISIBLE)
                        llHour.setVisibility(View.GONE);
                    if (llDate.getVisibility() == View.VISIBLE)
                        llDate.setVisibility(View.GONE);
                    lvProviders.setAdapter(adapterDriver);
                    adapterDriver.notifyDataSetChanged();

                } else {
                    Toast.makeText(this,
                            "No Providers found",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case Const.ServiceCode.CREATE_REQUEST_PROVIDER:
                AppLog.Log(Const.TAG, "Create Request Response::::" + response);
                AndyUtils.removeCustomProgressRequestDialog();
                AndyUtils.removeCustomProgressDialog();
                if (pContent.isSuccess(response)) {
                    Toast.makeText(getApplicationContext(), "Your Request is Successful", Toast.LENGTH_LONG).show();
                    pHelper.putRequestId(pContent
                            .getRequestId(response));
                  /*  AndyUtils.showCustomProgressDialog(this,
                            getString(R.string.text_creating_request), false, this);*/

                  /*  stopUpdateProvidersLoaction();*/
                    startCheckingStatusUpdate();

                } else {
                    Toast.makeText(getApplicationContext(), "Your Request is Unsuccessful", Toast.LENGTH_LONG).show();
                }
                break;
            case Const.ServiceCode.CREATE_REQUEST_PROVIDER_LATER:
                AppLog.Log(Const.TAG, "Create Request Later Response" + response);
                AndyUtils.removeCustomProgressRequestDialog();
                AndyUtils.removeCustomProgressDialog();
                if (pContent.isSuccess(response)) {
                    Toast.makeText(getApplicationContext(), "Your Request is Successful", Toast.LENGTH_LONG).show();
                    pHelper.putRequestId(pContent
                            .getRequestId(response));
                  /*  AndyUtils.showCustomProgressDialog(this,
                            getString(R.string.text_creating_request), false, this);*/

                  /*  stopUpdateProvidersLoaction();*/
                } else {
                    Toast.makeText(getApplicationContext(), "Your Request is Unsuccessful", Toast.LENGTH_LONG).show();
                }
                break;
            case Const.ServiceCode.GET_REQUEST_STATUS:
                AppLog.Log(Const.TAG, "Get Request Response::::" + response);
                if (pContent.isSuccess(response)) {
                    AndyUtils.removeCustomProgressRequestDialog();
                    switch (pContent.checkRequestStatus(response)) {
                        case Const.IS_WALK_STARTED:
                        case Const.IS_WALKER_ARRIVED:
                        case Const.IS_WALKER_STARTED:
                            Intent whosnoking = new Intent(MapActivity.this, who_is_noking.class);
                            whosnoking.putExtra("Selected_position", pHelper.getSelectedId());
                            whosnoking.putExtra("listDriver", listDriver);
                            if (ArrayType == 1) {
                                whosnoking.putExtra("ArrayType", 1);
                            } else if (ArrayType == 2) {
                                whosnoking.putExtra("ArrayType", 2);
                            } else if (ArrayType == 3) {
                                whosnoking.putExtra("ArrayType", 3);
                            }
                            startActivity(whosnoking);
                            break;
                        case Const.IS_CANCELLED:
                           /* Toast.makeText(MapActivity.this, "Your Request is rejected by Provider. Try after Sometime.", Toast.LENGTH_LONG).show();
                            pHelper.clearRequestData();
                            Intent gotoChooseurNok = new Intent(MapActivity.this, ChooseYourNok.class);
                            finish();
                            startActivity(gotoChooseurNok);*/

                            break;
                        case Const.IS_COMPLETED:
                            stopCheckingStatusUpdate();
                            gotoRateFragment(pContent.getDriverDetail(response));
                            break;
                        case Const.IS_REQEUST_CREATED:
//					AndyUtils.removeCustomProgressDialog();
                            if (pHelper.getRequestId() != Const.NO_REQUEST) {
                               /* Driver driverInfo = pContent
                                        .getDriverDetail(response);*/
                                /*Log.d("is request created", response);
                                AndyUtils.showCustomProgressRequestDialog(this,
                                        getString(R.string.text_contacting), true, null);*/
                                stopCheckingStatusUpdate();
                                Intent who_is_noking = new Intent(MapActivity.this, who_is_noking.class);
                                who_is_noking.putExtra("Selected_position", pHelper.getSelectedId());
                                who_is_noking.putExtra("listDriver", listDriver);
                                if (ArrayType == 1) {
                                    who_is_noking.putExtra("ArrayType", 1);
                                } else if (ArrayType == 2) {
                                    who_is_noking.putExtra("ArrayType", 2);
                                } else if (ArrayType == 3) {
                                    who_is_noking.putExtra("ArrayType", 3);
                                }
                                startActivity(who_is_noking);
                                /*AndyUtils.showCustomProgressDialog(this,
                                        getString(R.string.text_contacting), false,
                                        this, driverInfo);*/

                            }
                            isContinueRequest = true;
                            break;
                        case Const.NO_REQUEST:
                            stopCheckingStatusUpdate();
                            Toast.makeText(MapActivity.this, "Your Request is rejected by Provider. Try after Sometime.", Toast.LENGTH_SHORT).show();
                            pHelper.clearRequestData();
                            Intent gotoHome = new Intent(MapActivity.this, ChooseYourNok.class);
                            finish();
                            startActivity(gotoHome);

                            break;
                        default:
                            isContinueRequest = false;
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
            case Const.ServiceCode.CANCEL_REQUEST:
                if (pContent.isSuccess(response)) {

                }
                pHelper.clearRequestData();
                AndyUtils.removeCustomProgressRequestDialog();
                break;
        }
    }

    private void gotoRateFragment(Driver driver) {
        try {

            Intent gotoFeedback = new Intent(MapActivity.this, Feedback.class);
            gotoFeedback.putExtra(Const.DRIVER, driver);
            startActivity(gotoFeedback);
        } catch (Exception e) {
            e.printStackTrace();
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


    private class TimerRequestStatus extends TimerTask {
        @Override
        public void run() {
            if (isContinueRequest) {
                isContinueRequest = false;
                getRequestStatus(String.valueOf(pHelper.getRequestId()));
            }
        }

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

    //shows Massagers
    private void showDriverListDialog(ArrayList<Driver> listDriver) {
        if (llProviderDetail.getVisibility() == View.VISIBLE) {
            llProviderDetail.setVisibility(View.GONE);
        }
        //  llProviderList.setVisibility(View.VISIBLE);
        Intent whosNoking = new Intent(MapActivity.this, who_is_noking.class);
        whosNoking.putExtra("listDriver", listDriver);
        whosNoking.putExtra("content", etSource.getText().toString());
        etSource.setText("");
        whosNoking.putExtra("ArrayType", ArrayType);
        startActivity(whosNoking);


    }

    @Override
    public void onProgressCancel() {
        AndyUtils.removeCustomProgressDialog();
        Toast.makeText(getApplicationContext(), "Your Request is being Cancelled", Toast.LENGTH_LONG).show();
        cancleRequest();
    }

    private void cancleRequest() {
        if (!AndyUtils.isNetworkAvailable(this)) {
            AndyUtils.showToast(getResources().getString(R.string.no_internet),
                    this);
            return;
        }
        AndyUtils.removeCustomProgressRequestDialog();
        AndyUtils.showCustomProgressRequestDialog(this,
                getString(R.string.text_canceling_request), true, null);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.CANCEL_REQUEST);
        map.put(Const.Params.ID, String.valueOf(pHelper.getUserId()));
        map.put(Const.Params.TOKEN,
                String.valueOf(pHelper.getSessionToken()));
        map.put(Const.Params.REQUEST_ID,
                String.valueOf(pHelper.getRequestId()));
        // new HttpRequester(activity, map, Const.ServiceCode.CANCEL_REQUEST,
        // this);
        requestQueue.add(new VolleyHttpRequest(Request.Method.POST, map,
                Const.ServiceCode.CANCEL_REQUEST, this, this));
    }


    class Driverlist_Filterable extends BaseAdapter implements Filterable {

        private LayoutInflater inflater;
        private ViewHolder holder;
        private ArrayList<Driver> listDriver, filteredData;
        private Context context;
        private ImageOptions imageOptions;
        private AQuery aQuery;
        ValueFilter valueFilter;

        public Driverlist_Filterable(Context context, ArrayList<Driver> listDriver) {
            this.context = context;
            this.listDriver = listDriver;
            this.filteredData = listDriver;
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            imageOptions = new ImageOptions();
            imageOptions.fileCache = true;
            imageOptions.memCache = true;
            imageOptions.fallback = R.drawable.default_user;

        }

        /* @Override
         public void add(Driver object) {
             if (listDriver != null)
                 listDriver.add(object);
             else
                 filteredData.add(object);
         }

         @Override
         public void remove(Driver object) {
             if (listDriver != null)
                 listDriver.remove(object);
             else
                 filteredData.remove(object);
         }
     */
    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getCount()
     */
        @Override
        public int getCount() {
            return listDriver.size();
        }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItem(int)
     */

        /*
         * (non-Javadoc)
         *
         * @see android.widget.Adapter#getItemId(int)
         */
    /*@Override
    public int getPosition(Driver item) {
        return filteredData.indexOf(item);
    }*/
        @Override
        public Object getItem(int position) {
            return listDriver.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.widget.Adapter#getView(int, android.view.View,
         * android.view.ViewGroup)
         */
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.dialog_walker_info, parent, false);
                aQuery = new AQuery(context);
                holder = new ViewHolder();
                holder.etName = (MyTitleFontTextView) convertView
                        .findViewById(R.id.etName);
                holder.etDetail = (MyTitleFontTextView) convertView
                        .findViewById(R.id.etdetail);
                holder.etDesc = (MyTitleFontTextView) convertView
                        .findViewById(R.id.etdesc);
                holder.rtbProviderRating = (RatingBar) convertView
                        .findViewById(R.id.rtbProviderRating);
                holder.imgProviderPic = (ImageView) convertView
                        .findViewById(R.id.imgProviderPic);
                holder.llNOK = (LinearLayout) convertView.findViewById(R.id.llNOK);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Driver driver = listDriver.get(position);
            holder.etName.setText(driver.getFirstName() + " " + driver.getLastName());
            holder.etDesc.setText(driver.getBio());
            holder.etDetail.setText(driver.getPhone());
            holder.rtbProviderRating.setRating((float) driver.getRating());
            aQuery.id(holder.imgProviderPic).progress(R.id.pBar)
                    .image(driver.getPicture(), imageOptions);
            holder.llNOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listposition = position;
                    pickMeUp();
                }
            });

            return convertView;
        }

        private class ViewHolder {
            MyTitleFontTextView etName, etDesc, etDetail;
            RatingBar rtbProviderRating;
            ImageView imgProviderPic;
            LinearLayout llNOK;
        }

        private class ValueFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                if (constraint != null && constraint.length() > 0) {
                    ArrayList<Driver> filterList = new ArrayList<Driver>();
                    AppLog.Log("filtered data", "size" + filteredData.size());
                    for (int i = 0; i < filteredData.size(); i++) {
                        AppLog.Log("Match name", "" + (filteredData.get(i).getFirstName().toUpperCase()).contains(constraint.toString().toUpperCase()));
                        AppLog.Log("filtered data ", "" + (filteredData.get(i).getFirstName().toUpperCase()));
                        AppLog.Log("Constraint data", "" + constraint.toString().toUpperCase());
                        if ((filteredData.get(i).getFirstName().toUpperCase()).contains(constraint.toString().toUpperCase())) {
                            Driver country = new Driver();
                            country.setFirstName(filteredData.get(i).getFirstName());
                            country.setLastName(filteredData.get(i).getLastName());
                            country.setBio(filteredData.get(i).getBio());
                            country.setPhone(filteredData.get(i).getPhone());
                            country.setRating(filteredData.get(i).getRating());
                            //country.setContactNumber(filteredData.get(i).getContactNumber());
                            country.setPicture(filteredData.get(i).getPicture());
                            filterList.add(country);
                        }
                    }
                    results.count = filterList.size();
                    AppLog.Log("results count", "" + results.count);
                    results.values = filterList;
                    AppLog.Log("results values", "" + results.values);
                } else {
                    results.count = filteredData.size();
                    AppLog.Log("results count else", "" + results.count);
                    results.values = filteredData;
                }
                return results;

            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                // mContactsList.clear();

                listDriver = (ArrayList<Driver>) results.values;
                notifyDataSetChanged();
           /* if (!TextUtils.isEmpty(constraint)) {
                // hideHader = false;
            } else {
                // hideHader = true;
            }*/
            }

        }


        @Override
        public Filter getFilter() {
            if (valueFilter == null) {
                valueFilter = new ValueFilter();
            }
            return valueFilter;
        }

    }

    class DriverListLaterAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private ViewHolder holder;
        private ArrayList<Driver> listDriver;
        private Context context;
        private ImageOptions imageOptions;
        private AQuery aQuery;

        public DriverListLaterAdapter(Context context, ArrayList<Driver> listDriver) {
            this.context = context;
            this.listDriver = listDriver;
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            imageOptions = new ImageOptions();
            imageOptions.fileCache = true;
            imageOptions.memCache = true;
            imageOptions.fallback = R.drawable.default_user;


        }

        /*
         * (non-Javadoc)
         *
         * @see android.widget.Adapter#getCount()
         */
        @Override
        public int getCount() {
            return listDriver.size();
        }


        /*
         * (non-Javadoc)
         *
         * @see android.widget.Adapter#getItem(int)
         */
        @Override
        public Object getItem(int position) {
            return listDriver.get(position);
        }

        /*
         * (non-Javadoc)
         *
         * @see android.widget.Adapter#getItemId(int)
         */
        @Override
        public long getItemId(int position) {
            return 0;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.widget.Adapter#getView(int, android.view.View,
         * android.view.ViewGroup)
         */
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.dialog_walker_info, parent, false);
                aQuery = new AQuery(context);
                holder = new ViewHolder();
                holder.etName = (MyTitleFontTextView) convertView
                        .findViewById(R.id.etName);
                holder.etDetail = (MyTitleFontTextView) convertView
                        .findViewById(R.id.etdetail);
                holder.etDesc = (MyTitleFontTextView) convertView
                        .findViewById(R.id.etdesc);
                holder.rtbProviderRating = (RatingBar) convertView
                        .findViewById(R.id.rtbProviderRating);
                holder.imgProviderPic = (ImageView) convertView
                        .findViewById(R.id.imgProviderPic);
                holder.llNOK = (LinearLayout) convertView.findViewById(R.id.llNOK);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Driver driver = listDriver.get(position);
            holder.etName.setText(driver.getFirstName() + "" + driver.getLastName());
            holder.etDesc.setText(driver.getBio());
            holder.etDetail.setText(driver.getPhone());
            holder.rtbProviderRating.setRating((float) driver.getRating());
            aQuery.id(holder.imgProviderPic).progress(R.id.pBar)
                    .image(driver.getPicture(), imageOptions);
            holder.llNOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listposition = position;
                    sendData();

                }
            });
            return convertView;
        }

        private class ViewHolder {
            MyTitleFontTextView etName, etDesc, etDetail;
            RatingBar rtbProviderRating;
            ImageView imgProviderPic;
            LinearLayout llNOK;
        }

    }
}
