package br.com.lvc.worldwar.entitie;


public class Castle {
	
	
	private int id;
	private String name;
	private String description;
	private int image;
	private MapPosition mapPosition;
	
	
	public Castle() {
	}

	

	public Castle(int id, String name, String description, int image,
			MapPosition mapPosition) {
		super();
		this.image = image;
		this.id = id;
		this.name = name;
		this.description = description;
		this.mapPosition = mapPosition;
	}


	public int getImage() {
		return image;
	}
	public void setImage(int image) {
		this.image = image;
	}

	public MapPosition getMapPosition() {
		return mapPosition;
	}

	public void setMapPosition(MapPosition mapPosition) {
		this.mapPosition = mapPosition;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	

}
