package ua.rokytne.bakery.orm;

/***
 * Class describe element of database - Place.
 * @author Vadym
 *
 */
public class Place {

	private int id;
	private String name;
	
	public Place(String name) {
		this(-1, name);
	}

	public Place(int id, String name) {
		setName(name);
		setID(id);
	}
	
	public int getID(){
		return this.id;
	}
	
	public void setID(int value){
		this.id = value;
	}
	
	/**
	 * 
	 * @return value of name field.
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Setter for field name.
	 * @param value name of city.
	 */
	public void setName(String value){
		this.name = value;
	}
	
	@Override
	public String toString(){
		return this.name;
	}
}
