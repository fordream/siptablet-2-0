package com.shopivr.component.base;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import z.lib.base.BaseServiceCallBack;
import z.lib.base.BaseUtils;
import z.lib.base.BaseUtils.API;
import z.lib.base.CommonAndroid;
import z.lib.base.LogUtils;
import z.lib.base.callback.RestClient;
import z.lib.base.callback.RestClient.RequestMethod;
import z.lib.base.callback.TACallBack;
import z.lib.base.callback.TARestClient;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;

import com.app.knock.db.TAGroup;
import com.app.knock.db.TASetting;
import com.app.knock.db.TASetting.SettingType;
import com.app.knock.db.TAShareTel;
import com.app.knock.db.TAUser;
import com.atmarkcafe.tracker.ReportApplication;
import com.shopivrtablet.R;
import com.csipsimple.api.ISipService;
import com.csipsimple.api.SipCallSession;
import com.csipsimple.api.SipCallSession.InvState;
import com.csipsimple.api.SipManager;
import com.shopivr.component.model.TADataStore;
import com.shopivr.service.utils.TASession.ClientStatus;
import com.shopivr.service.utils.TAUtils.TAAddressBook;

public class TAMainApplication extends ReportApplication {
	private static TAMainApplication instance;

	public static TAMainApplication getInstance() {
		return instance;
	}

	private Thread.UncaughtExceptionHandler androidDefaultUEH;

	private Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
		public void uncaughtException(Thread thread, Throwable ex) {
			try {
				service.hangupAll();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			androidDefaultUEH.uncaughtException(thread, ex);
		}
	};

	public static class ClientPresentCallState {
		/**
		 * 
		 */
		public static final int FREE = 0;

		/**
		 * 
		 */
		public static final int IN_CALLING = 1;

		/**
		 * answer was selected but response not yet
		 */
		public static final int CONFIRMING = 2;

		/**
		 * in call
		 */
		public static final int CONFIRMED = 3;

		/**
		 * pre park
		 */
		public static final int PRE_PARK = 4;
	}

	// default
	protected int clientCallState = ClientPresentCallState.FREE;

	public int getClientCallState() {
		return getInstance().clientCallState;
	}

	public void setClientCallState(int clientCallState) {
		getInstance().clientCallState = clientCallState;
	}

	private Typeface bold, regular;

	public Typeface getBold() {
		if (bold == null) {
			bold = CommonAndroid.FONT.getTypefaceFromAsset(this, "font/KozGoPr6N-Bold.otf");
		}
		return bold;
	}

	public Typeface getRegular() {

		if (regular == null) {
			regular = CommonAndroid.FONT.getTypefaceFromAsset(this, "font/KozGoPr6N-Regular.otf");
		}
		return regular;
	}

	private void setShowMainActivity(final boolean isShowMainActivity) {
		init(new TAMainApplicationCallBackIinit() {

			@Override
			public void onServiceDisconnected() {

			}

			@Override
			public void onServiceConnected() {
				try {
					getService().setShowScreen(isShowMainActivity);
				} catch (Exception e) {
				}
			}
		});
	}

	/*
	 * private ActivityLifecycleCallbacks callbacks = new
	 * ActivityLifecycleCallbacks() {
	 * 
	 * @Override public void onActivityStopped(Activity activity) {
	 * 
	 * // TODO String name = TAMainActivity.class.getName(); if
	 * (name.equals(activity.getClass().getName())) {
	 * setShowMainActivity(false); } }
	 * 
	 * @Override public void onActivityStarted(Activity activity) { String name
	 * = TAMainActivity.class.getName(); if
	 * (name.equals(activity.getClass().getName())) { setShowMainActivity(true);
	 * } }
	 * 
	 * @Override public void onActivitySaveInstanceState(Activity activity,
	 * Bundle outState) { }
	 * 
	 * @Override public void onActivityResumed(Activity activity) {
	 * 
	 * }
	 * 
	 * @Override public void onActivityPaused(Activity activity) { // ONPAUSE }
	 * 
	 * @Override public void onActivityDestroyed(Activity activity) { }
	 * 
	 * @Override public void onActivityCreated(Activity activity, Bundle
	 * savedInstanceState) { } };
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		//TADataStore.setEnable_tlsCommon(this);
		// FIXME call from background
		// registerActivityLifecycleCallbacks(callbacks);
		androidDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(handler);

		instance = this;

		bold = CommonAndroid.FONT.getTypefaceFromAsset(this, "font/KozGoPr6N-Bold.otf");
		regular = CommonAndroid.FONT.getTypefaceFromAsset(this, "font/KozGoPr6N-Regular.otf");

		TABroardcastManager.sendBroardCastStartSipService(this);
	}

	public interface TAMainApplicationCallBackIinit {

		public void onServiceConnected();

		public void onServiceDisconnected();
	}

	public void init(final TAMainApplicationCallBackIinit callBackIinit) {

		if (service == null) {
			connection = new ServiceConnection() {

				@Override
				public void onServiceConnected(ComponentName arg0, IBinder arg1) {
					service = ISipService.Stub.asInterface(arg1);
					callBackIinit.onServiceConnected();
				}

				@Override
				public void onServiceDisconnected(ComponentName arg0) {
					callBackIinit.onServiceDisconnected();
					service = null;
				}
			};
			bindService(new Intent(SipManager.INTENT_SIP_SERVICE), connection, Context.BIND_AUTO_CREATE);
		} else {
			callBackIinit.onServiceConnected();
		}
	}

	// ================================================================
	// REGISTER SERVICE
	// ================================================================

	private ISipService service;
	private ServiceConnection connection;

	public ISipService getService() {
		return service;
	}

	public void execute(final RequestMethod method, final String api, final JSONObject params, final BaseServiceCallBack cheerzServiceCallBack) {

		execute(method, api, params, new HashMap<String, String>(), cheerzServiceCallBack);
	}

	public void execute(final RequestMethod method, final String api, final JSONObject jsonbject, final Map<String, String> headers, final BaseServiceCallBack cheerzServiceCallBack) {
		if (cheerzServiceCallBack != null) {
			cheerzServiceCallBack.onStart();
		}

		new AsyncTask<String, String, String>() {
			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				if (cheerzServiceCallBack != null) {
					if (client.getResponseCode() == 200) {
						try {
							JSONObject jsonObject = new JSONObject(client.getResponse());
							JSONObject temp = jsonObject.optJSONObject("res");
							// String is_succes =
							// CommonAndroid.getString(jsonObject,
							// BaseUtils.KEY.is_success);
							String is_succes = CommonAndroid.getString(temp, BaseUtils.KEY.is_success);
							String err_msg = CommonAndroid.getString(jsonObject, BaseUtils.KEY.err_msg);
							if (BaseUtils.VALUE.STATUS_API_SUCCESS.equals(is_succes)) {
								cheerzServiceCallBack.onSucces(client.getResponse());
							} else {
								cheerzServiceCallBack.onError(err_msg);
							}

							return;
						} catch (Exception exception) {
						}
					}

					// cheerzServiceCallBack.onError(getString(new
					// Setting(KnockApplication.this).isLangEng() ?
					// R.string.msg_err_connect : R.string.msg_err_connect_j));
					cheerzServiceCallBack.onError("err connect!!");
				}
			}

			private RestClient client;

			@Override
			protected String doInBackground(String... paramStrs) {
				client = new RestClient(BaseUtils.API.BASESERVER(TAMainApplication.this) + api);
				client.addHeader("Content-Type", "application/json; charset=utf-8");
				client.addHeader("Accept", "application/json; charset=utf-8");

				if (headers != null) {
					Set<String> keyHeaders = headers.keySet();
					for (String key : keyHeaders) {
						client.addParam(key, headers.get(key));
					}
				}
				client.addParam2(jsonbject);
				client.execute(method);
				return null;
			}
		}.execute("");
	}

	public void logout() {
		init(new TAMainApplicationCallBackIinit() {

			@Override
			public void onServiceDisconnected() {

			}

			@Override
			public void onServiceConnected() {
				try {
					getService().logout();
				} catch (RemoteException e) {
				}
			}
		});
	}

	/**
	 * 
	 * @param type
	 * @param taCallBack
	 */
	public void getAddressBook(final TAAddressBook type, final TACallBack taCallBack) {
		taCallBack.onStart();
		HashMap<TAAddressBook, String> apis = new HashMap<TAAddressBook, String>();
		apis.put(TAAddressBook.address_book_type_business, API.API_address_book_type_business);
		apis.put(TAAddressBook.address_book_type_sip_personal, API.API_address_book_type_sip_personal);
		apis.put(TAAddressBook.address_book_type_group, API.API_address_book_type_group);
		apis.put(TAAddressBook.address_book_type_local, API.API_address_book_type_local);

		TARestClient restClient = new TARestClient(BaseUtils.API.BASESERVER(TAMainApplication.this) + apis.get(type));
		String user = new TASetting(this).get(SettingType.user);// DataStore.getInstance().init(TAMainApplication.this).getUser();
		String password = new TASetting(this).get(SettingType.password);
		restClient.addParam("request_user_password", password);
		restClient.addParam("request_user", user);
		// address_book_type_business
		restClient.addParam("phone_book_type", "0");
		restClient.addParam("keyword", "");

		// address_book_type_sip_personal
		// API_address_book_type_group
		restClient.addHeader("image_demand_flag", "1");
		restClient.addHeader("last_update_date", "");// YYYY-MM-DD HH:MM:SS.SSS
														// æŒ‡å®šæ—¥ä»˜ã‚ˆã‚Šå¾Œã�«Updateã�•ã‚Œã�Ÿãƒ‡ãƒ¼ã‚¿ã�®ã�¿ã‚’å�–å¾—
		restClient.execute(RequestMethod.POST, new TACallBack() {
			@Override
			public void onSuccess(String status, String errorMessage, String response) {
				try {
					JSONObject res = new JSONObject(response).getJSONObject("res");
					String err_msg = res.getString("err_msg");
					String count = res.getString("count");
					String is_success = res.getString("is_success");
					String err_code = res.getString("err_code");
					if (type == TAAddressBook.address_book_type_business) {
						JSONArray users = res.getJSONArray("sharetel");

						TAShareTel taUser = new TAShareTel(TAMainApplication.this);
						taUser.delete();
						for (int i = 0; i < users.length(); i++) {
							taUser.add(users.getJSONObject(i));
						}
					} else if (type == TAAddressBook.address_book_type_sip_personal) {
						JSONArray users = res.getJSONArray("user");

						TAUser taUser = new TAUser(TAMainApplication.this);
						taUser.delete();
						// TODO add demo
						for (int i = 0; i < users.length(); i++) {
							taUser.add(users.getJSONObject(i));
						}
					} else if (type == TAAddressBook.address_book_type_group) {
						TAGroup taGroup = new TAGroup(TAMainApplication.this);
						JSONArray groups = res.getJSONArray("group");
						taGroup.delete();
						for (int i = 0; i < groups.length(); i++) {
							JSONObject group = groups.getJSONObject(i);
							taGroup.add(group);
						}
					}

				} catch (Exception e) {
				}
				taCallBack.onSuccess(status, errorMessage, response);
			}
		});
	}

	/**
	 * 
	 * @param stauts
	 * @param taCallBack
	 */
	public void updateStatus(final ClientStatus status, final String sip_userid, final TACallBack taCallBack) {
		taCallBack.onStart();
		LogUtils.e("abcs", "update status");
		String user = new TASetting(this).get(SettingType.user);// DataStore.getInstance().init(TAMainApplication.this).getUser();
		String password = new TASetting(this).get(SettingType.password);
		TARestClient restClient = new TARestClient(BaseUtils.API.BASESERVER(TAMainApplication.this) + API.API_UPDATE_STATUS);
		restClient.addParam("request_user_password", password);
		restClient.addParam("request_user", user);
		restClient.addParam("user_id", sip_userid);
		if (status == ClientStatus.ENABLE) {
			restClient.addParam("status", "1");
		} else {
			restClient.addParam("status", "0");
		}

		restClient.execute(RequestMethod.POST, new TACallBack() {
			@Override
			public void onSuccess(String status, String message, String response) {
				try {
					JSONObject jsonObject = new JSONObject(response);
					String is_success = CommonAndroid.getString(jsonObject.getJSONObject("res"), "is_success");
					String err_code = CommonAndroid.getString(jsonObject.getJSONObject("res"), "err_code");
					String err_msg = CommonAndroid.getString(jsonObject.getJSONObject("res"), "err_msg");
					if ("0".equals(is_success)) {
						taCallBack.onSuccess(null, message, response);
					} else {
						if (CommonAndroid.isBlank(err_msg)) {
							err_msg = getString(R.string.error_update_status);
						}
						taCallBack.onError("0", err_msg);
					}
				} catch (Exception exception) {
					taCallBack.onError("0", getString(R.string.error_update_status));
				}
			}
		});

	}

	public void callApiLogout() {
		init(new TAMainApplicationCallBackIinit() {

			@Override
			public void onServiceDisconnected() {

			}

			@Override
			public void onServiceConnected() {
				try {
					getService().callApiLogout();
				} catch (Exception e) {
				}
			}
		});
	}

	public void hold() {
		init(new TAMainApplicationCallBackIinit() {
			@Override
			public void onServiceDisconnected() {

			}

			@Override
			public void onServiceConnected() {
				try {
					// TODO
					SipCallSession session = TASipManager.getSipCallingSession(getService());
					getService().hold(session.getCallId());
				} catch (Exception e) {
				}
			}
		});
	}

	public void makeCallFromTranferKeyBoard(final String number) {
		init(new TAMainApplicationCallBackIinit() {
			@Override
			public void onServiceDisconnected() {

			}

			@Override
			public void onServiceConnected() {
				try {
					getService().makeCall(number, 1);
				} catch (Exception e) {
				}
			}
		});

	}

	public void hangup(final int callId, final int status) {
		init(new TAMainApplicationCallBackIinit() {
			@Override
			public void onServiceDisconnected() {

			}

			@Override
			public void onServiceConnected() {
				try {
					getService().hangup(callId, status);
				} catch (Exception e) {
				}
			}
		});
	}

	public void cancelTransfer() {
		init(new TAMainApplicationCallBackIinit() {
			@Override
			public void onServiceDisconnected() {

			}

			@Override
			public void onServiceConnected() {
				try {
					SipCallSession[] callSessions = null;
					try {
						callSessions = getService().getCalls();

						for (int i = 0; i < callSessions.length; i++) {
							SipCallSession session = callSessions[i];

							if (session.isActive()) {
								if (session.getCallState() != InvState.CONFIRMED) {
									getService().hangup(session.getCallId(), 0);
								}
							} else {
							}
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
				} catch (Exception e) {
				}
			}
		});

	}

	public void exferTransfer(final int callId, final int otherCallId) {
		init(new TAMainApplicationCallBackIinit() {
			@Override
			public void onServiceDisconnected() {

			}

			@Override
			public void onServiceConnected() {
				try {
					getService().xferReplace(callId, otherCallId, 0);
					// getService().hangup(callId, otherCallId);
				} catch (Exception e) {
				}
			}
		});

	}

	public int[] getCallIdInProcess() {
		try {
			return getService().getCallIdInProcess();
		} catch (Exception e) {
			return new int[] { -1, -1 };
		}
	}

	public void finishTransferToCall2(int callId) {

	}

	public void startRing(final String remoteContact) {
		init(new TAMainApplicationCallBackIinit() {
			@Override
			public void onServiceDisconnected() {

			}

			@Override
			public void onServiceConnected() {
				try {
					
					getService().startRing(remoteContact);
				} catch (Exception e) {
				}
			}
		});
	}

	public void downloadApk(final String urlApk) {
		init(new TAMainApplicationCallBackIinit() {
			@Override
			public void onServiceDisconnected() {

			}

			@Override
			public void onServiceConnected() {
				try {
					getService().downloadApk(urlApk);
				} catch (Exception e) {
				}
			}
		});
	}

	public void callApiCheck(final String user, final String password, final String shopCode, final String status) {
		init(new TAMainApplicationCallBackIinit() {
			@Override
			public void onServiceDisconnected() {
				CommonAndroid.toast(getApplicationContext(), "onServiceDisconnected");
			}

			@Override
			public void onServiceConnected() {
				try {
					getService().callApiCheck(user, password, shopCode, status);
				} catch (Exception e) {
					CommonAndroid.toast(getApplicationContext(), e.getMessage());
				}
			}
		});
	}

	public void hangupAll() {
		init(new TAMainApplicationCallBackIinit() {
			@Override
			public void onServiceDisconnected() {
				CommonAndroid.toast(getApplicationContext(), "onServiceDisconnected");
			}

			@Override
			public void onServiceConnected() {
				try {
					getService().hangupAll();
				} catch (Exception e) {
				}
			}
		});
	}
}
