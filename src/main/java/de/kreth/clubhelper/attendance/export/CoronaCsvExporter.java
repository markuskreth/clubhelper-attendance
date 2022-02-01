package de.kreth.clubhelper.attendance.export;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.StreamResourceWriter;
import com.vaadin.flow.server.VaadinSession;

import de.kreth.clubhelper.attendance.data.PersonAttendance;
import de.kreth.clubhelper.data.Adress;
import de.kreth.clubhelper.data.Contact;

class CoronaCsvExporter implements Exporter {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private ExportData data;

    @Override
    public String getName() {
	return "Corona CSV Exporter";
    }

    @Override
    public StreamResource asResource(ExportData data) {
	this.data = data;
	return new StreamResource(getFileName(), new StreamResourceWriter() {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void accept(OutputStream stream, VaadinSession session) throws IOException {
	    	
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(stream, StandardCharsets.UTF_8));
		writeHead(out);

		for (PersonAttendance att : data.getData()) {
		    List<Contact> contacts = data.getContactFor(att);
		    Adress adress = data.getAdressFor(att);

		    StringBuilder line = new StringBuilder();
		    line.append(att.getPrename()).append(";");
		    line.append(att.getSurname()).append(";");
		    Optional<Contact> email = contacts.stream().filter(CoronaCsvExporter::filterEmail).findFirst();
		    Optional<Contact> phone = contacts.stream().filter(CoronaCsvExporter::filterPhone).findFirst();

		    if (email.isPresent()) {
			line.append(email.get().getValue()).append(";");
		    } else {
			line.append("E-Mail unbekannt").append(";");
			logger.warn("E-Mail unknown for " + att);
		    }
		    if (phone.isPresent()) {
			line.append(phone.get().getValue()).append(";");
		    } else {
			line.append("Telefon unbekannt").append(";");
			logger.warn("Phone unknown for " + att);
		    }
		    if (adress != null) {
			line.append(adress.getAdress1()).append(",")
				.append(adress.getPlz()).append(",")
				.append(adress.getCity());
		    } else {
			line.append("Adresse unbekannt");
			logger.warn("Adress unknown for " + att);
		    }
		    out.append(line);
		    out.newLine();
		}
		out.flush();
	    }
	});
    }

    static boolean filterEmail(Contact contact) {
	return "Email".equals(contact.getType());
    }

    static boolean filterPhone(Contact contact) {
	return "Mobile".equals(contact.getType()) || "Telefon".equals(contact.getType());
    }

    LocalDate getDate() {
	return data.getDate();
    }

    protected void writeHead(BufferedWriter out) throws IOException {
	out.append("Anwesenheit Trampolin am " + getDate());
	out.newLine();
    }

    @Override
    public String getFileName() {

	return "anwesenheit_" + getDate().toString() + ".csv";
    }

}
