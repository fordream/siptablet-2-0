package com.shopivr.service.receiver;

//import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.os.Bundle;


import com.csipsimple.api.SipManager;
import com.shopivr.component.TAMainActivity;

public class NetworkChangeBroadcastReceiver extends BroadcastReceiver {

	public NetworkChangeBroadcastReceiver() {
		super();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent serviceIntent = new Intent(SipManager.INTENT_SIP_SERVICE);
		serviceIntent.setPackage(context.getPackageName());
		serviceIntent.putExtra(SipManager.EXTRA_OUTGOING_ACTIVITY,
				new ComponentName(context, TAMainActivity.class));
		serviceIntent.putExtra("checknetwork", true);
		context.startService(serviceIntent);
	}
}