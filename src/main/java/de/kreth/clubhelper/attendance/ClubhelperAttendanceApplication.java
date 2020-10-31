package de.kreth.clubhelper.attendance;

import java.util.Locale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;

@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
public class ClubhelperAttendanceApplication {

    public static void main(String[] args) {
	Locale.setDefault(Locale.GERMANY);
	SpringApplication.run(ClubhelperAttendanceApplication.class, args);
    }

}
