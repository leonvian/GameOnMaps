package br.com.lvc.worldwar.entitie;

import com.google.android.gms.maps.model.LatLng;

public class MapPosition {
	
	private double lat;
	private double lng;
	
	public MapPosition() {
	}
	
	

	public MapPosition(double lat, double lng) {
		super();
		this.lat = lat;
		this.lng = lng;
	}

	
	public MapPosition(LatLng latLng) {
		this(latLng.latitude, latLng.longitude);
	}
	
	


	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}
	
	

}
