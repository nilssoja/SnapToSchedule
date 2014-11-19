package com.example.snaptoschedule;

public class ScheduleItem {

	public String startMillis;
	public String endMillis;
	public String title;
	public String description;
	
	//TODO: recuring events
	//http://stackoverflow.com/questions/13268914/how-to-add-event-in-android-calendar-repeated-every-3-days
	
	
	public ScheduleItem() {
		super();
	}


	//startMillis should be format of "2012, 9, 14, 8, 45"
	// year, month, day, hrs, min
	public String getStartMillis() {
		return startMillis;
	}


	public void setStartMillis(String startMillis) {
		this.startMillis = startMillis;
	}


	//endMillis should be format of "2012, 9, 14, 8, 45"
	// year, month, day, hrs, min
	public String getEndMillis() {
		return endMillis;
	}


	public void setEndMillis(String endMillis) {
		this.endMillis = endMillis;
	}


	//This is the name of the class. example: "Computer Programming"
	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	//This will contain any data like room and building numbers if it exists
	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
}
