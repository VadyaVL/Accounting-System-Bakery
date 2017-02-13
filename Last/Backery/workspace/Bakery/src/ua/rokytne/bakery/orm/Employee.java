package ua.rokytne.bakery.orm;

public class Employee {
	
	private int id;
	private String fullName;
	
	public Employee(int id, String name){
		setFullName(name);
		setID(id);
	}
	
	public Employee(String name){
		this(-1, name);
	}
	

	public int getID(){
		return this.id;
	}
	
	public String getFullName(){
		return this.fullName;
	}
	
	public void setID(int value){
		this.id = value;
	}
	
	public void setFullName(String value){
		this.fullName = value;
	}
	
	@Override
	public String toString(){
		return fullName;
	}
}
