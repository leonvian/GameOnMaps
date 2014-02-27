package br.com.lvc.defenser.entitie;

import br.com.lvc.worldwar.entitie.MapPosition;

public class Enemy implements Comparable<Enemy> { 

	public static final int DEFAULT_SPEED = 200;
	public static final int DEFAULT_STRENGTH = 50;
	public static final int DEFAULT_LIFE_MAX = 100;
	 
	private int name;
	private int description;
	private int imageInRes;
	private int strength;
	private int speed;
	private MapPosition mapPosition;
	private int life = DEFAULT_LIFE_MAX;
	private int lifeMax = DEFAULT_LIFE_MAX;
	
	public Enemy() {
	}

	public Enemy(int name, int description, int imageInRes, int strength,
			int speed, MapPosition mapPosition) {
		super();
		this.name = name;
		this.description = description;
		this.imageInRes = imageInRes;
		this.strength = strength;
		this.speed = speed;
		this.mapPosition = mapPosition;
	}
	
	

	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}

	public int getLifeMax() {
		return lifeMax;
	}

	public void setLifeMax(int lifeMax) {
		this.lifeMax = lifeMax;
	}

	public int getName() {
		return name;
	}

	public void setName(int name) {
		this.name = name;
	}

	public int getDescription() {
		return description;
	}

	public void setDescription(int description) {
		this.description = description;
	}

	public int getImageInRes() {
		return imageInRes;
	}

	public void setImageInRes(int imageInRes) {
		this.imageInRes = imageInRes;
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public MapPosition getMapPosition() {
		return mapPosition;
	}

	public void setMapPosition(MapPosition mapPosition) {
		this.mapPosition = mapPosition;
	}

	@Override
	public int compareTo(Enemy another) {
	    
		return 0;
	}
	
	public void decreaseLife(int decreaseLife) { 
		life = life - decreaseLife;
	}
	
	public void increaseLife(int increaseLife) {
		life = life + increaseLife;
	}
	
	public boolean hasToEliminate() {
		if(life <= 0)
			return true;
		else
			return false;
	}
	
	  

}