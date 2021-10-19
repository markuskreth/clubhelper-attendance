package de.kreth.clubhelper.attendance.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexLayout.ContentAlignment;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexDirection;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.DomEvent;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.VaadinServlet;

import de.kreth.clubhelper.attendance.data.PersonAttendance;
import de.kreth.clubhelper.attendance.remote.Business;
import de.kreth.clubhelper.vaadincomponents.groupfilter.GroupFilter;
import de.kreth.clubhelper.vaadincomponents.groupfilter.GroupFilterEvent;
import de.kreth.clubhelper.vaadincomponents.groupfilter.GroupFilterListener;

@Push
@Route("")
@PWA(name = "MTV Trampolin Anwesenheit", shortName = "Anwesenheit", description = "Dies ist eine App zur Erfassung von Anwesenheiten für die Trampolingruppe des MTV Groß-Buchholz.", enableInstallPrompt = false)
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
@PageTitle("Anwesenheit")
@PreAuthorize("hasRole('ROLE_trainer')")
public class AttendanceView extends VerticalLayout
		implements ValueChangeListener<ComponentValueChangeEvent<TextField, String>>, GroupFilterListener {

	final Logger logger = LoggerFactory.getLogger(getClass());
	
	private static final long serialVersionUID = 1L;

	private String personeditorUrl;

	private final PersonUiList personList;

	private DatePicker date;

	private Label attendanceSum;
	
	private final AtomicInteger attendanceCount = new AtomicInteger();

	public AttendanceView(@Value("${personeditor.url}") String personeditorUrl) {
		this.personeditorUrl = personeditorUrl;
		LoggerFactory.getLogger(getClass()).info("Using PersonEditor URL: " + personeditorUrl);
		personList = new PersonUiList();
		createUi();
		refreshData();
		updateSum();
		logger.info(getClass().getName() + " gestartet.");
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
		filter.setValueChangeMode(ValueChangeMode.TIMEOUT);
		filter.setValueChangeTimeout(700);
		
		GroupFilter groupFilter = new GroupFilter(getRestService().getAllGroups());
		groupFilter.addListener(this);

		ComboBox<PersonSort> sorting = new ComboBox<>("Sortierung") {
			
			private static final long serialVersionUID = -3767819950249464482L;

			@Override
			public PersonSort getEmptyValue() {
				return PersonSort.None;
			}
		};
		sorting.setItems(PersonSort.values());
		sorting.setValue(PersonSort.None);
		
		Grid<PersonAttendance> grid = new Grid<>();
		Column<PersonAttendance> attendanceCol = grid.addColumn(new ComponentRenderer<>(this::attendanteComponent));
		grid.addItemClickListener(this::showItemText);
		grid.setMinHeight("400px");
		attendanceSum = new Label();
		attendanceSum.getElement().addEventListener("click", this::showText);
		FooterRow footerRow = grid.appendFooterRow();
		footerRow.getCell(attendanceCol).setComponent(attendanceSum);

		grid.setDataProvider(personList.getDataProvider());

		Button printButton = new Button(VaadinIcon.PRINT.create());
		printButton.addClickListener(e -> printButton.getUI().ifPresent(
				ui -> ui.navigate(PrintAttendance.class, date.getValue().format(DateTimeFormatter.BASIC_ISO_DATE))));
		FlexLayout dateAndPrint = new FlexLayout(date, printButton);

		dateAndPrint.setFlexDirection(FlexDirection.ROW);
		dateAndPrint.setAlignItems(Alignment.START);
		dateAndPrint.setFlexWrap(FlexWrap.WRAP);
		dateAndPrint.setAlignContent(ContentAlignment.START);
		dateAndPrint.setAlignSelf(Alignment.END, printButton);

		FlexLayout sortAndFilter = new FlexLayout(sorting, filter);

		sortAndFilter.setFlexDirection(FlexDirection.ROW);
		sortAndFilter.setAlignItems(Alignment.START);
		sortAndFilter.setFlexWrap(FlexWrap.WRAP);
		sortAndFilter.setAlignContent(ContentAlignment.START);
		sortAndFilter.setAlignSelf(Alignment.END, printButton);

		add(dateAndPrint, groupFilter, sortAndFilter, grid);

		setSizeFull();
		expand(grid);

		date.addValueChangeListener(ev -> refreshData());
		sort(PersonSort.None);
		sorting.addValueChangeListener(ev -> sort(ev.getValue()));
	}

	void sort (final PersonSort order) {
		personList.sort(p -> new PersonAttendanceComparable(p, order));
	}
	
	void showItemText(ItemClickEvent<PersonAttendance> event) {
		PersonAttendance item = event.getItem();
		String text = item.getPrename() + " " + item.getSurname();
		logger.debug("Notification für " + item);
		Notification.show(text);
	}
	
	void showText(DomEvent ev) {
		String text = ev.getSource().getText();
		logger.debug("Notification für " + ev.getSource() + ": ");
		Notification.show(text);
	}
	
	Button createEditorButton(PersonAttendance p) {
		Button b = new Button(VaadinIcon.PENCIL.create());
		b.addClickListener(ev -> this.onClick(p.getId()));
		b.getElement().setProperty("title", "Editor für " + p.getSurname() + ", " + p.getPrename());
		b.addClassName("BUTTON_LINK");
		return b;
	}

	private void onClick(Long personId) {
		logger.info("Opening Editor für Id=" + personId);
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
		StringBuilder logText = new StringBuilder();
		logText.append("Filtering by Name: " + event.getValue());
		logger.info(logText.toString());
		personList.setFilterText(event.getValue());
	}
	
	private Component attendanteComponent(PersonAttendance person) {

		Checkbox box = new Checkbox();
		box.setValue(person.isAttendante());
		box.addValueChangeListener(ev -> sendPersonAttendance(person, ev));
		Label name = new Label(person.getPrename() + " " + person.getSurname());
		HorizontalLayout layout = new HorizontalLayout(box, name);
		
		if (withEditor()) {
			ContextMenu menu = new ContextMenu(layout);			
			menu.addItem(new Button(VaadinIcon.PENCIL.create()), ev -> this.onClick(person.getId()));
		}
		
		return layout;
	}

	private void sendPersonAttendance(PersonAttendance person, ComponentValueChangeEvent<Checkbox, Boolean> ev) {
		
		Boolean selected = ev.getValue();
		LocalDate attendanceDate = date.getValue();
		logger.info("Changing Attendance Value for " + person + " to " + selected);
		try {

			PersonAttendance result = getRestService().sendAttendance(person, attendanceDate, selected);

			personList.update(result);

			if (selected.booleanValue()) {
				attendanceCount.incrementAndGet();
			} else {
				attendanceCount.decrementAndGet();
			}
			updateSum();
		} catch (Exception e) {
			logger.error("Error sending " + person, e);
			refreshData();
		}
	}

	private void refreshData() {
		Business restService = getRestService();
		List<PersonAttendance> attendanceAsJson = restService.getAttendance(date.getValue());
		personList.setPersons(attendanceAsJson);

		attendanceCount.set((int) attendanceAsJson.stream().filter(PersonAttendance::isAttendante).count());
		updateSum();
		logger.info("Refreshed View.");
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

	class PersonAttendanceComparable implements Comparable<PersonAttendanceComparable> {

		final PersonAttendance toCompare;
		final PersonSort order;

		public PersonAttendanceComparable(PersonAttendance toCompare) {
			this(toCompare, PersonSort.None);
		}

		public PersonAttendanceComparable(PersonAttendance toCompare, PersonSort order) {
			super();
			this.toCompare = toCompare;
			this.order = Objects.requireNonNull(order);
		}

		@Override
		public int compareTo(PersonAttendanceComparable o) {
			if (toCompare.isAttendante() == o.toCompare.isAttendante()) {
				switch (order) {
				case ByPrename:
					return toCompare.getPrename().compareTo(o.toCompare.getPrename());
				case BySurname:
					return toCompare.getSurname().compareTo(o.toCompare.getSurname());
				default:
					if (toCompare.getId() != null) {
						return toCompare.getId().compareTo(o.toCompare.getId());
					}
					else {
						return 0;
					}
				}
			} else {
				return Boolean.compare(o.toCompare.isAttendante(), toCompare.isAttendante());
			}
		}
		
	}
}
