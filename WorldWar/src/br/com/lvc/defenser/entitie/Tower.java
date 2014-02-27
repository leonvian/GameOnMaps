package br.com.lvc.defenser.entitie;

import br.com.lvc.worldwar.entitie.MapPosition;

public class Tower {
	
	private int name;
	private MapPosition mapPosition;
	private int imageInRes;
	
	public Tower() {
	}
		
	public Tower(int name, MapPosition mapPosition, int imageInRes) {
		super();
		this.name = name;
		this.mapPosition = mapPosition;
		this.imageInRes = imageInRes;
	}

	
	public int getImageInRes() {
		return imageInRes;
	}
	
	public void setImageInRes(int imageInRes) {
		this.imageInRes = imageInRes;
	}



	public MapPosition getMapPosition() {
		return mapPosition;
	}




	public void setMapPosition(MapPosition mapPosition) {
		this.mapPosition = mapPosition;
	}




	public int getName() {
		return name;
	}
	
	public void setName(int name) {
		this.name = name;
	}
}
