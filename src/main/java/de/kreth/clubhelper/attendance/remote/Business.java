package de.kreth.clubhelper.attendance.remote;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.Authentication;

import de.kreth.clubhelper.attendance.data.PersonAttendance;
import de.kreth.clubhelper.data.Adress;
import de.kreth.clubhelper.data.Contact;
import de.kreth.clubhelper.data.GroupDef;
import de.kreth.clubhelper.data.OrderBy;
import de.kreth.clubhelper.data.Person;

public interface Business {

    List<Person> getPersons(OrderBy order);

    List<PersonAttendance> getAttendance(LocalDate date);

    Authentication getCurrent();

    PersonAttendance sendAttendance(PersonAttendance person, LocalDate attendanceDate, Boolean isAttendant);

    List<Contact> getContacts(Long personId);

    Adress getAdress(Long personId);

    List<GroupDef> getAllGroups();

}