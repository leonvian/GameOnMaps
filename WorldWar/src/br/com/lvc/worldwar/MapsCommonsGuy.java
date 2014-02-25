package br.com.lvc.worldwar;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsCommonsGuy extends FragmentActivity implements OnInfoWindowClickListener, OnMarkerDragListener {

	private static final String STATE_NAV="nav";
	private static final int[] MAP_TYPE_NAMES= { R.string.normal,
		R.string.hybrid, R.string.satellite, R.string.terrain };
	private static final int[] MAP_TYPES= { GoogleMap.MAP_TYPE_NORMAL,
		GoogleMap.MAP_TYPE_HYBRID, GoogleMap.MAP_TYPE_SATELLITE,
		GoogleMap.MAP_TYPE_TERRAIN };
	private GoogleMap map=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		SupportMapFragment mapFrag = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);

		mapFrag.setRetainInstance(true);
		//initListNav();

		map=mapFrag.getMap();

		if (savedInstanceState == null) {
			CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(40.76793169992044,-73.98180484771729));
			CameraUpdate zoom= CameraUpdateFactory.zoomTo(15);

			map.moveCamera(center);
			map.animateCamera(zoom);

			addMarker(map, 40.748963847316034, -73.96807193756104,
					R.string.un, R.string.united_nations);
			addMarker(map, 40.76866299974387, -73.98268461227417,
					R.string.lincoln_center,
					R.string.lincoln_center_snippet);
			addMarker(map, 40.765136435316755, -73.97989511489868,
					R.string.carnegie_hall, R.string.practice_x3);
			addMarker(map, 40.70686417491799, -74.01572942733765,
					R.string.downtown_club, R.string.heisman_trophy);
		}

		//  map.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
		map.setOnInfoWindowClickListener(this);
		map.setOnMarkerDragListener(this);
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		Toast.makeText(this, marker.getTitle(), Toast.LENGTH_LONG).show();
	}

	@Override
	public void onMarkerDragStart(Marker marker) {
		LatLng position=marker.getPosition();

		Log.d(getClass().getSimpleName(), String.format("Drag from %f:%f",
				position.latitude,
				position.longitude));
	}

	@Override
	public void onMarkerDrag(Marker marker) {
		LatLng position=marker.getPosition();

		Log.d(getClass().getSimpleName(),
				String.format("Dragging to %f:%f", position.latitude,
						position.longitude));
	}

	@Override
	public void onMarkerDragEnd(Marker marker) {
		LatLng position=marker.getPosition();

		Log.d(getClass().getSimpleName(), String.format("Dragged to %f:%f",
				position.latitude,
				position.longitude));
	}


	private void addMarker(GoogleMap map, double lat, double lon,
			int title, int snippet) {
		map.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
				.title(getString(title))
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.castle_um))
				.snippet(getString(snippet))
				.draggable(true));
	}
}

