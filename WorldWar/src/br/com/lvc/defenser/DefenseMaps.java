package br.com.lvc.defenser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
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

import com.google.android.gms.maps.CameraUpdateFactory;
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
public class DefenseMaps extends FragmentActivity implements   OnInfoWindowClickListener, OnMarkerClickListener, OnMarkerDragListener {

	private static final int VIBRATE_TIME = 150;
	private static final char LIFE_BAR = '|';
	private static final float RADIUS_IN_METERS =  10000.0f;
	private static final int TIME_TO_WALK = 10; 
	private static final double TOTAL_TIME_TO_FINISH_PATH = 60000 * 60;
	private static final float MINIMUN_DISTANCE_TO_ACHIEVED = 10f;

	protected GoogleMap map; 

	private Castle myCastle = null;

	//private List<Marker> castleMarkers = new ArrayList<Marker>();


	private Marker myCastleMarker;	
	private Handler handler = new Handler();

	private double elapsedTime = 0;
	private double elapsedPercent = 0;
	private  double lastFrameTime = 0;

	private HashMap<Marker, MapDrawer> hashMapMarkerEnemyDrawer = new HashMap<Marker, MapDrawer>();
	private HashMap<Circle, Tower> hashMapTowerCircle = new HashMap<Circle, Tower>();
	private HashMap<Enemy, View> hashMapEnemyView = new HashMap<Enemy, View>();
	private List<EnemyMarker> enemyMarkers = new ArrayList<EnemyMarker>();


	private TextView textViewInstructions;
	private ViewGroup layoutEnemyStatusOne;
	private ViewGroup layoutEnemyStatusTwo;
	private ViewGroup layoutEnemyStatusMain;

	private OnMapLongClickListener longClickPutCastleOnMap = new LongClickPutCastleOnMap();
	private OnMapLongClickListener longClickPutTowerOnMap = new LongClickPutTowerOnMap();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.defense_maps); 
		layoutEnemyStatusOne = (ViewGroup) findViewById(R.id.layout_enemys_status_one);
		layoutEnemyStatusTwo = (ViewGroup) findViewById(R.id.layout_enemys_status_two);
		layoutEnemyStatusMain = (ViewGroup) findViewById(R.id.layout_enemys_status_main);

		textViewInstructions = (TextView) findViewById(R.id.text_view_instructions);
		map = retrieveMap();   

		map.setInfoWindowAdapter(new DefenseMapsWindowAdapter(this, hashMapMarkerEnemyDrawer));

		map.setOnMapLongClickListener(longClickPutCastleOnMap);
		map.setOnInfoWindowClickListener(this);
		map.setOnMarkerClickListener(this);
		map.setOnMarkerDragListener(this);

	}


	public void onClickStart(View view) { 
		startGame();  
	}

	private void startGame() {
		findViewById(R.id.layout_instructions).setVisibility(View.GONE);
		findViewById(R.id.layout_button_start).setVisibility(View.GONE); 
		layoutEnemyStatusMain.setVisibility(View.VISIBLE);

		map.setOnMapLongClickListener(longClickPutTowerOnMap);
		createUnityEnemysOnMaps();
		atualizeAllEnemyStatus();
		scheduleMarch();
		map.moveCamera( CameraUpdateFactory.newLatLngZoom(myCastleMarker.getPosition(), 15));
	}

	private void atualizeAllEnemyStatus() {
		for(EnemyMarker enemyMarker : enemyMarkers) {
			Enemy enemy = enemyMarker.getEnemy();

			View view = null;
			if(hashMapEnemyView.containsKey(enemy)) {
				view = hashMapEnemyView.get(enemy);
			} else {
				view = createViewLifeStatusByEnemy();
				hashMapEnemyView.put(enemy, view);
			}

			atualizeViewLifeStatusByEnemy(view, enemy);
			if(layoutEnemyStatusOne.getChildCount() <= layoutEnemyStatusTwo.getChildCount())
				layoutEnemyStatusOne.addView(view);
			else
				layoutEnemyStatusTwo.addView(view);
		}
	}


	private void atualizeViewLifeStatusByEnemy(View view, Enemy enemy) {

		ImageView imageViewEnemy = (ImageView) view.findViewById(R.id.image_view_enemy);
		imageViewEnemy.setImageResource(enemy.getImageInRes());


		int totalLife = enemy.getLifeMax();
		int aThird = totalLife / 3;
		int twoThird = aThird + aThird;
		int remainLife = enemy.getLife();
		String lifeInBarsAThird = getLifeInBars(aThird);

		TextView textViewLifeFine = (TextView) view.findViewById(R.id.text_view_life_fine);
		textViewLifeFine.setText("");

		TextView textViewLifeAttention = (TextView) view.findViewById(R.id.text_view_life_attention);
		textViewLifeAttention.setText("");

		TextView textViewLifeDanger = (TextView) view.findViewById(R.id.text_view_life_danger);
		textViewLifeDanger.setText("");

		if(remainLife <= 0) {
			view.setVisibility(View.GONE);
			hashMapEnemyView.remove(enemy);
		} else if(remainLife <= aThird) {
			String lifeInBars = getLifeInBars(remainLife);
			textViewLifeDanger.setText(lifeInBars);
		} else if(remainLife <= (twoThird)) {
			textViewLifeDanger.setText(lifeInBarsAThird);
			textViewLifeAttention.setText(getLifeInBars(remainLife - aThird));
		} else {
			textViewLifeDanger.setText(lifeInBarsAThird);
			textViewLifeAttention.setText(lifeInBarsAThird);
			textViewLifeFine.setText(getLifeInBars(remainLife - (twoThird)));
		}

	}


	private String getLifeInBars(int lifes) {
		StringBuilder stringBuilder = new StringBuilder();
		for(int i = 0; i < lifes; i ++) {
			stringBuilder.append(LIFE_BAR);
		}

		String toString = stringBuilder.toString();
		return toString;
	}

	private View createViewLifeStatusByEnemy() {
		View view = LayoutInflater.from(this).inflate(R.layout.enemys_status, null);
		return view;
	}

	private void atualizeInstructions(int messageRes) {
		textViewInstructions.setText(messageRes);
	}


	private GoogleMap retrieveMap() {
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		mapFragment.setRetainInstance(true);
		GoogleMap googleMap = mapFragment.getMap();
		return googleMap;
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

				boolean stop = true;

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

					LatLng to = myCastleMarker.getPosition(); 

					double nextLongitude = (from.longitude + (to.longitude - from.longitude) *  elapsedPercent);
					double nextLatitude = (from.latitude + (to.latitude - from.latitude) *  elapsedPercent);

					LatLng nextMove = new LatLng(nextLatitude, nextLongitude);
					marker.setPosition(nextMove);

					if(!isTargetWasAchieved(nextMove, to)) {
						stop = false;
					}

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

	private boolean isTargetWasAchieved(LatLng source, LatLng target) {
		float distance = getDistanceInMeters(source, target);
		Log.i("DISTANCE TARGET", "Distance target: " + distance);
		if(distance <= MINIMUN_DISTANCE_TO_ACHIEVED) {
			return true;
		} else {
			return false;
		}
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
				View viewEnemyStatus = hashMapEnemyView.get(enemy);
				atualizeViewLifeStatusByEnemy(viewEnemyStatus, enemy);
			}

		}

	}


	private void createUnityEnemysOnMaps() { 
		List<Enemy> unitys = EnemyFactory.createListUnitys();
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
		return false;
	}

	
	private boolean isMarkerInsideCircle(LatLng latLng, Tower circle) {
		float distance = getDistanceInMeters(latLng, toLatLng(circle.getMapPosition()));		
		
		if(distance > RADIUS_IN_METERS) 
			return false;
		else
			return true;
	}

	private MarkerOptions createCastleMarker(Castle castle) {
		MarkerOptions markerOptions = createMarkerOptionsNoDraggable(castle.getImage(), castle.getMapPosition());
		markerOptions.snippet(castle.getDescription());
		markerOptions.title(castle.getName());
		return markerOptions;
	} 

	private MarkerOptions createTowerMarker(Tower tower) {
		MarkerOptions markerOptions = createMarkerOptionsNoDraggable(tower.getImageInRes(), tower.getMapPosition());
		markerOptions.title(getString(tower.getName()));
		return markerOptions;
	} 
	
	private MarkerOptions createMarkerOptionsNoDraggable(int imageInRes, MapPosition mapPosition) {
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.icon(BitmapDescriptorFactory.fromResource(imageInRes));
		markerOptions.draggable(false);
		return markerOptions;
	}

	private float getDistanceInMeters(LatLng source, LatLng target) {
		float[] results = new float[3];
		Location.distanceBetween(source.latitude, source.longitude, target.latitude, target.longitude, results);
		float distanceMeters = results[0];
		return distanceMeters; 
	}	

	private void vibrate() {
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(VIBRATE_TIME);
	}


	private class LongClickPutCastleOnMap implements OnMapLongClickListener {

		@Override
		public void onMapLongClick(LatLng point) { 
			vibrate();
			if(myCastle == null) {
				MapPosition mapPosition = new MapPosition(point);
				myCastle = new Castle(1, "Brasil", "Patria", R.drawable.castle_um, mapPosition);
				myCastleMarker = map.addMarker( createCastleMarker(myCastle) ); 
				CastleDrawer castleDrawer = new CastleDrawer(myCastle);
				hashMapMarkerEnemyDrawer.put(myCastleMarker, castleDrawer);	
				findViewById(R.id.layout_button_start).setVisibility(View.VISIBLE);
				atualizeInstructions(R.string.you_can_move_your_castle_dragging);
			} else {
				atualizeInstructions(R.string.your_castle_was_build_you_can_drag_and_drop_to_a_better_place);
			}

		}

	}

	private class LongClickPutTowerOnMap implements OnMapLongClickListener {
		@Override
		public void onMapLongClick(LatLng point) { 
			vibrate();
			MapPosition mapPosition = new MapPosition(point);
			Tower tower = TowerFactory.createSimpleTower(mapPosition);

			Marker towerMarker = map.addMarker(createTowerMarker(tower));

			TowerDrawer towerDrawer = new TowerDrawer(tower);
			hashMapMarkerEnemyDrawer.put(towerMarker, towerDrawer);

			Circle circle = drawCircleOnMap(point, Tower.CIRCLE_INSIDE_COLOR, Tower.CIRCLE_MARGIN_COLOR);
			hashMapTowerCircle.put(circle, tower);
		}
	}
	
	private Circle drawCircleOnMap(LatLng position, int shadeColor, int strokeColor){
		CircleOptions circleOptions = new CircleOptions().center(position).radius(RADIUS_IN_METERS).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(5);
		Circle mCircle = map.addCircle(circleOptions);
		
		return mCircle;
	}

	

	@Override
	public void onMarkerDragStart(Marker marker) { }

	@Override
	public void onMarkerDrag(Marker marker) { }

	@Override
	public void onMarkerDragEnd(Marker marker) {}
}