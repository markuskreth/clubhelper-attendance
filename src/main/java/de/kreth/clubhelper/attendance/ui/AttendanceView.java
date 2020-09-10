package de.kreth.clubhelper.attendance.ui;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.AbstractDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinServlet;

import de.kreth.clubhelper.attendance.data.Attendance;
import de.kreth.clubhelper.attendance.data.Person;
import de.kreth.clubhelper.attendance.remote.ClubhelperRest;

@Route("attendance")
@PageTitle("Anwesenheit")
public class AttendanceView extends VerticalLayout {

    private static final long serialVersionUID = 1L;

    private List<Person> personList;

    public AttendanceView() {
	createUi();
	refreshData();
    }

    private void createUi() {
	add(new H1("Dies sind die Personen"));

	Grid<Person> grid = new Grid<>(Person.class);
	personList = new ArrayList<>();
	grid.setDataProvider(new AttendanceDataProvider());
	add(grid);
    }

    void refreshData() {
	personList.clear();
	ClubhelperRest restService = getRestService();
	List<Attendance> attendanceAsJson = restService.getAttendance(LocalDate.of(2018, 1, 17));

	List<Person> persons = attendanceAsJson.stream()
		.map(Attendance::getPerson)
		.collect(Collectors.toList());

	personList.addAll(persons);
    }

    ClubhelperRest getRestService() {
	return WebApplicationContextUtils.getWebApplicationContext(VaadinServlet.getCurrent().getServletContext())
		.getBean(ClubhelperRest.class);
    }

    class AttendanceDataProvider extends AbstractDataProvider<Person, Void> {

	private static final long serialVersionUID = 1L;

	@Override
	public boolean isInMemory() {
	    return true;
	}

	@Override
	public int size(Query<Person, Void> query) {
	    return personList.size();
	}

	@Override
	public Stream<Person> fetch(Query<Person, Void> query) {
	    int offset = query.getOffset();
	    int limit = query.getLimit();
	    if (limit <= 0) {
		limit = personList.size();
	    }
	    return personList.subList(offset, limit).stream();
	}

    }
}
