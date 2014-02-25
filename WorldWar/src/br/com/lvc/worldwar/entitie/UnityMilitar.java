package br.com.lvc.worldwar.entitie;


public class UnityMilitar {

	public static final int KNIGHT_SPEED = 200;
	public static final int KNIGHT_STRENGTH = 50;
	
	private int castle;
	private int image;
	private int name;
	private int description;
	private int strength;
	private int speed;
	private MapPosition mapPosition;
	
	
	public UnityMilitar() {
	}
	
	
	
	public UnityMilitar(int castle, int speed, int image, int name, int description,
			int strength, MapPosition mapPosition) {
		super();
		this.speed = speed;
		this.castle = castle;
		this.image = image;
		this.name = name;
		this.description = description;
		this.strength = strength;
		this.mapPosition = mapPosition;
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

	public int getCastle() {
		return castle;
	}


	public void setCastle(int castle) {
		this.castle = castle;
	}


	public int getImage() {
		return image;
	}


	public void setImage(int image) {
		this.image = image;
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


	public int getStrength() {
		return strength;
	}


	public void setStrength(int strength) {
		this.strength = strength;
	}

	
	
}