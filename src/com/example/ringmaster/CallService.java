package com.example.ringmaster;

import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class CallService extends Service {

	private TelephonyManager telephonyMgr;
	private SensorManager sensorMgr;
	private AudioManager audioMgr;
	private SensorEventListener sensorListener;
	private PhoneCallListener phoneListener;
	private boolean ringing = false;// 用于判断手机是否正在响铃中，因为我们只在响铃状态下实现翻转静音功能

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

//		Log.i(TAG, "onCreate");

		Sensor orientation = null;
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		telephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		sensorMgr = (SensorManager) this.getSystemService(SENSOR_SERVICE);
		audioMgr = (AudioManager) this.getSystemService(AUDIO_SERVICE);

		// 监听电话状态，PhoneCallListener是我们自定义的一个监听类
		phoneListener = new PhoneCallListener();
		// 注册电话状态监听器
		telephonyMgr.listen(phoneListener, PhoneCallListener.LISTEN_CALL_STATE);

		// 感应监听器，SensorListener是我们自定义的一个监听类
		sensorListener = new SensorListener();
		// 获取Sensor对象
		List<Sensor> sensorList = sensorMgr.getSensorList(Sensor.TYPE_ALL);
		int size = sensorList.size();
		for (int i = 0; i < size; i++) {
			int type = sensorList.get(i).getType();
//			Log.d(TAG, "sensor type:" + String.valueOf(type));
			if (type == Sensor.TYPE_ACCELEROMETER) {// 找到我们要的感应器
				orientation = sensorList.get(i);
				break;
			}
		}
		if (orientation != null) { // 注册感应监听器
			sensorMgr.registerListener(sensorListener, orientation,
					SensorManager.SENSOR_DELAY_NORMAL);
		} else { // 没有对应的Sensor,取消.
			stopSelf();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
//		Log.i(TAG, "onDestroy");

		telephonyMgr.listen(phoneListener, 0);
		sensorMgr.unregisterListener(sensorListener);
	}
	

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private class PhoneCallListener extends PhoneStateListener{
		  @Override
		  public void onCallStateChanged(int state, String incomingNumber){
//		    Log.i(TAG, "onCallStateChanged");
		    switch (state) {
		    case TelephonyManager.CALL_STATE_IDLE://电话处于待机状态
//		      Log.d(TAG, "CALL_STATE_IDLE");
		      ringing = false;
		      break;
		    case TelephonyManager.CALL_STATE_OFFHOOK://电话通话中
//		      Log.d(TAG, "CALL_STATE_OFFHOOK");
		      ringing = false;
		      break;
		    case TelephonyManager.CALL_STATE_RINGING://有电话来电
//		      Log.d(TAG, "CALL_STATE_RINGING");
		      ringing = true;
		      break;
		    }   
		    super.onCallStateChanged(state, incomingNumber);
		  }
		}


		class SensorListener implements SensorEventListener{
		  @Override
		  public void onAccuracyChanged(Sensor sensor, int accuracy) {}       
		  
		  @Override
		  public void onSensorChanged(SensorEvent event) {     
//		    if (event.values[2] < 0 && ringing) {//电话正在响铃中，且手机面朝下
			  if (event.values[2] < 0 ) {//电话正在响铃中，且手机面朝下
//		      Log.d(TAG, "change to Silent mode");
		      int ringerMode = AudioManager.RINGER_MODE_SILENT;
		      audioMgr.setRingerMode(ringerMode);
		      
		    }else{
//		      Log.d(TAG, "change to Ringing mode");
//		     audioMgr.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		    }
		  }
		}

}
