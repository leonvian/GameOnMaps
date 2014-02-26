package br.com.lvc.defenser;

import java.util.HashMap;

import android.app.Activity;
import android.view.View;
import br.com.lvc.defenser.entitie.EnemyDrawer;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

public class DefenseMapsWindowAdapter  implements InfoWindowAdapter {
	
	private HashMap<Marker, EnemyDrawer> hashMapMarkerEnemy;
	private Activity activity;
	
	public DefenseMapsWindowAdapter(Activity activity, HashMap<Marker, EnemyDrawer> hashMapMarkerEnemy) {
	   this.hashMapMarkerEnemy = hashMapMarkerEnemy;
	   this.activity = activity;
	}

	// TODO Auto-generated method stub
	@Override
	public View getInfoContents(Marker marker) {
		EnemyDrawer enemyDrawer = hashMapMarkerEnemy.get(marker);
		View view = enemyDrawer.getView(activity);
		return view;
	}
	
	@Override
	public View getInfoWindow(Marker marker) {
		return null;
	}
	

}
