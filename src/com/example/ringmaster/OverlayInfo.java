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
	private int mode;//�ֻ�ģʽ�� 1������  2���� 3������ 4���𶯼�����
	private int distance; //������Ч�ľ��� ��λ����
	
	
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
