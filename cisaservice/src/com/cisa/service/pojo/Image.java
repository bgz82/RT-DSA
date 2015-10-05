package com.cisa.service.pojo;

import java.io.Serializable;

public class Image implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4000846187936782703L;
		private String ImageURL;
		private String Description;
		private String TimeStamp;
		private String lattitude;
		private String longitude;

		public Image(String URL, String desc, String time, String lat,
				String lon) {
			ImageURL = URL;
			Description = desc;
			TimeStamp = time;
			lattitude = lat;
			longitude = lon;
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
	}
