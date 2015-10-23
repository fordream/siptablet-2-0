package z.lib.base;

import org.json.JSONObject;

import z.lib.base.callback.RestClient.RequestMethod;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

import com.app.knock.db.TASetting;
import com.app.knock.db.TASetting.SettingType;
import com.shopivr.component.base.TAMainApplication;

public class BaseUtils {

	public static class API {
		// public static final String BASESERVER = "http://192.168.2.7/";

		public static final String API_USER_LOGIN = "mobileuc/user/check";
		public static final String API_USER_INFOR = "mobileuc/server/info";

		public static final String API_UPDATE_STATUS = "mobileuc/status/upd";

		public static final String API_address_book_type_business = "mobileuc/sharetel/search";

		public static final String API_address_book_type_sip_personal = "mobileuc/user/search";

		public static final String API_address_book_type_group = "mobileuc/group/search";

		public static final String API_address_book_type_local = "";

		public static String BASESERVER(Context context) {
			String server = new TASetting(context).get(SettingType.server);//.getInstance().init(context).getServer();

			if (!server.startsWith("http://")) {
				server = "http://" + server;
			}

			if (!server.endsWith("/")) {
				server = server + "/";
			}
			return server;
		}

	}

	public static final class KEY {
		public static final String KEY_SCREEN = "KEY_SCREEN";
		public static final String is_success = "is_success";
		public static final String err_msg = "err_msg";
		public static final String REMEMBERPASSWORD = "req[param][remember]";
	}

	public enum SCREEN {
		LOGIN, MAIL_BOX, FAQ, DOWNLOAD, CONTACT, NEWS, ABOUT_SPMC, HOME, DEFAULT, PROFILE
	}

	public final static class VALUE {

		public static final String STATUS_API_SUCCESS = "0";
		public static final String STATUS_REMEMBER_SUCCESS = "1";
		public static final String STATUS_API_FAIL = "0";

	};

	public static void execute(Context context, final RequestMethod method, final String api, final JSONObject params, final BaseServiceCallBack cheerzServiceCallBack) {
		((TAMainApplication) context.getApplicationContext()).execute(method, api, params, cheerzServiceCallBack);
	}

	public static void showDialog(Context activity, String message) {
		Builder builder = new Builder(activity);
		builder.setMessage(message);
		builder.show();
	}

	public static void playMedia(Context context, int mediaid) {
		try {
			MediaPlayer mediaplayer = new MediaPlayer();
			mediaplayer = MediaPlayer.create(context, mediaid);
			if (mediaplayer == null) {
				Log.v("media err", "Create() on MediaPlayer failed.");
			} else {
				mediaplayer.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mediaplayer) {
						mediaplayer.stop();
						mediaplayer.release();
					}
				});
				mediaplayer.setVolume(0.9f, 0.9f);
				mediaplayer.start();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
