package de.kreth.clubhelper.attendance.remote;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import de.kreth.clubhelper.attendance.data.Attendance;
import de.kreth.clubhelper.attendance.data.Person;
import de.kreth.clubhelper.attendance.data.PersonAttendance;

@Service
public class BusinessImpl implements Business {

    @Autowired
    private RestTemplate webClient;

    @Value("${resourceserver.api.url}")
    private String apiUrl;

    @Override
    public List<Person> getPersons() {
	String url = apiUrl + "/person";
	Person[] list = webClient.getForObject(url, Person[].class);
	return Arrays.asList(list);
    }

    @Override
    public List<PersonAttendance> getAttendance(LocalDate date) {

	String url = apiUrl + "/attendance/" + date.format(DateTimeFormatter.ISO_DATE);

	try {
	    List<Person> persons = getPersons();
	    ResponseEntity<Attendance[]> forEntity = webClient.getForEntity(url, Attendance[].class);

	    Attendance[] body = forEntity.getBody();
	    List<PersonAttendance> result = new ArrayList<>();
	    for (Attendance attendance : body) {
		persons.remove(attendance.getPerson());
		result.add(PersonAttendance.createBy(attendance));
	    }

	    result.addAll(persons.stream()
		    .map(PersonAttendance::createBy)
		    .collect(Collectors.toList()));

	    return result;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    @Override
    public Authentication getCurrent() {
	return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public void sendAttendance(PersonAttendance person, LocalDate attendanceDate, Boolean isAttendant) {

    }
}
