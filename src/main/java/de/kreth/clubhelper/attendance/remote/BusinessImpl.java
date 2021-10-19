package de.kreth.clubhelper.attendance.remote;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import de.kreth.clubhelper.attendance.data.PersonAttendance;
import de.kreth.clubhelper.data.Adress;
import de.kreth.clubhelper.data.Attendance;
import de.kreth.clubhelper.data.Contact;
import de.kreth.clubhelper.data.GroupDef;
import de.kreth.clubhelper.data.Person;

@Service
public class BusinessImpl implements Business {

	protected Logger logger = LoggerFactory.getLogger(getClass());

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
	public PersonAttendance sendAttendance(PersonAttendance person, LocalDate attendanceDate, Boolean isAttendant) {

		if (isAttendant != null && isAttendant.booleanValue()) {
			String url = apiUrl + "/attendance/for/" + person.getId();
			Attendance result = webClient.postForObject(url, attendanceDate, Attendance.class);
			if (!person.getId().equals(result.getPerson().getId())) {
				throw new IllegalStateException("Wrong person Id returned");
			}
			return PersonAttendance.createBy(result);
		} else {
			String url = apiUrl + "/attendance/" + person.getId() + "/"
					+ attendanceDate.format(DateTimeFormatter.ISO_DATE);
			webClient.delete(url);
			person.setOnDate(null);
			return person;
		}
	}

	@Override
	public List<PersonAttendance> getAttendance(LocalDate date) {

		String url = apiUrl + "/attendance/" + date.format(DateTimeFormatter.ISO_DATE);

		try {
			List<Person> persons = new ArrayList<>(getPersons());
			Attendance[] body = webClient.getForObject(url, Attendance[].class);

			List<PersonAttendance> result = new ArrayList<>();
			for (Attendance attendance : body) {
				persons.remove(attendance.getPerson());
				result.add(PersonAttendance.createBy(attendance));
			}

			result.addAll(persons.stream().map(PersonAttendance::createBy).collect(Collectors.toList()));

			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<Contact> getContacts(Long personId) {
		String url = apiUrl + "/contact/for/" + personId;
		Contact[] contactArr = webClient.getForObject(url, Contact[].class);

		return Arrays.asList(contactArr);
	}

	@Override
	public Adress getAdress(Long personId) {
		String url = apiUrl + "/adress/for/" + personId;
		Adress[] arr = webClient.getForObject(url, Adress[].class);

		return arr != null && arr.length > 0 ? arr[0] : null;
	}

	@Override
	public Authentication getCurrent() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	@Override
	public List<GroupDef> getAllGroups() {
		List<GroupDef> allGroups = new ArrayList<GroupDef>();

		String url = apiUrl + "/group";
		GroupDef[] arr = webClient.getForObject(url, GroupDef[].class);
		allGroups.addAll(Arrays.asList(arr));
		return allGroups;

	}

}
