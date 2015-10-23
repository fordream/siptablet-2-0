package com.shopivr.component.base;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.csipsimple.api.ISipService;
import com.csipsimple.api.SipProfile;
import com.csipsimple.utils.AccountListUtils;
import com.shopivr.component.model.TADataStore;

public class BaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getApplication().getContentResolver().registerContentObserver(SipProfile.ACCOUNT_STATUS_URI, true, contentObserver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		getApplication().getContentResolver().unregisterContentObserver(contentObserver);
	}

	private ContentObserver contentObserver = new ContentObserver(new Handler()) {
		public void onChange(boolean selfChange) {
			_onChange();
		}
	};

	/**
	 * on login changle
	 * 
	 * @param accountStatusDisplay
	 */
	public AccountListUtils.AccountStatusDisplay _onChange() {
		long id = TADataStore.getActiveId(BaseActivity.this);
		if (id != -1) {
			return AccountListUtils.getAccountDisplay(BaseActivity.this, id);
		} else {
			return null;
		}
	}

	public ISipService getISipService() {
		return ((TAMainApplication) getApplication()).getService();
	}

	public void setVisibility(int res, boolean isVisibility) {
		int visibility = isVisibility ? View.VISIBLE : View.GONE;
		findViewById(res).setVisibility(visibility);
	}

	public void startActivityAndFinish(Class<?> class1) {
		startActivity(new Intent(this, class1));
		finish();
	}

	public void makeText(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

//	/** check Proximity Service is running? */
//	public static boolean isServiceRunning(Context context) {
//		boolean result = false;
//		ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
//		List<RunningServiceInfo> rsi = am.getRunningServices(Integer.MAX_VALUE);
//		for (RunningServiceInfo runningServiceInfo : rsi) {
//			if (runningServiceInfo.service.getClassName().equals(ProximityService.class.getName())) {
//				result = true;
//			}
//		}
//		return result;
//	}
//
//	/** Start/stop Proximity Service */
//	public static void ProximityService(Context context, boolean start) {
//		Intent intentService = new Intent(context, ProximityService.class);
//		if (isServiceRunning(context))
//			context.stopService(intentService);
//		if (start)
//			context.startService(intentService);
//		else
//			context.stopService(intentService);
//	}

}