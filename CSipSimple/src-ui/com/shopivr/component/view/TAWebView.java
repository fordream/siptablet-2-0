package com.shopivr.component.view;

import org.apache.http.util.EncodingUtils;
import org.json.JSONObject;

import z.lib.base.CommonAndroid;
import z.lib.base.LogUtils;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.app.knock.db.AccountDB;
import com.app.knock.db.TASetting;
import com.app.knock.db.TASetting.SettingType;
import com.shopivr.component.TAMainActivity;
import com.shopivr.service.utils.TAMainStatus;

//ta.com.component.view.TAWebView
@SuppressLint("NewApi")
public class TAWebView extends WebView {
	private enum STATUSLOADWEB {
		NONE, LOADING, ERROR, SUCCESS
	}

	private STATUSLOADWEB loadSucess = STATUSLOADWEB.NONE;

	public TAWebView(Context context, AttributeSet attrs) {
		super(context, attrs);

		if (getSettings() != null) {
			getSettings().setLoadsImagesAutomatically(true);
			getSettings().setJavaScriptEnabled(true);
			getSettings().setAppCacheEnabled(true);
			// setClickable(true);
			getSettings().setDomStorageEnabled(true);
		}

		setWebChromeClient(new WebChromeClient() {
			@SuppressLint("NewApi")
			@Override
			public boolean onConsoleMessage(ConsoleMessage consoleMessage) {

				return super.onConsoleMessage(consoleMessage);
			}

			@Override
			public boolean onJsAlert(WebView view, String url, String message, JsResult result) {

				return super.onJsAlert(view, url, message, result);
			}

			@Override
			public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {

				return super.onJsConfirm(view, url, message, result);
			}
		});

		setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.startsWith("dial://")) {
					String number = url.replace("dial://", "");
					TAMainActivity.getInstance().makeCall(number);
					return true;
				}
				return super.shouldOverrideUrlLoading(view, url);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				loadSucess = STATUSLOADWEB.SUCCESS;
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				loadSucess = STATUSLOADWEB.ERROR;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {

				super.onPageStarted(view, url, favicon);
				loadSucess = STATUSLOADWEB.LOADING;

			}

		});
	}

	private String getBaseUrl() {
		String url = new TASetting(getContext()).get(SettingType.server);// DataStore.getInstance().init(getContext()).getServer();

		if (!url.endsWith("/")) {
			url = url + "/";
		}
		if (!url.startsWith("http")) {
			url = "http://" + url;
		}

		return url;
	}

	public JSONObject createJsonTest() {
		String user_id = "";
		String password = "";
		JSONObject object = new JSONObject();
		try {
			AccountDB accountDB = new AccountDB(getContext());
			Cursor cursor = getContext().getContentResolver().query(accountDB.getContentUri(), null, null, null, String.format("%s DESC", AccountDB.time_update));

			if (cursor != null) {
				if (cursor.moveToNext()) {
					user_id = CommonAndroid.getString(cursor, AccountDB.user_id);
					password = CommonAndroid.getString(cursor, AccountDB.password);
				}
				cursor.close();
			}
			object.put("user_id", user_id);
			object.put("password", password);
			object.put("request_user", user_id);
			object.put("request_user_password", password);
			object.put("login_mode_flg", "0");
			object.put("server", new TASetting(getContext()).get(SettingType.server));
			JSONObject object2 = new JSONObject();
			object2.put("req", object);
			return object2;
		} catch (Exception ex) {
		}
		return object;
	}

	private long curentTime = System.currentTimeMillis();

	private boolean needUpdateWhenLoginSucces = true;

	public void loadWhenLoginSuccess() {
		if (!needUpdateWhenLoginSucces) {
			return;
		}
		needUpdateWhenLoginSucces = false;
		//LogUtils.e("testweb", String.format("%s %s", "time", (System.currentTimeMillis() - curentTime) / 1000));
		curentTime = System.currentTimeMillis();
		final String url = getBaseUrl() + "frontend/phonelogin";
		String postData = createJsonTest().toString();
		postUrl(url, EncodingUtils.getBytes(postData, "UTF-8"));
	}

	public void updateUI(TAMainStatus status, String callId, String userName) {
		if (TAMainStatus.LOGOUT == status) {
			needUpdateWhenLoginSucces = true;
		}
		if (userName == null)
			userName = "";

		if (userName.startsWith("<sip:")) {
			userName = userName.replace("<sip:", "");
		}

		if (userName.contains("@")) {
			userName = userName.substring(0, userName.indexOf("@"));
		}

		if (status == TAMainStatus.Outgoing) {
			String tmp = userName;

			if (tmp.length() >= 1) {
				tmp = tmp.substring(1);
			}

			if (!tmp.startsWith("*")) {
				String javascript = String.format("javascript:outgoing('%s')", userName);
				loadUrl(javascript);
			}

		} else if (status == TAMainStatus.Incoming) {
			loadUrl(String.format("javascript:ringing({\"callid\":\"%s\",\"ani\":\"%s\"})", callId, userName));
		} else if (status == TAMainStatus.LOGOUT) {
			loadUrl("javascript:logout()");
		}
	}

	public void menuLeft(int index) {
		String url = getBaseUrl() + (index == 1 ? "frontend/gtel/schedule/" : (index == 2 ? "frontend/gtel/recents/" : "/frontend/gtel/help/"));
		loadUrl(url);
	}
}