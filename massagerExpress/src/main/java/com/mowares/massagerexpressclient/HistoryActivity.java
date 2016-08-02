/**
 *
 */
package com.mowares.massagerexpressclient;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.hb.views.PinnedSectionListView;
import com.mowares.massagerexpressclient.adapter.HistoryAdapter;
import com.mowares.massagerexpressclient.adapter.TypeInvoiceAdapter;
import com.mowares.massagerexpressclient.component.MyTitleFontTextView;
import com.mowares.massagerexpressclient.models.History;
import com.mowares.massagerexpressclient.models.TypeInvoice;
import com.mowares.massagerexpressclient.parse.AsyncTaskCompleteListener;
import com.mowares.massagerexpressclient.parse.ParseContent;
import com.mowares.massagerexpressclient.parse.VolleyHttpRequest;
import com.mowares.massagerexpressclient.utils.AndyUtils;
import com.mowares.massagerexpressclient.utils.AppLog;
import com.mowares.massagerexpressclient.utils.Const;
import com.mowares.massagerexpressclient.utils.PreferenceHelper;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * @author Kishan H Dhamat
 */
public class HistoryActivity extends BaseActivity implements OnItemClickListener, View.OnClickListener, Response.ErrorListener, AsyncTaskCompleteListener {

    private TreeSet<Integer> mSeparatorsSet = new TreeSet<Integer>();
    private PinnedSectionListView lvHistory;
    private HistoryAdapter historyAdapter;
    private ArrayList<History> historyList;
    private ArrayList<History> historyListOrg;
    private PreferenceHelper preferenceHelper;
    private ParseContent parseContent;
    private ImageView tvNoHistory;
    private ArrayList<Date> dateList = new ArrayList<Date>();
    private TextView fromDateBtn;
    private TextView toDateBtn;
    Calendar cal;
    private int day;
    private int month;
    private int year;
    private DatePickerDialog fromPiker;
    private OnDateSetListener dateset;
    private DatePickerDialog toPiker;
    private RequestQueue requestQueue;
    private ImageButton btnActionBack;
    protected MyTitleFontTextView txtTitle;
    private String todays_date;
    private SimpleDateFormat sdf;
    private int my_noks = 0, future_noks = 0;

    /*
     * (non-Javadoc)
     *
     * @see android.support.v7.app.ActionBarActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        InitUI();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("My NOKS")) {
                my_noks = bundle.getInt("My NOKS");
                Const.menu_myProfile = 1;
                Const.menu_newNok = 1;
                Const.menu_customerService = 1;
                Const.menu_getReady = 1;
                Const.menu_futureNok = 1;
                Const.menu_mynok = 0;
                txtTitle.setText("My NOKS");
                getHistory();
            }
            if (bundle.containsKey("Future NOKS")) {
                future_noks = bundle.getInt("Future NOKS");
                Const.menu_myProfile = 1;
                Const.menu_newNok = 1;
                Const.menu_customerService = 1;
                Const.menu_getReady = 1;
                Const.menu_futureNok = 0;
                Const.menu_mynok = 1;
                txtTitle.setText("Future NOKS");
                getFutureNoks();
            }
        } else {
            getHistory();
        }
    }

    private void InitUI() {
        txtTitle = (MyTitleFontTextView) findViewById(R.id.tvTitle);
        txtTitle.setText(getString(R.string.text_history));
        cal = Calendar.getInstance();
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        todays_date = sdf.format(cal.getTime());
        //	setIcon(R.drawable.back);
        requestQueue = Volley.newRequestQueue(this);
        lvHistory = (PinnedSectionListView) findViewById(R.id.lvHistory);
        lvHistory.setOnItemClickListener(this);
        fromDateBtn = (TextView) findViewById(R.id.fromDateBtn);
        toDateBtn = (TextView) findViewById(R.id.toDateBtn);
        fromDateBtn.setOnClickListener(this);
        toDateBtn.setOnClickListener(this);
        historyList = new ArrayList<History>();

        tvNoHistory = (ImageView) findViewById(R.id.ivEmptyView);
        findViewById(R.id.btnSearch).setOnClickListener(this);
        preferenceHelper = new PreferenceHelper(this);
        parseContent = new ParseContent(this);
        dateList = new ArrayList<Date>();
        historyListOrg = new ArrayList<History>();
        btnActionBack = (ImageButton) findViewById(R.id.btnActionBack);
        btnActionMenu = (ImageButton) findViewById(R.id.btnActionMenu);
        btnActionBack.setOnClickListener(this);
        btnActionMenu.setOnClickListener(this);
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);
        dateset = new OnDateSetListener() {
            private String userDate;

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                userDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                if (view == fromPiker.getDatePicker())
                    fromDateBtn.setText(userDate);
                else
                    toDateBtn.setText(userDate);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (my_noks == 1) {
            Const.menu_myProfile = 1;
            Const.menu_newNok = 1;
            Const.menu_customerService = 1;
            Const.menu_getReady = 1;
            Const.menu_futureNok = 1;
            Const.menu_mynok = 0;
        } else if (future_noks == 1) {
            Const.menu_myProfile = 1;
            Const.menu_newNok = 1;
            Const.menu_customerService = 1;
            Const.menu_getReady = 1;
            Const.menu_futureNok = 0;
            Const.menu_mynok = 1;
        }
        selectedPosition = -1;
    }

    public void showWalkerDetailDialog() {
    }

    public void showBillDialog(final ArrayList<TypeInvoice> listTypeInvoice,
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


        ((TextView) mDialog.findViewById(R.id.tvTotal1)).setText(totalTmp);

        Button btnConfirm = (Button) mDialog
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

    /**
     *
     */
    private void getHistory() {
        if (!AndyUtils.isNetworkAvailable(this)) {
            AndyUtils.showToast(
                    getResources().getString(R.string.dialog_no_inter_message),
                    this);
            return;
        }
        AndyUtils.showCustomProgressDialog(this,
                getResources().getString(R.string.progress_getting_history),
                false, null);
        HashMap<String, String> map = new HashMap<String, String>();
        if (fromDateBtn.getText().toString()
                .equals(getString(R.string.text_from_date))
                && toDateBtn.getText().toString()
                .equals(getString(R.string.text_to_date))) {
            map.put(Const.URL,
                    Const.ServiceType.HISTORY + Const.Params.ID + "="
                            + preferenceHelper.getUserId() + "&"
                            + Const.Params.TOKEN + "="
                            + preferenceHelper.getSessionToken());
        } else {
            AppLog.Log(
                    "History",
                    Const.ServiceType.HISTORY + Const.Params.ID + "="
                            + preferenceHelper.getUserId() + "&"
                            + Const.Params.TOKEN + "="
                            + preferenceHelper.getSessionToken() + "&"
                            + Const.Params.FROM_DATE + "="
                            + fromDateBtn.getText().toString() + "&"
                            + Const.Params.TO_DATE + "="
                            + toDateBtn.getText().toString());
            map.put(Const.URL,
                    Const.ServiceType.HISTORY + Const.Params.ID + "="
                            + preferenceHelper.getUserId() + "&"
                            + Const.Params.TOKEN + "="
                            + preferenceHelper.getSessionToken() + "&"
                            + Const.Params.FROM_DATE + "="
                            + fromDateBtn.getText().toString() + "&"
                            + Const.Params.TO_DATE + "="
                            + toDateBtn.getText().toString());
        }
        // new HttpRequester(this, map, Const.ServiceCode.HISTORY, true, this);
        requestQueue.add(new VolleyHttpRequest(Method.GET, map,
                Const.ServiceCode.HISTORY, this, this));
    }

    private void getFutureNoks() {
        if (!AndyUtils.isNetworkAvailable(this)) {
            AndyUtils.showToast(
                    getResources().getString(R.string.dialog_no_inter_message),
                    this);
            return;
        }
        AndyUtils.showCustomProgressDialog(this,
                getResources().getString(R.string.text_loading),
                false, null);
        HashMap<String, String> map = new HashMap<String, String>();

        AppLog.Log(
                "Future Noks",
                Const.ServiceType.HISTORY + Const.Params.ID + "="
                        + preferenceHelper.getUserId() + "&"
                        + Const.Params.TOKEN + "="
                        + preferenceHelper.getSessionToken() + "&"
                        + Const.Params.FROM_DATE + "="
                        + todays_date + "&"
                        + Const.Params.TO_DATE + "="
                        + "");
        map.put(Const.URL,
                Const.ServiceType.HISTORY + Const.Params.ID + "="
                        + preferenceHelper.getUserId() + "&"
                        + Const.Params.TOKEN + "="
                        + preferenceHelper.getSessionToken() + "&"
                        + Const.Params.FROM_DATE + "="
                        + todays_date + "&"
                        + Const.Params.TO_DATE + "="
                        + "");
        // new HttpRequester(this, map, Const.ServiceCode.HISTORY, true, this);
        requestQueue.add(new VolleyHttpRequest(Method.GET, map,
                Const.ServiceCode.HISTORY, this, this));
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
     * .AdapterView, android.view.View, int, long)
     */
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                            long arg3) {

        if (mSeparatorsSet.contains(position))
            return;
        final History history = historyListOrg.get(position);

        showBillDialog(history.getListTypeInvoice(), history.getTotal(),
                history.getPromoBonus(), history.getReferralBonus(), null);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.uberdriverforx.parse.AsyncTaskCompleteListener#onTaskCompleted(java
     * .lang.String, int)
     */
    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        AndyUtils.removeCustomProgressDialog();
        switch (serviceCode) {
            case Const.ServiceCode.HISTORY:
                AppLog.Log("TAG", "History Response :" + response);
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    final Calendar cal = Calendar.getInstance();
                    historyListOrg.clear();
                    historyList.clear();
                    dateList.clear();
                    parseContent.parseHistory(response, historyList);

                    Collections.sort(historyList, new Comparator<History>() {
                        @Override
                        public int compare(History o1, History o2) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat(
                                    "yyyy-MM-dd");
                            try {
                                String firstStrDate = o1.getDate();
                                String secondStrDate = o2.getDate();
                                Date date2 = dateFormat.parse(secondStrDate);
                                Date date1 = dateFormat.parse(firstStrDate);
                                return date2.compareTo(date1);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            return 0;
                        }
                    });

                    HashSet<Date> listToSet = new HashSet<Date>();
                    for (int i = 0; i < historyList.size(); i++) {
//					AppLog.Log("date", historyList.get(i).getDate() + "");
                        if (listToSet.add(sdf.parse(historyList.get(i).getDate()))) {
                            dateList.add(sdf.parse(historyList.get(i).getDate()));
                        }

                    }

                    for (int i = 0; i < dateList.size(); i++) {

                        cal.setTime(dateList.get(i));
                        History item = new History();
                        item.setDate(sdf.format(dateList.get(i)));
                        historyListOrg.add(item);

                        mSeparatorsSet.add(historyListOrg.size() - 1);
                        for (int j = 0; j < historyList.size(); j++) {
                            Calendar messageTime = Calendar.getInstance();
                            messageTime.setTime(sdf.parse(historyList.get(j)
                                    .getDate()));
                            if (cal.getTime().compareTo(messageTime.getTime()) == 0) {
                                historyListOrg.add(historyList.get(j));
                            }
                        }
                    }

                    if (historyList.size() > 0) {
                        lvHistory.setVisibility(View.VISIBLE);
                        tvNoHistory.setVisibility(View.GONE);
                    } else {
                        lvHistory.setVisibility(View.GONE);
                        tvNoHistory.setVisibility(View.VISIBLE);
                    }
//				Log.i("historyListOrg size  ", "" + historyListOrg.size());

                    historyAdapter = new HistoryAdapter(this, historyListOrg,
                            mSeparatorsSet);
                    lvHistory.setAdapter(historyAdapter);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
            case R.id.btnActionMenu:
                popupWindow.showAsDropDown(v, 0, 0);
                break;
            case R.id.fromDateBtn:
                fromPiker = new DatePickerDialog(this, dateset, year, month, day);
                fromPiker.show();
                break;
            case R.id.toDateBtn:
                toPiker = new DatePickerDialog(this, dateset, year, month, day);
                toPiker.show();
                break;
            case R.id.btnSearch:
                getHistory();
                break;
            default:
                break;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.uberorg.ActionBarBaseActivitiy#isValidate()
     */
    protected boolean isValidate() {
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        // TODO Auto-generated method stub
        AppLog.Log(Const.TAG, error.toString());

    }
}
