package com.mowares.massagerexpressclient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.mowares.massagerexpressclient.component.MyFontButton;
import com.mowares.massagerexpressclient.component.MyFontEdittextView;
import com.mowares.massagerexpressclient.component.MyFontTextView;
import com.mowares.massagerexpressclient.parse.AsyncTaskCompleteListener;
import com.mowares.massagerexpressclient.parse.HttpRequester;
import com.mowares.massagerexpressclient.parse.ParseContent;
import com.mowares.massagerexpressclient.parse.VolleyHttpRequest;
import com.mowares.massagerexpressclient.utils.AndyUtils;
import com.mowares.massagerexpressclient.utils.AppLog;
import com.mowares.massagerexpressclient.utils.Const;
import com.mowares.massagerexpressclient.utils.LocationHelper;
import com.mowares.massagerexpressclient.utils.PreferenceHelper;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.util.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

public class registration extends BaseActivity implements View.OnClickListener, AsyncTaskCompleteListener, Response.ErrorListener {

    private MyFontButton imgvRegNoking;
    private final int MY_SCAN_REQUEST_CODE = 111;
    private MyFontEdittextView txtFname, txtLName, txtEmail, txtCreditCardNum, txtCvc;
    private MyFontTextView txtMonth, txtYear;
    /* private Spinner spMonth, spYear;*/
    private LinearLayout llMonth, llYear, llregistration;
    private RadioButton rbPaypal, rbCreditCard;
    public static final String[] PREFIXES_AMERICAN_EXPRESS = {"34", "37"};
    public static final String[] PREFIXES_DISCOVER = {"60", "62", "64", "65"};
    public static final String[] PREFIXES_JCB = {"35"};
    public static final String[] PREFIXES_DINERS_CLUB = {"300", "301", "302",
            "303", "304", "305", "309", "36", "38", "37", "39"};
    public static final String[] PREFIXES_VISA = {"4"};
    public static final String[] PREFIXES_MASTERCARD = {"50", "51", "52",
            "53", "54", "55"};
    public static final String AMERICAN_EXPRESS = "American Express";
    public static final String DISCOVER = "Discover";
    public static final String JCB = "JCB";
    public static final String DINERS_CLUB = "Diners Club";
    public static final String VISA = "Visa";
    public static final String MASTERCARD = "MasterCard";
    public static final String UNKNOWN = "Unknown";
    public static final int MAX_LENGTH_STANDARD = 16;
    public static final int MAX_LENGTH_AMERICAN_EXPRESS = 15;
    public static final int MAX_LENGTH_DINERS_CLUB = 14;
    private String type, loginType, token, id, Bmonth = "12";
    protected PreferenceHelper preferenceHelper;
    private ParseContent pContent;
    private int month;
    private RequestQueue requestQueue;
    private RelativeLayout rltop;
    private String[] arrayMonth;
    private ImageButton btnActionBack, btnActionMenu, imgCam;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setTitle(getString(R.string.text_registration));
        init();
        preferenceHelper = new PreferenceHelper(this);
        loginType = preferenceHelper.getLoginBy();
        requestQueue = Volley.newRequestQueue(this);
        token = preferenceHelper.getSessionToken();
        id = preferenceHelper.getUserId();
        pContent = new ParseContent(this);
        ArrayList<String> years = new ArrayList<String>();
        for (int i = 2016; i <= 2035; i++) {
            years.add(Integer.toString(i));
        }
        final CharSequence[] ArrayYears = years.toArray(new CharSequence[years.size()]);

        ArrayAdapter<String> YearList = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, years);
        YearList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> MonthList = ArrayAdapter.createFromResource(getApplicationContext(), R.array.months, android.R.layout.simple_spinner_item);
        MonthList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Drop down layout style - list view with radio button

        llregistration.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                AndyUtils.hideSoftKeyboard(registration.this);
                return false;
            }
        });

        txtCreditCardNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (TextUtils.isBlank(s.toString())) {
                    txtCreditCardNum.setCompoundDrawablesWithIntrinsicBounds(
                            null, null, null, null);
                }
                type = getType(s.toString());

                if (type.equals(VISA)) {
                    txtCreditCardNum.setCompoundDrawablesWithIntrinsicBounds(
                            getResources().getDrawable(
                                    R.drawable.ub__creditcard_visa), null,
                            null, null);

                } else if (type.equals(MASTERCARD)) {
                    txtCreditCardNum.setCompoundDrawablesWithIntrinsicBounds(
                            getResources().getDrawable(
                                    R.drawable.ub__creditcard_mastercard),
                            null, null, null);

                } else if (type.equals(AMERICAN_EXPRESS)) {
                    txtCreditCardNum.setCompoundDrawablesWithIntrinsicBounds(
                            getResources().getDrawable(
                                    R.drawable.ub__creditcard_amex), null,
                            null, null);

                } else if (type.equals(DISCOVER)) {
                    txtCreditCardNum.setCompoundDrawablesWithIntrinsicBounds(
                            getResources().getDrawable(
                                    R.drawable.ub__creditcard_discover), null,
                            null, null);

                } else if (type.equals(DINERS_CLUB)) {
                    txtCreditCardNum.setCompoundDrawablesWithIntrinsicBounds(
                            getResources().getDrawable(
                                    R.drawable.ub__creditcard_discover), null,
                            null, null);

                } else {
                    txtCreditCardNum.setCompoundDrawablesWithIntrinsicBounds(
                            null, null, null, null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        /*rbPaypal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (rbPaypal.isChecked()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(registration.this);
                    builder.setTitle("Scan Card");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            scan();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                }
            }
        });*/
        imgCam.setOnClickListener(this);
        arrayMonth = getResources().getStringArray(R.array.months);
        llMonth.setOnClickListener(this);
        llYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogYears = new AlertDialog.Builder(registration.this);
                dialogYears.setSingleChoiceItems(ArrayYears, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {
                        txtYear.setText(ArrayYears[position]);
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialogYears = dialogYears.create();
                alertDialogYears.show();
            }
        });
        imgvRegNoking.setOnClickListener(this);
    }

    public void init() {
        rltop = (RelativeLayout) findViewById(R.id.rltop);
        llregistration = (LinearLayout) findViewById(R.id.llregistration);
        txtFname = (MyFontEdittextView) findViewById(R.id.txtFirstName);
        txtLName = (MyFontEdittextView) findViewById(R.id.txtFamilyName);
        txtEmail = (MyFontEdittextView) findViewById(R.id.txtEmail);
        txtCreditCardNum = (MyFontEdittextView) findViewById(R.id.txtCreditCardNumber);
        imgCam = (ImageButton) findViewById(R.id.imgCamera);
        txtCvc = (MyFontEdittextView) findViewById(R.id.txtCVC);
        txtMonth = (MyFontTextView) findViewById(R.id.txtMonth);
        txtYear = (MyFontTextView) findViewById(R.id.txtYear);
        llMonth = (LinearLayout) findViewById(R.id.llMonth);
        llYear = (LinearLayout) findViewById(R.id.llYear);
        rbCreditCard = (RadioButton) findViewById(R.id.rbCreditcard);
       /* rbPaypal = (RadioButton) findViewById(R.id.rbPayPal);*/
        btnActionBack = (ImageButton) findViewById(R.id.btnActionBack);
        btnActionMenu = (ImageButton) findViewById(R.id.btnActionMenu);
        btnActionMenu.setOnClickListener(this);
        btnActionBack.setVisibility(View.GONE);
        imgvRegNoking = (MyFontButton) findViewById(R.id.imgvRegNoking);
    }

    public void exitAppMethod() {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        exitAppMethod();
    }

    private void scan() {
        Intent scanIntent = new Intent(this, CardIOActivity.class);

        // required for authentication with card.io
        // scanIntent.putExtra(CardIOActivity.EXTRA_APP_TOKEN,
        // Const.MY_CARDIO_APP_TOKEN);

        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default:
        // true
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); // default:
        // false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default:
        // false

        // hides the manual entry button
        // if set, developers should provide their own manual entry
        // mechanism in
        // the app
        scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, false); // default:
        // false

        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this
        // activity.
        startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MY_SCAN_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null
                            && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                        CreditCard scanResult = data
                                .getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                        // Never log a raw card number. Avoid displaying it, but if
                        // necessary use getFormattedCardNumber()
                        // resultStr = "Card Number: " +
                        // scanResult.getRedactedCardNumber()
                        // + "\n";
                        txtCreditCardNum.setText(scanResult.getRedactedCardNumber());

                        // Do something with the raw number, e.g.:
                        // myService.setCardNumber( scanResult.cardNumber );

                        if (scanResult.isExpiryValid()) {
                            //comment by me

                           /* spMonth.setId(scanResult.expiryMonth + 1);
                            //spMonth.setText(scanResult.expiryMonth + "");
                            spYear.setText(scanResult.expiryYear + "");*/
                        }

                        if (scanResult.cvv != null) {
                            // Never log or display a CVV
                            // resultStr += "CVV has " + scanResult.cvv.length()
                            // + " digits.\n";
                            txtCvc.setText(scanResult.cvv);
                        }

                        // if (scanResult.postalCode != null) {
                        // resultStr += "Postal Code: " + scanResult.postalCode +
                        // "\n";
                        // }
                    } else {
                        // resultStr = "Scan was canceled.";
                        AndyUtils.showToast("Scan was canceled.", this);
                    }
                } else {
                    AndyUtils.showToast("Scan was uncessfull.", this);
                }
                break;

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgvRegNoking:
                if (isValidate()) {
                    String lastFour = txtCreditCardNum.getText().toString();
                    lastFour = lastFour.substring(lastFour.length() - 4);
                    AppLog.Log("Last Four digits are:", "" + lastFour);
                    onSubmitButtonClick();
                }
                break;
            case R.id.llMonth:
                AlertDialog.Builder dialogMonth = new AlertDialog.Builder(registration.this);
                dialogMonth.setSingleChoiceItems(arrayMonth, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {
                        txtMonth.setText(arrayMonth[position]);
                        month = position + 1;
                        if (month < 10) {
                            Bmonth = "0" + month;
                        } else {
                            Bmonth = "" + month;
                        }

                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = dialogMonth.create();
                alertDialog.show();
                break;
            case R.id.btnActionMenu:
                popupWindow.showAsDropDown(v, 0, 0);
                break;
            case R.id.imgCamera:
                AlertDialog.Builder builder = new AlertDialog.Builder(registration.this);
                builder.setTitle("Scan Card");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        scan();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialogCamera = builder.create();
                alertDialogCamera.show();

                break;
        }
    }

    private void addCard(String stripeToken, String lastFour) {
        // AppLog.Log(TAG, "Final token : " + peachToken.substring(3));
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.ADD_CARD);
        map.put(Const.Params.ID, new PreferenceHelper(this).getUserId());
        map.put(Const.Params.TOKEN,
                new PreferenceHelper(this).getSessionToken());
        map.put(Const.Params.STRIPE_TOKEN, stripeToken);
        map.put(Const.Params.LAST_FOUR, lastFour);
        // map.put(Const.Params.CARD_TYPE, type);
        // new HttpRequester(this, map, Const.ServiceCode.ADD_CARD, this);
        requestQueue.add(new VolleyHttpRequest(Request.Method.POST, map,
                Const.ServiceCode.ADD_CARD, this, this));
    }

    public void saveCreditCard() {

        if (!AndyUtils.isNetworkAvailable(this)) {
            AndyUtils.showToast(
                    getResources().getString(R.string.dialog_no_inter_message),
                    this);
            return;
        }

        Card card = new Card(txtCreditCardNum.getText().toString(),
                Integer.parseInt(Bmonth), Integer.parseInt(txtYear.getText().toString()), txtCvc.getText().toString());

        boolean validation = card.validateCard();
        if (validation) {
            AndyUtils.showCustomProgressDialog(this,
                    getString(R.string.text_registering), false, null);
            new Stripe().createToken(card, Const.PUBLISHABLE_KEY,
                    new TokenCallback() {
                        public void onSuccess(Token token) {
                            // getTokenList().addToList(token);
                            // AndyUtils.showToast(token.getId(), activity);
                            String lastFour = txtCreditCardNum.getText().toString();
                            lastFour = lastFour.substring(lastFour.length() - 5);
                            addCard(token.getId(), lastFour);
                            // finishProgress();
                        }

                        public void onError(Exception error) {
                            AndyUtils.showToast("Error",
                                    registration.this);
                            // finishProgress();
                            AndyUtils.removeCustomProgressDialog();
                        }
                    });
        } else if (!card.validateNumber()) {
            // handleError("The card number that you entered is invalid");
            AndyUtils.showToast("The card number that you entered is invalid",
                    this);
        } else if (!card.validateExpiryDate()) {
            // handleError("");
            AndyUtils.showToast(
                    "The expiration date that you entered is invalid", this);
        } else if (!card.validateCVC()) {
            // handleError("");
            AndyUtils.showToast("The CVC code that you entered is invalid",
                    this);

        } else {
            // handleError("");
            AndyUtils.showToast(
                    "The card details that you entered are invalid", this);
        }
    }

    protected boolean isValidate() {
        if (txtCreditCardNum.getText().length() == 0
                || txtCvc.getText().length() == 0 || txtMonth.getText().length() == 0 || txtYear.getText().length() == 0) {
            AndyUtils.showToast("Enter Proper data", this);
            return false;
        }
        return true;
    }

    public String getType(String number) {
        if (!TextUtils.isBlank(number)) {
            if (TextUtils.hasAnyPrefix(number, PREFIXES_AMERICAN_EXPRESS)) {
                return AMERICAN_EXPRESS;
            } else if (TextUtils.hasAnyPrefix(number, PREFIXES_DISCOVER)) {
                return DISCOVER;
            } else if (TextUtils.hasAnyPrefix(number, PREFIXES_JCB)) {
                return JCB;
            } else if (TextUtils.hasAnyPrefix(number, PREFIXES_DINERS_CLUB)) {
                return DINERS_CLUB;
            } else if (TextUtils.hasAnyPrefix(number, PREFIXES_VISA)) {
                return VISA;
            } else if (TextUtils.hasAnyPrefix(number, PREFIXES_MASTERCARD)) {
                return MASTERCARD;
            } else {
                return UNKNOWN;
            }
        }
        return UNKNOWN;

    }

    private void onSubmitButtonClick() {
        if (txtFname.getText().length() == 0) {
            AndyUtils.showToast(
                    getResources().getString(R.string.text_enter_name), this);
            return;
        } else if (txtLName.getText().length() == 0) {
            AndyUtils.showToast(
                    getResources().getString(R.string.text_enter_lname), this);
            return;
        } else if (txtEmail.getText().length() == 0) {
            AndyUtils.showToast(
                    getResources().getString(R.string.text_enter_email), this);
            return;
        }
        else {
            saveCreditCard();
        }
    }

    private void updateSimpleProfile(String type) {

        if (!AndyUtils.isNetworkAvailable(this)) {
            AndyUtils.showToast(
                    getResources().getString(R.string.dialog_no_inter_message),
                    this);
            return;
        }
        if (type.equals(Const.MANUAL)) {
            AppLog.Log("Registration Page", "Simple Profile update method");
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(Const.URL, Const.ServiceType.UPDATE_PROFILE);
            map.put(Const.Params.ID, id);
            map.put(Const.Params.TOKEN, token);
            map.put(Const.Params.FIRSTNAME, txtFname.getText().toString());
            map.put(Const.Params.LAST_NAME, txtLName.getText().toString());
            map.put(Const.Params.EMAIL, txtEmail.getText().toString());
            new HttpRequester(this, map, Const.ServiceCode.UPDATE_PROFILE, this);
        }
    }


    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        AndyUtils.removeCustomProgressDialog();
        AppLog.Log("Response", response);
        switch (serviceCode) {
            case Const.ServiceCode.UPDATE_PROFILE:
                AndyUtils.removeCustomProgressDialog();
                if (new ParseContent(this).isSuccessWithStoreId(response)) {
                    Toast.makeText(this, getString(R.string.toast_request_again),
                            Toast.LENGTH_SHORT).show();
                    preferenceHelper.putIsRegistered(true);
                    finish();
                   /* Intent ChooseYourNok = new Intent(registration.this, ChooseYourNok.class);
                    startActivity(ChooseYourNok);*/

                } else {
                    Toast.makeText(this, getString(R.string.toast_update_failed),
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case Const.ServiceCode.ADD_CARD:

                if (new ParseContent(this).isSuccess(response)) {
                    updateSimpleProfile(loginType);
                    // setResult(Activity.RESULT_OK);
                } else {
                    AndyUtils.showToast(
                            getString(R.string.text_not_add_card_unscucess), this);
                    setResult(Activity.RESULT_CANCELED);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        AppLog.Log(Const.TAG, volleyError.getMessage());
    }

}