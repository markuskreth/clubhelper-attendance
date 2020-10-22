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
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + id;
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
	Person other = (Person) obj;
	if (id != other.id)
	    return false;
	return true;
    }

    @Override
    public String toString() {
	return "Person [id=" + id + ", prename=" + prename + ", surname=" + surname + "]";
    }

}
