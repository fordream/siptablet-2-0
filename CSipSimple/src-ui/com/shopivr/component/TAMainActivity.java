package com.shopivr.component;

import java.util.Timer;
import java.util.TimerTask;

import z.lib.base.AudioSpeakerManager;
import z.lib.base.CommonAndroid;
import z.lib.base.LogUtils;
import z.lib.base.callback.TACallBack;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.app.knock.db.AccountDB;
import com.app.knock.db.TASetting;
import com.app.knock.db.TASetting.SettingType;
import com.csipsimple.api.SipCallSession;
import com.csipsimple.api.SipCallSession.InvState;
import com.csipsimple.api.SipManager;
import com.csipsimple.api.SipUri;
import com.csipsimple.api.SipUri.ParsedSipContactInfos;
import com.csipsimple.utils.AccountListUtils.AccountStatusDisplay;
import com.shopivr.component.base.BaseActivity;
import com.shopivr.component.base.TAMainApplication;
import com.shopivr.component.base.TAMainApplication.ClientPresentCallState;
import com.shopivr.component.base.TAMainApplication.TAMainApplicationCallBackIinit;
import com.shopivr.component.base.TASipManager;
import com.shopivr.component.model.TADataStore;
import com.shopivr.component.view.CallingViewStatus;
import com.shopivr.component.view.ContactView;
import com.shopivr.component.view.ContactView.ContactType;
import com.shopivr.component.view.DTMSView;
import com.shopivr.component.view.LoginView;
import com.shopivr.component.view.Menu2View;
import com.shopivr.component.view.Menu3View;
import com.shopivr.component.view.MenuLeftView;
import com.shopivr.component.view.MenuNotificationView;
import com.shopivr.component.view.MenuUserView;
import com.shopivr.service.utils.NatEnableUtils;
import com.shopivr.service.utils.TAMainStatus;
import com.shopivr.service.utils.TASession;
import com.shopivr.service.utils.TASession.ClientStatus;
import com.shopivr.service.utils.TAUtils;
import com.shopivrtablet.R;

public class TAMainActivity extends BaseActivity {
	private CallingViewStatus callingviewstatus;

	private void ta_main_on_of(final boolean isFromRegister, final boolean isChecked) {

		((TAMainApplication) getApplication()).init(new TAMainApplicationCallBackIinit() {
			@Override
			public void onServiceConnected() {
				boolean hasCallInProcess = false;
				try {
					SipCallSession[] calls = ((TAMainApplication) getApplication()).getService().getCalls();
					for (SipCallSession aCall : calls) {

						if (aCall.getCallState() != SipCallSession.InvState.DISCONNECTED) {
							hasCallInProcess = true;
							break;
						}
					}
				} catch (Exception e) {

				}

				if (!hasCallInProcess || !isFromRegister) {
					ta_main_on_of.setChecked(isChecked);
				}
			}

			@Override
			public void onServiceDisconnected() {

			}
		});
		ta_main_on_of.setChecked(isChecked);
	}

	public void setClientCallState(int clientCallState) {
		TAMainApplication.getInstance().setClientCallState(clientCallState);
	}

	public int getClientCallState() {
		return TAMainApplication.getInstance().getClientCallState();
	}

	protected int currentCallId = SipCallSession.INVALID_CALL_ID;

	private static final String TAG = "TAMainActivity";
	private static TAMainActivity instance;

	private long startTime = 0L;
	private Handler handler = null;
	private Runnable runnable = null;

	private Handler customHandler = new Handler();

	long timeInMilliseconds = 0L;
	long timeSwapBuff = 0L;
	long updatedTime = 0L;

	Timer parkTimer;

	public static TASession.ClientStatus getClientStatus() {
		return TASession.status;
	}

	@Override
	public void onBackPressed() {
		boolean neeOnBackPressed = true;
		try {
			SipCallSession[] calls = ((TAMainApplication) getApplication()).getService().getCalls();
			if (calls.length > 0) {
				neeOnBackPressed = false;
			}

			if (!loginview.neeOnBackPressed()) {
				neeOnBackPressed = false;
			}

			View views[] = new View[] { //
			menuleftview, //
					menuusertview, //
					dtmsview,// dtms
					menu4View,// call
					menu5View,//
					menu2view,

			};//
			for (View view : views) {
				if (view.getVisibility() == View.VISIBLE) {
					view.setVisibility(View.GONE);
					neeOnBackPressed = false;
				}
			}

			// contactview
			if (!contactview.neeOnBackPressed(calls)) {
				neeOnBackPressed = false;
			}
			// menu 3
			if (menu3View.getVisibility() == View.VISIBLE) {
				if (calls.length == 2) {
				} else if (calls.length < 2) {
					menu3View.setVisibility(View.GONE);
				}

				neeOnBackPressed = false;
			}
		} catch (Exception ex) {

		}

		if (neeOnBackPressed) {
			// super.onBackPressed();
		}
	}

	public BroadcastReceiver callCalback = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			WakeLock wakeLock = null;

			wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyWakeLock");

			wakeLock.acquire();
			// do nothing
			wakeLock.release();

			boolean isShopmodeEnable = "true".equals(new TASetting(TAMainActivity.this).get(SettingType.shopmode));

			SipCallSession callSession = intent.getParcelableExtra(SipManager.EXTRA_CALL_INFO);
			int callId = callSession.getCallId();
			int callState = callSession.getCallState();
			String callIdx = callSession.getWebSipId();
			// first call
			if (currentCallId == SipCallSession.INVALID_CALL_ID) {
				currentCallId = callId;
			}

			// remote contact information
			ParsedSipContactInfos remoteContactInfo = SipUri.parseSipContact(callSession.getRemoteContact());

			String userName = callSession.getRemoteContact();// TODO
			switch (callState) {
			case InvState.INCOMING: {
				if (getClientCallState() == ClientPresentCallState.CONFIRMED) {
				} else {
					// close park
					menu3View.setVisibility(View.GONE);

					updateControlUI(TAMainStatus.Incoming, false);
					ta_main_web.updateUI(TAMainStatus.Incoming, callIdx, userName);
				}
			}
				break;
			case InvState.CONNECTING: {
				if (getClientCallState() == ClientPresentCallState.CONFIRMED) {
				} else {
					updateCallInfor(remoteContactInfo);
				}
			}
				break;
			case InvState.EARLY: {
				if (callSession.isIncoming()) {
					if (TASession.status == ClientStatus.DISABLE) {
						return;
					}
					if ((getClientCallState() == ClientPresentCallState.CONFIRMING || getClientCallState() == ClientPresentCallState.CONFIRMED || getClientCallState() == ClientPresentCallState.IN_CALLING)
							&& callId != currentCallId) {
						// if client in a call
						// busy --> hangup this incoming call
						(TAMainApplication.getInstance()).hangup(callSession.getCallId(), 0);
					} else {
						((TAMainApplication) getApplication()).startRing(callSession.getRemoteContact());

						if (isShopmodeEnable) {
							//
							new AudioSpeakerManager(TAMainActivity.this).enableSpeaker(true);
						}
						// close park
						menu3View.setVisibility(View.GONE);
						menuusertview.setVisibility(View.GONE);
						setClientCallState(ClientPresentCallState.IN_CALLING);
						updateControlUI(TAMainStatus.Incoming, false);
						ta_main_web.updateUI(TAMainStatus.Incoming, callIdx, userName);
					}
					updateCallInfor(remoteContactInfo);
				} else {
					// outgoing
					if (getClientCallState() == ClientPresentCallState.CONFIRMED) {// conference
																					// to
																					// transfer

						ta_main_status.setText(R.string.call_state_transfer_early);
					} else {
						updateControlUI(TAMainStatus.Outgoing, false);
						updateCallInfor(remoteContactInfo);
						ta_main_web.updateUI(TAMainStatus.Outgoing, callIdx, userName);
					}
				}
			}
				break;
			case InvState.CALLING: {
				if (getClientCallState() == ClientPresentCallState.CONFIRMED) {
				} else {
					updateControlUI(TAMainStatus.Outgoing, false);
					ta_main_web.updateUI(TAMainStatus.Outgoing, callIdx, userName);
				}
			}
				break;
			case InvState.CONFIRMED: {
				if (menu2view.getVisibility() == View.VISIBLE || contactview.getVisibility() == View.VISIBLE) { // transfer
					// transfer confirmed

					if (getClientCallState() == ClientPresentCallState.CONFIRMED && currentCallId != callSession.getCallId()) {
						// delay time for confirm
						ta_main_status.setText(R.string.call_state_transfer_confirmed);

						final Intent xintent = intent;
						if (handler != null && runnable != null) {
							handler.removeCallbacks(runnable);
						}

						handler = new Handler();
						runnable = new Runnable() {
							@Override
							public void run() {
								callTranferNumber(xintent);
							}
						};

						handler.postDelayed(runnable, 5000);
					}

					return;
				}

				setClientCallState(ClientPresentCallState.CONFIRMED);
				updateControlUI(TAMainStatus.Talking, false);

				startTime = SystemClock.uptimeMillis();
				customHandler.postDelayed(updateTimerThread, 0);
			}
				break;
			case InvState.DISCONNECTED: {
				if (handler != null && runnable != null) {
					handler.removeCallbacks(runnable);
				}

				SipCallSession[] callSessions = null;
				try {
					callSessions = ((TAMainApplication) getApplication()).getService().getCalls();
					int j = 0;
					for (int i = 0; i < callSessions.length; i++) {
						SipCallSession session = callSessions[i];

						if (session.isActive()) {
							j += 1;
						} else {
						}
					}
					if (j == 0) {
						if (menu2view.getVisibility() == View.VISIBLE || contactview.getVisibility() == View.VISIBLE) { // transfer
							menu2view.setVisibility(View.GONE);
						}

						setClientCallState(ClientPresentCallState.FREE);

						currentCallId = SipCallSession.INVALID_CALL_ID;
						// enableClient((TASession.previousStatus ==
						// ClientStatus.ENABLE) ? true : false, false);
						enableClient((TASession.previousStatus == ClientStatus.ENABLE) ? true : false, false);
						updateControlUI(TAMainStatus.NONE, false);
						updateCallInfor(null);
						customHandler.removeCallbacks(updateTimerThread);

						menu2view.setVisibility(View.GONE);
						contactview.setVisibility(View.GONE);

						// ta_main_web.updateUI(TAMainStatus.DISCONNECTED,
						// callId, userName);
					} else {
						if ((menu2view.getVisibility() == View.VISIBLE || contactview.getVisibility() == View.VISIBLE) && currentCallId != callSession.getCallId()) { // transfer
							ta_main_status.setText(R.string.status_tranfer);

							callTranferNumber(intent);

							return;
						}
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
				break;
			default:
				break;
			}
		}

		private void callTranferNumber(Intent intent) {
			LogUtils.i("TRANSFER", "TRANSFERING.....");
			menu2view.callTranferNumber(intent);
			contactview.callTranferNumber(intent);
		}
	};

	public BroadcastReceiver buddyStateChanged = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String parkNumberString = intent.getStringExtra(SipManager.EXTRA_BUDDY_NUMBER);
			String state = intent.getStringExtra(SipManager.EXTRA_BUDDY_STATUS);

			String str = "On the phone";
			boolean park;

			if (state != null && state.contains(str)) {
				park = true;
			} else {
				park = false;
			}

			int parkNumber = -1;
			if (parkNumberString != null) {
				try {
					if (parkNumberString.equals(getISipService().getPark(1))) {
						parkNumber = 1;
					} else if (parkNumberString.equals(getISipService().getPark(2))) {
						parkNumber = 2;
					} else if (parkNumberString.equals(getISipService().getPark(3))) {
						parkNumber = 3;
					} else if (parkNumberString.equals(getISipService().getPark(4))) {
						parkNumber = 4;
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				updateParkState(parkNumber, park);
			}
		}
	};

	int parkTimeCount = 0;
	boolean parkState;
	boolean parkFlash;
	boolean park1St;
	boolean park2St;
	boolean park3St;
	boolean park4St;

	public void updateParkState(int parkNum, boolean state) {
		switch (parkNum) {
		case 1:
			park1St = state;
			break;
		case 2:
			park2St = state;
			break;
		case 3:
			park3St = state;
			break;
		case 4:
			park4St = state;
			break;
		default:
			break;
		}

		if (park1St || park2St || park3St || park4St) {
			// TODO
			if (parkTimer == null) {
				parkTimer = new Timer();
			}

			parkTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					parkTimerProcess();
				}
			}, 0, 500);
			parkState = true;
		} else {
			parkState = false;
			parkTimer.cancel();
			parkTimer = null;
			parkTimerProcess();
		}
	}

	private void parkTimerProcess() {
		if (!parkState) {

			parkFlash = true;

			for (int i = 1; i < 4; i++) {
				menu3View.parkBtnFlash(i, parkFlash, false);
			}

			if (parkTimer == null) {
				// close park
				menu3View.setVisibility(View.GONE);
			} else {
				parkTimer.cancel();
				parkTimer = null;
			}
		} else {
			// TODO PARK
		}

		if (parkFlash && getClientCallState() != ClientPresentCallState.IN_CALLING) {
			// [_parkBtn setButtonState:MCButtonStateInActive];
			// [_parkBtn setButtonState:MCButtonStateFlashON];
		} else {
			// [_parkBtn setButtonState:MCButtonStateActive];
		}

		if (park1St) {
			if (getClientCallState() == ClientPresentCallState.IN_CALLING) {
				menu3View.parkBtnFlash(1, true, false);
			} else {
				menu3View.parkBtnFlash(1, parkFlash, true);
			}
		} else {
			if (getClientCallState() != ClientPresentCallState.IN_CALLING) {
				menu3View.parkBtnFlash(1, false, false);
			} else {
				menu3View.parkBtnFlash(1, true, true);
			}
		}

		if (park2St) {
			if (getClientCallState() == ClientPresentCallState.IN_CALLING) {
				menu3View.parkBtnFlash(2, true, false);
			} else {
				menu3View.parkBtnFlash(2, parkFlash, true);
			}
		} else {
			if (getClientCallState() != ClientPresentCallState.IN_CALLING) {
				menu3View.parkBtnFlash(2, false, false);
			} else {
				menu3View.parkBtnFlash(2, true, true);
			}
		}

		if (park3St) {
			if (getClientCallState() == ClientPresentCallState.IN_CALLING) {
				menu3View.parkBtnFlash(3, true, false);
			} else {
				menu3View.parkBtnFlash(3, parkFlash, true);
			}
		} else {
			if (getClientCallState() != ClientPresentCallState.IN_CALLING) {
				menu3View.parkBtnFlash(3, false, false);
			} else {
				menu3View.parkBtnFlash(3, true, true);
			}
		}

		if (park4St) {
			if (getClientCallState() == ClientPresentCallState.IN_CALLING) {
				menu3View.parkBtnFlash(4, true, false);
			} else {
				menu3View.parkBtnFlash(4, parkFlash, true);
			}
		} else {
			if (getClientCallState() != ClientPresentCallState.IN_CALLING) {
				menu3View.parkBtnFlash(4, false, false);
			} else {
				menu3View.parkBtnFlash(4, true, true);
			}
		}

		parkFlash = !parkFlash;
		parkTimeOverPro();
	}

	private void parkTimeOverPro() {
		parkTimeCount++;

		if (parkTimeCount == 60) {
			// reset buddy
			try {
				getISipService().resetBuddyList();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	private void sendParkNumber(String number) {
		if (getClientCallState() == ClientPresentCallState.FREE) {
			makeCall(number);
		} else {
			if (number != null) {
				int length = number.length();

				for (int i = 0; i < length; i++) {
					char c = number.charAt(i);

					String str = Character.toString(c);
					// TODO
					onDtms(str);
				}

				if (getClientCallState() == ClientPresentCallState.FREE) {
					new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
						@Override
						public void run() {
							// Do something here
							final AlertDialog.Builder builder = new AlertDialog.Builder(TAMainActivity.this);
							builder.setTitle(R.string.dialog_title );//"Ã£â‚¬Å’Ã¤Â¿ï¿½Ã§â€¢â„¢Ã£â‚¬ï¿½Ã¦â€œï¿½Ã¤Â½Å“Ã£ï¿½Å’Ã¥Â¤Â±Ã¦â€¢â€”Ã£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â€”Ã£ï¿½Å¸Ã£â‚¬â€š");
							builder.setMessage(R.string.dialog_content);//"Ã£â€šâ€šÃ£ï¿½â€ Ã¤Â¸â‚¬Ã¥ÂºÂ¦Ã£â‚¬Å’Ã¤Â¿ï¿½Ã§â€¢â„¢Ã£â‚¬ï¿½Ã¦â€œï¿½Ã¤Â½Å“Ã£â€šâ€™Ã¥Â®Å¸Ã¨Â¡Å’Ã£ï¿½â€”Ã£ï¿½Â¦Ã£ï¿½ï¿½Ã£ï¿½Â Ã£ï¿½â€¢Ã£ï¿½â€žÃ£â‚¬â€š");
							builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated
									// method stub
								}
							});
							AlertDialog alertDialog = builder.create();
							alertDialog.show();
						}
					}, 3000);
				}
			}
		}
	}

	// TODO
	private void enableClient(final boolean enable, final boolean savePrevious) {
		AccountDB accountDB = new AccountDB(this);
		Cursor cursor = getContentResolver().query(accountDB.getContentUri(), null, null, null, String.format("%s DESC", AccountDB.time_update));

		String user_id = "";
		if (cursor != null) {
			if (cursor.moveToNext()) {
				user_id = CommonAndroid.getString(cursor, AccountDB.user_id);
			}
			cursor.close();
		}
		((TAMainApplication) getApplication()).updateStatus(enable ? ClientStatus.ENABLE : ClientStatus.DISABLE, user_id, new TACallBack() {
			@Override
			public void onSuccess(String status, String errorMessage, String response) {
				super.onSuccess(status, errorMessage, response);

				ta_main_on_of(false, enable);
				if (savePrevious == true) {
					TASession.saveStatus();
				}
				// TASession.status = enable ? ClientStatus.ENABLE :
				// ClientStatus.DISABLE;

				TASession.status = enable ? ClientStatus.ENABLE : ClientStatus.DISABLE;

				if (getClientCallState() == ClientPresentCallState.FREE) {
					if (TASession.status == ClientStatus.ENABLE) {
						ta_main_status.setText(R.string.client_status_setting_on);
					} else {
						ta_main_status.setText(R.string.client_status_setting_off);
					}
				}
			}

			@Override
			public void onError(String status, String message) {
				super.onError(status, message);
			}
		});
	}

	public void makeCall(String number) {
		if (CommonAndroid.isBlank(number)) {
			return;
		} else {
			try {
				Log.i("ShopIVR", Long.toString(TADataStore.getActiveId(this)));

				if (getClientCallState() != ClientPresentCallState.CONFIRMED && getClientCallState() != ClientPresentCallState.PRE_PARK) {
					txt_main_number_call.setText(number);
				}

				((TAMainApplication) getApplication()).getService().makeCall(number, (int) TADataStore.getActiveId(this));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	public static TAMainActivity getInstance() {
		return instance;
	}

	private com.shopivr.component.view.Menu5View menu5View;
	private com.shopivr.component.view.Menu4View menu4View;
	private Menu3View menu3View;
	private Menu2View menu2view;
	private MenuLeftView menuleftview;
	private MenuUserView menuusertview;
	private DTMSView dtmsview;

	private ContactView contactview;
	private LoginView loginview;
	private MenuNotificationView menunotificationtview;
	private View ta_main_mainview;
	private View ta_main_loading;
	private View main_menu_left;
	private View ta_login_user_infor;

	private OnClickListener onClickTaMainBtn = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if (v.getId() == R.id.ta_login_user_infor) {
				if (getClientCallState() != ClientPresentCallState.FREE) {
					return;
				}
				menuusertview.setVisibility(View.VISIBLE);
			} else if (v.getId() == R.id.main_menu_left) {
				menuleftview.setVisibility(View.VISIBLE);
			} else if (v.getId() == R.id.ta_main_btn_1) {
				if (taMainStatus == TAMainStatus.NONE) {
					// return;
				}
				// SipCallSession session =
				// TASipManager.getSipCallingSession(getISipService());
				// if (session != null) {
				// ((TAMainApplication)
				// getApplication()).getService().hangup(session.getCallId(),
				// 0);
				TAMainApplication.getInstance().hangupAll();
				// }

			} else if (v.getId() == R.id.ta_main_btn_2) {
				if (taMainStatus == TAMainStatus.NONE || taMainStatus == TAMainStatus.Incoming || taMainStatus == TAMainStatus.Outgoing) {
					return;
				}

				// TODO
				ta_main_on_of(false, false);

				String status = getString(R.string.status_tranfer);
				ta_main_status.setText(status);

				((TAMainApplication) getApplication()).hold();

				updateControlUI(TAMainStatus.TransferStart, false);
				menu2view.setVisibility(View.VISIBLE);
				menu2view.clear();
			} else if (v.getId() == R.id.ta_main_btn_3) {
				if (taMainStatus == TAMainStatus.NONE || taMainStatus == TAMainStatus.Incoming || taMainStatus == TAMainStatus.Outgoing) {
					return;
				}

				// setClientCallState(ClientPresentCallState.PRE_PARK);
				parkTimeCount = 0;
				menu3View.setVisibility(View.VISIBLE);
			} else if (v.getId() == R.id.ta_main_btn_4) {
				// Keypadd
				if (taMainStatus == TAMainStatus.Outgoing || taMainStatus == TAMainStatus.Incoming) {
					return;
				}
				if (taMainStatus == TAMainStatus.Talking) {
					showDtms(true);
				} else {
					menu4View.setVisibility(View.VISIBLE);

					// enableClient(false, true);
					if (getClientCallState() == ClientPresentCallState.FREE) {
						enableClient(false, true);
					}
				}
			} else if (v.getId() == R.id.ta_main_btn_5) {
				menu5View.setVisibility(View.VISIBLE);

				// enableClient(false, true);
				if (getClientCallState() == ClientPresentCallState.FREE) {
					enableClient(false, true);
				}
			} else if (v.getId() == R.id.ta_main_btn_6) {

				// LogUtils.e("ShopIVR-CALL:", "TOUCH ANSWER - 1");
				if (taMainStatus != TAMainStatus.Incoming) {
					return;
				}
				SipCallSession session = TASipManager.getSipCallingSession(getISipService());
				try {

					// LogUtils.e("ShopIVR-CALL:", "TOUCH ANSWER - 2");

					taMainStatus = TAMainStatus.Talking;
					setClientCallState(ClientPresentCallState.CONFIRMING);
					((TAMainApplication) getApplication()).getService().answer(session.getCallId(), SipCallSession.StatusCode.OK);
					enableClient(false, true);
					new AudioSpeakerManager(TAMainActivity.this).enableSpeaker(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (v.getId() == R.id.ta_login_main_menu) {
				// menunotificationtview.setVisibility(View.VISIBLE);
			}

			if (menu3View.getVisibility() == View.VISIBLE) {
				// FIXME _03_02_inactive
				ta_main_btn_3.setBackgroundResource(R.drawable._03_active);
			} else if (menu4View.getVisibility() == View.VISIBLE) {
				ta_main_btn_4.setBackgroundResource(R.drawable._04_active);
			} else if (menu5View.getVisibility() == View.VISIBLE) {
				ta_main_btn_5.setBackgroundResource(R.drawable._05_activie);
			} else if (menu2view.getVisibility() == View.VISIBLE) {
				// ta_main_btn_2.setBackgroundResource(R.drawable._02);
			}
		}
	};

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		// onCreate
		super.onCreate(savedInstanceState);

		LogUtils.e("CALL-LOG", "onCreate");
		// FIXME call from background
		// Window window = getWindow();
		// window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
		// WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		instance = this;
		setContentView(R.layout.ta_main);

		// FIXME call from background
		// CommonAndroid.unlockScreen(this);

		callingviewstatus = CommonAndroid.getView(this, R.id.callingviewstatus);
		callingviewstatus.show(false);
		ta_main_loading = CommonAndroid.getView(this, R.id.ta_main_loading);

		showLoading(false, false);

		ta_main_loading.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

		findViewById(R.id.ta_main_gray).setBackgroundColor(Color.GRAY);
		contactview = CommonAndroid.getView(this, R.id.contactview);

		dtmsview = CommonAndroid.getView(this, R.id.dtmsview);
		menu5View = CommonAndroid.getView(this, R.id.menu5);
		menu4View = CommonAndroid.getView(this, R.id.menu4view);
		menu3View = CommonAndroid.getView(this, R.id.menu3view);
		menu2view = CommonAndroid.getView(this, R.id.menu2view);
		menuleftview = CommonAndroid.getView(this, R.id.menuleftview);
		main_menu_left = CommonAndroid.getView(this, R.id.main_menu_left);
		menunotificationtview = CommonAndroid.getView(this, R.id.menunotificationtview);
		ta_main_mainview = CommonAndroid.getView(this, R.id.ta_main_mainview);
		menuusertview = CommonAndroid.getView(this, R.id.menuusertview);
		loginview = CommonAndroid.getView(this, R.id.loginview);
		ta_login_main_menu = CommonAndroid.getView(this, R.id.ta_login_main_menu);
		txt_main_number_call = CommonAndroid.getView(this, R.id.txt_main_number_call);
		txt_main_number_call_time = CommonAndroid.getView(this, R.id.txt_main_number_call_time);
		ta_main_status = CommonAndroid.getView(this, R.id.ta_main_status);
		ta_main_btn_1 = CommonAndroid.getView(this, R.id.ta_main_btn_1);
		ta_main_btn_2 = CommonAndroid.getView(this, R.id.ta_main_btn_2);
		ta_main_btn_3 = CommonAndroid.getView(this, R.id.ta_main_btn_3);
		ta_main_btn_4 = CommonAndroid.getView(this, R.id.ta_main_btn_4);
		ta_main_btn_5 = CommonAndroid.getView(this, R.id.ta_main_btn_5);
		ta_main_btn_6 = CommonAndroid.getView(this, R.id.ta_main_btn_6);
		ta_main_on_of = CommonAndroid.getView(this, R.id.ta_main_on_of);
		ta_main_web = CommonAndroid.getView(this, R.id.ta_main_web);
		ta_login_user_infor = CommonAndroid.getView(this, R.id.ta_login_user_infor);
		ta_main_btn_1.setOnClickListener(onClickTaMainBtn);
		ta_main_btn_2.setOnClickListener(onClickTaMainBtn);
		ta_main_btn_3.setOnClickListener(onClickTaMainBtn);
		ta_main_btn_4.setOnClickListener(onClickTaMainBtn);
		ta_main_btn_5.setOnClickListener(onClickTaMainBtn);
		ta_main_btn_6.setOnClickListener(onClickTaMainBtn);
		ta_login_main_menu.setOnClickListener(onClickTaMainBtn);
		ta_login_user_infor.setOnClickListener(onClickTaMainBtn);
		main_menu_left.setOnClickListener(onClickTaMainBtn);

		CommonAndroid.hiddenKeyBoard(this);

		// TODO
		ta_main_on_of.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean on = ((ToggleButton) v).isChecked();

				TASession.status = on ? ClientStatus.ENABLE : ClientStatus.DISABLE;
				TASession.saveStatus();
				enableClient(on, true);
			}
		});

		SipCallSession session = TASipManager.getSipCallSessionFromIntent(getIntent());
		if (session != null) {
			sessionCallId = session.getCallId();
		}

		registerReceiver(receiverLoginApi, new IntentFilter(TAUtils.ACTION.ACTION_BROADCAST_LOGIN_CALLBACK));
		getApplication().registerReceiver(callCalback, new IntentFilter(SipManager.TA_ACTION_SIP_CALL_CHANGED_UI));
		getApplication().registerReceiver(callCalback, new IntentFilter(SipManager.ACTION_SIP_CALL_CHANGED));

		registerReceiver(buddyStateChanged, new IntentFilter(SipManager.TA_ACTION_SIP_BUDDY_CHANGED_STATE));
		updateControlUI(TAMainStatus.NONE, false);
		loginview.setVisibility(View.GONE);
		TAMainApplication.getInstance().init(new TAMainApplicationCallBackIinit() {
			@Override
			public void onServiceDisconnected() {

			}

			@Override
			public void onServiceConnected() {

				NatEnableUtils.enableNat(TAMainActivity.this);

				// ACVUtils.logALL(TAMainActivity.this);
				// ACVUtils.saveTestAccount(TAMainActivity.this);
				boolean isDownloadingApk = false;
				try {
					isDownloadingApk = TAMainApplication.getInstance().getService().isDownloadingApk();
				} catch (RemoteException e) {
				}

				if (isDownloadingApk) {
					loginview.setVisibility(View.GONE);
					ta_main_mainview.setVisibility(View.GONE);
					showDialogForDownload();
					// finish();
					return;
				}
				// ProximityService(TAMainActivity.this, true);
				if (getIntent().hasExtra(SipManager.EXTRA_CALL_INFO)) {
					if (TAUtils.availableForCalls(TAMainActivity.this)) { // on
						loginview.setVisibility(View.GONE);
						ta_main_mainview.setVisibility(View.VISIBLE);
						updateDisplayAccount();
						ta_main_web.loadWhenLoginSuccess();
					} else {
						// loginview.setVisibility(View.VISIBLE);
						// ta_main_mainview.setVisibility(View.GONE);
						// logout();
					}
				} else {
					if (TAUtils.availableForCalls(TAMainActivity.this)) { // on
						loginview.setVisibility(View.GONE);
						ta_main_mainview.setVisibility(View.VISIBLE);
						updateDisplayAccount();
						ta_main_web.loadWhenLoginSuccess();
					} else {
						loginview.setVisibility(View.VISIBLE);
						ta_main_mainview.setVisibility(View.GONE);

						loginview.checkUpdate();
					}

				}
			}

		});

	}

	private void showDialogForDownload() {
		LoginCheckDialog loginCheckDialog = new LoginCheckDialog(this, getString(R.string.downloadingupdateversion)) {
			@Override
			public void onClickOk() {
				finish();
			}
		};
		loginCheckDialog.setShowButtonCancel(false);
		loginCheckDialog.show();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			unregisterReceiver(receiverLoginApi);
			unregisterReceiver(callCalback);
			unregisterReceiver(buddyStateChanged);
		} catch (Exception exception) {

		}
	}

	// ---------------------------------------------
	// ACV
	// ----------------------------------------------
	@Override
	public AccountStatusDisplay _onChange() {
		AccountStatusDisplay display = super._onChange();
		if (!CommonAndroid.NETWORK.haveConnectTed(this)) {
		} else {
			if (display == null) {
				return display;
			}

			if (display.statusLabel.equals(getString(R.string.acct_inactive))) {

				if (getClientCallState() == ClientPresentCallState.FREE) {
					// logout
					loginview.setVisibility(View.VISIBLE);
					ta_main_mainview.setVisibility(View.GONE);

					TADataStore dataStore = new TADataStore(this);
					dataStore.deleteAll();

					((TAMainApplication) getApplication()).callApiLogout();

					ta_main_web.updateUI(TAMainStatus.LOGOUT, "", "");
					showLoading(false, false);
				}
			} else if (display.statusLabel.equals(getString(R.string.acct_regerror))) {
			} else if (display.statusLabel.equals(getString(R.string.acct_registered))) {
				// success
				// login success
				ta_main_web.loadWhenLoginSuccess();
				loginview.setVisibility(View.GONE);
				ta_main_mainview.setVisibility(View.VISIBLE);
				showLoading(false, false);
				// default
				TASession.status = ClientStatus.ENABLE;
				ta_main_on_of(true, true);

				updateControlUI(taMainStatus, false);
				enableClient(true, true);
				updateDisplayAccount();

				try {
					getISipService().resetBuddyList();
				} catch (RemoteException e) {
					e.printStackTrace();
				}

				// TODO
			} else if (display.statusLabel.equals(getString(R.string.acct_registering))) {
				// registering

			} else if (display.statusLabel.equals(getString(R.string.acct_unregistered))) {
				// not register
				showLoading(false, false);

				ta_main_web.updateUI(TAMainStatus.LOGOUT, "", "");
			} else {
				showLoading(false, false);
			}
		}

		return display;

	}

	private void updateDisplayAccount() {
		TextView ta_main_user_name = CommonAndroid.getView(this, R.id.ta_main_user_name);
		TextView ta_main_user_number = CommonAndroid.getView(this, R.id.ta_main_user_number);
		ta_main_user_name.setText("");
		ta_main_user_number.setText("");
		Cursor cursor = getContentResolver().query(new AccountDB(this).getContentUri(), null, null, null, String.format("%s DESC", AccountDB.time_update));

		if (cursor != null) {
			if (cursor.moveToNext()) {
				ta_main_user_name.setText(String.format(getString(R.string.sip_user_number_format), CommonAndroid.getString(cursor, AccountDB.shop_name), ""));
				ta_main_user_number.setText(String.format(getString(R.string.ta_main_user_name), CommonAndroid.getString(cursor, AccountDB.fast_name),
						CommonAndroid.getString(cursor, AccountDB.last_name)));
			}
			cursor.close();
		}
	}

	private View ta_login_main_menu;
	private TextView txt_main_number_call;
	private TextView txt_main_number_call_time;
	private TextView ta_main_status;
	private View ta_main_btn_1;
	private View ta_main_btn_2;
	private View ta_main_btn_3;
	private View ta_main_btn_4;
	private View ta_main_btn_5;
	private View ta_main_btn_6;
	private ToggleButton ta_main_on_of;
	private com.shopivr.component.view.TAWebView ta_main_web;

	// public void closeMenu() {

	// boolean on = (TASession.previousStatus == ClientStatus.ENABLE) ? true :
	// false;
	// Log.e("ShopIVR STATUS", "PREFIOUS:" + (on ? "ON" : "OFF"));

	public void closeMenu(View v) {
		LogUtils.i("MENU", " - CLOSE");
		if (v != null) {
			v.setVisibility(View.GONE);
			updateControlUI(taMainStatus, false);
		}

		if (getClientCallState() != ClientPresentCallState.FREE) {
			// do nothing
			return;
		}
		boolean on = (TASession.previousStatus == ClientStatus.ENABLE) ? true : false;
		// Log.e("ShopIVR STATUS", "PREFIOUS:" + (on ? "ON" : "OFF"));

		enableClient(on, false);
	}

	public void rollbackMenu() {
		if (getClientCallState() == ClientPresentCallState.FREE) {
			enableClient((TASession.previousStatus == ClientStatus.ENABLE) ? true : false, false);
		}
		updateControlUI(taMainStatus, false);
	}

	public void menu3Check(int index) {
		try {
			updateControlUI(taMainStatus, false);
			sendParkNumber(getISipService().getPark(index));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void menu4Call(String number) {
		if (!CommonAndroid.isBlank(number)) {
			setClientCallState(ClientPresentCallState.IN_CALLING);

			updateControlUI(TAMainStatus.Outgoing, false);
			makeCall(number);
		} else {
			rollbackMenu();
		}
	}

	public void menuLeft(int index) {
		ta_main_web.menuLeft(index);
	}

	public void changeCheckedUser(boolean checked) {
		new TASetting(this).setSetting(SettingType.shopmode, checked + "");
	}

	public void menu5(int i) {
		// closeMenu();
		contactview.show(ContactType.CONTACT5, i);
	}

	public void showContactView(ContactType type, int index) {
		contactview.show(type, index);
	}

	public void contactClose(ContactType type) {
		CommonAndroid.hiddenKeyBoard(this);
		if (type == ContactType.TRANSFER2) {
			// menu2view.setVisibility(View.VISIBLE);
		} else if (type == ContactType.CONTACT5) {
			// menu5View.setVisibility(View.VISIBLE);
		}

		rollbackMenu();
	}

	public void contactClose1(ContactType type) {
		CommonAndroid.hiddenKeyBoard(this);
		if (type == ContactType.TRANSFER2) {
			menu2view.setVisibility(View.VISIBLE);
		} else if (type == ContactType.CONTACT5) {
			menu5View.setVisibility(View.VISIBLE);
		}
	}

	public void hiddenKeyBoard() {
		CommonAndroid.hiddenKeyBoard(this);
	}

	/**
	 * 
	 * @param user
	 * @param password
	 * @param shopCode
	 */
	public void check(String user, String password, String shopCode) {
		CommonAndroid.hiddenKeyBoard(this);
		this.user = user;
		this.password = password;
		this.shopCode = shopCode;
		TAMainApplication.getInstance().callApiCheck(user, password, shopCode, "1");

	}

	private String user, password, shopCode;
	// LOGIN
	private BroadcastReceiver receiverLoginApi = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String status = intent.getStringExtra(TAUtils.KEY.STATUS);
			if ("start".equals(status)) {
				showLoading(true, false);
			} else if ("error".equals(status)) {
				String message = intent.getStringExtra(TAUtils.KEY.MESSGAE);
				CommonAndroid.showDialog(TAMainActivity.this, message);
				showLoading(false, false);
			} else if ("check".equals(status)) {
				showLoading(false, false);
				String message = intent.getStringExtra(TAUtils.KEY.MESSGAE);

				LoginCheckDialog loginCheckDialog = new LoginCheckDialog(TAMainActivity.this, message) {

					@Override
					public void onClickOk() {
						try {
							TAMainApplication.getInstance().getService().callApiCheck(user, password, shopCode, "0");
						} catch (RemoteException e) {
						}

					}
				};

				loginCheckDialog.show();
			}
		}

	};

	private int sessionCallId = -1;

	public int getSessionID() {
		return sessionCallId;
	}

	public void setSessionID(int callId) {
		sessionCallId = callId;
	}

	private void updateCallInfor(ParsedSipContactInfos contactInfo) {
		if (contactInfo != null) {
			txt_main_number_call.setText(contactInfo.userName);
		} else {
			txt_main_number_call.setText("---");
			txt_main_number_call_time.setText("---");
		}

	}

	TAMainStatus taMainStatus;

	private void setEnabale(boolean isEnable, View... vs) {
		for (View v : vs) {
			v.setEnabled(isEnable);
		}
	}

	private void updateControlUI(final TAMainStatus taMainStatus, boolean hasParkHold) {
		// LogUtils.e("CALL-LOG", "UpdateUI:" + taMainStatus);

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				try {
					TAMainActivity.this.taMainStatus = taMainStatus;
					if (getClientCallState() != ClientPresentCallState.FREE) {
						ta_main_on_of.setEnabled(false);
					} else {
						ta_main_on_of.setEnabled(true);
					}
					ta_main_btn_1.setVisibility(View.VISIBLE);
					ta_main_btn_2.setVisibility(View.VISIBLE);
					ta_main_btn_3.setVisibility(View.VISIBLE);
					ta_main_btn_4.setVisibility(View.VISIBLE);
					ta_main_btn_5.setVisibility(View.VISIBLE);
					ta_main_btn_6.setVisibility(View.VISIBLE);

					callingviewstatus.show(false);
					setEnabale(false, ta_main_btn_1, ta_main_btn_2, ta_main_btn_3, ta_main_btn_4, ta_main_btn_5, ta_main_btn_6);
					if (taMainStatus == TAMainStatus.Receive_block) {
						ta_main_btn_1.setBackgroundResource(R.drawable._01);
						ta_main_btn_2.setBackgroundResource(R.drawable._02);//
						ta_main_btn_3.setBackgroundResource(R.drawable._03);
						ta_main_btn_4.setBackgroundResource(R.drawable._04_active);//
						ta_main_btn_5.setBackgroundResource(R.drawable._05_activie);//
						ta_main_btn_6.setBackgroundResource(R.drawable._06);//

						setEnabale(true, ta_main_btn_4, ta_main_btn_5);
					} else if (taMainStatus == TAMainStatus.Receive_open) {
						ta_main_btn_1.setBackgroundResource(R.drawable._01);
						ta_main_btn_2.setBackgroundResource(R.drawable._02);//
						ta_main_btn_3.setBackgroundResource(R.drawable._03);
						ta_main_btn_4.setBackgroundResource(R.drawable._04_active);//
						ta_main_btn_5.setBackgroundResource(R.drawable._05_activie);//
						ta_main_btn_6.setBackgroundResource(R.drawable._06);//
						setEnabale(true, ta_main_btn_4, ta_main_btn_5);
					} else if (taMainStatus == TAMainStatus.Incoming) {
						ta_main_btn_1.setBackgroundResource(R.drawable._01_active2);
						ta_main_btn_2.setBackgroundResource(R.drawable._02);
						ta_main_btn_3.setBackgroundResource(R.drawable._03);
						ta_main_btn_4.setBackgroundResource(R.drawable._04);//
						ta_main_btn_5.setBackgroundResource(R.drawable._05_activie);//
						ta_main_btn_6.setBackgroundResource(R.drawable._06_active);//
						ta_main_status.setText(R.string.call_state_incoming);
						callingviewstatus.show(true);

						setEnabale(true, ta_main_btn_1, ta_main_btn_5, ta_main_btn_6);
					} else if (taMainStatus == TAMainStatus.Talking) {
						ta_main_btn_1.setBackgroundResource(R.drawable._01_active);
						ta_main_btn_2.setBackgroundResource(R.drawable._02_active);
						ta_main_btn_3.setBackgroundResource(R.drawable._03_active);
						ta_main_btn_4.setBackgroundResource(R.drawable._04_active);//
						ta_main_btn_5.setBackgroundResource(R.drawable._05_activie);//
						ta_main_btn_6.setBackgroundResource(R.drawable._06);//
						ta_main_status.setText(R.string.call_state_confirmed);

						setEnabale(true, ta_main_btn_1, ta_main_btn_2, ta_main_btn_3, ta_main_btn_4, ta_main_btn_5);
					} else if (taMainStatus == TAMainStatus.TransferOutgoing || taMainStatus == TAMainStatus.TransferStart || taMainStatus == TAMainStatus.TransferCalling) {
						ta_main_btn_1.setBackgroundResource(R.drawable._01);
						ta_main_btn_2.setBackgroundResource(R.drawable._02_active2);
						ta_main_btn_3.setBackgroundResource(R.drawable._03);
						ta_main_btn_4.setBackgroundResource(R.drawable._04);//
						ta_main_btn_5.setBackgroundResource(R.drawable._05);//
						ta_main_btn_6.setBackgroundResource(R.drawable._06);//

						setEnabale(true, ta_main_btn_2);
					} else if (taMainStatus == TAMainStatus.Outgoing) {
						ta_main_btn_1.setBackgroundResource(R.drawable._01_active);
						ta_main_btn_2.setBackgroundResource(R.drawable._02);
						ta_main_btn_3.setBackgroundResource(R.drawable._03);
						ta_main_btn_4.setBackgroundResource(R.drawable._04);//
						ta_main_btn_5.setBackgroundResource(R.drawable._05_activie);//
						ta_main_btn_6.setBackgroundResource(R.drawable._06);//
						ta_main_status.setText(R.string.call_state_calling);

						setEnabale(true, ta_main_btn_1, ta_main_btn_5);
					} else {
						ta_main_btn_1.setBackgroundResource(R.drawable._01);
						ta_main_btn_2.setBackgroundResource(R.drawable._02);//
						ta_main_btn_3.setBackgroundResource(R.drawable._03);
						ta_main_btn_4.setBackgroundResource(R.drawable._04_active);//
						ta_main_btn_5.setBackgroundResource(R.drawable._05_activie);//
						ta_main_btn_6.setBackgroundResource(R.drawable._06);//

						if (ta_main_on_of.isChecked()) {
							ta_main_status.setText(R.string.client_status_setting_on);
						} else {
							ta_main_status.setText(R.string.client_status_setting_off);
						}

						setEnabale(true, ta_main_btn_4, ta_main_btn_5);
					}
				} catch (Exception exception) {
					LogUtils.e("CALL-LOG", exception);
				}
			}
		});
	}

	public void logout() {
		TAMainApplication.getInstance().logout();
	}

	public void showLoading(boolean b, boolean isContact) {
		ta_main_loading.setVisibility(b ? View.VISIBLE : View.GONE);
		findViewById(R.id.progressBar1).setVisibility(isContact ? View.GONE : View.VISIBLE);
		findViewById(R.id.loading_contact).setVisibility(isContact ? View.VISIBLE : View.GONE);
	}

	/**
	 * 
	 * @param indexCallButton
	 * @param sharetel_id
	 * @param group_id
	 * @param user_id
	 * @param idContact
	 */
	public void callFromContactTransfer(int indexCallButton, String exten) {

		if (indexCallButton == 1) {

			if (!CommonAndroid.isBlank(exten)) {
				ta_main_status.setText(R.string.call_state_transfer_start);

				((TAMainApplication) getApplication()).hold();
				((TAMainApplication) getApplication()).makeCallFromTranferKeyBoard(exten);

			} else {

			}
		} else {
			// exten = CommonAndroid.isBlank(exten) ? phone : exten;

			if (!CommonAndroid.isBlank(exten)) {
				contactview.setVisibility(View.GONE);
				if (getClientCallState() == ClientPresentCallState.FREE) { // transfer
					setClientCallState(ClientPresentCallState.IN_CALLING);
					updateControlUI(TAMainStatus.Outgoing, false);
				} else {
					ta_main_status.setText(R.string.call_state_transfer_start);
				}

				makeCall(exten);
			}
		}
	}

	public void menu5Close() {

		LogUtils.i("MENU", "5 - CLOSE");
		// String sip_userid = DataStore.getInstance().init(this).getUser();
		// CommonAndroid.showDialog(this, sip_userid);
		menu5View.setVisibility(View.GONE);
		enableClient((TASession.previousStatus == ClientStatus.ENABLE) ? true : false, false);
	}

	public void onDtms(String number) {
		if (number == null) {
			return;
		}
		SipCallSession session = TASipManager.getSipCallingSession(getISipService());
		int key = 0;
		if (session != null) {
			if ("#".equals(number)) {
				key = KeyEvent.KEYCODE_POUND;
			} else if ("*".equals(number)) {
				key = KeyEvent.KEYCODE_STAR;
			} else {
				try {
					key = Integer.parseInt(number);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
			try {
				((TAMainApplication) getApplication()).getService().sendDtmf(session.getCallId(), key);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @param isShow
	 */
	public void showDtms(boolean isShow) {
		dtmsview.setVisibility(isShow ? View.VISIBLE : View.GONE);
	}

	private String getTimeFormat(long l) {
		long totalSess = l / 1000;
		long sess = totalSess % 60;
		long minusTotal = (totalSess - sess) / 60;
		long minus = minusTotal % 60;
		long hour = minusTotal / 60;

		if (hour == 0) {
			return String.format("%s:%s", ((minus > 9) ? minus : "0" + minus), ((sess > 9) ? sess : "0" + sess));
		} else {
			return String.format("%s:%s:%s", ((hour > 9) ? hour : "0" + hour), ((minus > 9) ? minus : "0" + minus), ((sess > 9) ? sess : "0" + sess));
		}
	}

	private Runnable updateTimerThread = new Runnable() {

		@Override
		public void run() {

			timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

			txt_main_number_call_time.setText(getTimeFormat(timeInMilliseconds));
			customHandler.postDelayed(this, 0);
		}
	};

	public void menu2CallNumber(String number) {
		// isCallTranferNumber = true;
		ta_main_status.setText(R.string.call_state_transfer_start);
		((TAMainApplication) getApplication()).makeCallFromTranferKeyBoard(number);
	}

	public void finishTransferToCall(int callId) {
		((TAMainApplication) getApplication()).exferTransfer(callId, currentCallId);
	}

	public void closeTransferMenu(View v) {
		updateControlUI(TAMainStatus.Talking, false);
	}

	public void updateWeb(String url) {
		ta_main_web.loadUrl(url);
	}

	public void downloadApk(String urlApk) {

		((TAMainApplication) getApplication()).downloadApk(urlApk);
		finish();
	}

	public void closeContactTransferMenu(ContactType mContactType) {
		if (mContactType == ContactType.TRANSFER2) {
			updateControlUI(TAMainStatus.Talking, false);
		} else if (mContactType == ContactType.CONTACT5) {
			rollbackMenu();
		}
	}
}
