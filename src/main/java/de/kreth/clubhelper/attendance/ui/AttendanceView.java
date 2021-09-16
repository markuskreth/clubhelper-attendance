package de.kreth.clubhelper.attendance.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.KeyUpEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.FooterRow.FooterCell;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.DomEvent;
import com.vaadin.flow.dom.DomEventListener;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.VaadinServlet;

import de.kreth.clubhelper.attendance.data.PersonAttendance;
import de.kreth.clubhelper.attendance.remote.Business;
import de.kreth.clubhelper.vaadincomponents.groupfilter.GroupFilter;
import de.kreth.clubhelper.vaadincomponents.groupfilter.GroupFilterEvent;
import de.kreth.clubhelper.vaadincomponents.groupfilter.GroupFilterListener;

@Route("")
@PWA(name = "MTV Trampolin Anwesenheit", shortName = "Anwesenheit", description = "Dies ist eine App zur Erfassung von Anwesenheiten für die Trampolingruppe des MTV Groß-Buchholz.", enableInstallPrompt = false)
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
@PageTitle("Anwesenheit")
@PreAuthorize("hasRole('ROLE_trainer')")
public class AttendanceView extends VerticalLayout
		implements ValueChangeListener<ComponentValueChangeEvent<TextField, String>>, GroupFilterListener {

	private static final long serialVersionUID = 1L;

	private String personeditorUrl;

	private final PersonUiList personList;

	private DatePicker date;

	private Label attendanceSum;
	private UpdateFilterHandler currentHandler;
	
	private final AtomicInteger attendanceCount = new AtomicInteger();

	public AttendanceView(@Value("${personeditor.url}") String personeditorUrl) {
		this.personeditorUrl = personeditorUrl;
		LoggerFactory.getLogger(getClass()).info("Using PersonEditor URL: " + personeditorUrl);
		personList = new PersonUiList();
		createUi();
		refreshData();
		updateSum();
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
		//		filter.addValueChangeListener(this);
		filter.addKeyUpListener(this::filterTyped);
		GroupFilter groupFilter = new GroupFilter(getRestService().getAllGroups());
		groupFilter.addListener(this);

		Grid<PersonAttendance> grid = new Grid<>();
		Column<PersonAttendance> attendanceCol = grid.addColumn(new ComponentRenderer<>(this::attendanteComponent))
				.setHeader("Anwesend").setFlexGrow(2).setSortable(true);
		grid.addColumn(PersonAttendance::getPrename).setHeader("Vorname").setFlexGrow(3).setSortable(true);
		grid.addColumn(PersonAttendance::getSurname).setHeader("Nachname").setFlexGrow(3).setSortable(true);
		if (withEditor()) {
			grid.addComponentColumn(this::createEditorButton).setFlexGrow(1);
		}
		
		grid.addItemClickListener(this::showItemText);

		attendanceSum = new Label();
		attendanceSum.getElement().addEventListener("click", this::showText);
		FooterRow footerRow = grid.appendFooterRow();
		footerRow.getCell(attendanceCol).setComponent(attendanceSum);

		grid.setDataProvider(personList.getDataProvider());

		Button printButton = new Button(VaadinIcon.PRINT.create());
		printButton.addClickListener(e -> printButton.getUI().ifPresent(
				ui -> ui.navigate(PrintAttendance.class, date.getValue().format(DateTimeFormatter.BASIC_ISO_DATE))));
		HorizontalLayout components = new HorizontalLayout(date, filter, printButton);
		components.setAlignSelf(Alignment.END, printButton);

		add(components, groupFilter, grid);

		setHeight("100%");
		grid.setHeight("100%");

		date.addValueChangeListener(ev -> refreshData());
	}

	void showItemText(ItemClickEvent<PersonAttendance> event) {
		PersonAttendance item = event.getItem();
		String text = item.getPrename() + item.getSurname();
		Notification.show(text);
	}
	
	void showText(DomEvent ev) {
		String text = ev.getSource().getText();
		Notification.show(text);
	}
	
	Button createEditorButton(PersonAttendance p) {
		Button b = new Button(VaadinIcon.PENCIL.create());
		b.addClickListener(ev -> this.onClick(ev, p.getId()));
		b.getElement().setProperty("title", "Editor für " + p.getSurname() + ", " + p.getPrename());
		b.addClassName("BUTTON_LINK");
		return b;
	}

	private void onClick(ClickEvent<Button> ev, Long personId) {
		getUI().ifPresent(ui -> {
			Page page = ui.getPage();
			String url = editUrlForPersonId(personId);
			page.open(url, "_self");
		});
	}

	private String editUrlForPersonId(Long personId) {
		return this.personeditorUrl + "/" + personId;
	}

	private boolean withEditor() {
		return !"NONE".equals(personeditorUrl);
	}

	@Override
	public void valueChanged(ComponentValueChangeEvent<TextField, String> event) {
		personList.setFilterText(event.getValue());
	}

	void filterTyped(KeyUpEvent event) {
		TextField field = (TextField) event.getSource();
		if (currentHandler != null) {
			currentHandler.execute.set(false);
		}
		currentHandler = new UpdateFilterHandler(field.getValue()).execute();
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

		if (selected.booleanValue()) {
			attendanceCount.incrementAndGet();
		} else {
			attendanceCount.decrementAndGet();
		}
		updateSum();
	}

	private void refreshData() {
		Business restService = getRestService();
		List<PersonAttendance> attendanceAsJson = restService.getAttendance(date.getValue());
		personList.setPersons(attendanceAsJson);

		attendanceCount.set((int) attendanceAsJson.stream().filter(PersonAttendance::isAttendante).count());
		updateSum();
	}

	private void updateSum() {
		attendanceSum.setText("Anwesend: " + attendanceCount.get());
	}

	Business getRestService() {
		return WebApplicationContextUtils.getWebApplicationContext(VaadinServlet.getCurrent().getServletContext())
				.getBean(Business.class);
	}

	@Override
	public void groupFilterChange(GroupFilterEvent event) {
		personList.setFilterGroups(event.getFilteredGroups());
	}

	class UpdateFilterHandler extends Thread {
		
		final AtomicBoolean execute = new AtomicBoolean();
		
		private final String text;
		
		public UpdateFilterHandler(String text) {
			super();
			this.text = text;
		}
		
		public UpdateFilterHandler execute() {
			start();
			return this;
		}

		@Override
		public void run() {
			
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				return;
			}
			if (execute.get()) {
				personList.setFilterText(text);
			}
		}
		
	}
}
