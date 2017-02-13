package ua.rokytne.bakery.orm;

public class ReportTeam {

	private int id;
	private int user_id;
	private String datetime;
	
	public ReportTeam(int id, int userID, String datetime){
		setID(id);
		setUserID(userID);
		setDateTime(datetime);
	}

	public ReportTeam(int userID, String datetime){
		this(-1, userID, datetime);
	}

	public void setID(int value){
		this.id = value;
	}

	public void setUserID(int value){
		this.user_id = value;
	}
	
	public void setDateTime(String value){
		this.datetime = value;
	}
	
	public int getID(){
		return this.id;
	}

	public int getUserID(){
		return this.user_id;
	}
	
	public String getDateTime(){
		return this.datetime;
	}
	
	@Override
	public String toString(){
		return datetime;
	}
	
}
