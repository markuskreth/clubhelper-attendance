package de.kreth.clubhelper.attendance.data;

import java.time.LocalDate;

public class Attendance {

    private int id;

    private LocalDate onDate;

    private Person person;

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public LocalDate getOnDate() {
	return onDate;
    }

    public void setOnDate(LocalDate onDate) {
	this.onDate = onDate;
    }

    public Person getPerson() {
	return person;
    }

    public void setPerson(Person person) {
	this.person = person;
    }

}
