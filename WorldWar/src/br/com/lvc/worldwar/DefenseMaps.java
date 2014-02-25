package br.com.lvc.worldwar;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import br.com.lvc.worldwar.entitie.Castle;
import br.com.lvc.worldwar.entitie.MapPosition;
import br.com.lvc.worldwar.entitie.UnityMilitar;
import br.com.lvc.worldwar.entitie.UnityMilitarFactory;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class DefenseMaps extends FragmentActivity implements OnMapLongClickListener, OnInfoWindowClickListener, OnMarkerClickListener, OnMarkerDragListener {

	private static final double EARTH_RADIUS = 6378100.0;

	private static final int TIME_TO_WALK = 10; 
	private static final double TOTAL_TIME_TO_FINISH_PATH = 60000 * 60;

	protected GoogleMap map; 


	private MapPosition mapPositionBr = new MapPosition(-19.38566266047098, -42.99701750278473);


	private Castle castleBr = new Castle(1, "Brasil", "Patria",R.drawable.castle_um, mapPositionBr);

	//private List<Marker> castleMarkers = new ArrayList<Marker>();
	private List<UnityMilitarMarker> unitysMarkers = new ArrayList<UnityMilitarMarker>();

	private Marker myCastleMarker;	
	private Handler handler = new Handler();

	private double elapsedTime = 0;
	private double elapsedPercent = 0;
	private  double lastFrameTime = 0;

	private HashMap<Marker, Castle> hashMapMarkerCastle = new HashMap<Marker, Castle>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.defense_maps);
		map = retrieveMap();   
		myCastleMarker = map.addMarker( createCastleMarker(castleBr) );
		hashMapMarkerCastle.put(myCastleMarker, castleBr);
 
		map.setInfoWindowAdapter(new CustomInfoWindowAdapter(this, hashMapMarkerCastle));
  
		map.setOnMapLongClickListener(this);
		map.setOnInfoWindowClickListener(this);
		map.setOnMarkerClickListener(this);
		map.setOnMarkerDragListener(this);
		createUnityEnemysOnMaps();
		scheduleMarch();

	}

 

	private GoogleMap retrieveMap() {
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		mapFragment.setRetainInstance(true);
		GoogleMap googleMap = mapFragment.getMap();
		return googleMap;
	}

	private MarkerOptions createCastleMarker(Castle castle) {
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.icon(BitmapDescriptorFactory.fromResource(castle.getImage()));
		markerOptions.snippet(castle.getDescription());
		markerOptions.title(castle.getName());
		markerOptions.position(toLatLng(castle));
		markerOptions.draggable(true);

		return markerOptions;
	}

	

	@Override
	public void onMapLongClick(LatLng point) {
		vibrate();

		Log.i("POINT", "LAT/LNG " + point);

	}

	private void vibrate() {
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		//		v.vibrate(250);
	}

	@Override
	public void onInfoWindowClick(Marker marker) {

	}

	/**
	 * N‹o esta correspodenndo ao tempo total de marcha
	 */
	private void scheduleMarch() {
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				double currentTime = new Date().getTime();

				if(lastFrameTime == 0)
					lastFrameTime = currentTime;

				boolean stop = false;

				double deltaTime = currentTime - lastFrameTime;

				elapsedTime += deltaTime; 

				elapsedPercent = elapsedTime / TOTAL_TIME_TO_FINISH_PATH; 
				for(UnityMilitarMarker unityMilitarMarker : unitysMarkers) {

					Marker marker = unityMilitarMarker.getMarker();
					LatLng from = marker.getPosition(); 
					LatLng to = toLatLng(castleBr.getMapPosition());


					double nextLongitude = (from.longitude + (to.longitude - from.longitude) *  elapsedPercent);
					double nextLatitude = (from.latitude + (to.latitude - from.latitude) *  elapsedPercent);


					LatLng nextMove = new LatLng(nextLatitude, nextLongitude);

					marker.setPosition(nextMove);
				}

				if(elapsedTime > TOTAL_TIME_TO_FINISH_PATH) {
					elapsedTime = TOTAL_TIME_TO_FINISH_PATH;
					stop = true;
				}

				lastFrameTime = currentTime;

				if(!stop)
					scheduleMarch();
			}
		}, TIME_TO_WALK);
	}

	private List<UnityMilitarMarker> getUnitysByCastle(int castle) {
		return unitysMarkers;
	}


	public void onClickCreateUnity(View view) {
		vibrate();
		//map.moveCamera( CameraUpdateFactory.newLatLngZoom(target, 21));
	}
	
	private void createUnityEnemysOnMaps() { 
		List<UnityMilitar> unitys = UnityMilitarFactory.getInstance().getListUnitys();
		for(UnityMilitar unity : unitys) {
			Marker unityMarker = map.addMarker(createUnityMarker(unity));
			UnityMilitarMarker unityMilitarMarker = new UnityMilitarMarker(unity, unityMarker);
			unitysMarkers.add(unityMilitarMarker);
		} 
	}
	
	private MarkerOptions createUnityMarker(UnityMilitar unity) {
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.icon(BitmapDescriptorFactory.fromResource(unity.getImage()));
		markerOptions.title(getString(unity.getName()));
		markerOptions.snippet(getString(unity.getDescription()));
		markerOptions.position(toLatLng(unity));

		return markerOptions;
	}
  
	private Castle getMyCastle() {
		return castleBr;
	}

	private LatLng toLatLng(UnityMilitar unityMilitar) {
		MapPosition mapPosition = unityMilitar.getMapPosition();
		LatLng latLng = toLatLng(mapPosition);
		return latLng;
	}

	private LatLng toLatLng(Castle castle) {
		MapPosition mapPosition = castle.getMapPosition();
		LatLng latLng = toLatLng(mapPosition);
		return latLng;
	}

	private LatLng toLatLng(MapPosition mapPosition) {
		LatLng latLng = new LatLng(mapPosition.getLat(), mapPosition.getLng());
		return latLng;
	}

	
	private LatLng toLatLng(Location location) {
		LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
		return latLng;
	}
 
	private class UnityMilitarMarker {

		private UnityMilitar unityMilitar;
		private Marker marker;

		public UnityMilitarMarker(UnityMilitar unityMilitar, Marker marker) {
			super();
			this.unityMilitar = unityMilitar;
			this.marker = marker;
		}

		public UnityMilitar getUnityMilitar() {
			return unityMilitar;
		}

		public Marker getMarker() {
			return marker;
		}

	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		Log.i("MARKER CLICK", "LISTENER");
		return false;
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

}
