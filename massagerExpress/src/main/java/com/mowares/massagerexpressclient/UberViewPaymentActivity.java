/**
 *
 */
package com.mowares.massagerexpressclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.mowares.massagerexpressclient.adapter.PaymentListAdapter;
import com.mowares.massagerexpressclient.component.MyTitleFontTextView;
import com.mowares.massagerexpressclient.models.Card;
import com.mowares.massagerexpressclient.parse.AsyncTaskCompleteListener;
import com.mowares.massagerexpressclient.parse.ParseContent;
import com.mowares.massagerexpressclient.parse.VolleyHttpRequest;
import com.mowares.massagerexpressclient.utils.AndyUtils;
import com.mowares.massagerexpressclient.utils.AppLog;
import com.mowares.massagerexpressclient.utils.Const;
import com.mowares.massagerexpressclient.utils.PreferenceHelper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Hardik A Bhalodi
 */
public class UberViewPaymentActivity extends BaseActivity implements Response.ErrorListener, AsyncTaskCompleteListener, View.OnClickListener {
    private ImageButton btnActionBack, btnActionMenu;
    protected MyTitleFontTextView txtTitle;
    private ListView listViewPayment;
    private PaymentListAdapter adapter;
    private ArrayList<Card> listCards;
    private int REQUEST_ADD_CARD = 1;
    private ImageView tvNoHistory, ivCredit, ivCash;
    private TextView tvHeaderText;
    private View v;
    private TextView btnAddNewPayment;
    private int paymentMode;
    private LinearLayout llPaymentList;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_payment);
        //	setIcon(R.drawable.back);
        txtTitle = (MyTitleFontTextView) findViewById(R.id.tvTitle);
        txtTitle.setText(getString(R.string.text_cards));
        requestQueue = Volley.newRequestQueue(this);
        listViewPayment = (ListView) findViewById(R.id.listViewPayment);
        llPaymentList = (LinearLayout) findViewById(R.id.llPaymentList);
        tvNoHistory = (ImageView) findViewById(R.id.ivEmptyView);
        tvHeaderText = (TextView) findViewById(R.id.tvHeaderText);
        btnAddNewPayment = (TextView) findViewById(R.id.tvAddNewPayment);
        btnActionBack = (ImageButton) findViewById(R.id.btnActionBack);
        btnActionMenu = (ImageButton) findViewById(R.id.btnActionMenu);
        btnActionBack.setOnClickListener(this);
        btnActionMenu.setOnClickListener(this);
        ivCash = (ImageView) findViewById(R.id.ivCash);
        ivCredit = (ImageView) findViewById(R.id.ivCredit);

        btnAddNewPayment.setOnClickListener(this);
        paymentMode = (int) new PreferenceHelper(this).getPaymentMode();
        v = findViewById(R.id.line);
        listCards = new ArrayList<Card>();
        adapter = new PaymentListAdapter(this, listCards, new PreferenceHelper(
                this).getDefaultCard());
        listViewPayment.setAdapter(adapter);
        getCards();
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
            case R.id.tvAddNewPayment:
                startActivityForResult(new Intent(this,
                        UberAddPaymentActivity.class), REQUEST_ADD_CARD);
                break;
            default:
                break;
        }
    }


    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onResume()
     */
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        selectedPosition = -1;

    }

    /*
     * (non-Javadoc)
     *
     * @see com.uberorg.ActionBarBaseActivitiy#isValidate()
     */
    protected boolean isValidate() {
        // TODO Auto-generated method stub
        return false;
    }

    private void getCards() {
        AndyUtils.showCustomProgressDialog(this,
                getString(R.string.progress_loading), false, null);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL,
                Const.ServiceType.GET_CARDS + Const.Params.ID + "="
                        + new PreferenceHelper(this).getUserId() + "&"
                        + Const.Params.TOKEN + "="
                        + new PreferenceHelper(this).getSessionToken());
        // new HttpRequester(this, map, Const.ServiceCode.GET_CARDS, true,
        // this);
        requestQueue.add(new VolleyHttpRequest(Method.GET, map,
                Const.ServiceCode.GET_CARDS, this, this));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.uberorg.ActionBarBaseActivitiy#onTaskCompleted(java.lang.String,
     * int)
     */
    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        AndyUtils.removeCustomProgressDialog();
        switch (serviceCode) {
            case Const.ServiceCode.GET_CARDS:
                if (new ParseContent(this).isSuccess(response)) {
                    listCards.clear();
                    new ParseContent(this).parseCards(response, listCards);
                    AppLog.Log("UberViewPayment", "listCards : " + listCards.size());
                    if (listCards.size() > 0) {
                        llPaymentList.setVisibility(View.VISIBLE);
                        tvNoHistory.setVisibility(View.GONE);
                        paymentMode = 1;
                        tvHeaderText.setVisibility(View.VISIBLE);
                    } else {
                        llPaymentList.setVisibility(View.GONE);
                        tvNoHistory.setVisibility(View.VISIBLE);
                        tvHeaderText.setVisibility(View.GONE);
                        paymentMode = 0;
                    }
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.uberorg.ActionBarBaseActivitiy#onActivityResult(int, int,
     * android.content.Intent)
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case Activity.RESULT_OK:
                getCards();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        // TODO Auto-generated method stub
        AppLog.Log(Const.TAG, error.getMessage());
    }
}
