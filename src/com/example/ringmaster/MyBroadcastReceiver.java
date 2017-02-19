package com.example.ringmaster;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;

public class MyBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		AudioManager audio = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		
		int ringmode = intent.getIntExtra("ringmode", 0);
		switch (ringmode){
		case 1:{
			silent(audio);
			break;
		}
		case 2:{
			vibrate(audio);
			break;
		}
		case 3:{
			ring(audio);
			break;
		}
		case 4:{
			ringAndVibrate(audio);
			break;
		}
		}
	}
	
	
	// 铃声和震动  
    protected void ringAndVibrate(AudioManager audio){  
        audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);  
        audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_ON);  
        audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_ON);  
    }  
    // 铃声  
    protected void ring(AudioManager audio){  
        audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);  
        audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_OFF);  
        audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_OFF);  
    }  
    // 震动  
    protected void vibrate(AudioManager audio){  
        audio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);  
        audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_ON);  
        audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_ON);  
    }  
    // 静音  
    protected void silent(AudioManager audio){  
        audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);  
        audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_OFF);  
        audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_OFF);  
    }  
    // 改变声音-----减小  
    protected void lowerVoice(AudioManager audio, int voice){  
        audio.setRingerMode(AudioManager.ADJUST_LOWER);  
        audio.adjustVolume(AudioManager.ADJUST_LOWER, voice);  
    }  
    // 改变声音-----增加  
    protected void addVoice(AudioManager audio, int voice){  
        audio.setRingerMode(AudioManager.ADJUST_RAISE);  
        audio.adjustVolume(AudioManager.ADJUST_RAISE, voice);  
    }  

}
