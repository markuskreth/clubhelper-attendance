package de.kreth.clubhelper.attendance.data;

import java.time.LocalDate;

import org.springframework.lang.Nullable;

public class PersonAttendance {

    private int id;

    private String prename;

    private String surname;

    @Nullable
    private LocalDate onDate;

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public String getPrename() {
	return prename;
    }

    public void setPrename(String prename) {
	this.prename = prename;
    }

    public String getSurname() {
	return surname;
    }

    public void setSurname(String surname) {
	this.surname = surname;
    }

    public LocalDate getOnDate() {
	return onDate;
    }

    public boolean isAttendante() {
	return onDate != null;
    }

    public void setOnDate(LocalDate onDate) {
	this.onDate = onDate;
    }

    public static PersonAttendance createBy(Attendance attendance) {
	PersonAttendance pa = new PersonAttendance();
	Person person = attendance.getPerson();
	pa.id = person.getId();
	pa.prename = person.getPrename();
	pa.surname = person.getSurname();
	pa.onDate = attendance.getOnDate();
	return pa;
    }

    public static PersonAttendance createBy(Person person) {
	PersonAttendance pa = new PersonAttendance();
	pa.id = person.getId();
	pa.prename = person.getPrename();
	pa.surname = person.getSurname();
	pa.onDate = null;
	return pa;
    }

}
