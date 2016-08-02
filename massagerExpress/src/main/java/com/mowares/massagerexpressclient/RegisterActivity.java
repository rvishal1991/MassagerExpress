package com.mowares.massagerexpressclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.android.volley.VolleyError;
import com.mowares.massagerexpressclient.fragments.UberMainFragment;
import com.mowares.massagerexpressclient.fragments.UberRegisterFragment;
import com.mowares.massagerexpressclient.fragments.UberSignInFragment;
import com.mowares.massagerexpressclient.fragments.VerifyCodeFragment;
import com.mowares.massagerexpressclient.utils.AndyUtils;
import com.mowares.massagerexpressclient.utils.AppLog;
import com.mowares.massagerexpressclient.utils.Const;
import com.mowares.massagerexpressclient.utils.PreferenceHelper;
import com.splunk.mint.Mint;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;


/**
 * @author Hardik A Bhalodi
 */
public class RegisterActivity extends ActionBarBaseActivitiy {

    Permission[] permissions = new Permission[]{Permission.EMAIL};
    SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
            .setPermissions(permissions).build();
    public PreferenceHelper phelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Mint.initAndStartSession(RegisterActivity.this, "1a9fb841"); // actionBar.hide();
        setContentView(R.layout.register_activity);
        SimpleFacebook.setConfiguration(configuration);
        phelper = new PreferenceHelper(this);
        // actionBar.setDisplayShowHomeEnabled(true);
        // actionBar.setDisplayHomeAsUpEnabled(true);

        //setIcon(R.drawable.back);

        if (getIntent().getBooleanExtra("goToVerify", false)) {
            goToVerifyFragment();
        } else if (getIntent().getBooleanExtra("isSignin", false)) {
            gotSignInFragment();
        } else {
            goToRegisterFragment();
        }
        UberMainFragment mainFrag = new UberMainFragment();
        addFragment(mainFrag, false, Const.FRAGMENT_MAIN);
    }

    @Override
    protected void onDestroy() {
        Mint.flush();
        Mint.closeSession(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnActionNotification:
                onBackPressed();
                break;

            default:
                break;
        }
    }

	public void registerGcmReceiver(BroadcastReceiver mHandleMessageReceiver) {
        if (mHandleMessageReceiver != null) {
			AndyUtils.showCustomProgressDialog(this,
					getString(R.string.progress_loading), false, null);
			//new GCMRegisterHendler(RegisterActivity.this, mHandleMessageReceiver);

		}
	}

    public void unregisterGcmReceiver(BroadcastReceiver mHandleMessageReceiver) {
        if (mHandleMessageReceiver != null) {

            if (mHandleMessageReceiver != null) {
//			unregisterReceiver(mHandleMessageReceiver);
            }
        }
    }

    private void gotSignInFragment() {
        UberSignInFragment signInFrag = new UberSignInFragment();
        clearBackStack();
        addFragment(signInFrag, false, Const.FRAGMENT_SIGNIN);
    }

    private void goToRegisterFragment() {
        UberRegisterFragment regFrag = new UberRegisterFragment();
        clearBackStack();
        addFragment(regFrag, false, Const.FRAGMENT_REGISTER);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.uberorg.ActionBarBaseActivitiy#isValidate()
     */
    @Override
    protected boolean isValidate() {
        return false;
    }

    public void showKeyboard(View v) {
        InputMethodManager inputManager = (InputMethodManager) this
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        // View view = activity.getCurrentFocus();
        // if (view != null) {
        inputManager.showSoftInput(v, InputMethodManager.SHOW_FORCED);
        // }
    }

    private void goToVerifyFragment() {
        VerifyCodeFragment verifyFragment = new VerifyCodeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Const.Params.TOKEN, phelper.getSessionToken());
        bundle.putString(Const.Params.ID, phelper.getUserId());
        verifyFragment.setArguments(bundle);
        addFragment(verifyFragment, false, Const.FRAGMENT_VERIFY);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        // TODO Auto-generated method stub
        AppLog.Log(Const.TAG, error.getMessage());
    }
}