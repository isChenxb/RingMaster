package com.example.ringmaster;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

public class MainActivity extends Activity {

	// ��ͼ���
	private MapView mMapView = null;// �ٶȵ�ͼ�ؼ�
	private Button bt_location;
	private CheckBox cb_call;
	private BaiduMap mBaiduMap;// �ٶȵ�ͼ����

	// λ�����
	private LocationClient mLocationClient = null;
	private BDLocationListener mLocationListener;
	private boolean isFirstIn = true;
	private double latitude = 0.0;
	private double longitude = 0.0;

	// ���������
	private ArrayList<OverlayInfo> list;
	private BitmapDescriptor icon = null;
	private OverlayOptions overlayOptions = null;
	private Marker marker = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_main);

		initView();// ��ʼ��View���
		initLocation();// ��ʼ��Location���
		initOverlay();// ��ʼ�����������

		
		cb_call.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked){
					Intent intent = new Intent(MainActivity.this , CallService.class);
					MainActivity.this.startService(intent);
					
					Toast.makeText(MainActivity.this, "��ת�����ѿ���", 0).show();
				}else{
					Intent intent = new Intent(MainActivity.this , CallService.class);
					MainActivity.this.stopService(intent);
					
					Toast.makeText(MainActivity.this, "��ת�����ѹر�", 0).show();
				}
			}
			
		});
		
		
		mBaiduMap.setOnMapLongClickListener(new BaiduMap.OnMapLongClickListener() {

					@Override
					public void onMapLongClick(final LatLng latlng) {
						// TODO Auto-generated method stub

						AlertDialog.Builder builder = new Builder(
								MainActivity.this);
						View view = View.inflate(MainActivity.this,
								R.layout.alert_layout, null);

						final RadioGroup rg_mode = (RadioGroup) view
								.findViewById(R.id.rg_mode);
						final EditText et_name = (EditText) view
								.findViewById(R.id.et_name);
						final EditText et_distance = (EditText) view
								.findViewById(R.id.et_distance);

						builder.setView(view);
						builder.setPositiveButton("ȷ��",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										String name = et_name.getText()
												.toString().trim();
										String distance_str = et_distance
												.getText().toString().trim();
										int checkId = rg_mode
												.getCheckedRadioButtonId();
										int mode = 0;
										String mode_str = null;
										switch (checkId) {
										case R.id.rb_Mode_mute: {
											mode = 1;
											mode_str = "����";
											break;
										}
										case R.id.rb_Mode_shock: {
											mode = 2;
											mode_str = "��";
											break;
										}
										case R.id.rb_Mode_ring: {
											mode = 3;
											mode_str = "����";
											break;
										}
										case R.id.rb_Mode_shockring: {
											mode = 4;
											mode_str = "�𶯼�����";
											break;
										}
										}

										if (TextUtils.isEmpty(name)
												|| TextUtils
														.isEmpty(distance_str) || TextUtils.isEmpty(mode_str)) {
											Toast.makeText(MainActivity.this,
													"������������Ϣ", 0).show();
											return;
										}
										int distance = Integer
												.parseInt(distance_str);
										OverlayInfo info = new OverlayInfo(
												name, latlng.latitude, latlng.longitude,mode,
												distance);

										
										overlayOptions = new MarkerOptions()
												.position(latlng).icon(icon)
												.zIndex(5);
										marker = (Marker) mBaiduMap
												.addOverlay(overlayOptions);
										Bundle bundle = new Bundle();
										bundle.putSerializable("info", info);
										marker.setExtraInfo(bundle);
										list.add(info);
										writeOverlay();
										
										Toast.makeText(
												MainActivity.this,
												"���óɹ������ƣ�" + name + " ģʽ��"
														+ mode_str + " ��Χ"
														+ distance + "��", 1)
												.show();
										
										Intent intent = new Intent(MainActivity.this,LocationService.class);
//										Bundle bundle_list = new Bundle();
//										bundle_list.putSerializable("mBaiduMap", mBaiduMap);
//										intent.putExtras(bundle_list);
										stopService(intent);
										
										startService(intent);
									}
								});

						builder.setNegativeButton("ȡ��",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
									}
								});
						AlertDialog dialog = builder.create();
						dialog.show();
					}
				});

		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(final Marker arg0) {
				// TODO Auto-generated method stub
				Bundle bundle = arg0.getExtraInfo();
				final OverlayInfo info = (OverlayInfo) bundle.getSerializable("info");
				AlertDialog.Builder builder = new Builder(MainActivity.this);
				builder.setTitle("ѡ�����");
				String[] items = {"�鿴�趨" , "�༭�趨" , "ɾ���趨"};
				builder.setItems(items, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						switch (which){
						case 0:{
							AlertDialog.Builder builder = new Builder(MainActivity.this);
							builder.setTitle("�趨��Ϣ");
							View view = View.inflate(MainActivity.this, R.layout.alert_layout2, null);
							builder.setView(view);
							TextView tv_info = (TextView) view.findViewById(R.id.tv_info);
							StringBuilder sb = new StringBuilder();
							sb.append("���ƣ�"+info.getName()+"\r\n");
							String mode_str="";
							switch (info.getMode()){
							case 1:{
								mode_str="����";
								break;
							}
							case 2:{
								mode_str="��";
								break;
							}
							case 3:{
								mode_str="����";
								break;
							}
							case 4:{
								mode_str="�������";
								break;
							}
							}
							sb.append("ģʽ��"+mode_str+"\r\n");
							sb.append("��Χ��"+info.getDistance()+"��\r\n");
//							sb.append("��ǰ���룺"+DistanceUtil.getDistance(new LatLng(latitude,longitude), new LatLng(info.getLatitude(),info.getLongitude())));
							tv_info.setText(sb.toString());
							AlertDialog alert = builder.create();
							alert.show();
							break;
						}
						case 1:{
							AlertDialog.Builder builder = new Builder(MainActivity.this);
							View view = View.inflate(MainActivity.this, R.layout.alert_layout , null);
							builder.setView(view);
							final RadioGroup rg_mode = (RadioGroup) view.findViewById(R.id.rg_mode);
							RadioButton rb_Mode_mute = (RadioButton) view.findViewById(R.id.rb_Mode_mute);
							RadioButton rb_Mode_shock = (RadioButton) view.findViewById(R.id.rb_Mode_shock);
							RadioButton rb_Mode_ring = (RadioButton) view.findViewById(R.id.rb_Mode_ring);
							RadioButton rb_Mode_shockring = (RadioButton) view.findViewById(R.id.rb_Mode_shockring);
							final EditText et_name = (EditText) view.findViewById(R.id.et_name);
							final EditText et_distance = (EditText) view.findViewById(R.id.et_distance);
							switch (info.getMode()){
							case 1:{
								rb_Mode_mute.setChecked(true);
								break;
							}
							case 2:{
								rb_Mode_shock.setChecked(true);
								break;
							}
							case 3:{
								rb_Mode_ring.setChecked(true);
								break;
							}
							case 4:{
								rb_Mode_shockring.setChecked(true);
								break;
							}
							}
							
							et_name.setText(info.getName());
							et_distance.setText(info.getDistance()+"");
							
							builder.setPositiveButton("ȷ���޸�", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									String name = et_name.getText()
											.toString().trim();
									String distance_str = et_distance
											.getText().toString().trim();
									int checkId = rg_mode
											.getCheckedRadioButtonId();
									int mode = 0;
									String mode_str = null;
									switch (checkId) {
									case R.id.rb_Mode_mute: {
										mode = 1;
										mode_str = "����";
										break;
									}
									case R.id.rb_Mode_shock: {
										mode = 2;
										mode_str = "��";
										break;
									}
									case R.id.rb_Mode_ring: {
										mode = 3;
										mode_str = "����";
										break;
									}
									case R.id.rb_Mode_shockring: {
										mode = 4;
										mode_str = "�𶯼�����";
										break;
									}
									}

									if (TextUtils.isEmpty(name)
											|| TextUtils
													.isEmpty(distance_str) || TextUtils.isEmpty(mode_str)) {
										Toast.makeText(MainActivity.this,
												"������������Ϣ", 0).show();
										return;
									}
									
									int distance = Integer.parseInt(distance_str);
									info.setName(name);
									info.setMode(mode);
									info.setDistance(distance);
									
									writeOverlay();
									Toast.makeText(MainActivity.this, "�趨���޸�", 0).show();
									
									Intent intent = new Intent(MainActivity.this,LocationService.class);
									stopService(intent);
									
									startService(intent);
								}
							});
							
							builder.setNegativeButton("ȡ���޸�", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									
								}
							});
							
						    AlertDialog alert = builder.create();
						    alert.show();
							break;
						}
						case 2:{
							delete(arg0,info);
							Toast.makeText(MainActivity.this, "ɾ���趨�ɹ�", 0).show();
							writeOverlay();
							break;
						}
						}
					}
				});
				
				AlertDialog dialog = builder.create();
				dialog.show();
				return true;
			}
		});

		bt_location.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LatLng latLng = new LatLng(latitude, longitude);
				MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
				mBaiduMap.animateMapStatus(msu);

			}
		});
	}

	/*
	 * ��ʼ��View
	 */
	private void initView() {
		cb_call = (CheckBox) findViewById(R.id.cb_call);
		bt_location = (Button) findViewById(R.id.bt_location);
		mMapView = (MapView) findViewById(R.id.bmapview);
		mBaiduMap = mMapView.getMap();
		
		cb_call.setChecked(isServiceRunning(this));

		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
		mBaiduMap.setMapStatus(msu);
	}

	/*
	 * ��ʼ��Location
	 */
	private void initLocation() {
		// ��ͨ��ͼ
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

		mLocationClient = new LocationClient(this);
		mLocationListener = new MyLocationListener();

		mLocationClient.registerLocationListener(mLocationListener);
		LocationClientOption locationOption = new LocationClientOption();
		locationOption.setCoorType("bd09ll");// ��������
		locationOption.setIsNeedAddress(true);// ���ص�ǰλ��,����Ϊtrue�����ͨ��BDLocation.getAddrStr()���������û���ǰ��ַ
		locationOption.setOpenGps(true);// ��GPS
		locationOption.setScanSpan(1000);// ����ʱ����
		mLocationClient.setLocOption(locationOption);
	}

	/*
	 * ��ʼ�����������
	 */
	private void initOverlay() {
		icon = BitmapDescriptorFactory.fromResource(R.drawable.overlay_icon);
		list = new ArrayList<OverlayInfo>();
		
		File file = new File("data/data/com.example.ringmaster/location_info.txt");
		if (file.exists()){
			try {
				FileReader reader = new FileReader(file);
				BufferedReader buffer = new BufferedReader(reader);
				String aLine = "";
				while((aLine=buffer.readLine())!=null){
					String[] strs = aLine.split(",");
					String name = strs[0];
					Double lantitude = Double.parseDouble(strs[1]);
					Double longitude = Double.parseDouble(strs[2]);
					int mode = Integer.parseInt(strs[3]);
					int distance = Integer.parseInt(strs[4]);
					
					OverlayInfo info = new OverlayInfo(name,lantitude,longitude,mode,distance);
					list.add(info);
					
//					System.out.println(info.getName()+" "+info.getLatitude()+"  "+info.getLongitude()+"  "+info.getMode()+"  "+info.getDistance());
					
					LatLng latLng = new LatLng(lantitude , longitude);
					
					overlayOptions = new MarkerOptions().position(latLng).icon(icon).zIndex(5);
					marker = (Marker) mBaiduMap.addOverlay(overlayOptions);
					
					Bundle bundle = new Bundle();
					bundle.putSerializable("info", info);
					marker.setExtraInfo(bundle);
					
				}
				buffer.close();
				reader.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void writeOverlay(){
		try {
			File file = new File("data/data/com.example.ringmaster/location_info.txt");
			FileOutputStream fos = new FileOutputStream(file);
			StringBuilder sb = new StringBuilder();
			for (OverlayInfo info : list){
				sb.append(info.getName()+","+info.getLatitude()+","+info.getLongitude()+","+info.getMode()+","+info.getDistance());
				sb.append("\r\n");
			}
			fos.write(sb.toString().getBytes());
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		mBaiduMap.setMyLocationEnabled(true);
		mLocationClient.start();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mMapView.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (mBaiduMap.isMyLocationEnabled()) {
			mBaiduMap.setMyLocationEnabled(false);
			mLocationClient.stop();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mMapView.onDestroy();
	}
	
	private void delete(Marker marker , OverlayInfo info){
		marker.remove();
		list.remove(info);
	}
	
	public static boolean isServiceRunning(Context context) {  
	    ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);  
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {  
	        if ("com.example.ringmaster.CallService".equals(service.service.getClassName())) {  
	            return true;  
	        }  
	    }  
	    return false;  
	} 
	  

	private class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			MyLocationData data = new MyLocationData.Builder()//
					.accuracy(location.getRadius())// ����
					.latitude(latitude)// γ��
					.longitude(longitude)// ����
					.build();
			mBaiduMap.setMyLocationData(data);

			if (isFirstIn) {
				// ���þ�γ��
				LatLng latlng = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latlng);
				// ���ö������µ�ͼ
				mBaiduMap.animateMapStatus(msu);

				isFirstIn = false;

			}
		}

	}

}
