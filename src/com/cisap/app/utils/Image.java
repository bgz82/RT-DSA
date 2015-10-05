package com.cisap.app.utils;

public class Image {
	private String ImageURL;
	private String Description;
	private String TimeStamp;
	private String lattitude;
	private String longitude;
    private String event;
    private String updated;
    
	public Image(String URL, String desc, String time, String lat,
			String lon, String eve, String update) {
		ImageURL = URL;
		Description = desc;
		TimeStamp = time;
		lattitude = lat;
		longitude = lon;
		event = eve;
		updated = update;
	}

	public String getImageURL() {
		return ImageURL;
	}

	public String getDescripiton() {
		return Description;
	}

	public String getTimeStamp() {
		return TimeStamp;
	}

	public String getLattitude() {
		return lattitude;
	}

	public String getLongitude() {
		return longitude;
	}
	
	public String getEvent() {
		return event;
	}
	
	public String getUpdated() {
		return updated;
	}
		
}
