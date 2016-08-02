package com.mowares.massagerexpressclient;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
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
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.mowares.massagerexpressclient.adapter.PlacesAutoCompleteAdapter;
import com.mowares.massagerexpressclient.component.MyFontButton;
import com.mowares.massagerexpressclient.component.MyTitleFontTextView;
import com.mowares.massagerexpressclient.interfaces.OnProgressCancelListener;
import com.mowares.massagerexpressclient.models.Driver;
import com.mowares.massagerexpressclient.parse.AsyncTaskCompleteListener;
import com.mowares.massagerexpressclient.parse.ParseContent;
import com.mowares.massagerexpressclient.parse.VolleyHttpRequest;
import com.mowares.massagerexpressclient.utils.AndyUtils;
import com.mowares.massagerexpressclient.utils.AppLog;
import com.mowares.massagerexpressclient.utils.Const;
import com.mowares.massagerexpressclient.utils.LocationHelper;
import com.mowares.massagerexpressclient.utils.PreferenceHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class who_is_noking extends BaseActivity implements View.OnClickListener, LocationHelper.OnLocationReceived, AsyncTaskCompleteListener, Response.ErrorListener, OnProgressCancelListener {
    protected MyTitleFontTextView txtTitle;
    public static boolean isMapTouched = false;
    private ImageButton btnActionBack, btnActionMenu;
    private FrameLayout mapFrameLayout;
    private GoogleMap map;
    private LocationHelper locHelper;
    private boolean isContinueRequest, istaped;
    private Timer timer;
    private Location myLocation;
    private MapView mMapView;
    private float currentZoom = -1;
    private LatLng curretLatLng;
    private MainDrawerActivity activity;
    private PlacesAutoCompleteAdapter adapter;
    private AutoCompleteTextView etSource;
    private MyTitleFontTextView etName, etDesc, etdetail, etLastReviews;
    private LinearLayout llProviderDetail, llProviderList, llRequestProvider, llget_ready_to_nok,
            llCancel, llRequestService, llProviderPortfolio, llProviderPortfolio_Imageview, llProviderLast_Review;
    private MyFontButton btn_get_ready_to_nok, btn_cancel, btn_portfolio, btn_provider_lastreviews, btn_itsa_nok, btn_keep_looking;
    private ListView lvProviders;
    private DriverAdapter adapterDriver;
    private RequestQueue requestQueue;
    private PreferenceHelper pHelper;
    private boolean isSource;
    private int selectedPostion = -1;
    private ParseContent pContent;
    public int ArrayType;
    private ImageOptions imageOptions;
    private AQuery aQuery;
    private ArrayList<Driver> listDriver = new ArrayList<Driver>();
    private RatingBar rtbProviderRating, rtbRating;
    private ImageView ImgProviderPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_who_is_noking);
        Bundle bundle = getIntent().getExtras();
        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
        }
        InitUI();
        txtTitle.setText("Who's NOKing?");

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
        adapter = new PlacesAutoCompleteAdapter(getApplicationContext(),
                R.layout.autocomplete_list_text);
        etSource.setAdapter(adapter);
        locHelper = new LocationHelper(this);
        locHelper.setLocationReceivedLister(this);
        setUpMapIfNeeded();
        locHelper.onStart();

        if (bundle != null) {

            if (bundle.containsKey("content")) {
                String Content = bundle.getString("content");
                etSource.setText(Content);
                if (bundle.containsKey("listDriver")) {
                    listDriver = (ArrayList<Driver>) bundle.getSerializable("listDriver");

                    adapterDriver = new DriverAdapter(this, listDriver);
                    adapterDriver.notifyDataSetChanged();
                    if (llProviderDetail.getVisibility() == View.VISIBLE) {
                        llProviderDetail.setVisibility(View.GONE);
                    }
                    if (llProviderList.getVisibility() == View.GONE) {
                        llProviderList.setVisibility(View.VISIBLE);
                    }
                    if (llProviderPortfolio.getVisibility() == View.VISIBLE) {
                        llProviderPortfolio.setVisibility(View.GONE);
                    }
                    if (llRequestService.getVisibility() == View.VISIBLE)
                        llRequestService.setVisibility(View.GONE);
                    if (llRequestProvider.getVisibility() != View.GONE)
                        llRequestProvider.setVisibility(View.GONE);

                    lvProviders.setAdapter(adapterDriver);
                }
            }
            if (bundle.containsKey("ArrayType")) {
                ArrayType = bundle.getInt("ArrayType");
            }
            if (bundle.containsKey("Selected_position")) {
                selectedPostion = bundle.getInt("Selected_position");
                if (bundle.containsKey("listDriver")) {
                    listDriver = (ArrayList<Driver>) bundle.getSerializable("listDriver");
                    if (llProviderList.getVisibility() == View.VISIBLE) {
                        llProviderList.setVisibility(View.GONE);
                    }
                    if (llProviderDetail.getVisibility() == View.GONE) {
                        llProviderDetail.setVisibility(View.VISIBLE);
                        if (llRequestProvider.getVisibility() == View.GONE)
                            llRequestProvider.setVisibility(View.VISIBLE);
                        /*if (llProviderPortfolio.getVisibility() == View.GONE)
                            llProviderPortfolio.setVisibility(View.VISIBLE);
                        */
                        if (llRequestService.getVisibility() == View.VISIBLE) {
                            llRequestService.setVisibility(View.GONE);
                        }
                        providerDetail();
                        if (llget_ready_to_nok.getVisibility() == View.VISIBLE)
                            llget_ready_to_nok.setVisibility(View.INVISIBLE);
                    }
                }
            }
            if (bundle.containsKey("position")) {
                selectedPostion = bundle.getInt("position");
                istaped = bundle.getBoolean("is_taped");
                if (bundle.containsKey("listDriver")) {
                    listDriver = (ArrayList<Driver>) bundle.getSerializable("listDriver");
                    providerDetail();
                    if (llProviderList.getVisibility() == View.VISIBLE) {
                        llProviderList.setVisibility(View.GONE);
                    }
                    if (llProviderDetail.getVisibility() == View.GONE) {
                        llProviderDetail.setVisibility(View.VISIBLE);
                    }
                    if (llRequestProvider.getVisibility() == View.VISIBLE)
                        llRequestProvider.setVisibility(View.GONE);
                    if (llProviderPortfolio.getVisibility() == View.GONE)
                        llProviderPortfolio.setVisibility(View.VISIBLE);
                    if (llRequestService.getVisibility() == View.GONE)
                        llRequestService.setVisibility(View.VISIBLE);
                    adapterDriver = new DriverAdapter(this, listDriver);
                    adapterDriver.notifyDataSetChanged();
                }
            }
        }


       /* etSource.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });*/
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
                                isSource = true;
                                //  setMarker(curretLatLng, isSource);
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
    }

    private void providerDetail() {
        Driver driver = listDriver.get(selectedPostion);
        etName.setText(driver.getFirstName() + "  " + driver.getLastName());
        etDesc.setText(driver.getBio());
        etLastReviews.setText(driver.getBio());
        etdetail.setText(driver.getPhone());
        rtbProviderRating.setRating((float) driver.getRating());
        rtbRating.setRating((float) driver.getRating());
        aQuery.id(ImgProviderPic).progress(R.id.pBar)
                .image(driver.getPicture(), imageOptions);
    }

    private void InitUI() {
        aQuery = new AQuery(this);
        imageOptions = new ImageOptions();
        imageOptions.fileCache = true;
        imageOptions.memCache = true;
        imageOptions.fallback = R.drawable.default_user;
        btnActionBack = (ImageButton) findViewById(R.id.btnActionBack);
        mapFrameLayout = (FrameLayout) findViewById(R.id.mapFrameLayout);
        txtTitle = (MyTitleFontTextView) findViewById(R.id.tvTitle);

        etSource = (AutoCompleteTextView) findViewById(R.id.etEnterSouce);
        etName = (MyTitleFontTextView) findViewById(R.id.etName);
        etDesc = (MyTitleFontTextView) findViewById(R.id.etdesc);
        etdetail = (MyTitleFontTextView) findViewById(R.id.etdetail);
        etLastReviews = (MyTitleFontTextView) findViewById(R.id.etLastReviews);

        llProviderList = (LinearLayout) findViewById(R.id.llProviderList);
        llProviderDetail = (LinearLayout) findViewById(R.id.llProviderDetail);
        llRequestProvider = (LinearLayout) findViewById(R.id.llRequestProvider);
        llCancel = (LinearLayout) findViewById(R.id.llCancel);
        llget_ready_to_nok = (LinearLayout) findViewById(R.id.llget_ready_to_nok);
        llRequestService = (LinearLayout) findViewById(R.id.llRequestService);
        llProviderPortfolio = (LinearLayout) findViewById(R.id.llProviderPortfolio);
        llProviderLast_Review = (LinearLayout) findViewById(R.id.llProviderLast_Review);
        llProviderPortfolio_Imageview = (LinearLayout) findViewById(R.id.llProviderPortfolio_Imageview);

        btn_cancel = (MyFontButton) findViewById(R.id.btn_cancel);
        btn_get_ready_to_nok = (MyFontButton) findViewById(R.id.btn_get_ready_to_nok);
        btn_portfolio = (MyFontButton) findViewById(R.id.btn_provider_portfolioimage);
        btn_provider_lastreviews = (MyFontButton) findViewById(R.id.btn_provider_lastreviews);
        btn_itsa_nok = (MyFontButton) findViewById(R.id.btn_its_a_nok);
        btn_keep_looking = (MyFontButton) findViewById(R.id.btn_keep_looking);
        btnActionMenu = (ImageButton) findViewById(R.id.btnActionMenu);

        btn_cancel.setOnClickListener(this);
        btn_get_ready_to_nok.setOnClickListener(this);
        btn_portfolio.setOnClickListener(this);
        btn_provider_lastreviews.setOnClickListener(this);
        btnActionMenu.setOnClickListener(this);
        btn_itsa_nok.setOnClickListener(this);
        btn_keep_looking.setOnClickListener(this);

        lvProviders = (ListView) findViewById(R.id.lvProviders);
        rtbProviderRating = (RatingBar) findViewById(R.id.rtbProviderRating);
        rtbRating = (RatingBar) findViewById(R.id.rtbRating);
        ImgProviderPic = (ImageView) findViewById(R.id.imgProviderPic);
        btnActionBack.setOnClickListener(this);
        pHelper = new PreferenceHelper(this);
        pContent = new ParseContent(this);
        requestQueue = Volley.newRequestQueue(this);

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
                        //curretLatLng = camPos.target;
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnActionBack:
                finish();
                break;
            case R.id.btn_cancel:
                cancleRequest();
                break;
            case R.id.btnActionMenu:
                popupWindow.showAsDropDown(v, 0, 0);
                break;
            case R.id.btn_get_ready_to_nok:
                Intent GetReadyToNok = new Intent(who_is_noking.this, GetReadyToNok.class);
                if (ArrayType == 1) {
                    GetReadyToNok.putExtra("ArrayType", 1);
                } else if (ArrayType == 2) {
                    GetReadyToNok.putExtra("ArrayType", 2);
                } else if (ArrayType == 3) {
                    GetReadyToNok.putExtra("ArrayType", 3);
                }
                startActivity(GetReadyToNok);
                break;
            case R.id.btn_provider_lastreviews:
              /*  btn_provider_lastreviews.setBackgroundColor(getResources().getColor(R.color.black));
                btn_provider_lastreviews.setTextColor(getResources().getColor(R.color.white));*/
                btn_provider_lastreviews.setBackgroundResource(R.drawable.button_background_white);
                btn_provider_lastreviews.setTextColor(getResources().getColor(R.color.black));
                btn_portfolio.setBackgroundResource(R.drawable.button_background_black);
                btn_portfolio.setTextColor(getResources().getColor(R.color.white));
                llProviderLast_Review.setVisibility(View.VISIBLE);
                llProviderPortfolio_Imageview.setVisibility(View.GONE);
                break;
            case R.id.btn_provider_portfolioimage:
                /*btn_portfolio.setBackgroundColor(getResources().getColor(R.color.black));
                btn_portfolio.setTextColor(getResources().getColor(R.color.white));*/
                btn_portfolio.setBackgroundResource(R.drawable.button_background_white);
                btn_portfolio.setTextColor(getResources().getColor(R.color.black));
                btn_provider_lastreviews.setBackgroundResource(R.drawable.button_background_black);
                btn_provider_lastreviews.setTextColor(getResources().getColor(R.color.white));
                llProviderPortfolio_Imageview.setVisibility(View.VISIBLE);
                llProviderLast_Review.setVisibility(View.GONE);
                break;
            case R.id.btn_its_a_nok:
                pickMeUp();
                break;
            case R.id.btn_keep_looking:
                if (llProviderDetail.getVisibility() == View.VISIBLE) {
                    llProviderDetail.setVisibility(View.GONE);
                }
                if (llProviderList.getVisibility() == View.GONE) {
                    llProviderList.setVisibility(View.VISIBLE);
                }
                if (llProviderPortfolio.getVisibility() == View.VISIBLE) {
                    llProviderPortfolio.setVisibility(View.GONE);
                }
                if (llRequestService.getVisibility() == View.VISIBLE)
                    llRequestService.setVisibility(View.GONE);
                if (llRequestProvider.getVisibility() != View.GONE)
                    llRequestProvider.setVisibility(View.GONE);
                if (istaped) {
                    this.finish();
                }
                break;
        }
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
            //getAllProviders(curretLatLng, Const.ServiceCode.GET_PROVIDERS);
            animateCameraToMarker(latLang, false);
        } else {
            activity.showLocationOffDialog();
        }
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
            Intent registrationActivity = new Intent(who_is_noking.this, registration.class);
            startActivity(registrationActivity);
            return;
        }
        pHelper.putPaymentMode(0);
        AppLog.Log("List Driver Provider", "" + listDriver.get(selectedPostion).getDriverId());
        AppLog.Log("Selected position:", "when requesting" + selectedPostion);
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
        map.put(Const.Params.SUB_TYPE, String.valueOf(pHelper.getSubtypeId()));
        map.put(Const.Params.PROVIDER_ID,
                String.valueOf(listDriver.get(selectedPostion).getDriverId()));
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
    public void onTaskCompleted(String response, int serviceCode) {
        switch (serviceCode) {
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
                    adapterDriver.notifyDataSetChanged();
                } else {
                    Toast.makeText(this,
                            "No Providers found",
                            Toast.LENGTH_SHORT).show();
                    llProviderList.setVisibility(View.GONE);
                }
                AndyUtils.removeCustomProgressDialog();
                break;
            case Const.ServiceCode.GET_REQUEST_STATUS:
                AppLog.Log(Const.TAG, "Get Request Response::::" + response);
                if (pContent.isSuccess(response)) {
                    AndyUtils.removeCustomProgressRequestDialog();
                    switch (pContent.checkRequestStatus(response)) {
                        case Const.IS_WALK_STARTED:
                            AndyUtils.removeCustomProgressRequestDialog();
                            if (llRequestProvider.getVisibility() != View.VISIBLE)
                                llRequestProvider.setVisibility(View.VISIBLE);
                            if (llget_ready_to_nok.getVisibility() != View.VISIBLE)
                                llget_ready_to_nok.setVisibility(View.VISIBLE);
                            break;
                        case Const.IS_WALKER_ARRIVED:
                            AndyUtils.removeCustomProgressRequestDialog();
                            if (llRequestProvider.getVisibility() != View.VISIBLE)
                                llRequestProvider.setVisibility(View.VISIBLE);
                            if (llget_ready_to_nok.getVisibility() != View.VISIBLE)
                                llget_ready_to_nok.setVisibility(View.VISIBLE);
                            break;
                        case Const.IS_WALKER_STARTED:
                            AndyUtils.removeCustomProgressRequestDialog();
                            if (llRequestProvider.getVisibility() != View.VISIBLE)
                                llRequestProvider.setVisibility(View.VISIBLE);
                            if (llget_ready_to_nok.getVisibility() != View.VISIBLE)
                                llget_ready_to_nok.setVisibility(View.VISIBLE);
                            break;
                        case Const.IS_CANCELLED:
                            /*Toast.makeText(who_is_noking.this, "Your Request is rejected by Provider. Try after Sometime.", Toast.LENGTH_SHORT).show();
                            pHelper.clearRequestData();
                            Intent gotoChooseurNok = new Intent(who_is_noking.this, ChooseYourNok.class);
                            finish();
                            startActivity(gotoChooseurNok);*/

                            break;
                        case Const.IS_WALKER_RATED:

                            break;
                        case Const.IS_COMPLETED:
                           /* pHelper.clearRequestData();
                            isContinueRequest = false;
                            Toast.makeText(who_is_noking.this, "Your Request is complete. Thanks for using", Toast.LENGTH_SHORT).show();
                            gotoChooseYourNok();*/
                            AppLog.Log("complete", response);
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
                                /*AndyUtils.showCustomProgressDialog(this,
                                        getString(R.string.text_contacting), false,
                                        this, driverInfo);*/

                            }
                            isContinueRequest = true;
                            break;
                        case Const.NO_REQUEST:
                            stopCheckingStatusUpdate();
                            Toast.makeText(who_is_noking.this, "Your Request is rejected by Provider. Try after Sometime.", Toast.LENGTH_SHORT).show();
                            pHelper.clearRequestData();
                            Intent gotoHome = new Intent(who_is_noking.this, ChooseYourNok.class);
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
                    Toast.makeText(this,
                            "Your Request is Cancelled",
                            Toast.LENGTH_SHORT).show();

                    gotoChooseYourNok();
                }

                pHelper.clearRequestData();
                AndyUtils.removeCustomProgressRequestDialog();
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
                    if (llProviderPortfolio.getVisibility() == View.VISIBLE)
                        llProviderPortfolio.setVisibility(View.GONE);
                    if (llRequestService.getVisibility() == View.VISIBLE)
                        llRequestService.setVisibility(View.GONE);
                    if (llRequestProvider.getVisibility() != View.VISIBLE)
                        llRequestProvider.setVisibility(View.VISIBLE);
                    if (llget_ready_to_nok.getVisibility() == View.VISIBLE)
                        llget_ready_to_nok.setVisibility(View.INVISIBLE);
                } else {
                    Toast.makeText(getApplicationContext(), "Your Request is Unsuccessful", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void gotoRateFragment(Driver driver) {
        try {

            Intent gotoFeedback = new Intent(who_is_noking.this, Feedback.class);
            gotoFeedback.putExtra(Const.DRIVER, driver);
            startActivity(gotoFeedback);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Const.menu_myProfile = 1;
        Const.menu_newNok = 1;
        Const.menu_customerService = 1;
        Const.menu_getReady = 1;
        Const.menu_futureNok = 1;
        Const.menu_mynok = 1;
        if (pHelper.getRequestId() != Const.NO_REQUEST) {
            startCheckingStatusUpdate();
        }
    }

    public void gotoChooseYourNok() {
        Intent i = new Intent(who_is_noking.this, ChooseYourNok.class);
        startActivity(i);
        // close this activity
        finish();
    }

    private void showDriverListDialog(ArrayList<Driver> listDriver) {
        istaped = false;
        if (llProviderDetail.getVisibility() == View.VISIBLE) {
            llProviderDetail.setVisibility(View.GONE);
        }
        if (llProviderList.getVisibility() == View.GONE) {
            llProviderList.setVisibility(View.VISIBLE);
        }
        if (llProviderPortfolio.getVisibility() == View.VISIBLE) {
            llProviderPortfolio.setVisibility(View.GONE);
        }
        if (llRequestService.getVisibility() == View.VISIBLE)
            llRequestService.setVisibility(View.GONE);
        if (llRequestProvider.getVisibility() != View.GONE)
            llRequestProvider.setVisibility(View.GONE);
        //llProviderList.setVisibility(View.VISIBLE);
        this.listDriver = listDriver;
        adapterDriver = new DriverAdapter(this, listDriver);
        lvProviders.setAdapter(adapterDriver);
       /* btn_NokLater.setVisibility(View.GONE);
        btn_NokNow.setVisibility(View.GONE);
        btn_FindYourNok.setVisibility(View.GONE);*/
        lvProviders.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                selectedPostion = position;
                //* pickMeUp();*//*
            }
        });

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

    @Override
    public void onErrorResponse(VolleyError volleyError) {

    }

    private void setColorBackground() {

    }

    class DriverAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private ViewHolder holder;
        private ArrayList<Driver> listDriver;
        private Context context;
        private ImageOptions imageOptions;
        private AQuery aQuery;

        public DriverAdapter(Context context, ArrayList<Driver> listDriver) {
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
            holder.etName.setText(driver.getFirstName() + " " + driver.getLastName());
            holder.etDesc.setText(driver.getBio());
            holder.etDetail.setText(driver.getPhone());
            holder.rtbProviderRating.setRating((float) driver.getRating());
            aQuery.id(holder.imgProviderPic).progress(R.id.pBar)
                    .image(driver.getPicture(), imageOptions);
            holder.llNOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedPostion = position;
                    AppLog.Log("selectedPostion", "" + selectedPostion);
                    if (llProviderDetail.getVisibility() == View.GONE)
                        llProviderDetail.setVisibility(View.VISIBLE);
                    llProviderList.setVisibility(View.GONE);
                    providerDetail();
                    if (llProviderPortfolio.getVisibility() == View.GONE)
                        llProviderPortfolio.setVisibility(View.VISIBLE);
                    if (llRequestService.getVisibility() == View.GONE)
                        llRequestService.setVisibility(View.VISIBLE);
               /* pickMeUp();*/

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