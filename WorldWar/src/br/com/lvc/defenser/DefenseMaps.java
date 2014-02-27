package br.com.lvc.defenser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;
import br.com.lvc.defenser.entitie.CastleDrawer;
import br.com.lvc.defenser.entitie.Enemy;
import br.com.lvc.defenser.entitie.EnemyDrawer;
import br.com.lvc.defenser.entitie.EnemyFactory;
import br.com.lvc.defenser.entitie.MapDrawer;
import br.com.lvc.defenser.entitie.Tower;
import br.com.lvc.defenser.entitie.TowerDrawer;
import br.com.lvc.defenser.entitie.TowerFactory;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

//br.com.lvc.defenser.DefenseMaps
public class DefenseMaps extends FragmentActivity implements OnMapLongClickListener, OnInfoWindowClickListener, OnMarkerClickListener, OnMarkerDragListener {

	private static final float RADIUS_IN_METERS =  10000.0f;
	private static final int TIME_TO_WALK = 10; 
	private static final double TOTAL_TIME_TO_FINISH_PATH = 60000 * 60;

	protected GoogleMap map; 
	private MapPosition mapPositionBr = new MapPosition(-19.38566266047098, -42.99701750278473);


	private Castle castleBr = new Castle(1, "Brasil", "Patria", R.drawable.castle_um, mapPositionBr);

	//private List<Marker> castleMarkers = new ArrayList<Marker>();
	private List<EnemyMarker> enemyMarkers = new ArrayList<EnemyMarker>();

	private Marker myCastleMarker;	
	private Handler handler = new Handler();

	private double elapsedTime = 0;
	private double elapsedPercent = 0;
	private  double lastFrameTime = 0;

	private HashMap<Marker, MapDrawer> hashMapMarkerEnemyDrawer = new HashMap<Marker, MapDrawer>();
	private HashMap<Circle, Tower> hashMapTowerCircle = new HashMap<Circle, Tower>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.defense_maps);
		map = retrieveMap();   
		myCastleMarker = map.addMarker( createCastleMarker(castleBr) ); 
		CastleDrawer castleDrawer = new CastleDrawer(castleBr);
		hashMapMarkerEnemyDrawer.put(myCastleMarker, castleDrawer);
		
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
	
	@Override
	public void onMapLongClick(LatLng point) { 
		Log.i("POINT", "LAT/LNG " + point);
		MapPosition mapPosition = new MapPosition(point);
		Tower tower = TowerFactory.createSimpleTower(mapPosition);
		
		Marker towerMarker = map.addMarker(createTowerMarker(tower));
		
		TowerDrawer towerDrawer = new TowerDrawer(tower);
		hashMapMarkerEnemyDrawer.put(towerMarker, towerDrawer);
		
		Circle circle = drawMarkerWithCircle(point);
		hashMapTowerCircle.put(circle, tower);
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		
	}

	/**
	 * N�o esta correspodenndo ao tempo total de marcha
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
				List<EnemyMarker> enemysToRemove = new ArrayList<DefenseMaps.EnemyMarker>();

				for(EnemyMarker enemyMarker : enemyMarkers) {
					Enemy enemy = enemyMarker.getEnemy();
					Marker marker = enemyMarker.getMarker();
					
					hitTowerIfisInFireArea(enemyMarker);
					
					if(enemy.hasToEliminate()) {
						enemysToRemove.add(enemyMarker);
						marker.remove();
						continue;
					} 
					
					LatLng from = marker.getPosition(); 
					LatLng to = toLatLng(castleBr.getMapPosition()); 

					double nextLongitude = (from.longitude + (to.longitude - from.longitude) *  elapsedPercent);
					double nextLatitude = (from.latitude + (to.latitude - from.latitude) *  elapsedPercent);

					LatLng nextMove = new LatLng(nextLatitude, nextLongitude);

					marker.setPosition(nextMove);
				
				}
				
				enemyMarkers.removeAll(enemysToRemove);

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
	
	private void hitTowerIfisInFireArea(EnemyMarker enemyMarker) {
		Enemy enemy = enemyMarker.getEnemy();
		Marker marker = enemyMarker.getMarker();
		 
		Collection<Tower> circles = hashMapTowerCircle.values();
		for(Tower circle : circles) {
			LatLng target = marker.getPosition();
			if(isMarkerInsideCircle(target, circle)) {
				 Log.i("DEFENSE", "**** MARKER AVANÇOU TOWER");
				 enemy.decreaseLife(10);
			}
			
		}
		
	}


	private void createUnityEnemysOnMaps() { 
		List<Enemy> unitys = EnemyFactory.getListUnitys();
		for(Enemy unity : unitys) {
			Marker unityMarker = map.addMarker(createUnityMarker(unity));
			hashMapMarkerEnemyDrawer.put(unityMarker, getEnemeyDrawer(unity));
			EnemyMarker enemyMarker = new EnemyMarker(unity, unityMarker);
			enemyMarkers.add(enemyMarker); 
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


	private class EnemyMarker {

		private Enemy enemy;
		private Marker marker;

		public EnemyMarker(Enemy enemy, Marker marker) {
			super();
			this.enemy = enemy;
			this.marker = marker;
		}
		
		public Enemy getEnemy() {
			return enemy;
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
	
	private Circle drawMarkerWithCircle(LatLng position){
		
		int strokeColor = 0xffff0000; //red outline
		int shadeColor = 0x44ff0000; //opaque red fill

		CircleOptions circleOptions = new CircleOptions().center(position).radius(RADIUS_IN_METERS).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
		Circle mCircle = map.addCircle(circleOptions);
		
		return mCircle;

 
	}

	private boolean isMarkerInsideCircle(LatLng latLng, Tower circle) {
		
		float distance = getDistanceInMeters(latLng, toLatLng(circle.getMapPosition()));
		
		Log.i("DISTNCE", "DISTANCE " + distance);
	   if(distance > RADIUS_IN_METERS) {
	//	   Toast.makeText(getBaseContext(), "Outside", Toast.LENGTH_LONG).show();
		   return false;
	   } else {
		   Toast.makeText(getBaseContext(), "Inside", Toast.LENGTH_LONG).show();
		   return true;
	   }
		
		/*
		float[] distance = new float[2];

		Location.distanceBetween( latLng.latitude, latLng.longitude,
				circle.getCenter().latitude, circle.getCenter().longitude, distance);

		if( distance[0] < circle.getRadius()  ){
			Toast.makeText(getBaseContext(), "Outside", Toast.LENGTH_LONG).show();
			return false;
		} else {
			Toast.makeText(getBaseContext(), "Inside", Toast.LENGTH_LONG).show();
			return true;
		}
		*/
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
	
	private MarkerOptions createTowerMarker(Tower tower) {
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.icon(BitmapDescriptorFactory.fromResource(tower.getImageInRes()));
		//markerOptions.snippet(castle.getDescription());
		markerOptions.title(getString(tower.getName()));
		markerOptions.position(toLatLng(tower.getMapPosition()));
		markerOptions.draggable(true);

		return markerOptions;
	} 
	
	private float getDistanceInMeters(LatLng source, LatLng target) {
		float[] results = new float[3];
		Location.distanceBetween(source.latitude, source.longitude, target.latitude, target.longitude, results);
		float distanceMeters = results[0];
		return distanceMeters; 
	}


}
