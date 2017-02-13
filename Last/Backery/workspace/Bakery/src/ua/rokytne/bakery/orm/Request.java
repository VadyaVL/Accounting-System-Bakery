package ua.rokytne.bakery.orm;

public class Request {
	// id, Client_id, Date_Request, Date_Oformleniya, OK, User_id
	private int id;
	private int client_id;
	private String date_Request;
	private String date_Oformleniya;
	private boolean ok;
	private int user_id;
	private String printableName = "Заявка без імені";
	
	private String place = "0";
	
	public Request(int id, int client_id, String dateReq, String dateOf, boolean ok, int user_id){
		setId(id);
		setClientId(client_id);
		setDateRequest(dateReq);
		setDateOF(dateOf);
		setOK(ok);
		setUserId(user_id);
	}

	public Request(int client_id, String dateReq, String dateOf, boolean ok, int user_id){
		this(-1, client_id, dateReq, dateOf, ok, user_id);
	}

	public int getId(){
		return this.id;
	}
	
	public void setId(int value){
		this.id = value;
	}

	public int getClientId(){
		return this.client_id;
	}
	
	public void setClientId(int value){
		this.client_id = value;
	}
	
	public int getUserId(){
		return this.user_id;
	}
	
	public void setUserId(int value){
		this.user_id = value;
	}
	
	public boolean getOK(){
		return this.ok;
	}
	
	public void setOK(boolean value){
		this.ok = value;
	}
	
	public String getDateRequest(){
		return this.date_Request;
	}
	
	public void setDateRequest(String value){
		this.date_Request = value;
	}
	
	public String getDateOF(){
		return this.date_Oformleniya;
	}
	
	public void setDateOF(String value){
		this.date_Oformleniya = value;
	}

	public void setPlace(String value){
		this.place = value;
	}
	
	public String getPlace(){
		return this.place;
	}
	
	public void setPrintableName(String value){
		this.printableName = value;
	}
	
	@Override
	public String toString(){
		return this.printableName;
	}

}
