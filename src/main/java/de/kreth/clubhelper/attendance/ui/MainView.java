package de.kreth.clubhelper.attendance.ui;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinServlet;

import de.kreth.clubhelper.attendance.data.Person;
import de.kreth.clubhelper.attendance.remote.ClubhelperRest;

@Route("attendance")
public class MainView extends VerticalLayout {

    private static final long serialVersionUID = 1L;

    private Grid<Person> grid;
    private List<Person> personList;

    public MainView() {
	createUi();
	refreshData();
    }

    void refreshData() {
	personList.clear();
	personList.addAll(getRestService().getPersons());
    }

    private void createUi() {
	add(new H1("Dies sind die Personen"));

	grid = new Grid<>(Person.class);
	personList = new ArrayList<>();
	ListDataProvider<Person> data = new ListDataProvider<>(personList);
	grid.setDataProvider(data);
	add(grid);

    }

    private ClubhelperRest getRestService() {
	return WebApplicationContextUtils.getWebApplicationContext(VaadinServlet.getCurrent().getServletContext())
		.getBean(ClubhelperRest.class);
    }
}
