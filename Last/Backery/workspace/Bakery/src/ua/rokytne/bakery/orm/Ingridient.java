package ua.rokytne.bakery.orm;

/***
 * Class describe element of database - Ingridient.
 * @author Vadym
 *
 */
public class Ingridient {

	private int id;
	private float countOnStorage;
	private String name;
	
	public Ingridient(String name) {
		this(-1, name, 0);
	}

	public Ingridient(int id, String name) {
		this(id, name, 0);
	}
	

	public Ingridient(int id, String name, float countOnStor) {
		setName(name);
		setID(id);
		setCountOnStorage(countOnStor);
	}
	
	public int getID(){
		return this.id;
	}
	
	public void setID(int value){
		this.id = value;
	}
	
	public float getCountOnStorage(){
		return this.countOnStorage;
	}
	
	public void setCountOnStorage(float value){
		this.countOnStorage = value;
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
