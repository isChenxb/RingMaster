package com.example.ringmaster;

import java.io.Serializable;

public class OverlayInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private double latitude;
	private double longitude;
	private int mode;//手机模式： 1：静音  2：震动 3：响铃 4：震动加响铃
	private int distance; //设置生效的距离 单位：米
	
	
	public OverlayInfo() {
		super();
	}


	public OverlayInfo(String name, double latitude ,double longitude,
			int mode, int distance) {
		super();
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.mode = mode;
		this.distance = distance;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public double getLatitude() {
		return latitude;
	}


	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}


	public double getLongitude() {
		return longitude;
	}


	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}


	public int getMode() {
		return mode;
	}


	public void setMode(int mode) {
		this.mode = mode;
	}


	public int getDistance() {
		return distance;
	}


	public void setDistance(int distance) {
		this.distance = distance;
	}
	
	
}
