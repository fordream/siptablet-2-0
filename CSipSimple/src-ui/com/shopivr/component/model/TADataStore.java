package com.shopivr.component.model;

import z.lib.base.LogUtils;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.preference.PreferenceManager;

import com.csipsimple.api.SipProfile;
import com.shopivr.service.utils.NatEnableUtils;

public class TADataStore {
	private static final String SHARE_DATA_STORE = "VNCDataStore";
	private Context context;

	public TADataStore(Context context) {
		super();
		this.context = context;
	}

	// account, password, server, user
	public void save(String accountName, String password, String server, String userName, Boolean secure) {
		saveAccount(server, userName, password, accountName, secure);
	}

	public static void setEnable_tlsCommon(Context context, Boolean enable) {
		Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("enable_tls", enable);
		editor.commit();
	}

	private void saveAccount(String server, String username, String password, String account_name, Boolean secure) {
		// account_name = "PJSIP";
		setEnable_tlsCommon(context, secure);

		ContentValues values = new ContentValues();
		// secure = false;
		// values.put("id", value);
		values.put("acc_id", "<sip:" + username + "@" + server + ">");
		values.put("reg_uri", "sip:" + server + "");
		values.put("proxy", "sip:" + server + "");
		values.put("transport", secure ? "3" : "1");// 51
		values.put("display_name", account_name);
		values.put("wizard", "EXPERT");
		values.put("active", "1");
		values.put("priority", "100");
		values.put("mwi_enabled", "1");
		values.put("publish_enabled", "0");
		values.put("reg_timeout", "900");
		values.put("ka_interval", "0");
		values.put("allow_contact_rewrite", "1");
		values.put("contact_rewrite_method", "2");
		values.put("use_srtp", secure ? "2" : "-1");
		values.put("use_zrtp", "-1");
		values.put("reg_use_proxy", "3");
		values.put("realm", "*");
		values.put("scheme", "Digest");
		values.put("datatype", "0");
		values.put("force_contact", "");
		values.put("sip_stack", "0");
		values.put("reg_dbr", "-1");
		values.put("try_clean_reg", "0");
		values.put("username", username);
		values.put("data", password);
		
		if (NatEnableUtils.ENABLE_CHECK_NAT) {
			// NAT
			values.put("ice_cfg_use", "1"); // -1
			values.put("ice_cfg_enable", "1");//-1
			values.put("sip_stun_use", "1");//-1
			values.put("turn_cfg_use", "-1");//-1
			values.put("turn_cfg_enable", "-1");//-1
			// turn_cfg_server xxx
			// turn_cfg_user xxx
			// turn_cfg_pwd xxx
		}

		Cursor c = context.getContentResolver().query(SipProfile.ACCOUNT_URI, null, null, null, null);

		String id = null;
		while (c != null && c.moveToNext()) {
			String usernameOnDB = c.getString(c.getColumnIndex("username"));
			if (username.equals(usernameOnDB)) {
				id = c.getString(c.getColumnIndex("id"));
				break;
			}
		}
		if (c != null)
			c.close();

		unActiveAll();

		if (id == null) {
			context.getContentResolver().insert(SipProfile.ACCOUNT_URI, values);
		} else {
			// context.getContentResolver().update(SipProfile.ACCOUNT_STATUS_ID_URI_BASE,
			// values,
			// "id=?", new String[] { id });

			ContentValues cv = new ContentValues();
			cv.put(SipProfile.FIELD_ACTIVE, true);
			long accountId = Long.parseLong(id);
			context.getContentResolver().update(ContentUris.withAppendedId(SipProfile.ACCOUNT_URI, accountId), cv, null, null);
		}
	}

	public int unActiveAll() {
		ContentValues contentValues = new ContentValues();
		contentValues.put("active", "0");
		return context.getContentResolver().update(SipProfile.ACCOUNT_URI, contentValues, null, null);
	}

	public int deleteAll() {
		return context.getContentResolver().delete(SipProfile.ACCOUNT_URI, null, null);
	}

	public static long getActiveId(Context context) {
		// Cursor c = context.getContentResolver().query(SipProfile.ACCOUNT_URI,
		// null, "active='1'", null, null);
		Cursor c = context.getContentResolver().query(SipProfile.ACCOUNT_URI, null, null, null, null);

		while (c != null && c.moveToNext()) {
			long active = Long.parseLong(c.getString(c.getColumnIndex("active")));
			if (active == 1) {
				return Long.parseLong(c.getString(c.getColumnIndex("id")));
			}
		}

		if (c != null) {
			c.close();
		}

		return -1;
	}

	public static Cursor getActiveIdCursor(Context context) {
		Cursor c = context.getContentResolver().query(SipProfile.ACCOUNT_URI, null, "active='1'", null, null);
		if (c != null && c.moveToFirst()) {
			return c;
		}

		return null;
	}

	public static String getActiveService(Context context) {
		Cursor c = context.getContentResolver().query(SipProfile.ACCOUNT_URI, null, "active='1'", null, null);
		if (c != null && c.moveToFirst()) {
			return c.getString(c.getColumnIndex("reg_uri")).replace("sip:", "");
		}

		return null;
	}

	public void updateTransfer(boolean checked, String edtPhoneTime, String edtPhoneTransfer) {
		Editor edt = context.getSharedPreferences(SHARE_DATA_STORE, 0).edit();

		edt.putBoolean("checkBoxTransfer", checked);

		edt.putString("edtPhoneTime", edtPhoneTime);

		edt.putString("edtPhoneTransfer", edtPhoneTransfer);
		edt.commit();
	}

	public boolean isCheckBoxTransfer() {
		return context.getSharedPreferences(SHARE_DATA_STORE, 0).getBoolean("checkBoxTransfer", false);
	}

	public String getEdtPhoneTime() {
		return context.getSharedPreferences(SHARE_DATA_STORE, 0).getString("edtPhoneTime", "");
	}

	public String getEdtPhoneTransfer() {
		return context.getSharedPreferences(SHARE_DATA_STORE, 0).getString("edtPhoneTransfer", "");
	}

	public long getTimeComfirm(int callId) {
		return context.getSharedPreferences(SHARE_DATA_STORE, 0).getLong("TIME" + callId, 0);
	}

	public void saveTimeComfirm(int callId, long timeStart) {
		Editor edt = context.getSharedPreferences(SHARE_DATA_STORE, 0).edit();
		edt.putLong("TIME" + callId, timeStart);
		edt.commit();
	}

}