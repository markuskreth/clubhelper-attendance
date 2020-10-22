package de.kreth.clubhelper.attendance.ui;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinServlet;

import de.kreth.clubhelper.attendance.data.Person;
import de.kreth.clubhelper.attendance.remote.Business;
import de.kreth.clubhelper.attendance.remote.BusinessImpl;

@Route("print")
@Push
public class PrintAttendance extends VerticalLayout {

    private static final long serialVersionUID = 1L;

    private TextField namefilter;
    private HorizontalLayout filterPanel;

    private List<Person> personList;
    private Grid<Person> grid;
    ListDataProvider<Person> provider;

    private DatePicker datePicker;

    public PrintAttendance() {
	personList = new ArrayList<>();
	add(new H1("Druck der Anwesenheitsliste"));

	namefilter = new TextField("Namenfilter");
	datePicker = new DatePicker("Anwesenheit Datum", LocalDate.now());

	filterPanel = new HorizontalLayout();
	filterPanel.add(namefilter, datePicker);

	provider = DataProvider.ofCollection(personList);
	provider.addFilter(this::filterByName);

	grid = new Grid<>();
	grid.setDataProvider(provider);
	grid.setSizeFull();
	add(filterPanel, grid);

    }

    private boolean filterByName(Person p) {
	if (namefilter.getValue() == null || namefilter.getValue().isBlank()) {
	    return true;
	}

	String filter = namefilter.getValue().trim().toLowerCase();

	return p.getPrename().toLowerCase().startsWith(filter)
		|| p.getSurname().toLowerCase().startsWith(filter);
    }

    Business getRestService() {
	return WebApplicationContextUtils.getWebApplicationContext(VaadinServlet.getCurrent().getServletContext())
		.getBean(BusinessImpl.class);
    }

}
