package ua.rokytne.bakery.orm;

public class User {

	private int id;
	private String username;
	private String password;
	private String realName;
	private String photo;
	private int accessLevel;
	
	public User(String username, String password, String realName, String photo, int accessLevel){
		this(-1, username, password, realName, photo, accessLevel);
	}
	
	public User(int id, String username, String password, String realName, String photo, int accessLevel){
		setId(id);
		setUsername(username);
		setPassword(password);
		setRealName(realName);
		setPhoto(photo);
		setAccessLevel(accessLevel);
	}
	
	public void setId(int value){
		this.id = value;
	}
	
	public void setUsername(String value){
		this.username = value;
	}
	
	public void setPassword(String value){
		this.password = value;
	}
	
	public void setRealName(String value){
		this.realName = value;
	}
	
	public void setPhoto(String value){
		this.photo = value;
	}
	
	public void setAccessLevel(int value){
		this.accessLevel = value;
	}
	
	////////////////////////////////
	public int getId(){
		return this.id;
	}
	
	public String getUsername(){
		return this.username;
	}
	
	public String getPassword(){
		return this.password;
	}
	
	public String getRealName(){
		return this.realName;
	}
	
	public String getPhoto(){
		return this.photo;
	}
	
	public int getAccessLevel(){
		return this.accessLevel;
	}

	@Override
	public String toString(){
		return this.username;
	}
}
