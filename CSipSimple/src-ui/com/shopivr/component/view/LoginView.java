package com.shopivr.component.view;

import z.lib.base.CommonAndroid;
import z.lib.base.callback.RestClient;
import z.lib.base.callback.RestClient.RequestMethod;
import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.knock.db.TASetting;
import com.app.knock.db.TASetting.SettingType;
import com.shopivrtablet.R;
import com.shopivr.component.LoginCheckDialog;
import com.shopivr.component.TAMainActivity;

//ta.com.component.view.LoginView
public class LoginView extends LinearLayout implements View.OnClickListener {

	public LoginView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public LoginView(Context context) {
		super(context);
		init();
	}

	@Override
	public void setVisibility(int visibility) {
		super.setVisibility(visibility);

		if (visibility == View.VISIBLE) {
			if (getContext().getResources().getBoolean(R.bool.debug)) {
				vnc_login_edt_user.setText(new TASetting(getContext()).get(SettingType.user));
				vnc_login_edt_password.setText(new TASetting(getContext()).get(SettingType.password));
				vnc_login_edt_shopcode.setText(new TASetting(getContext()).get(SettingType.shopcode));
			} else {
				vnc_login_edt_user.setText("");
				vnc_login_edt_password.setText("");
				vnc_login_edt_shopcode.setText("");
			}
		}
	}

	private View login_update_version;

	private void init() {
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		li.inflate(R.layout.ta_login, this);
		setOnClickListener(this);

		login_update_version = CommonAndroid.getView(this, R.id.login_update_version);
		try {
			login_update_version.setOnClickListener(null);
			login_update_version.setVisibility(View.GONE);

			login_version = CommonAndroid.getView(this, R.id.login_version);
			login_btn_save_server = CommonAndroid.getView(this, R.id.login_btn_save_server);
			vnc_login_btn_setting = CommonAndroid.getView(this, R.id.vnc_login_btn_setting);
			vnc_login_btn_login = CommonAndroid.getView(this, R.id.vnc_login_btn_login);
			vnc_login_edt_server = CommonAndroid.getView(this, R.id.vnc_login_edt_server);
			vnc_login_edt_user = CommonAndroid.getView(this, R.id.vnc_login_edt_user);
			vnc_login_edt_shopcode = CommonAndroid.getView(this, R.id.vnc_login_edt_shopcode);
			vnc_login_edt_password = CommonAndroid.getView(this, R.id.vnc_login_edt_password);
			login_popup = CommonAndroid.getView(this, R.id.login_popup);
			login_version.setText(CommonAndroid.getVersionName(getContext()));

			login_popup.setVisibility(View.GONE);

			// add action
			login_popup.setOnClickListener(this);
			vnc_login_btn_setting.setOnClickListener(this);
			login_btn_save_server.setOnClickListener(this);
			vnc_login_btn_login.setOnClickListener(this);

			CommonAndroid.getView(this, R.id.login_ta_dropbox).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

				}
			});
		} catch (Exception ex) {
		}
	}

	@Override
	public void onClick(View v) {
		TAMainActivity.getInstance().hiddenKeyBoard();
		if (v == login_popup) {
			login_popup.setVisibility(View.GONE);
		} else if (v == login_btn_save_server) {
			String server = vnc_login_edt_server.getText().toString().trim();

			new TASetting(getContext()).setSetting(SettingType.server, server);
			login_popup.setVisibility(View.GONE);
		} else if (vnc_login_btn_setting == v) {
			login_popup.setVisibility(View.VISIBLE);
			vnc_login_edt_server.setText(new TASetting(getContext()).get(SettingType.server));
		} else if (vnc_login_btn_login == v) {
			String user = vnc_login_edt_user.getText().toString();
			String password = vnc_login_edt_password.getText().toString();
			String shopCode = vnc_login_edt_shopcode.getText().toString();
			String message = null;
			if (CommonAndroid.isBlank(user)) {
				message = getContext().getString(R.string.error_message_login_name);
			} else if (CommonAndroid.isBlank(password)) {
				message = getContext().getString(R.string.error_message_login_password);
			} else if (CommonAndroid.isBlank(shopCode)) {
				message = getContext().getString(R.string.error_message_login_shopcode);
			}

			if (CommonAndroid.isBlank(message)) {
				TAMainActivity.getInstance().check(user, password, shopCode);
			} else {
				CommonAndroid.showDialog(getContext(), message);
			}
		}
	}

	private View login_popup;
	private TextView login_version;
	private EditText vnc_login_edt_server;
	private EditText vnc_login_edt_user, vnc_login_edt_shopcode, vnc_login_edt_password;
	private Button login_btn_save_server, vnc_login_btn_setting, vnc_login_btn_login;

	public boolean neeOnBackPressed() {
		if (login_popup.getVisibility() == View.VISIBLE) {
			login_popup.setVisibility(View.GONE);
			return false;
		}
		return true;
	}

	public void checkUpdate() {
		new AsyncTask<String, String, String>() {
			String version = null;
			String message = null;

			protected void onPreExecute() {
				login_update_version.setVisibility(View.VISIBLE);
			};

			@Override
			protected String doInBackground(String... params) {

				RestClient restClient = new RestClient("https://akamai.sbb-sys.info/GTEL/download.plist");
				restClient.execute(RequestMethod.GET);

				String response = restClient.getResponse();
				// TODO getverion

				// version = null;
				if (restClient.getResponseCode() == -1) {
					message = getContext().getString(R.string.update_request_errorl);
				} else {
					String key = "<key>bundle-version</key>";
					String key1 = "<string>";
					String key2 = "</string>";
					if (!CommonAndroid.isBlank(response) && response.contains(key)) {
						response = response.substring(response.indexOf(key) + key.length());
						if (response.contains(key1)) {
							response = response.substring(response.indexOf(key1) + key1.length());
						}

						if (response.contains(key2)) {
							response = response.substring(0, response.indexOf(key2));
						}

						version = response.trim();
					}

					if (CommonAndroid.isBlank(version)) {
						message = getContext().getString(R.string.update_request_errorl);
					} else if (!CommonAndroid.getVersionName(getContext()).equals(version)) {
						message = getContext().getString(R.string.update_need);
					}
				}
				return null;
			}

			protected void onPostExecute(String result) {
				login_update_version.setVisibility(View.GONE);
				if (getVisibility() == View.VISIBLE) {
					if (CommonAndroid.isBlank(version)) {

						LoginCheckDialog loginCheckDialog = new LoginCheckDialog(getContext(), message) {
							@Override
							public void onClickOk() {

							}
						};
						loginCheckDialog.setShowButtonCancel(false);
						loginCheckDialog.show();
					} else if (!CommonAndroid.getVersionName(getContext()).equals(version)) {
						LoginCheckDialog loginCheckDialog = new LoginCheckDialog(getContext(), message) {
							@Override
							public void onClickOk() {
								String urlApk = "http://acvdev.com/utaehon/apk/TAMainActivity.apk";
								TAMainActivity.getInstance().downloadApk(urlApk);
							}
						};
						loginCheckDialog.setTextButtonOkie(getContext().getString(R.string.install));
						loginCheckDialog.show();
					}
				}

			};
		}.execute("");
	}
}