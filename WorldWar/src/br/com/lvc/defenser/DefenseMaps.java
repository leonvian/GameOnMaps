package br.com.lvc.defenser;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import br.com.lvc.defenser.entitie.Enemy;
import br.com.lvc.defenser.entitie.EnemyDrawer;
import br.com.lvc.defenser.entitie.EnemyFactory;
import br.com.lvc.worldwar.R;
import br.com.lvc.worldwar.entitie.Castle;
import br.com.lvc.worldwar.entitie.MapPosition;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

//br.com.lvc.defenser.DefenseMaps
public class DefenseMaps extends FragmentActivity implements OnMapLongClickListener, OnInfoWindowClickListener, OnMarkerClickListener, OnMarkerDragListener {

	private static final int TIME_TO_WALK = 10; 
	private static final double TOTAL_TIME_TO_FINISH_PATH = 60000 * 60;

	protected GoogleMap map; 
	private MapPosition mapPositionBr = new MapPosition(-19.38566266047098, -42.99701750278473);


	private Castle castleBr = new Castle(1, "Brasil", "Patria",R.drawable.castle_um, mapPositionBr);

	//private List<Marker> castleMarkers = new ArrayList<Marker>();
	private List<Marker> enemyMarkers = new ArrayList<Marker>();

	private Marker myCastleMarker;	
	private Handler handler = new Handler();

	private double elapsedTime = 0;
	private double elapsedPercent = 0;
	private  double lastFrameTime = 0;

	private HashMap<Marker, EnemyDrawer> hashMapMarkerEnemyDrawer = new HashMap<Marker, EnemyDrawer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.defense_maps);
		map = retrieveMap();   
		myCastleMarker = map.addMarker( createCastleMarker(castleBr) ); 

		map.setInfoWindowAdapter(new DefenseMapsWindowAdapter(this, hashMapMarkerEnemyDrawer));

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
		Log.i("POINT", "LAT/LNG " + point);
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
				
				for(Marker marker : enemyMarkers) {
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


	private void createUnityEnemysOnMaps() { 
		List<Enemy> unitys = EnemyFactory.getListUnitys();
		for(Enemy unity : unitys) {
			Marker unityMarker = map.addMarker(createUnityMarker(unity));
			hashMapMarkerEnemyDrawer.put(unityMarker, getEnemeyDrawer(unity));
			enemyMarkers.add(unityMarker);
		} 
	}
	
	private EnemyDrawer getEnemeyDrawer(Enemy enemy) {
		EnemyDrawer enemyDrawer = new EnemyDrawer(enemy);
		return enemyDrawer;
	}

	private MarkerOptions createUnityMarker(Enemy unity) {
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.icon(BitmapDescriptorFactory.fromResource(unity.getImageInRes()));
		markerOptions.title(getString(unity.getName()));
		markerOptions.snippet(getString(unity.getDescription()));
		markerOptions.position(toLatLng(unity));

		return markerOptions;
	}


	private LatLng toLatLng(Enemy unity) {
		MapPosition mapPosition = unity.getMapPosition();
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

	/*
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
 */
	@Override
	public boolean onMarkerClick(Marker marker) {
		Log.i("MARKER CLICK", "LISTENER");
		return false;
	}


	@Override
	public void onMarkerDragStart(Marker marker) {
		LatLng position = marker.getPosition();

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
