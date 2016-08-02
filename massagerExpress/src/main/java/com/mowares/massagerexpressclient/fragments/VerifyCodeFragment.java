/**
 * 
 */
package com.mowares.massagerexpressclient.fragments;

import java.util.HashMap;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.mowares.massagerexpressclient.R;
import com.mowares.massagerexpressclient.component.MyFontEdittextView;
import com.mowares.massagerexpressclient.parse.AsyncTaskCompleteListener;
import com.mowares.massagerexpressclient.parse.ParseContent;
import com.mowares.massagerexpressclient.parse.VolleyHttpRequest;
import com.mowares.massagerexpressclient.utils.AndyUtils;
import com.mowares.massagerexpressclient.utils.AppLog;
import com.mowares.massagerexpressclient.utils.Const;

/**
 * @author Kishan H Dhamat
 * 
 */
public class VerifyCodeFragment extends UberBaseFragmentRegister implements
		AsyncTaskCompleteListener {
	private MyFontEdittextView etVerifyCode;
	private LinearLayout llErrorMsg;
	private int is_skip = 0;
	private RequestQueue requestQueue;
	private ParseContent parseContent;
	private String token, id;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.automated.taxinow.fragments.UberBaseFragmentRegister#onCreate(android
	 * .os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);
		requestQueue = Volley.newRequestQueue(activity);
		token = getArguments().getString(Const.Params.TOKEN);
		id = getArguments().getString(Const.Params.ID);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		activity.btnActionMenu.setVisibility(View.GONE);
		activity.setTitle(getString(R.string.text_verification));
	//	activity.btnNotification.setVisibility(View.INVISIBLE);
		View verifyView = inflater.inflate(R.layout.verification_code_fragment,
				container, false);
		etVerifyCode = (MyFontEdittextView) verifyView
				.findViewById(R.id.etVerifyCode);
		llErrorMsg = (LinearLayout) verifyView.findViewById(R.id.llErrorMsg);
		verifyView.findViewById(R.id.btnVerifySubmit).setOnClickListener(this);
		parseContent = new ParseContent(activity);

		return verifyView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		etVerifyCode.requestFocus();
		activity.showKeyboard(etVerifyCode);
		// (getResources().getString(
		// R.string.text_forget_password));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.automated.taxinow.fragments.UberBaseFragmentRegister#onResume()
	 */
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		activity.currentFragment = Const.FRAGMENT_VERIFY;
		super.onResume();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnVerifySubmit:
			if (etVerifyCode.getText().length() == 0) {
				AndyUtils.showToast(
						getResources().getString(
								R.string.text_blank_verify_code), activity);
				return;
			} else {
				if (!AndyUtils.isNetworkAvailable(activity)) {
					AndyUtils
							.showToast(
									getResources().getString(
											R.string.dialog_no_inter_message),
									activity);
					return;
				}
				is_skip = 0;
				applyVerifyCode(true);
			}
			break;
		default:
			break;
		}
	}

	private void applyVerifyCode(boolean isShowLoader) {
		if (isShowLoader)
			AndyUtils.showCustomProgressDialog(activity,
					getString(R.string.progress_loading), false, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.VERIFY_CODE);
		map.put(Const.Params.VERIFY_CODE, etVerifyCode.getText().toString());

		map.put(Const.Params.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID);

		map.put(Const.Params.DEVICE_TOKEN, activity.phelper.getDeviceToken());

		map.put(Const.Params.EMAIL, activity.phelper.getEmail());

		// new HttpRequester(activity, map,
		// Const.ServiceCode.APPLY_REFFRAL_CODE,
		// this);
		requestQueue.add(new VolleyHttpRequest(Method.POST, map,
				Const.ServiceCode.VERIFY_CODE, this, this));
	}

	@Override
	public void onTaskCompleted(String response, int serviceCode) {
		// TODO Auto-generated method stub
		AndyUtils.removeCustomProgressDialog();
		AppLog.Log(Const.TAG, "VerifyCode Response ::: " + response);
		switch (serviceCode) {
		case Const.ServiceCode.VERIFY_CODE:
			if (parseContent.isSuccess(response)) {
				// parseContent.parseUserAndStoreToDb(response);
				activity.phelper.putIsVerify(true);
				goToReffralCodeFragment(activity.phelper.getUserId(),
						activity.phelper.getSessionToken());

				AppLog.Log("onTaskCompleted",
						"id:" + activity.phelper.getUserId());

				AppLog.Log("onTaskCompleted",
						"token:" + activity.phelper.getSessionToken());
			} else {
				llErrorMsg.setVisibility(View.VISIBLE);
				etVerifyCode.requestFocus();
			}
			break;

		default:
			break;
		}
	}

	private void goToReffralCodeFragment(String id, String token) {
		ReffralCodeFragment reffralCodeFragment = new ReffralCodeFragment();
		Bundle bundle = new Bundle();
		bundle.putString(Const.Params.TOKEN, token);
		bundle.putString(Const.Params.ID, id);
		reffralCodeFragment.setArguments(bundle);
		activity.addFragment(reffralCodeFragment, false,
				Const.FRAGMENT_REFFREAL);
	}

	@Override
	protected boolean isValidate() {
		return false;
	}

	@Override
	public boolean OnBackPressed() {
		return true;
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		AppLog.Log(Const.TAG, error.getMessage());

	}

}
