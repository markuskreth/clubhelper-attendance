package de.kreth.clubhelper.attendance.data;

public class Person {

    private int id;

    private String prename;

    private String surname;

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

    @Override
    public String toString() {
	return "Person [id=" + id + ", prename=" + prename + ", surname=" + surname + "]";
    }

}
