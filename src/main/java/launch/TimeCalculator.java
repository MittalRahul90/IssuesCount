package main.java.launch;

import java.util.Calendar;
import java.util.Date;

 
public class TimeCalculator {

	//Calculate the time for less than 24 hours open issues
	public Date calculate24HourTime() {
		

		Calendar cal = Calendar.getInstance();
	    cal.setTime(new Date());
	    cal.add(Calendar.DAY_OF_MONTH, -1); // get the last 24 hour time
	    return cal.getTime();  
		
	}
	
	//Calculate the time for greater than 24 hours and less than 7 days open issues
	public Date calculateSevenDayTime() {

		Calendar cal = Calendar.getInstance();
	    cal.setTime(new Date());
	    cal.add(Calendar.DAY_OF_MONTH, -7); //get the last 7th day's time
	    return cal.getTime();  
		
	}
	
}
