package z.lib.base;

import java.io.DataOutputStream;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.List;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog.Builder;
import android.app.KeyguardManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.StatFs;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.shopivrtablet.R;

@SuppressLint("NewApi")
public class CommonAndroid {

	public static String getBSSID(Context context) {
		try {
			WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiMan.getConnectionInfo();

			return wifiInfo.getBSSID();
		} catch (Exception exception) {
			return null;
		}
	}

	public interface ScreenCallBack {
		// when screen off
		public void screenOff();

		// when screen on but haven't lock
		public void screenOn();

		// when screen on but have lock
		public void screenOnHaveLock();

		// screen unlock
		public void screenUnlock();
	}

	public static BroadcastReceiver registerScreenAction(final Context context, final ScreenCallBack screenCallBack) {
		BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

				if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
					screenCallBack.screenOff();
				} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
					if (!haveLockScreen(context)) {
						screenCallBack.screenOn();
					} else {
						screenCallBack.screenOnHaveLock();
					}
				} else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
					screenCallBack.screenUnlock();
				}
			}
		};

		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_USER_PRESENT);
		context.registerReceiver(broadcastReceiver, filter);
		return broadcastReceiver;
	}

	public static void showDialog(Context context, String message, OnClickListener listener) {
		Builder builder = new Builder(context);
		builder.setMessage(message);
		builder.setCancelable(false);
		builder.setPositiveButton("OK", listener);
		builder.show();

	}

	public static void showDialog(Context context, String message) {
		Builder builder = new Builder(context);
		builder.setMessage(message);
		builder.setCancelable(false);
		builder.setPositiveButton(R.string.ok, null);
		builder.show();
	}

	public static void showDialog(Context context, String message, OnClickListener onClickListener, OnClickListener onClickListener2) {
		Builder builder = new Builder(context);
		builder.setMessage(message);
		builder.setCancelable(false);
		builder.setPositiveButton("OK", onClickListener);
		builder.setNegativeButton("Cancel", onClickListener2);
		builder.show();

	}

	public static void showDialogYESNO(Context context, String message, OnClickListener onClickListener, OnClickListener onClickListener2) {
		Builder builder = new Builder(context);
		builder.setMessage(message);
		builder.setCancelable(false);
		builder.setPositiveButton("YES", onClickListener);
		builder.setNegativeButton("NO", onClickListener2);
		builder.show();

	}

	public static boolean haveLockScreen(Context context) {
		KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
		return km.inKeyguardRestrictedInputMode();
	}

	public static void showMarketPublish(Context context, String publish) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("market://search?q=pub:" + publish));
		context.startActivity(intent);
	}

	public static void showMarketProductBuyPackage(Context context, String pack) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("market://details?id=" + pack));
		context.startActivity(intent);
	}

	public static boolean callPhone(Context context, String phone) {
		try {
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:" + phone));
			context.startActivity(callIntent);
			return true;
		} catch (ActivityNotFoundException e) {
			return false;
		}
	}

	public static boolean callWeb(Context context, String url) {
		try {
			Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			context.startActivity(myIntent);
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	public static void hiddenKeyBoard(Activity activity) {
		try {
			String service = Context.INPUT_METHOD_SERVICE;
			InputMethodManager imm = null;
			imm = (InputMethodManager) activity.getSystemService(service);
			IBinder binder = activity.getCurrentFocus().getWindowToken();
			imm.hideSoftInputFromWindow(binder, 0);
		} catch (Exception e) {
		}
	}

	public static void setOrientation(Activity activity, boolean islandscape) {
		if (islandscape) {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

	public static void hiddenTitleBarAndFullScreen(Activity activity) {
		activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
		int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		activity.getWindow().setFlags(flag, flag);
	}

	public static void showKeyBoard(Activity activity, EditText editText) {
		String service = Context.INPUT_METHOD_SERVICE;
		InputMethodManager imm = null;
		imm = (InputMethodManager) activity.getSystemService(service);
		imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
	}

	public static void showKeyBoard(Activity activity) {
		String service = Context.INPUT_METHOD_SERVICE;
		InputMethodManager imm = null;
		imm = (InputMethodManager) activity.getSystemService(service);
		imm.showSoftInput(activity.getCurrentFocus(), InputMethodManager.SHOW_FORCED);
	}

	public static int getVersionApp(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			return 0;
		}
	}

	public static String getVersionName(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (Exception e) {
			return null;
		}
	}

	public static void toast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	public static void toast(Context context, int message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	public static boolean checkApplicationRunning(Context context) {
		// <uses-permission android:name="android.permission.GET_TASKS" />
		ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
		String packageName = am.getRunningTasks(1).get(0).topActivity.getPackageName();
		if (packageName.equalsIgnoreCase(context.getPackageName())) {
			return true;
		}
		return false;
	}

	public static Intent getLaucher(String otherPackage, Context context) {
		PackageManager packageManager = (PackageManager) context.getPackageManager();
		return packageManager.getLaunchIntentForPackage(otherPackage);
	}

	/**
	 * @param packageName
	 * @return
	 */
	public static boolean isAppRuning(String packageName, Context mContext) {
		ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
		for (int i = 0; i < procInfos.size(); i++) {
			if (procInfos.get(i).processName.equals(packageName)) {
				return true;
			}
		}

		return false;
	}

	public static boolean isAppRunningOnTop(Context mContext) {
		ActivityManager am = (ActivityManager) mContext.getSystemService(Activity.ACTIVITY_SERVICE);
		String packageName = am.getRunningTasks(1).get(0).topActivity.getPackageName();
		if (packageName.equalsIgnoreCase(mContext.getPackageName())) {
			return true;
		}
		return false;
	}

	public static boolean checkPermission(String permission, Context mContext) {
		PackageManager packageManager = mContext.getPackageManager();
		return (packageManager.checkPermission(permission, mContext.getPackageName()) == PackageManager.PERMISSION_GRANTED);
	}

	// ============================================================================
	// GPS
	// ============================================================================
	public static class GPS {
		public static boolean isSupportGPS(Context context) {
			LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			List<String> lAllProviders = manager.getAllProviders();
			for (int i = 0; i < lAllProviders.size(); i++) {
				if (LocationManager.GPS_PROVIDER.equals(lAllProviders.get(i))) {
					return true;
				}
			}
			return false;
		}

		public static void showGPSSetting(Context context, int request_code) {
			String action = android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS;
			Intent intent = new Intent(action);
			((Activity) context).startActivityForResult(intent, request_code);
		}

		public static void onpenGPS(Context context) {
			final Intent poke = new Intent();
			String packageName = "com.android.settings";
			String className = "com.android.settings.widget.SettingsAppWidgetProvider";
			poke.setClassName(packageName, className);
			poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
			poke.setData(Uri.parse("3"));
			context.sendBroadcast(poke);
		}

		public static void turnOffGPS(Context context) {
			Intent poke = new Intent();
			poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
			poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
			poke.setData(Uri.parse("3"));
			context.sendBroadcast(poke);

		}

		public static boolean isOpenGPS(Context context) {
			LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		}
	}

	// ============================================================================
	// NetWork
	// ============================================================================
	public static class NETWORK {
		public static boolean haveConnectTed(Context context) {
			Object service = context.getSystemService(Context.CONNECTIVITY_SERVICE);
			ConnectivityManager connectivityManager = (ConnectivityManager) service;
			NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
			for (int i = 0; i < networkInfos.length; i++) {
				if (networkInfos[i].isConnected()) {
					return true;
				}
			}

			return false;
		}

		public static void opennetworkSim(Context context, int requestCode) {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setClassName("com.android.phone", "com.android.phone.NetworkSetting");
			((Activity) context).startActivityForResult(intent, requestCode);
		}

		public static void openWIFISetting(Context context, int requestCode) {
			Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
			((Activity) context).startActivityForResult(intent, requestCode);
		}

		public static void openNetWorkSetting(Context context, int requestCode) {
			Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
			((Activity) context).startActivityForResult(intent, requestCode);
		}
	}

	// ============================================================================
	// Account
	// ============================================================================
	public static class ACCOUNT {
		/**
		 * Check account google
		 * 
		 * @param context
		 * @return
		 */
		public static boolean haveMailAccountGoogleOnDevice(Context context) {
			AccountManager accountManager = AccountManager.get(context);
			Account[] accounts = accountManager.getAccountsByType("com.google");
			Account account;
			if (accounts.length > 0) {
				account = accounts[0];
			} else {
				account = null;
			}

			return account != null;
		}

	}

	// ============================================================================
	// ROOT CHECKER
	// ============================================================================
	public static class ROOTCHECKER {

		public static boolean checkRoot() {

			Process p;
			try {
				// Perform SU to get root privledges
				p = Runtime.getRuntime().exec("su");

				DataOutputStream os = new DataOutputStream(p.getOutputStream());
				os.writeBytes("remount rw");
				os.writeBytes("echo Root Test > /system/rootcheck.txt");
				os.writeBytes("exit\n");
				os.flush();

				p.waitFor();
				if (p.exitValue() != 255) {
					return true;
					// Phone is rooted
				} else {
					// Phone is not rooted
				}
			} catch (Exception e) {

			}
			return false;

		}
	}

	// ============================================================================
	// FONT
	// ============================================================================
	public static class FONT {
		public static final String PATH = "src/org/com/cnc/common/android/font/";
		public static final String BRADHITC = "BRADHITC.TTF";
		public static final String AGENCYB = "AGENCYB.TTF";
		public static final String BROADW = "BROADW.TTF";
		public static final String ALGER = "ALGER.TTF";

		public static void setTypeface(TextView tv, String fileAsset) {
			try {
				File file = new File(PATH + fileAsset);
				Log.i("file", file.exists() + "");
				Typeface tf = Typeface.createFromFile(file);
				tv.setTypeface(tf);
			} catch (Exception e) {
			}
		}

		public static Typeface getTypefaceFromAsset(Context context, String fileAsset) {
			try {
				AssetManager assertManager = context.getAssets();
				Typeface tf = Typeface.createFromAsset(assertManager, fileAsset);
				return tf;
			} catch (Exception e) {
				return null;
			}
		}

		public static void setTypefaceFromAsset(TextView tv, String fileAsset) {
			try {
				AssetManager assertManager = tv.getContext().getAssets();
				Typeface tf = Typeface.createFromAsset(assertManager, fileAsset);
				tv.setTypeface(tf);
			} catch (Exception e) {
			}
		}

		public static void main(String[] args) {
			File file = new File(PATH + ALGER);
			System.out.println(file.exists());
		}
	}

	// ============================================================================
	// CommonDeviceId
	// ============================================================================

	public static class DEVICEID {
		private static final int SIZE_10 = 10;
		public static final String TYPE_ID_IMEI = "IMEI";
		public static final String TYPE_ID_IPSEUDO_UNIQUE_ID = "Pseudo_Unique_Id";
		public static final String TYPE_ID_IANDROIDID = "AndroidId";
		public static final String TYPE_ID_IWLAN_MAC_ADDRESS = "WLAN_MAC_Address";
		public static final String TYPE_ID_IBT_MAC_ADDRESS = "BT_MAC_Address";
		public static final String TYPE_ID_ICOMBINED_DEVICE_ID = "Combined_Device_ID";

		public static final int SIZE_WIDTH_Y = 240;// Galaxy Y
		public static final int SIZE_HEIGHT_Y = 320;// Galaxy Y

		public static final int SIZE_WIDTH_EMULATOR_16 = 320;// EMULATOR
		public static final int SIZE_HEIGHT_EMULATOR_16 = 480;// EMULATOR

		public static final int SIZE_WIDTH_S = 480;// Galaxy S
		public static final int SIZE_HEIGHT_S = 800;// Galaxy S

		public static final int SIZE_WIDTH_TAB = 600;// Tab 7'
		public static final int SIZE_HEIGHT_TAB = 1024;// Tab 7'

		public static final int SIZE_WIDTH_VIEWSONIC = 600;// View Sonic
		public static final int SIZE_HEIGHT_VIEWSONIC = 1024;// View Sonic\

		public static boolean isTablet(Activity context) {
			Display display = context.getWindowManager().getDefaultDisplay();
			int width = display.getWidth();
			int height = display.getHeight();
			int min = width < height ? width : height;
			if (min > SIZE_WIDTH_S) {
				return true;
			}

			return false;
		}

		public static int getWidth(Activity context) {
			Display display = context.getWindowManager().getDefaultDisplay();
			return display.getWidth();
		}

		public static int getHeight(Activity context) {
			Display display = context.getWindowManager().getDefaultDisplay();
			return display.getHeight();
		}

		// Device ID
		public static String deviceId(Context context, String type) {
			if (TYPE_ID_IANDROIDID.equals(type)) {
				return deviceIdFromAndroidId(context);
			} else if (TYPE_ID_IBT_MAC_ADDRESS.equals(type)) {
				return deviceIdFromBT_MAC_Address(context);
			} else if (TYPE_ID_ICOMBINED_DEVICE_ID.equals(type)) {
				return deviceIdFromCombined_Device_ID(context);
			} else if (TYPE_ID_IMEI.equals(type)) {
				return deviceIdFromIMEI(context);
			} else if (TYPE_ID_IPSEUDO_UNIQUE_ID.equals(type)) {
				return deviceIdFromIMEI(context);
			} else if (TYPE_ID_IWLAN_MAC_ADDRESS.equals(type)) {
				return deviceIdFromWLAN_MAC_Address(context);
			}

			return null;
		}

		private static String deviceIdFromIMEI(Context context) {
			try {
				TelephonyManager TelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				return TelephonyMgr.getDeviceId();
			} catch (Exception e) {
				return null;
			}

		}

		private static String deviceIdFromPseudo_Unique_Id() {
			StringBuilder builder = new StringBuilder();
			builder.append("35");
			builder.append(Build.BOARD.length() % SIZE_10);
			builder.append(Build.BRAND.length() % SIZE_10);
			builder.append(Build.CPU_ABI.length() % SIZE_10);
			builder.append(Build.DEVICE.length() % SIZE_10);
			builder.append(Build.DISPLAY.length() % SIZE_10);
			builder.append(Build.HOST.length() % SIZE_10);
			builder.append(Build.ID.length() % SIZE_10);
			builder.append(Build.MANUFACTURER.length() % SIZE_10);
			builder.append(Build.MODEL.length() % SIZE_10);
			builder.append(Build.PRODUCT.length() % SIZE_10);
			builder.append(Build.TAGS.length() % SIZE_10);
			builder.append(Build.TYPE.length() % SIZE_10);
			builder.append(Build.USER.length() % SIZE_10);
			return builder.toString();
		}

		private static String deviceIdFromAndroidId(Context context) {
			try {
				ContentResolver cr = context.getContentResolver();
				return Secure.getString(cr, Secure.ANDROID_ID);
			} catch (Exception e) {
				return null;
			}
		}

		private static String deviceIdFromWLAN_MAC_Address(Context context) {
			try {
				WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				return wm.getConnectionInfo().getMacAddress();
			} catch (Exception e) {
				return null;
			}
		}

		private static String deviceIdFromBT_MAC_Address(Context context) {
			try {
				BluetoothAdapter m_BluetoothAdapter = null;
				m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				return m_BluetoothAdapter.getAddress();
			} catch (Exception e) {
				return null;
			}
		}

		private static String deviceIdFromCombined_Device_ID(Context context) {
			StringBuilder builder = new StringBuilder();
			builder.append(deviceIdFromIMEI(context));
			builder.append(deviceIdFromPseudo_Unique_Id());
			builder.append(deviceIdFromAndroidId(context));
			builder.append(deviceIdFromWLAN_MAC_Address(context));
			builder.append(deviceIdFromBT_MAC_Address(context));

			String m_szLongID = builder.toString();
			MessageDigest m = null;
			try {
				m = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
			byte p_md5Data[] = m.digest();
			String m_szUniqueID = new String();
			for (int i = 0; i < p_md5Data.length; i++) {
				int b = (0xFF & p_md5Data[i]);
				if (b <= 0xF)
					m_szUniqueID += "0";
				m_szUniqueID += Integer.toHexString(b);
			}

			return m_szUniqueID;
		}

		public static boolean canCallPhone(Context context) {
			TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			if (telephonyManager.getSimSerialNumber() != null) {
				if (telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY) {
					return true;
				}
			}

			return false;
		}

		public static void rescanSdcard(Context context) {
			new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
			IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_SCANNER_STARTED);
			intentFilter.addDataScheme("file");
			context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
		}

		public static String findDeviceID(Context context) {
			String deviceID = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
			return deviceID;
		}
	}

	// ============================================================================
	// RESIZE
	// ============================================================================
	public static class RESIZE {

		public static int getWidthScreen(Context context) {
			Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
			return display.getWidth();
		}

		public static int getHeightScreen(Context context) {
			Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
			return display.getHeight();
		}

		// -------------------------------------------------------
		// 20130408 fix
		// -------------------------------------------------------
		public static float _20130408_ScaleLandW960H640(Context context) {
			try {
				float SCREEN_HIGHT = 640;
				float SCREEN_WIDTH = 960;

				float res_width = getWidthScreen(context);
				float res_height = getHeightScreen(context);

				float scale = res_height / SCREEN_HIGHT;

				if (SCREEN_HIGHT / res_height < SCREEN_WIDTH / res_width) {
					scale = res_width / SCREEN_WIDTH;
				}

				return scale;
			} catch (Exception exception) {
				return 1.0f;
			}
		}

		public static void _20130408_resizeLandW960H640(View view, int width, int height) {
			ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
			float scale = _20130408_ScaleLandW960H640(view.getContext());
			layoutParams.width = (int) (width * scale);
			layoutParams.height = (int) (height * scale);
			view.setLayoutParams(layoutParams);
		}

		public static int _20130408_getSizeByScreenLandW960H640(Context context, int sizeFirst) {
			return (int) (sizeFirst * _20130408_ScaleLandW960H640(context));
		}

		public static void _20130408_sendViewToPositionLandW960H640(View view, int left, int top) {
			float scale = _20130408_ScaleLandW960H640(view.getContext());
			ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
			int _left = (int) (left * scale);
			int _top = (int) (top * scale);

			if (layoutParams instanceof RelativeLayout.LayoutParams) {
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			} else if (layoutParams instanceof LinearLayout.LayoutParams) {
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			} else if (layoutParams instanceof FrameLayout.LayoutParams) {
				FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			} else if (layoutParams instanceof TableRow.LayoutParams) {
				TableRow.LayoutParams lp = new TableRow.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			} else if (layoutParams instanceof TableLayout.LayoutParams) {
				TableLayout.LayoutParams lp = new TableLayout.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			}
		}

		// Scale Width
		public static float _20130408_ScaleW960(Context context) {
			try {
				float SCREEN_WIDTH = 960;
				float res_width = getWidthScreen(context);
				return res_width / SCREEN_WIDTH;
			} catch (Exception exception) {
				return 1.0f;
			}
		}

		public static void _20130408_resizeW960(View view, int width, int height) {
			ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
			float scale = _20130408_ScaleW960(view.getContext());
			layoutParams.width = (int) (width * scale);
			layoutParams.height = (int) (height * scale);
			if (width == LayoutParams.WRAP_CONTENT) {
				layoutParams.width = LayoutParams.WRAP_CONTENT;
			} else if (width == LayoutParams.MATCH_PARENT) {
				layoutParams.width = LayoutParams.MATCH_PARENT;
			} else if (width == LayoutParams.FILL_PARENT) {
				layoutParams.width = LayoutParams.FILL_PARENT;
			}

			if (height == LayoutParams.WRAP_CONTENT) {
				layoutParams.height = LayoutParams.WRAP_CONTENT;
			} else if (height == LayoutParams.MATCH_PARENT) {
				layoutParams.height = LayoutParams.MATCH_PARENT;
			} else if (height == LayoutParams.FILL_PARENT) {
				layoutParams.height = LayoutParams.FILL_PARENT;
			}

			view.setLayoutParams(layoutParams);
		}

		public static int _20130408_getSizeByScreenW960(Context context, int sizeFirst) {
			return (int) (sizeFirst * _20130408_ScaleW960(context));
		}

		public static void _20130408_sendViewToPositionW960(View view, int left, int top) {
			float scale = _20130408_ScaleW960(view.getContext());
			ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
			int _left = (int) (left * scale);
			int _top = (int) (top * scale);

			if (layoutParams instanceof RelativeLayout.LayoutParams) {
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			} else if (layoutParams instanceof LinearLayout.LayoutParams) {
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			} else if (layoutParams instanceof FrameLayout.LayoutParams) {
				FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			} else if (layoutParams instanceof TableRow.LayoutParams) {
				TableRow.LayoutParams lp = new TableRow.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			} else if (layoutParams instanceof TableLayout.LayoutParams) {
				TableLayout.LayoutParams lp = new TableLayout.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			}
		}

		// Height
		// Scale Width
		public static float _20130408_ScaleH960(Context context) {
			try {
				float SCREEN_WIDTH = 960;
				float res_width = getHeightScreen(context);
				return res_width / SCREEN_WIDTH;
			} catch (Exception exception) {
				return 1.0f;
			}
		}

		public static void _20130408_resizeH960(View view, int width, int height) {
			ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
			float scale = _20130408_ScaleH960(view.getContext());
			layoutParams.width = (int) (width * scale);
			layoutParams.height = (int) (height * scale);
			view.setLayoutParams(layoutParams);
		}

		public static int _20130408_getSizeByScreenH960(Context context, int sizeFirst) {
			return (int) (sizeFirst * _20130408_ScaleH960(context));
		}

		public static void _20130408_sendViewToPositionH960(View view, int left, int top) {
			float scale = _20130408_ScaleH960(view.getContext());
			ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
			int _left = (int) (left * scale);
			int _top = (int) (top * scale);

			if (layoutParams instanceof RelativeLayout.LayoutParams) {
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			} else if (layoutParams instanceof LinearLayout.LayoutParams) {
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			} else if (layoutParams instanceof FrameLayout.LayoutParams) {
				FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			} else if (layoutParams instanceof TableRow.LayoutParams) {
				TableRow.LayoutParams lp = new TableRow.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			} else if (layoutParams instanceof TableLayout.LayoutParams) {
				TableLayout.LayoutParams lp = new TableLayout.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			}
		}
	}

	// ============================================================================
	// SHORTCUT
	// ============================================================================
	/**
	 * <uses-permission
	 * android:name="com.android.launcher.permission.INSTALL_SHORTCUT" />
	 * 
	 * @author truongvv
	 * 
	 */
	public static class SHORTCUT {
		private Context context;

		public SHORTCUT(Context context) {
			this.context = context;
		}

		public void deleteShortCut(Class<?> cls, int resstrName, int resIcon) {
			Intent removeIntent = createIntent(cls, "com.android.launcher.action.UNINSTALL_SHORTCUT", resstrName, resIcon);
			context.sendBroadcast(removeIntent);
		}

		public void autoCreateShortCut(Class<?> clss, int resstrName, int resIcon) {
			Intent intentShortcut = createIntent(clss, null, resstrName, resIcon);
			intentShortcut.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
			context.sendBroadcast(intentShortcut);
		}

		private Intent createIntent(Class<?> cls, String action, int resstrName, int resIcon) {

			Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
			shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			// -------------------------------------------------------------
			// sam sung 2x
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB && Build.BRAND.equals("samsung")) {
				// sam sung 2.x
				// shortcutIntent.addCategory("android.intent.category.LAUNCHER");
			} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && Build.BRAND.equals("samsung")) {
				// sam sung 4.x
				shortcutIntent.addCategory("android.intent.category.LAUNCHER");
				shortcutIntent.setPackage(context.getPackageName());
				shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			} else {
				// orther
				shortcutIntent.addCategory("android.intent.category.LAUNCHER");
				shortcutIntent.setPackage(context.getPackageName());
			}
			// -------------------------------------------------------

			shortcutIntent.setClass(context, cls);

			Intent intentShortcut = new Intent();
			if (action != null) {
				intentShortcut = new Intent(action);
			}
			intentShortcut.putExtra("android.intent.extra.shortcut.INTENT", shortcutIntent);
			String title = context.getResources().getString(resstrName);
			intentShortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
			// intentShortcut
			// .setAction("com.android.launcher.action.INSTALL_SHORTCUT");
			intentShortcut.putExtra("duplicate", false);

			final int icon = resIcon;

			intentShortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context, icon));
			return intentShortcut;
		}

		public void removieShortCutLauncher() {
			Intent intent = CommonAndroid.getLaucher(context.getPackageName(), context);
			intent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
			context.sendBroadcast(intent);
		}

		public void createShortCutLauncher(int resstrName, int resIcon) {
			Intent intent = CommonAndroid.getLaucher(context.getPackageName(), context);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //

			// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// -------------------------------------------------------------
			// sam sung 2x
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB && Build.BRAND.equals("samsung")) {
				// sam sung 2.x
				// shortcutIntent.addCategory("android.intent.category.LAUNCHER");
			} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && Build.BRAND.equals("samsung")) {
				// sam sung 4.x
				// shortcutIntent.addCategory("android.intent.category.LAUNCHER");
				// shortcutIntent.setPackage(context.getPackageName());
				intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			} else {
				// orther
				// shortcutIntent.addCategory("android.intent.category.LAUNCHER");
				// shortcutIntent.setPackage(context.getPackageName());
			}
			// -------------------------------------------------------

			Intent intentShortcut = new Intent();
			intentShortcut.putExtra("android.intent.extra.shortcut.INTENT", intent);
			intentShortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getResources().getString(resstrName));
			intentShortcut.putExtra("duplicate", false);
			intentShortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context, resIcon));
			intentShortcut.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
			context.sendBroadcast(intent);
		}

	}

	// ============================================================================
	// STORE AVAILABLE
	// ============================================================================
	public static class STOREAVAIABLE {
		public static long avaiableInternalStoreMemory() {
			StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
			long bytesAvailable = (long) stat.getFreeBlocks() * (long) stat.getBlockSize();
			return (bytesAvailable / 1048576);
		}

		public static long totalInternalStorageMemory() {
			StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
			long bytesAvailable = (long) stat.getBlockSize() * (long) stat.getBlockCount();
			return bytesAvailable / 1048576;
		}

		public static long availableExternalStorageMemory() {
			StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
			long bytesAvailable = (long) stat.getFreeBlocks() * (long) stat.getBlockSize();
			return bytesAvailable / 1048576;
		}

		public static long totalExternalStorageMemory() {
			StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
			long bytesAvailable = (long) stat.getBlockSize() * (long) stat.getBlockCount();
			return bytesAvailable / 1048576;
		}
	}

	public static boolean isBlank(String columnName) {
		return columnName == null || columnName != null && columnName.trim().equals("");
	}

	public static <T extends View> T getView(View view, int res) {
		T v = (T) view.findViewById(res);
		return v;
	}

	public static <T extends View> T getView(Activity view, int res) {
		T v = (T) view.findViewById(res);
		return v;
	}

	public static void showView(boolean b, View... vs) {

		if (vs != null) {
			for (View v : vs) {
				v.setVisibility(b ? View.VISIBLE : View.GONE);
			}
		}

	}

	public static String getString(JSONObject jsonObject, String key) {
		if (jsonObject != null && jsonObject.has(key)) {
			try {
				return jsonObject.getString(key);
			} catch (JSONException e) {
			}
		}
		return "";
	}

	public static String getString(Cursor cursor, String key) {
		try {
			return cursor.getString(cursor.getColumnIndex(key));
		} catch (Exception exception) {
			return "";
		}
	}

	/**
	 * 
	 */

	public static final class StringConnvert {
		public static final String convertVNToAlpha(String str) {
			if (isBlank(str)) {
				return str;
			}
			String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
			Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
			// D D d d
			return null;
		}

		private static boolean isBlank(String str) {
			return str == null || (str != null && str.trim().equals(""));
		}
	}

	/*
	 * */

	public static final void setAnimationOnClick(View view) {
		OnTouchListener onTouchListener = new OnTouchListener() {
			private Animation a_normal, a_selected, a_current;
			private boolean canUseAnimation = true;

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				/**
				 */
				// a_normal = new AlphaAnimation(1, 1f);
				if (a_normal == null) {
					a_normal = new ScaleAnimation(1, 1, 1, 1, v.getWidth() / 2, v.getHeight() / 2);
					a_normal.setDuration(0);
					a_normal.setFillAfter(true);
				}
				// a_selected = new AlphaAnimation(0.5f, 0.5f);
				if (a_selected == null) {
					a_selected = new ScaleAnimation(0.9f, 0.9f, 0.9f, 0.9f, v.getWidth() / 2, v.getHeight() / 2);
					a_selected.setDuration(0);
					a_selected.setFillAfter(true);
				}
				if (a_current == null) {
					a_current = a_normal;
				}

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					canUseAnimation = true;
					a_current = a_selected;
					v.startAnimation(a_current);
				} else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
					canUseAnimation = false;
					a_current = a_normal;
					v.startAnimation(a_current);
				} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
					float x = event.getX();
					float y = event.getY();
					float w = v.getWidth();
					float h = v.getHeight();
					if (x < 0 || x > w) {
						if (a_current.equals(a_selected)) {
							canUseAnimation = false;
							a_current = a_normal;
							v.startAnimation(a_current);
						}
					} else if (y < 0 || y > h) {
						if (a_current.equals(a_selected)) {
							canUseAnimation = false;
							a_current = a_normal;
							v.startAnimation(a_current);
						}
					} else {
						if (canUseAnimation) {
							// if (a_current.equals(a_normal)) {
							a_current = a_selected;
							v.startAnimation(a_current);
							// }
						}
					}
				}
				return false;
			}
		};
		if (view != null) {
			view.setOnTouchListener(onTouchListener);
		}
	}

	public static View initView(Context context, int notificationItem, ViewGroup root) {
		return ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(notificationItem, root);
	}

	public static <T extends View> T getView(BaseAdialog loginCheckDialog, int res) {
		T v = (T) loginCheckDialog.findViewById(res);
		return v;
	}

	public static void unlockScreen(Context context) {
		KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
		final KeyguardManager.KeyguardLock kl = km.newKeyguardLock("MyKeyguardLock");
		kl.disableKeyguard();
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyWakeLock");
		wakeLock.acquire();
	}

	public static String getNameFromContactId(String contactId, Context context) {
		String name = "";
		String whereName = ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID + " = " + contactId;
		String[] whereNameParams = new String[] { ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE };
		Cursor nameCur = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, whereName, whereNameParams, ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);
		if (nameCur != null) {
			while (nameCur.moveToNext()) {
				String given = nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
				String family = nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
				String display = nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME));
				name = "Name: " + given + " Family: " + family + " Displayname: " + display;
			}
			nameCur.close();
		}
		return name;
	}

	public static void setOnClickListener(View menuuser_btn, View.OnClickListener menuUserView) {
		if (menuuser_btn != null)
			menuuser_btn.setOnClickListener(menuUserView);
	}
}