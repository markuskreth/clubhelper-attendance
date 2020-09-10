package de.kreth.clubhelper.attendance.remote;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import de.kreth.clubhelper.attendance.data.Attendance;
import de.kreth.clubhelper.attendance.data.Person;

@Service
public class ClubhelperRest {

    @Autowired
    private RestTemplate webClient;

    @Value("${resourceserver.api.url}")
    private String apiUrl;

    public List<Person> getPersons() {
	String url = apiUrl + "/person";
	Person[] list = webClient.getForObject(url, Person[].class);
	return Arrays.asList(list);
    }

    public List<Attendance> getAttendance(LocalDate date) {

	String url = apiUrl + "/attendance/" + date.format(DateTimeFormatter.ISO_DATE);

	try {
	    ResponseEntity<Attendance[]> forEntity = webClient.getForEntity(url, Attendance[].class);
	    return Arrays.asList(forEntity.getBody());
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    public Authentication getCurrent() {
	return SecurityContextHolder.getContext().getAuthentication();
    }
}
