package de.kreth.clubhelper.attendance.remote;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.Authentication;

import de.kreth.clubhelper.attendance.data.PersonAttendance;
import de.kreth.clubhelper.data.Person;

public interface Business {

    List<Person> getPersons();

    List<PersonAttendance> getAttendance(LocalDate date);

    Authentication getCurrent();

    PersonAttendance sendAttendance(PersonAttendance person, LocalDate attendanceDate, Boolean isAttendant);

}