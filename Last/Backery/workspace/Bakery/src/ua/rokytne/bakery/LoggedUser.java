package ua.rokytne.bakery;

import java.util.Date;

import ua.rokytne.bakery.orm.User;

public class LoggedUser {


	public static int STATUS = 0;
	// 0 - UNLOGGED
	// 1 - LOGGED
	// 2 - GONE AWAY
	
	public static int ID = 1;
	public static int ROLE = 0;
	public static Date begin = new Date();
	public static String name = "UNNAMED USER";
	public static String username = "UNNAMED USER";
	//private User user;
	
	public LoggedUser(User user){
		//this.user = user;
		ID = user.getId();
		begin = new Date();
		name  = user.getRealName();
		ROLE = user.getAccessLevel(); 
		STATUS = 1;
		username = user.getUsername();
		Main.mainFrame.enable();
		Main.insertLog("Увійшов до системи");
	}
	
}
