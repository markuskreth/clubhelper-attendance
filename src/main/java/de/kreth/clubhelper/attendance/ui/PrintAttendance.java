package de.kreth.clubhelper.attendance.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinServlet;

import de.kreth.clubhelper.attendance.export.ExportData;
import de.kreth.clubhelper.attendance.export.Exporter;
import de.kreth.clubhelper.attendance.remote.Business;
import de.kreth.clubhelper.attendance.remote.BusinessImpl;

@Route("print")
@Push
@PreAuthorize("hasRole('ROLE_trainer')")
public class PrintAttendance extends VerticalLayout implements HasUrlParameter<String>, BeforeEnterObserver {

    private static final long serialVersionUID = 1L;
    private LocalDate onDate;

    public PrintAttendance() {
	add(new H1("Druck der Anwesenheitsliste"));
    }

    Business getRestService() {
	return WebApplicationContextUtils.getWebApplicationContext(VaadinServlet.getCurrent().getServletContext())
		.getBean(BusinessImpl.class);
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
	onDate = LocalDate.parse(parameter, DateTimeFormatter.BASIC_ISO_DATE);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

	HorizontalLayout exporterLayout = new HorizontalLayout();

	add(exporterLayout);

	ExportData createFor = ExportData.createFor(onDate, getRestService());

	List<Exporter> exporters = Exporter.getExporters();
	for (Exporter exporter : exporters) {
	    Button exportButton = new Button(exporter.getName(), VaadinIcon.DOWNLOAD.create());
	    StreamResource resource = exporter.asResource(createFor);
	    Anchor anchor = new Anchor(resource, null);
	    anchor.getElement().setAttribute("download", true);
	    anchor.add(exportButton);
	    exporterLayout.add(anchor);
	}
    }

}
