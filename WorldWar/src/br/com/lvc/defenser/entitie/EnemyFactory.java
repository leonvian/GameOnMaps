package br.com.lvc.defenser.entitie;

import java.util.ArrayList;
import java.util.List;

import br.com.lvc.worldwar.R;
import br.com.lvc.worldwar.entitie.MapPosition;

import com.google.android.gms.maps.model.LatLng;

public class EnemyFactory {
	

	static LatLng[] ENEMY_POSITIONS = {
		/*	new LatLng(-6.622134565519439,21.16429377347231),
			new LatLng(-16.08918164626128,26.086164750158787),
			new LatLng(30.030040648831495,6.5744490176439285),
			new LatLng(49.29571026207442,-5.378672629594803),
			new LatLng(-42.52157676221938,25.73460090905428),
			new LatLng(72.82871295558336,-41.58961113542318),
			new LatLng(70.68503863263622,-61.62867531180382),
			new LatLng(68.35016792567947,8.859604597091675),
			new LatLng(60.43497092011095,30.656478926539418),
			new LatLng(39.26537772056473,29.77756932377815),
			new LatLng(23.119068541046335,31.535383835434917),
			new LatLng(-26.235366649384314,31.535383835434917), */
			new LatLng(-35.28247281310884,10.44163752347231)
	};
	
	
	public static List<Enemy> getListUnitys() {
		List<Enemy> unitys = new ArrayList<Enemy>();
		for(LatLng position : ENEMY_POSITIONS) {
			unitys.add( createSimpleEnemy(position) );
		}
		
		return unitys;
	}
	
	public static Enemy createSimpleEnemy(LatLng position) {
		MapPosition mapPosition = new MapPosition(position); 
		Enemy enemy = new Enemy(R.string.knight, R.string.knight_description, R.drawable.knight,Enemy.DEFAULT_STRENGTH, Enemy.DEFAULT_SPEED,mapPosition);
		return enemy;
	}
	

}
