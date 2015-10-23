package com.shopivr.service.utils;

import z.lib.base.CommonAndroid;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.csipsimple.api.SipCallSession;
import com.csipsimple.api.SipProfile;
import com.csipsimple.service.SipService;
import com.csipsimple.utils.AccountListUtils;
import com.csipsimple.utils.AccountListUtils.AccountStatusDisplay;

public class TAUtils {

	public enum TAAddressBook {
		address_book_type_business//
		, address_book_type_sip_personal//
		, address_book_type_group//
		, address_book_type_local
	}

	public enum TAStatus {
		on//
		, off//
	}

	public final static class KEY {
		public static final String STATUS = "STATUS";
		public static final String MESSGAE = "MESSGAE";
	}

	public final static class ACTION {

		public static final String ACTION_BROADCAST_LOGIN_CALLBACK = "ACTION_BROADCAST_LOGIN_CALLBACK";

	}

	public static boolean availableForCalls(Context context) {
		boolean availableForCalls = false;
		Cursor c = context.getContentResolver().query(SipProfile.ACCOUNT_URI, null, null, null, null);

		if (c != null) {
			while (c.moveToNext()) {
				final SipProfile account = new SipProfile(c);
				AccountStatusDisplay accountStatusDisplay = AccountListUtils.getAccountDisplay(context, account.id);
				if (accountStatusDisplay.availableForCalls) {
					availableForCalls = true;
				}
			}

			c.close();
		}
		return availableForCalls;
	}

	public static final void sendBoardcastUpateUI(final Context context, SipCallSession callSession) {
		final Intent intent = SipService.buildCallUiIntent(context, (SipCallSession) callSession);
		context.sendBroadcast(intent);

		if (!CommonAndroid.isAppRunningOnTop(context)) {
			new SipCallSessionParse(callSession) {
				@Override
				public void incoming(SipCallSession callSession) {
					super.incoming(callSession);
					context.startActivity(intent);
				}
			};
		}
	}
}
