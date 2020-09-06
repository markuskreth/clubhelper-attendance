package de.kreth.clubhelper.attendance.ui;

import java.util.List;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import de.kreth.clubhelper.attendance.data.Person;
import de.kreth.clubhelper.attendance.remote.ClubhelperRest;

@Route("attendance")
public class MainView extends VerticalLayout {

    private static final long serialVersionUID = 1L;

    @Autowired
    private ClubhelperRest rest;

    private Grid<Person> grid;
    private List<Person> personList;

    public MainView() {
	createUi();
    }

    void refreshData() {
	personList.clear();
	personList.addAll(rest.getPersons());
    }

    private void createUi() {
	add(new H1("Dies sind die Personen"));

	Authentication authentication = SecurityContextHolder.getContext()
		.getAuthentication();
	if (!authentication.getPrincipal().equals("anonymousUser")) {
	    KeycloakPrincipal<?> principal = (KeycloakPrincipal<?>) authentication.getPrincipal();

	    KeycloakSecurityContext keycloakSecurityContext = principal.getKeycloakSecurityContext();

	    String preferredUsername = keycloakSecurityContext.getIdToken().getPreferredUsername();
	    Anchor logout = new Anchor(
		    "http://localhost:9090/auth/realms/Demo/protocol/openid-connect/logout?redirect_uri=" +
			    "http://localhost:9091/",
		    "Logout");
	    add(new HorizontalLayout(new Span(preferredUsername), logout));
	} else {
	    add(new Span("No Logged in User"));
	}

//	grid = new Grid<>(Person.class);
//	personList = new ArrayList<>();
//	ListDataProvider<Person> data = new ListDataProvider<>(personList);
//	grid.setDataProvider(data);
//	add(grid);

    }
}
