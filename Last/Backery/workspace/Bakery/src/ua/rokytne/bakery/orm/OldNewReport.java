package ua.rokytne.bakery.orm;

public class OldNewReport {

	private int id;
	private String dateTime;
	private boolean old;
	private int user_id;
		
	public OldNewReport(int id, String dateTime, boolean old, int u_id){
		setId(id);
		setDateTime(dateTime);
		setOld(old);
		setUserID(u_id);
	}

	public OldNewReport(String dateTime, boolean old, int u_id){
		this(-1, dateTime, old, u_id);
	}
	
	///////////////////////////////////////
	public void setId(int value){
		this.id = value;
	}
	
	public void setDateTime(String value){
		this.dateTime = value;
	}
	
	public void setOld(boolean value){
		this.old = value;
	}
	
	public void setUserID(int value){
		this.user_id = value;
	}
	
	
	///////////////////////////////////////
	public int getId(){
		return this.id;
	}
	
	public String getDateTime(){
		return this.dateTime;
	}
	
	public boolean getOld(){
		return this.old;
	}
	
	public int getUserID(){
		return this.user_id;
	}
	
	@Override
	public String toString(){
		if(old)
			return "Попередній";
		
		return "Новий";
	}
	
}
