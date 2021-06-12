package de.kreth.clubhelper.attendance.export;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.slf4j.Logger;

import com.vaadin.flow.server.StreamResource;

public interface Exporter {

    public enum Style {
	CORONA_CSV
    }

    String getName();

    String getFileName();

    StreamResource asResource(ExportData data);

    public static List<Exporter> getExporters() {
	Logger logger = org.slf4j.LoggerFactory.getLogger(Exporter.class);
	Reflections reflections = new Reflections(Exporter.class, SubTypesScanner.class);
	Set<Class<? extends Exporter>> subTypes = reflections.getSubTypesOf(Exporter.class);
	List<Exporter> exporters = new ArrayList<Exporter>();

	for (Class<? extends Exporter> class1 : subTypes) {
	    try {
		exporters.add(class1.getDeclaredConstructor().newInstance());
		logger.info("Found Exporter: " + class1.getName());
	    } catch (ReflectiveOperationException | IllegalArgumentException
		    | SecurityException e) {
		logger.error("Error creating Exporter type: " + class1.getName(), e);
	    }
	}

	return exporters;
    }
}
