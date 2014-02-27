package br.com.lvc.defenser.entitie;

import br.com.lvc.worldwar.R;
import br.com.lvc.worldwar.entitie.MapPosition;

public class TowerFactory {
	
	
	public static Tower createSimpleTower(MapPosition mapPosition) {
		Tower tower = new Tower(R.string.tower, mapPosition, R.drawable.tower);
		return tower;
	}

}
