package com.shopivr.component.base;

import java.lang.reflect.Field;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.text.TextUtils;

import com.csipsimple.api.ISipService;
import com.csipsimple.api.SipCallSession;
import com.csipsimple.api.SipConfigManager;
import com.csipsimple.api.SipManager;
import com.csipsimple.service.SipService;
import com.csipsimple.utils.PreferencesWrapper;
import com.shopivr.component.model.TADataStore;

public class TASipManager {

	public static boolean checkActivityIsTop(SipService pjService) {
		ActivityManager am = (ActivityManager) pjService
				.getSystemService(Activity.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
		ComponentName componentInfo = taskInfo.get(0).topActivity;
		componentInfo.getPackageName();
		return componentInfo.getPackageName().equals(
				pjService.getApplication().getPackageName())
				&& taskInfo.get(0).topActivity.getClassName().equals(
						"com.vnc.component.MainActivity");
	}

	public static boolean isChangle(SipCallSession curentSession,
			SipCallSession sipCallSession) {
		if (curentSession == null && sipCallSession != null) {
			return true;
		} else if (curentSession != null && sipCallSession == null) {
			return true;
		} else if (curentSession != null && sipCallSession != null) {

			if (curentSession.getCallId() != sipCallSession.getCallId()) {
				return true;
			}

			if (curentSession.getCallState() != sipCallSession.getCallState()) {
				return true;
			} else {
				if (curentSession.getCallState() == SipCallSession.InvState.CONFIRMED) {
					if (curentSession.isLocalHeld() != sipCallSession
							.isLocalHeld()) {
						return true;
					}

					// if (curentSession.isRecording() != sipCallSession
					// .isRecording()) {
					// return true;
					// }
				}
			}
		}

		return false;
	}

	//
	public static SipCallSession getActiveCallInfo(ISipService iSipService) {
		SipCallSession currentCallInfo = null;
		SipCallSession callsInfo[];
		try {
			callsInfo = iSipService.getCalls();
			for (SipCallSession callInfo : callsInfo) {
				currentCallInfo = getPrioritaryCall(callInfo, currentCallInfo);
			}
			return currentCallInfo;

		} catch (RemoteException e) {
			return null;
		} catch (Exception exception) {
			return null;
		}
	}

	/**
	 * Get the call with the higher priority comparing two calls
	 * 
	 * @param call1
	 *            First call object to compare
	 * @param call2
	 *            Second call object to compare
	 * @return The call object with highest priority
	 */
	private static SipCallSession getPrioritaryCall(SipCallSession callInfo,
			SipCallSession currentCallInfo) {
		// We prefer the not null
		if (callInfo == null) {
			return currentCallInfo;
		} else if (currentCallInfo == null) {
			return callInfo;
		}
		// We prefer the one not terminated
		if (callInfo.isAfterEnded()) {
			return currentCallInfo;
		} else if (currentCallInfo.isAfterEnded()) {
			return callInfo;
		}
		// We prefer the one not held
		if (callInfo.isLocalHeld()) {
			return currentCallInfo;
		} else if (currentCallInfo.isLocalHeld()) {
			return callInfo;
		}
		return (callInfo.getCallStart() > currentCallInfo.getCallStart()) ? currentCallInfo
				: callInfo;
	}

	public static void makeCall(String number, Context context) {
		long id = TADataStore.getActiveId(context);
		try {
			((TAMainApplication) context.getApplicationContext()).getService()
					.makeCall(number, (int) id);
		} catch (RemoteException e) {
		}
	}

	public static SipCallSession getSipCallSession(ISipService iSipService,
			long sessionCallId) {
		try {
			return iSipService.getCallInfo((int) sessionCallId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean isCompatible(int apiLevel) {
		return android.os.Build.VERSION.SDK_INT >= apiLevel;
	}

	public static String getCpuAbi() {
		if (isCompatible(4)) {
			Field field;
			try {
				field = android.os.Build.class.getField("CPU_ABI");
				return field.get(null).toString();
			} catch (Exception e) {
			}

		}
		return "armeabi";
	}

	public static void resetCodecsSettings(PreferencesWrapper preferencesWrapper) {
		boolean supportFloating = false;
		boolean isHeavyCpu = false;
		String abi = getCpuAbi();
		if (!TextUtils.isEmpty(abi)) {
			if (abi.equalsIgnoreCase("mips") || abi.equalsIgnoreCase("x86")) {
				supportFloating = true;
			}
			if (abi.equalsIgnoreCase("armeabi-v7a")
					|| abi.equalsIgnoreCase("x86")) {
				isHeavyCpu = true;
			}
		}

		// For Narrowband
		preferencesWrapper.setCodecPriority("PCMU/8000/1",
				SipConfigManager.CODEC_NB, "60");

		// For Wideband
		preferencesWrapper.setCodecPriority("PCMU/8000/1",
				SipConfigManager.CODEC_WB, "60");

		// Bands repartition
		preferencesWrapper.setPreferenceStringValue("band_for_wifi",
				SipConfigManager.CODEC_WB);

	}

	public static boolean isDisConnect(SipCallSession session) {
		return (session == null)
				|| (session != null && session.getCallState() == SipCallSession.InvState.DISCONNECTED);
	}

	public static SipCallSession isCalling(ISipService iSipService) {
		try {
			SipCallSession[] sessions = iSipService.getCalls();

			for (SipCallSession session : sessions) {
				if (session.getCallState() != SipCallSession.InvState.DISCONNECTED) {
					return session;
				}
			}
		} catch (Exception exception) {

		}
		return null;
	}

	public static SipCallSession getSipCallingSession(ISipService iSipService) {
		SipCallSession session = null;
		try {
			session = iSipService.getLastSipCallSession();
		} catch (Exception e) {
		}

		if (session == null) {
			session = isCalling(iSipService);
		}

		return session;
	}

	public static SipCallSession getSipCallSessionFromIntent(Intent intent) {
		return (SipCallSession) intent
				.getParcelableExtra(SipManager.EXTRA_CALL_INFO);
	}
}