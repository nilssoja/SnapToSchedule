package com.example.snaptoschedule;

public class ScheduleItem {


	public int year;
	public int month;
	public int day;
	public int startHour;
	public int startMinute;
	public int endHour;
	public int endMinute;
	public String title;
	public String description;
	public String location;
	public String rrule;
	
	//TODO: recuring events
	//http://stackoverflow.com/questions/13268914/how-to-add-event-in-android-calendar-repeated-every-3-days
	
	
	public ScheduleItem() {
		super();
	}


	//int for year. example 2014
	public int getYear() {
		return year;
	}


	public void setYear(int year) {
		this.year = year;
	}


	//int for month note that january is 0
	public int getMonth() {
		return month;
	}


	public void setMonth(int month) {
		this.month = month;
	}


	//int for day this starts at 1 unlike month
	public int getDay() {
		return day;
	}


	public void setDay(int day) {
		this.day = day;
	}


	//int for starting hour this starts at 0 and uses military time
	public int getStartHour() {
		return startHour;
	}


	public void setStartHour(int startHour) {
		this.startHour = startHour;
	}


	//int for starting minute this starts at 0
	public int getStartMinute() {
		return startMinute;
	}


	public void setStartMinute(int startMinute) {
		this.startMinute = startMinute;
	}


	//int for ending hour this starts at 0 and uses military time
	public int getEndHour() {
		return endHour;
	}


	public void setEndHour(int endHour) {
		this.endHour = endHour;
	}


	//int for ending minute this starts at 0
	public int getEndMinute() {
		return endMinute;
	}


	public void setEndMinute(int endMinute) {
		this.endMinute = endMinute;
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


	public String getLocation() {
		return location;
	}


	public void setLocation(String location) {
		this.location = location;
	}


	public String getRrule() {
		return rrule;
	}


	public void setRrule(String rrule) {
		this.rrule = rrule;
	}
	
	
	
}
