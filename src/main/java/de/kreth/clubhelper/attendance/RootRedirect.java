package de.kreth.clubhelper.attendance;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Component
@RequestMapping
public class RootRedirect {

    @GetMapping
    public String redirectToAttendance() {
	return "redirect:/attendance";
    }
}
