package de.kreth.clubhelper.attendance.data;

import java.time.LocalDate;

import org.springframework.lang.Nullable;

import de.kreth.clubhelper.data.Attendance;
import de.kreth.clubhelper.data.Person;

public class PersonAttendance {

    private Long id;

    private String prename;

    private String surname;

    @Nullable
    private LocalDate onDate;

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
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

    @Override
    public String toString() {
	return "PersonAttendance [id=" + id + ", prename=" + prename + ", surname=" + surname + ", onDate=" + onDate
		+ "]";
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

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((id == null) ? 0 : id.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	PersonAttendance other = (PersonAttendance) obj;
	if (id == null) {
	    if (other.id != null)
		return false;
	} else if (!id.equals(other.id))
	    return false;
	return true;
    }

}
