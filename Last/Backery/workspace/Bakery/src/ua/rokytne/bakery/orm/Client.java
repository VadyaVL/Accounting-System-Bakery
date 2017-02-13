package ua.rokytne.bakery.orm;

public class Client {
	
	private int id;
	private String name;
	private int place_id;
	
	public Client(String name, int place_id){
		this(-1, name, place_id);
	}

	public Client(int id, String name, int place_id){
		setID(id);
		setName(name);
		setPlaceID(place_id);
	}
	
	public void setID(int value){
		this.id = value;
	}
	
	public void setName(String value){
		this.name = value;
	}
	
	public void setPlaceID(int value){
		this.place_id = value;
	}
	
	public int getID(){
		return this.id;
	}
	
	public String getName(){
		return this.name;
	}
	
	public int getPlaceID(){
		return this.place_id;
	}
	
	@Override
	public String toString(){
		return this.name;
	}
	
}
