/**
 * Copyright (C) 2010-2012 Regis Montoya (aka r3gis - www.r3gis.fr)
 * This file is part of CSipSimple.
 *
 *  CSipSimple is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  If you own a pjsip commercial license you can also redistribute it
 *  and/or modify it under the terms of the GNU Lesser General Public License
 *  as an android library.
 *
 *  CSipSimple is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CSipSimple.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * This file contains relicensed code from Apache copyright of 
 * Copyright (C) 2006 The Android Open Source Project
 */

package com.csipsimple.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;

import com.csipsimple.models.CallerInfo;

/**
 * Ringer manager for the Phone app.
 */
public class Ringer {
    private static final String THIS_FILE = "Ringer";
   
    private static final int VIBRATE_LENGTH = 1000; // ms
    private static final int PAUSE_LENGTH = 1000; // ms

    // Uri for the ringtone.
    Uri customRingtoneUri;

    Ringtone ringtone = null;				// [sentinel]
    Vibrator vibrator;
    VibratorThread vibratorThread;
    RingerThread ringerThread;
    Context context;

    public Ringer(Context aContext) {
        context = aContext;
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    /**
     * Starts the ringtone and/or vibrator. 
     * 
     */
    public void ring(String remoteContact, String defaultRingtone) {
        Log.d(THIS_FILE, "==> ring() called...");

        synchronized (this) {

        	AudioManager audioManager =
                (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        	
        	//Save ringtone at the begining in case we raise vol
            ringtone = getRingtone(remoteContact, defaultRingtone);
            
        	//No ring no vibrate
            int ringerMode = audioManager.getRingerMode();
            if (ringerMode == AudioManager.RINGER_MODE_SILENT) {
            	Log.d(THIS_FILE, "skipping ring and vibrate because profile is Silent");
            	return;
            }
            
            // Vibrate
            int vibrateSetting = audioManager.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER);
            Log.d(THIS_FILE, "v=" + vibrateSetting + " rm=" + ringerMode);
            if (vibratorThread == null &&
            		(vibrateSetting == AudioManager.VIBRATE_SETTING_ON || 
            				ringerMode == AudioManager.RINGER_MODE_VIBRATE)) {
                vibratorThread = new VibratorThread();
                Log.d(THIS_FILE, "Starting vibrator...");
                vibratorThread.start();
            }

            // Vibrate only
            if (ringerMode == AudioManager.RINGER_MODE_VIBRATE ||
            		audioManager.getStreamVolume(AudioManager.STREAM_RING) == 0 ) {
            	Log.d(THIS_FILE, "skipping ring because profile is Vibrate OR because volume is zero");
            	return;
            }

            // Ringer normal, audio set for ring, do it
            if(ringtone == null) {
            	Log.d(THIS_FILE, "No ringtone available - do not ring");
            	return;
            }

            Log.d(THIS_FILE, "Starting ring with " + ringtone.getTitle(context));
            
            if (ringerThread == null) {
            	ringerThread = new RingerThread();
            	Log.d(THIS_FILE, "Starting ringer...");
            	audioManager.setMode(AudioManager.MODE_RINGTONE);
            	ringerThread.start();
            }
        }
    }

    /**
     * @return true if we're playing a ringtone and/or vibrating
     *     to indicate that there's an incoming call.
     *     ("Ringing" here is used in the general sense.  If you literally
     *     need to know if we're playing a ringtone or vibrating, use
     *     isRingtonePlaying() or isVibrating() instead.)
     */
    public boolean isRinging() {
    	return (ringerThread != null || vibratorThread != null);
    }
    
    /**
     * Stops the ringtone and/or vibrator if any of these are actually
     * ringing/vibrating.
     */
	public void stopRing() {
		synchronized (this) {
			Log.d(THIS_FILE, "==> stopRing() called...");

			stopVibrator();
			stopRinger();
		}
	}
    
    
	private void stopRinger() {
		if (ringerThread != null) {
			ringerThread.interrupt();
			try {
				ringerThread.join(250);
			} catch (InterruptedException e) {
			}
			ringerThread = null;
		}
	}
    
	private void stopVibrator() {

		if (vibratorThread != null) {
			vibratorThread.interrupt();
			try {
				vibratorThread.join(250); // Should be plenty long (typ.)
			} catch (InterruptedException e) {
			} // Best efforts (typ.)
			vibratorThread = null;
		}
	}

	public void updateRingerMode() {

		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		synchronized (this) {
			int ringerMode = audioManager.getRingerMode();
			// Silent : stop everything
			if (ringerMode == AudioManager.RINGER_MODE_SILENT) {
				stopRing();
				return;
			}

			// Vibrate
			int vibrateSetting = audioManager.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER);
			// If not already started restart it
			if (vibratorThread == null && (vibrateSetting == AudioManager.VIBRATE_SETTING_ON || ringerMode == AudioManager.RINGER_MODE_VIBRATE)) {
				vibratorThread = new VibratorThread();
				vibratorThread.start();
			}

			// Vibrate only
			if (ringerMode == AudioManager.RINGER_MODE_VIBRATE || audioManager.getStreamVolume(AudioManager.STREAM_RING) == 0) {
				stopRinger();
				return;
			}
			
			//Ringer
			if (ringerThread == null) {
            	ringerThread = new RingerThread();
            	Log.d(THIS_FILE, "Starting ringer...");
            	audioManager.setMode(AudioManager.MODE_RINGTONE);
            	ringerThread.start();
            }

		}
	}

    private class VibratorThread extends Thread {
        public void run() {
        	try {
	            while (true) {
	                vibrator.vibrate(VIBRATE_LENGTH);
	                Thread.sleep(VIBRATE_LENGTH + PAUSE_LENGTH);
	            }
        	} catch (InterruptedException ex) {
        		Log.d(THIS_FILE, "Vibrator thread interrupt");
        	} finally {
        		vibrator.cancel();
        	}
    		Log.d(THIS_FILE, "Vibrator thread exiting");
        }
    }
    
    private class RingerThread extends Thread {
    	public void run() {
            try {
	    		while (true) {
	    			ringtone.play();
	    			while (ringtone.isPlaying()) {
	    				Thread.sleep(100);
	    			}
	    		}
            } catch (InterruptedException ex) {
        		Log.d(THIS_FILE, "Ringer thread interrupt");
            } finally {
            	if(ringtone != null) {
            		ringtone.stop();
            	}
            }
    		Log.d(THIS_FILE, "Ringer thread exiting");
    	}
    }

    private Ringtone getRingtone(String remoteContact, String defaultRingtone) {
    	Uri ringtoneUri = Uri.parse(defaultRingtone);
		
		// TODO - Should this be in a separate thread? We would still have to wait for
		// it to complete, so at present, no.
		CallerInfo callerInfo = CallerInfo.getCallerInfoFromSipUri(context, remoteContact);
		
		if(callerInfo != null && callerInfo.contactExists && callerInfo.contactRingtoneUri != null) {
			Log.d(THIS_FILE, "Found ringtone for " + callerInfo.name);
			ringtoneUri = callerInfo.contactRingtoneUri;
		}
		
		return RingtoneManager.getRingtone(context, ringtoneUri);
    }
}
