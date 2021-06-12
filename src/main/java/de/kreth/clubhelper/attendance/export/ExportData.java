package de.kreth.clubhelper.attendance.export;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.kreth.clubhelper.attendance.data.PersonAttendance;
import de.kreth.clubhelper.attendance.remote.Business;
import de.kreth.clubhelper.data.Adress;
import de.kreth.clubhelper.data.Contact;

public class ExportData {

    private List<PersonAttendance> data;
    private Map<Long, List<Contact>> contacts;
    private Map<Long, Adress> adress;
    private LocalDate onDate;

    private ExportData(LocalDate onDate) {
	this.onDate = onDate;
    }

    private void setData(List<PersonAttendance> data) {
	this.data = data;
    }

    private void addContacts(Long personId, List<Contact> contact) {
	if (contacts == null) {
	    contacts = new HashMap<Long, List<Contact>>();
	}
	contacts.put(personId, contact);
    }

    private void setAdress(Long personId, Adress adress) {
	if (this.adress == null) {
	    this.adress = new HashMap<Long, Adress>();
	}
	this.adress.put(personId, adress);
    }

    public LocalDate getDate() {
	return onDate;
    }

    public List<PersonAttendance> getData() {
	return Collections.unmodifiableList(data);
    }

    public List<Contact> getContactFor(PersonAttendance att) {
	return contacts.get(att.getId());
    }

    public Adress getAdressFor(PersonAttendance att) {
	return adress.get(att.getId());
    }

    public static ExportData createFor(LocalDate onDate, Business business) {
	List<PersonAttendance> attendance = business.getAttendance(onDate);

	ExportData data = new ExportData(onDate);
	data.setData(attendance);

	for (Iterator<PersonAttendance> iter = attendance.iterator(); iter.hasNext();) {
	    PersonAttendance personAttendance = iter.next();
	    if (personAttendance.getOnDate() == null) {
		iter.remove();
		continue;
	    }
	    data.addContacts(personAttendance.getId(), business.getContacts(personAttendance.getId()));
	    data.setAdress(personAttendance.getId(), business.getAdress(personAttendance.getId()));
	}
	return data;
    }
}
