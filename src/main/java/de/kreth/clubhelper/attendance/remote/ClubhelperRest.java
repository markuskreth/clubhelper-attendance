package de.kreth.clubhelper.attendance.remote;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.kreth.clubhelper.attendance.data.Person;

@Service
public class ClubhelperRest {

//    @Autowired
//    private RestTemplate webClient;

    @Value("${resourceserver.api.url}")
    private String fooApiUrl;

    public List<Person> getPersons() {
	String url = fooApiUrl + "/person";
//	Person[] list = webClient.getForObject(url, Person[].class);
	return Arrays.asList();
    }
}
