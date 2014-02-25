package br.com.lvc.worldwar;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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

public class CastleWarMaps extends FragmentActivity implements OnMapLongClickListener, OnInfoWindowClickListener, OnMarkerClickListener, OnMarkerDragListener {

	private static final double EARTH_RADIUS = 6378100.0;

	private static final int TIME_TO_WALK = 10; 
	private static final double TOTAL_TIME_TO_FINISH_PATH = 60000 * 60;

	protected GoogleMap map; 

	private MapPosition mapPositionEn = new MapPosition(53.058059950093885,-3.597773015499115);
	private MapPosition mapPositionBr = new MapPosition(-19.38566266047098, -42.99701750278473);


	private Castle castleBr = new Castle(1, "Brasil", "Patria",R.drawable.castle_um, mapPositionBr);
	private Castle castleEn = new Castle(2, "Inglaterra", "Patria",R.drawable.castle_dois, mapPositionEn);

	//private List<Marker> castleMarkers = new ArrayList<Marker>();
	private List<UnityMilitarMarker> unitysMarkers = new ArrayList<UnityMilitarMarker>();

	private Marker myCastleMarker;

	private List<Castle> castles = new ArrayList<Castle>();
	private Handler handler = new Handler();

	private double elapsedTime = 0;
	private double elapsedPercent = 0;
	private  double lastFrameTime = 0;

	private HashMap<Marker, Castle> hashMapMarkerCastle = new HashMap<Marker, Castle>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		map = retrieveMap();
		castles.add(castleBr);
		castles.add(castleEn);

		myCastleMarker = map.addMarker( createCastleMarker(castleBr) );
		hashMapMarkerCastle.put(myCastleMarker, castleBr);

		//castleMarkers.add( myCastleMarker );

		Marker markerEn = map.addMarker(createCastleMarker(castleEn)); 
		hashMapMarkerCastle.put(markerEn, castleEn);
		//castleMarkers.add( );

		PolylineOptions polylineOptions = createPolylineOptions();
		polylineOptions.add(toLatLng(castleBr), toLatLng(castleEn));
		map.addPolyline(polylineOptions);
		map.setInfoWindowAdapter(new CustomInfoWindowAdapter(this, hashMapMarkerCastle));


		map.setOnMapLongClickListener(this);
		map.setOnInfoWindowClickListener(this);
		map.setOnMarkerClickListener(this);
		map.setOnMarkerDragListener(this);

	}


	private PolylineOptions createPolylineOptions() {
		PolylineOptions polylineOptions = new PolylineOptions();
		polylineOptions.width(2);
		polylineOptions.color(Color.BLACK);

		return polylineOptions;
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

	private MarkerOptions createUnityMarker(UnityMilitar unity) {
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.icon(BitmapDescriptorFactory.fromResource(unity.getImage()));
		markerOptions.title(getString(unity.getName()));
		markerOptions.snippet(getString(unity.getDescription()));
		markerOptions.position(toLatLng(unity));

		return markerOptions;
	}

	@Override
	public void onMapLongClick(LatLng point) {
		vibrate();
		
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon));
		markerOptions.position(point);
		
		map.addMarker(markerOptions);
		
	}

	private void vibrate() {
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		//		v.vibrate(250);
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		if(isACastle(marker)) {
			Castle castle = getCastleByMarker(marker);
			scheduleMarch();
		}
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
				List<UnityMilitarMarker> unitysByCastle = getUnitysByCastle(getMyCastle().getId());

				for(UnityMilitarMarker unityMilitarMarker : unitysByCastle) {

					Marker marker = unityMilitarMarker.getMarker();
					LatLng from = marker.getPosition(); 
					LatLng to = toLatLng(castleEn.getMapPosition());
					

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

	private boolean isACastle(Marker marker) {
		String id = marker.getId();
		Set<Marker> castleMarkers = hashMapMarkerCastle.keySet();
		for(Marker castleMarker : castleMarkers) {
			if(id.equals(castleMarker.getId())) {
				return true;
			} 
		}

		return false;
	}

	private Castle getCastleByMarker(Marker marker) {
		LatLng latLng = marker.getPosition();
		Castle castle = getCastleByLatLng(latLng);
		return castle;
	}

	private Castle getCastleByLatLng(LatLng latLng) {
		for(Castle castle : castles) {
			MapPosition mapPosition = 	castle.getMapPosition();
			LatLng castleLatLng = toLatLng(mapPosition);
			if(latLng.equals(castleLatLng))
				return castle;
		}
		return null;
	}


	public void onClickCreateUnity(View view) {
		vibrate();
		LatLng latLng = myCastleMarker.getPosition();
		LatLng target = generateLocationToUnity(latLng);
		UnityMilitar unity = UnityMilitarFactory.getInstance().createSimpleKnight(getMyCastle().getId(), target);
		Marker unityMarker = map.addMarker(createUnityMarker(unity));

		UnityMilitarMarker unityMilitarMarker = new UnityMilitarMarker(unity, unityMarker);
		unitysMarkers.add(unityMilitarMarker);

		//map.moveCamera( CameraUpdateFactory.newLatLngZoom(target, 21));
	}

	private LatLng generateLocationToUnity(LatLng centre) {
		double radius = 2; 		

		double lat = centre.latitude * Math.PI / 180.0; 
		double lon = centre.longitude * Math.PI / 180.0;

		LatLng point = null;
		boolean flag = false;

		while (!flag) {

			for (double t = 0; t <= Math.PI * 2; t += 0.3) {

				double latPoint = lat + (radius / EARTH_RADIUS) * Math.sin(t);
				double lonPoint = lon + (radius / EARTH_RADIUS) * Math.cos(t) / Math.cos(lat);

				point = new LatLng(latPoint * 180.0 / Math.PI, lonPoint * 180.0 / Math.PI);
				if(isPositionAvailableToCreateUnity(point)) {
					flag = true;
					break;
				}
			}

			radius = radius + 1;
		}


		return point;

	}

	private boolean isPositionAvailableToCreateUnity(LatLng latLng) { 
		for(UnityMilitarMarker unityMilitarMarker : unitysMarkers) {
			Marker markerUnity = unityMilitarMarker.getMarker();
			LatLng unityLatLng = markerUnity.getPosition();
			float distance = getDistanceInMeters(latLng, unityLatLng);
			if(distance <= 2)
				return false;

		}
		return true;
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

	@SuppressWarnings("unused")
	private LatLng toLatLng(Location location) {
		LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
		return latLng;
	}

	private void drawMarkerWithCircle(LatLng position){
		double radiusInMeters = 100.0;
		int strokeColor = 0xffff0000; //red outline
		int shadeColor = 0x44ff0000; //opaque red fill

		CircleOptions circleOptions = new CircleOptions().center(position).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
		Circle mCircle = map.addCircle(circleOptions);


		//MarkerOptions markerOptions = new MarkerOptions().position(position);
		//mMarker = map.addMarker(markerOptions);
	}

	private boolean isMarkerInsideCircle(Marker marker, Circle circle) {
		float[] distance = new float[2];

		Location.distanceBetween( marker.getPosition().latitude, marker.getPosition().longitude,
				circle.getCenter().latitude, circle.getCenter().longitude, distance);

		if( distance[0] < circle.getRadius()  ){
			Toast.makeText(getBaseContext(), "Outside", Toast.LENGTH_LONG).show();
			return false;
		} else {
			Toast.makeText(getBaseContext(), "Inside", Toast.LENGTH_LONG).show();
			return true;
		}
	}

	private float getDistanceInMeters(LatLng source, LatLng target) {
		float[] results = new float[3];
		Location.distanceBetween(source.latitude, source.longitude, target.latitude, target.longitude, results);
		float distanceMeters = results[0];
		return distanceMeters; 
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