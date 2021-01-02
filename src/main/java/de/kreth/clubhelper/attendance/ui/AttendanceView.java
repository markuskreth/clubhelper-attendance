package de.kreth.clubhelper.attendance.ui;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.VaadinServlet;

import de.kreth.clubhelper.attendance.data.PersonAttendance;
import de.kreth.clubhelper.attendance.remote.Business;

@Route("")
@PWA(name = "MTV Trampolin Anwesenheit", shortName = "Anwesenheit", description = "Dies ist eine App zur Erfassung von Anwesenheiten für die Trampolingruppe des MTV Groß-Buchholz.", enableInstallPrompt = false)
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
@PageTitle("Anwesenheit")
public class AttendanceView extends VerticalLayout
	implements ValueChangeListener<ComponentValueChangeEvent<TextField, String>> {

    private static final long serialVersionUID = 1L;

    private String personeditorUrl;

    private final PersonUiList personList;

    private DatePicker date;

    public AttendanceView(@Value("${personeditor.url}") String personeditorUrl) {
	this.personeditorUrl = personeditorUrl;
	personList = new PersonUiList();
	createUi();
	refreshData();
    }

    private void createUi() {
	add(new H1("Anwesenheit"));

	date = new DatePicker(LocalDate.now());
	date.setLabel("Anwesenheit Datum");
	date.setRequired(true);
	date.setLocale(Locale.getDefault());

	TextField filter = new TextField("Filter des Vor- oder Nachnamens");
	filter.setPlaceholder("Filter nach Name...");
	filter.setClearButtonVisible(true);

	filter.addValueChangeListener(this);

	Grid<PersonAttendance> grid = new Grid<>();
	grid.addColumn(new ComponentRenderer<>(this::attendanteComponent)).setHeader("Anwesend").setFlexGrow(2)
		.setSortable(true);
	grid.addColumn(PersonAttendance::getPrename).setHeader("Vorname").setFlexGrow(3).setSortable(true);
	grid.addColumn(PersonAttendance::getSurname).setHeader("Nachname").setFlexGrow(3).setSortable(true);
	if (withEditor()) {
	    grid.addComponentColumn(this::createEditorButton).setFlexGrow(1);
	}

	grid.setDataProvider(personList.getDataProvider());

	add(date, filter, grid);
	date.addValueChangeListener(ev -> refreshData());
    }

    Button createEditorButton(PersonAttendance p) {
	Button b = new Button(VaadinIcon.PENCIL.create());
	b.addClickListener(ev -> this.onClick(ev, p.getId()));
	return b;
    }

    private void onClick(ClickEvent<Button> ev, Long personId) {
	getUI().ifPresent(ui -> {
	    Page page = ui.getPage();
	    String url = this.personeditorUrl + "/" + personId;
	    page.open(url, "_self");
	});
    }

    private boolean withEditor() {
	return personeditorUrl != null;
    }

    @Override
    public void valueChanged(ComponentValueChangeEvent<TextField, String> event) {
	personList.setFilterText(event.getValue());
    }

    private Checkbox attendanteComponent(PersonAttendance person) {

	Checkbox box = new Checkbox();
	box.setValue(person.isAttendante());
	box.addValueChangeListener(ev -> sendPersonAttendance(person, ev));
	return box;
    }

    private void sendPersonAttendance(PersonAttendance person, ComponentValueChangeEvent<Checkbox, Boolean> ev) {
	Boolean selected = ev.getValue();
	LocalDate attendanceDate = date.getValue();
	PersonAttendance result = getRestService().sendAttendance(person, attendanceDate, selected);
	personList.update(result);
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
