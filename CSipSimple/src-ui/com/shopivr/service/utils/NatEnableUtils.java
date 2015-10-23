package com.shopivr.service.utils;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class NatEnableUtils {
	public static final boolean ENABLE_CHECK_NAT = false;
	private static final String enable_ice = "enable_ice";
	private static final String enable_stun = "enable_stun";
	private static final String stun_server = "stun_server";
	private static final String enable_stun2 = "enable_stun2";
	private static final String stun_server2 = "stun_server2";

	private static final String enable_turn = "enable_turn";
	private static final String turn_server = "turn_server";
	private static final String turn_username = "turn_username";
	private static final String turn_password = "turn_password";

	public static class TURN {
		public static void enableTurn(Context context, boolean enable) {
			enable(context, enable_turn, enable);
		}

		public static boolean isEnableTurn(Context context) {
			return getEnable(context, enable_turn);
		}

		public static void set(Context context, String server, String username, String password) {
			save(context, turn_server, server);
			save(context, turn_username, username);
			save(context, turn_password, password);
		}

		public static String getTurnServer(Context context) {
			return getStr(context, turn_server);
		}

		public static String getTurnUserName(Context context) {
			return getStr(context, turn_username);
		}

		public static String getTurnPassword(Context context) {
			return getStr(context, turn_password);
		}
	}

	public static class ICE {
		public static void enableNatICE(Context context, boolean enable) {
			enable(context, enable_ice, enable);
		}

		public static boolean isEnableNatICE(Context context) {
			return getEnable(context, enable_ice);
		}
	}

	public static class STUN1 {
		// ----------------------------------------------------------------------//
		/**
		 * stun 1
		 * 
		 * @param context
		 * @param enable
		 * @param server
		 *            nhieu server cach nhau dau ,
		 */
		public static void enableStunServer(Context context, boolean enable, String server) {
			enable(context, enable_stun, enable);
			save(context, stun_server, server);
		}

		public static boolean isEnableStun(Context context) {
			return getEnable(context, enable_stun);
		}

		public static String getStunServer(Context context) {
			return getStr(context, stun_server);
		}
	}

	public static class STUN2 {
		public static void enableStun2Server(Context context, boolean enable, String server) {
			enable(context, enable_stun2, enable);
			save(context, stun_server2, server);
		}

		public static boolean isEnableStun2(Context context) {
			return getEnable(context, enable_stun2);
		}

		public static String getStunServer2(Context context) {
			return getStr(context, stun_server2);
		}
	}

	/**
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	private static boolean getEnable(Context context, String key) {
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, false);
	}

	private static String getStr(Context context, String key) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString(key, "");
	}

	private static void enable(Context context, String key, boolean value) {
		Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	private static void save(Context context, String key, String value) {
		Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static void enableNat(Context context) {
		if (ENABLE_CHECK_NAT) {
			ICE.enableNatICE(context, true);
			TURN.enableTurn(context, false);
			STUN1.enableStunServer(context, true, null);
		}
	}
}
