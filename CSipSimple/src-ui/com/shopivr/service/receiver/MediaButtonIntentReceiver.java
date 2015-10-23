package com.shopivr.service.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.view.KeyEvent;

import com.csipsimple.pjsip.UAStateReceiver;

//ta.org.service.receiver.MediaButtonIntentReceiver
public class MediaButtonIntentReceiver extends BroadcastReceiver {

	public static void onCreate(Context context) {
		IntentFilter mediaFilter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
		mediaFilter.setPriority(2147483647);
		context.getApplicationContext().registerReceiver(new MediaButtonIntentReceiver(), mediaFilter);
		ComponentName mediaButtonReceiver = new ComponentName(context.getApplicationContext().getPackageName(), MediaButtonIntentReceiver.class.getName());
		AudioManager mAudioManager = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
		mAudioManager.registerMediaButtonEventReceiver(mediaButtonReceiver);
	}

	public MediaButtonIntentReceiver() {
		super();
	}

	private static final String TAG = "EFREE-RECV";
	private static UAStateReceiver uaReceiver = null;

	public static void setService(UAStateReceiver aUAReceiver) {
		uaReceiver = aUAReceiver;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (uaReceiver == null) {
			return;
		}

		if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
			KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
			if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
				return;

			int keyCodes[] = new int[] { KeyEvent.KEYCODE_HEADSETHOOK,//
					KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,//
					KeyEvent.KEYCODE_MEDIA_PLAY,//
					KeyEvent.KEYCODE_MEDIA_PAUSE,//
			};
			boolean needCheck = false;

			for (int key : keyCodes) {
				if (key == keyEvent.getKeyCode()) {
					needCheck = true;
				}
			}

			if (needCheck && uaReceiver.handleHeadsetButton()) {
				abortBroadcast();
			}
			// LogUtils.e(TAG, "Sending it over to PlayerService..." +
			// keyEvent.getKeyCode());
			//
			// switch (keyEvent.getKeyCode()) {
			// case KeyEvent.KEYCODE_HEADSETHOOK:
			// LogUtils.e(TAG, "Received: KEYCODE_HEADSETHOOK");
			// break;
			// case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
			// LogUtils.e(TAG, "Received: KEYCODE_MEDIA_PLAY_PAUSE");
			// break;
			// case KeyEvent.KEYCODE_MEDIA_PLAY:
			//
			// LogUtils.e(TAG, "Received: KEYCODE_MEDIA_PLAY");
			// break;
			// case KeyEvent.KEYCODE_MEDIA_PAUSE:
			// LogUtils.e(TAG, "Received: KEYCODE_MEDIA_PAUSE");
			// break;
			// }
		}

		// if
		// (intent.getAction().equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY))
		// {
		// LogUtils.e(TAG, "Received: pause");
		// } else if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
		// KeyEvent keyEvent = (KeyEvent)
		// intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
		// if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
		// return;
		//
		// LogUtils.e(TAG, "Sending it over to PlayerService..." +
		// keyEvent.getKeyCode());
		//
		// switch (keyEvent.getKeyCode()) {
		// case KeyEvent.KEYCODE_HEADSETHOOK:
		// LogUtils.e(TAG, "Received: KEYCODE_HEADSETHOOK");
		// break;
		// case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
		// LogUtils.e(TAG, "Received: KEYCODE_MEDIA_PLAY_PAUSE");
		// break;
		// case KeyEvent.KEYCODE_MEDIA_PLAY:
		//
		// LogUtils.e(TAG, "Received: KEYCODE_MEDIA_PLAY");
		// break;
		// case KeyEvent.KEYCODE_MEDIA_PAUSE:
		// LogUtils.e(TAG, "Received: KEYCODE_MEDIA_PAUSE");
		// break;
		// }
		// }
		//
		// abortBroadcast();
	}
}
