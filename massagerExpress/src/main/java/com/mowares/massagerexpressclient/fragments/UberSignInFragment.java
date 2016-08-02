package com.mowares.massagerexpressclient.fragments;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.mowares.massagerexpressclient.MainDrawerActivity;
import com.mowares.massagerexpressclient.R;
import com.mowares.massagerexpressclient.component.MyFontButton;
import com.mowares.massagerexpressclient.component.MyFontEdittextView;
import com.mowares.massagerexpressclient.parse.ParseContent;
import com.mowares.massagerexpressclient.parse.VolleyHttpRequest;
import com.mowares.massagerexpressclient.utils.AndyUtils;
import com.mowares.massagerexpressclient.utils.AppLog;
import com.mowares.massagerexpressclient.utils.Const;
import com.mowares.massagerexpressclient.utils.PreferenceHelper;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.Permission.Type;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnProfileListener;

/**
 * @author Hardik A Bhalodi
 */
public class UberSignInFragment extends UberBaseFragmentRegister
        implements
        com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks,
        com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener {

    private MyFontEdittextView etEmail, etPassword;
    private MyFontButton btnSignIn;
    private ImageButton btnGPlus;
    private ImageButton btnFb;

    // Gplus
    private ConnectionResult mConnectionResult;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private static final int RC_SIGN_IN = 0;
    private boolean mSignInClicked;

    // FB
    private SimpleFacebook mSimpleFacebook;

    private Button btnForgetPassowrd;
    private EditText etNumber;
    private ParseContent pContent;
    private RequestQueue requestQueue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Scope scope = new Scope("https://www.googleapis.com/auth/plus.login");
        // Scope scopePro = new
        // Scope("https://www.googleapis.com/auth/plus.me");
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(scope).build();
        requestQueue = Volley.newRequestQueue(activity);
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        activity.setTitle(getResources().getString(R.string.text_signin));
        activity.btnActionMenu.setVisibility(View.GONE);
        View view = inflater.inflate(R.layout.login, container, false);
        etEmail = (MyFontEdittextView) view.findViewById(R.id.etEmail);
        etPassword = (MyFontEdittextView) view.findViewById(R.id.etPassword);
        btnSignIn = (MyFontButton) view.findViewById(R.id.btnSignIn);
        btnGPlus = (ImageButton) view.findViewById(R.id.btnGplus);
        btnFb = (ImageButton) view.findViewById(R.id.btnFb);
        etNumber = (EditText) view.findViewById(R.id.etNumber);
        btnForgetPassowrd = (Button) view.findViewById(R.id.btnForgetPassword);
        btnForgetPassowrd.setOnClickListener(this);
        btnGPlus.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
        btnFb.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);

        pContent = new ParseContent(activity);
        return view;
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub

        super.onResume();
        activity.currentFragment = Const.FRAGMENT_SIGNIN;
        activity.actionBar.setTitle(getString(R.string.text_signin));
        mSimpleFacebook = SimpleFacebook.getInstance(activity);

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btnFb:
                if (!mSimpleFacebook.isLogin()) {
                    activity.setFbTag(Const.FRAGMENT_SIGNIN);
                    mSimpleFacebook.login(new OnLoginListener() {

                        @Override
                        public void onLogin(String accessToken, List<Permission> acceptedPermissions, List<Permission> declinedPermissions) {
                            Toast.makeText(activity, "success", Toast.LENGTH_SHORT)
                                    .show();
                        }

                        @Override
                        public void onCancel() {

                        }

                        @Override
                        public void onFail(String arg0) {
                            Toast.makeText(activity, "fb login failed",
                                    Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onException(Throwable arg0) {
                        }


                        public void onNotAcceptingPermissions(Type arg0) {
                            // Log.w("UBER",
                            // String.format(
                            // "You didn't accept %s permissions",
                            // arg0.name()));
                        }

                    });
                } else {
                    getProfile();
                }
                break;
            case R.id.btnGplus:
                mSignInClicked = true;
                if (!mGoogleApiClient.isConnecting()) {
                    AndyUtils.showCustomProgressDialog(activity,
                            getString(R.string.text_getting_info), true, null);

                    mGoogleApiClient.connect();
                }
                break;
            case R.id.btnSignIn:
                if (isValidate()) {
                    login();
                }
                break;
            case R.id.btnForgetPassword:
                activity.addFragment(new ForgetPasswordFragment(), true,
                        Const.FOREGETPASS_FRAGMENT_TAG);
                break;

            default:
                break;
        }
    }

    private void getProfile() {
        AndyUtils.showCustomProgressDialog(activity,
                getString(R.string.text_getting_info), true, null);

        mSimpleFacebook.getProfile(new OnProfileListener() {
            @Override
            public void onComplete(Profile profile) {
                AndyUtils.removeCustomProgressDialog();
                Log.i("Uber", "My profile id = " + profile.getId());
                btnGPlus.setEnabled(false);
                btnFb.setEnabled(false);
                loginSocial(profile.getId(), Const.SOCIAL_FACEBOOK);
            }
        });
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // TODO Auto-generated method stub
        if (!mIntentInProgress) {
            // Store the ConnectionResult so that we can use it later when the
            // user clicks
            // 'sign-in'.

            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all

                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        // TODO Auto-generated method stub
        AndyUtils.removeCustomProgressDialog();
        mSignInClicked = false;
        btnGPlus.setEnabled(false);

        String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
        Person currentPerson = Plus.PeopleApi
                .getCurrentPerson(mGoogleApiClient);

        String personName = currentPerson.getDisplayName();

        String personPhoto = currentPerson.getImage().toString();
        String personGooglePlusProfile = currentPerson.getUrl();
        loginSocial(currentPerson.getId(), Const.SOCIAL_GOOGLE);
        // signIn();
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void resolveSignInError() {

        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                activity.startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(), RC_SIGN_IN, null,
                        0, 0, 0, Const.FRAGMENT_SIGNIN);
            } catch (SendIntentException e) {
                // The intent was canceled before it was sent. Return to the
                // default
                // state and attempt to connect to get an updated
                // ConnectionResult.
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            } catch (NullPointerException e) {
                Log.i("Exception is", "" + e);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (requestCode == RC_SIGN_IN) {

            if (resultCode != Activity.RESULT_OK) {
                mSignInClicked = false;
                AndyUtils.removeCustomProgressDialog();
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        } else {

            mSimpleFacebook.onActivityResult(requestCode, resultCode,
                    data);
            if (mSimpleFacebook.isLogin()) {
                getProfile();
            } else {
                Toast.makeText(activity, "facebook login failed",
                        Toast.LENGTH_SHORT).show();
            }

            super.onActivityResult(requestCode, resultCode, data);

        }

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    protected boolean isValidate() {
        String msg = null;
        if (TextUtils.isEmpty(etEmail.getText().toString())
                && TextUtils.isEmpty(etNumber.getText().toString())) {
            msg = getResources().getString(R.string.text_enter_email);
        } else if (!TextUtils.isEmpty(etEmail.getText().toString())) {
            if (!AndyUtils.eMailValidation(etEmail.getText().toString())) {
                msg = getResources().getString(R.string.text_enter_valid_email);
            }
        }
        if (TextUtils.isEmpty(etPassword.getText().toString())) {
            msg = getResources().getString(R.string.text_enter_password);
        }
        if (msg == null)
            return true;

        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
        return false;
    }

    // private void signIn() {
    // Intent intent = new Intent(activity, MainDrawerActivity.class);
    // startActivity(intent);
    // activity.finish();
    // }

    private void login() {
        if (!AndyUtils.isNetworkAvailable(activity)) {
            AndyUtils.showToast(getResources().getString(R.string.no_internet),
                    activity);
            return;
        }
        AndyUtils.showCustomProgressDialog(activity,
                getResources().getString(R.string.text_signing), false, null);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.LOGIN);
        map.put(Const.Params.EMAIL, etEmail.getText().toString());
        map.put(Const.Params.PASSWORD, etPassword.getText().toString());
        map.put(Const.Params.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID);
        map.put(Const.Params.DEVICE_TOKEN,
                new PreferenceHelper(activity).getDeviceToken());
        map.put(Const.Params.LOGIN_BY, Const.MANUAL);
        // new HttpRequester(activity, map, Const.ServiceCode.LOGIN, this);
        requestQueue.add(new VolleyHttpRequest(Method.POST, map,
                Const.ServiceCode.LOGIN, this, this));
    }

    private void loginSocial(String id, String loginType) {
        if (!AndyUtils.isNetworkAvailable(activity)) {
            AndyUtils.showToast(getResources().getString(R.string.no_internet),
                    activity);
            return;
        }
        AndyUtils.showCustomProgressDialog(activity,
                getResources().getString(R.string.text_signin), false, null);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.LOGIN);
        map.put(Const.Params.SOCIAL_UNIQUE_ID, id);
        map.put(Const.Params.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID);
        map.put(Const.Params.DEVICE_TOKEN,
                new PreferenceHelper(activity).getDeviceToken());
        map.put(Const.Params.LOGIN_BY, loginType);
        // new HttpRequester(activity, map, Const.ServiceCode.LOGIN, this);
        requestQueue.add(new VolleyHttpRequest(Method.POST, map,
                Const.ServiceCode.LOGIN, this, this));

    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        // TODO Auto-generated method stub
        ParseContent parseContent = new ParseContent(activity);
        AndyUtils.removeCustomProgressDialog();
        super.onTaskCompleted(response, serviceCode);
        switch (serviceCode) {
            case Const.ServiceCode.LOGIN:
                if (parseContent.isSuccessWithStoreId(response)) {
                    parseContent.parseUserAndStoreToDb(response);
                    new PreferenceHelper(activity).putPassword(etPassword.getText()
                            .toString());
                    if (isAdded()) {
                        startActivity(new Intent(activity, MainDrawerActivity.class));
                        activity.finish();
                    }
                } else {
                    if (parseContent.isNotVerified(response)) {
                        activity.phelper.putEmail(etEmail.getText().toString()
                                .trim());
                        activity.phelper.putPassword(etPassword.getText()
                                .toString());
                        goToVerifyFragment();
                    } else {
                        AndyUtils.showToast(
                                getResources().getString(R.string.signin_failed),
                                activity);
                        btnFb.setEnabled(true);
                        btnGPlus.setEnabled(true);
                    }
                }
                break;

            default:
                break;
        }
    }

    private void goToVerifyFragment() {
        VerifyCodeFragment verifyFragment = new VerifyCodeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Const.Params.TOKEN, activity.phelper.getSessionToken());
        bundle.putString(Const.Params.ID, activity.phelper.getUserId());
        verifyFragment.setArguments(bundle);
        activity.addFragment(verifyFragment, false, Const.FRAGMENT_VERIFY);
    }

    @Override
    public boolean OnBackPressed() {
        // TODO Auto-generated method stub
        // activity.removeAllFragment(new UberMainFragment(), false,
        // Const.FRAGMENT_MAIN);
        activity.goToMainActivity();
        return false;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        // TODO Auto-generated method stub
        AppLog.Log(Const.TAG, error.getMessage());
    }

	/*
     * (non-Javadoc)
	 * 
	 * @see com.uberorg.fragments.BaseFragmentRegister#OnBackPressed()
	 */

}
