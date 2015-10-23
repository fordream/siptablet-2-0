package z.lib.base;

import android.content.Context;
import android.media.AudioManager;

public class AudioSpeakerManager {
	private Context context;

	public AudioSpeakerManager(Context context) {
		this.context = context;
	}

	public void enableSpeaker(boolean isOn) {
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		if(isOn){
	        audioManager.setMode(AudioManager.MODE_IN_CALL);    
	        audioManager.setMode(AudioManager.MODE_NORMAL); 
	    }else{
	        //Seems that this back and forth somehow resets the audio channel
	        audioManager.setMode(AudioManager.MODE_NORMAL);     
	        audioManager.setMode(AudioManager.MODE_IN_CALL);        
	    }
	    audioManager.setSpeakerphoneOn(isOn);
	}
}