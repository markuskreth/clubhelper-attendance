package de.kreth.clubhelper.attendance.ui;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinServlet;

import de.kreth.clubhelper.attendance.data.PersonAttendance;
import de.kreth.clubhelper.attendance.remote.Business;

@Route("")
@PageTitle("Anwesenheit")
public class AttendanceView extends VerticalLayout
	implements ValueChangeListener<ComponentValueChangeEvent<TextField, String>> {

    private static final long serialVersionUID = 1L;

    private final PersonUiList personList;

    private DatePicker date;

    public AttendanceView() {
	personList = new PersonUiList();
	createUi();
	refreshData();
    }

    private void createUi() {
	add(new H1("Anwesenheit"));

	date = new DatePicker(LocalDate.now());
	date.setLabel("Anwesenheit Datum");
	date.setRequired(true);

	TextField filter = new TextField("Filter des Vor- oder Nachnamens");
	filter.setPlaceholder("Filter nach Name...");
	filter.setClearButtonVisible(true);
//	filter.setValueChangeMode(ValueChangeMode.EAGER);

	filter.addValueChangeListener(this);

	Grid<PersonAttendance> grid = new Grid<>();
	grid.addColumn(new ComponentRenderer<>(this::attendanteComponent)).setHeader("Anwesend");
	grid.addColumn(PersonAttendance::getPrename).setHeader("Vorname");
	grid.addColumn(PersonAttendance::getSurname).setHeader("Nachname");

	grid.setDataProvider(personList.getDataProvider());

	add(date, filter, grid);
	date.addValueChangeListener(ev -> refreshData());
    }

    @Override
    public void valueChanged(ComponentValueChangeEvent<TextField, String> event) {
	personList.setFilterText(event.getValue());
    }

//    @Override
//    public void onComponentEvent(KeyPressEvent event) {
//	TextField tf = (TextField) event.getSource();
//	personList.setFilterText(tf.getValue());
//    }

    private Checkbox attendanteComponent(PersonAttendance person) {

	Checkbox box = new Checkbox();
	box.setValue(person.isAttendante());
	box.addValueChangeListener(ev -> sendPersonAttendance(person, ev));
	return box;
    }

    private void sendPersonAttendance(PersonAttendance person, ComponentValueChangeEvent<Checkbox, Boolean> ev) {
	Boolean selected = ev.getValue();
	LocalDate attendanceDate = date.getValue();
	getRestService().sendAttendance(person, attendanceDate, selected);
    }

    private void refreshData() {
	Business restService = getRestService();
	List<PersonAttendance> attendanceAsJson = restService.getAttendance(date.getValue());
	personList.setPersons(attendanceAsJson);
    }

    Business getRestService() {
	return WebApplicationContextUtils.getWebApplicationContext(VaadinServlet.getCurrent().getServletContext())
		.getBean(Business.class);
    }

}
