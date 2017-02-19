package com.example.ringmaster;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class LocationService extends Service {

	private ArrayList<OverlayInfo> list = null;
	private LocationManager mLocationManager = null;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location location = getLocation(this);
		
		initOverlay();//初始化覆盖物信息
		checkLocation(location);//检查当前位置是否到达预设地点
		
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 5, new LocationListener(){

			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				checkLocation(location);
			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
	}
	
	//检查位置是否到达预设地点
	private void checkLocation(Location location){
		System.out.println("check location");
		
		if (location == null){
			Toast.makeText(this, "无法获取定位，请确定GPS定位是否打开", 0).show();
			return;
		}
		
		LatLng location_now = new LatLng(location.getLatitude(),location.getLongitude());
		
		LatLng latLng = null;
		for (OverlayInfo info : list){
			latLng = new LatLng(info.getLatitude() , info.getLongitude());
			
			System.out.println(DistanceUtil.getDistance(location_now, latLng));
			
			if (DistanceUtil.getDistance(location_now, latLng) <= info.getDistance()){
				//已经达到预设地点的范围
				Intent intent = new Intent();
				intent.setAction("com.example.ringmaster.location");
				intent.putExtra("ringmode", info.getMode());
				sendBroadcast(intent);
				
				Toast.makeText(this, info.getName()+"  的设置已经生效", 0).show();
			}
		}
	}
	
	//初始化覆盖物信息
	private void initOverlay(){
		list = new ArrayList<OverlayInfo>();
		try {
			File file = new File("data/data/com.example.ringmaster/location_info.txt");
			FileReader reader = new FileReader(file);
			BufferedReader buffered = new BufferedReader(reader);
			String aLine = "";
			while ((aLine = buffered.readLine())!=null){
				String[] strs = aLine.split(",");
				OverlayInfo info = new OverlayInfo();
				info.setName(strs[0]);
				info.setLatitude(Double.parseDouble(strs[1]));
				info.setLongitude(Double.parseDouble(strs[2]));
				info.setMode(Integer.parseInt(strs[3]));
				info.setDistance(Integer.parseInt(strs[4]));
				
				list.add(info);
			}
			buffered.close();
			reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("init overlay over");
	}
	
	public static Location getLocation(Context context) {  
        LocationManager locMan = (LocationManager) context  
                .getSystemService(Context.LOCATION_SERVICE);  
        Location location = locMan  
                .getLastKnownLocation(LocationManager.GPS_PROVIDER);  
        if(location==null){  
            location = locMan  
            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);  
        }
        return location;  
    }  
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
