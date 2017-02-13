package ua.rokytne.bakery.orm;

public class Debit {

	private int user_id;
	private int production_id;
	private int employee_id;
	private int count;
	private String dateTime;
	
	public Debit(int user_id, int production_id, int employee_id, int count, String dateTime){
		setUserID(user_id);
		setProductionID(production_id);
		setEmployeeID(employee_id);
		setCount(count);
		setDateTime(dateTime);
	}
	
	public void setUserID(int value){
		this.user_id = value;
	}
	
	public void setProductionID(int value){
		this.production_id = value;
	}
	
	public void setEmployeeID(int value){
		this.employee_id = value;
	}

	public void setCount(int value){
		this.count = value;
	}

	public void setDateTime(String value){
		this.dateTime = value;
	}
	
	public int getUserID(){
		return this.user_id;
	}
	
	public int getProductionID(){
		return this.production_id;
	}
	
	public int getEmployeeID(){
		return this.employee_id;
	}

	public int getCount(){
		return this.count;
	}

	public String getDateTime(){
		return this.dateTime;
	}
	
	@Override
	public String toString(){
		return dateTime;
	}
	
}
