package com.cisap.app.utils;

import java.io.Serializable;


public class SendImage implements Serializable{
	
	private static final long serialVersionUID = 4000846187936782703L;
	
	private String imageData;
	private String description;
	private String timeStamp;
	private String lattitude;
	private String longitude;
	private String event;
	
	public SendImage()
	{
		
	}

	public SendImage(String data, String desc, String time, String lat,
			String lon, String eve) {
		this.imageData = data;
		this.description = desc;
		this.timeStamp = time;
		this.lattitude = lat;
		this.longitude = lon;
		this.event = eve;
	}
	
	public void setImageData(String iData) {
		System.out.println("Setting Image Data");
		this.imageData = iData;
	}

	public String getImageData() {
		return this.imageData;
	}
	
	public void setDescription(String desc) {
		this.description = desc;
	}
	
	public String getDescription() {
		return this.description;
	}

	public void setTimeStamp(String tStamp) {
		this.timeStamp = tStamp;
	}

	public String getTimeStamp() {
		return this.timeStamp;
	}

	public void setLattitude(String lat) {
		this.lattitude = lat;
	}

	public String getLattitude() {
		return this.lattitude;
	}


	public void setLongitude(String lon) {
		this.longitude = lon;
	}

	public String getLongitude() {
		return this.longitude;
	}
	
	public void setEvent(String eve) {
		this.event = eve;
	}
	
	public String getEvent(String orien)
	{
		return this.event;
	}
	
}
