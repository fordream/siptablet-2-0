package com.app.knock.db;

import z.lib.base.CommonAndroid;
import android.content.Context;
import android.database.Cursor;

import com.shopivrtablet.R;

public class AccountDB extends SkypeTable {
	public AccountDB(Context context) {
		super(context);

		addColumns(status);
		addColumns(time_update);
		/**
		 * input
		 */
		addColumns(shop_code);
		addColumns(user_id);
		addColumns(password);
		addColumns(exten);
		addColumns(conflict_confirmation_flg);

		/**
		 * API CHECK
		 */
		addColumns(shop_name);
		addColumns(prefix);
		addColumns(last_name);
		addColumns(fast_name);
		addColumns(last_name_kana);
		addColumns(fast_name_kana);
		addColumns(image);
		addColumns(mail);
		addColumns(gateway);
		addColumns(gateway_sip_port);
		addColumns(gateway_rtp_port);
		addColumns(codec);
		addColumns(encryption);
		addColumns(description);
		addColumns(sip_userid);
		addColumns(sip_password);
		addColumns(auth);
		addColumns(update_date);
		addColumns(supdate_user);

		// API INFOR
		addColumns(sip_host);
		addColumns(sip_port);
		addColumns(sip_host_secondary);
		addColumns(sip_port_secondary);
	}

	public static final String status = "status";
	public static final String time_update = "time_update";
	/**
	 * input
	 */
	public static final String shop_code = "shop_code";
	public static final String user_id = "user_id";
	public static final String password = "password";
	public static final String exten = "exten";
	public static final String conflict_confirmation_flg = "conflict_confirmation_flg";

	/**
	 * API CHECK
	 */
	public static final String shop_name = "shop_name";
	public static final String prefix = "prefix";
	public static final String last_name = "last_name";
	public static final String fast_name = "fast_name";
	public static final String last_name_kana = "last_name_kana";
	public static final String fast_name_kana = "fast_name_kana";
	public static final String image = "image";
	public static final String mail = "mail";
	public static final String gateway = "gateway";
	public static final String gateway_sip_port = "gateway_sip_port";
	public static final String gateway_rtp_port = "gateway_rtp_port";
	public static final String codec = "codec";
	public static final String encryption = "encryption";
	public static final String description = "description";
	public static final String sip_userid = "sip_userid";
	public static final String sip_password = "sip_password";
	public static final String auth = "auth";
	public static final String update_date = "update_date";
	public static final String supdate_user = "supdate_user";

	// API INFOR
	public static final String sip_host = "sip_host";
	public static final String sip_port = "sip_port";
	public static final String sip_host_secondary = "host_secondary";
	public static final String sip_port_secondary = "port_secondary";

	public String getUserDisplayNumber() {
		String order = String.format("%s DESC", AccountDB.time_update);
		Cursor cursor = querry(null, order);

		String userDisplayNumber = "";
		if (cursor != null) {
			if (cursor.moveToNext()) {
				userDisplayNumber = String.format(getContext().getString(R.string.menuusernumber),//
						CommonAndroid.getString(cursor, AccountDB.sip_userid),//
						""
//						CommonAndroid.getString(cursor, AccountDB.prefix),//
						);
			}
			cursor.close();
		}

		return userDisplayNumber;

	}

	
}