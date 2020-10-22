package de.kreth.clubhelper.attendance.ui;

import java.time.LocalDate;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route
@PageTitle("Value Change Tests")
public class ValueChangeTest extends VerticalLayout {

    private static final long serialVersionUID = 1L;
    private TextArea eventProtocoll;

    public ValueChangeTest() {
	eventProtocoll = new TextArea();
	DatePicker dateChanger = new DatePicker();
	dateChanger.addValueChangeListener(new DateChangeListener());
	TextField textField = new TextField(this::appendValueLine);
	add(dateChanger, textField, eventProtocoll);
    }

    private void appendValueLine(ComponentValueChangeEvent<?, ?> event) {
	eventProtocoll.setValue(eventProtocoll.getValue() + System.lineSeparator()
		+ event.getSource().getClass().getSimpleName() + ": " + event.getValue());
    }

    private class DateChangeListener
	    implements ValueChangeListener<ComponentValueChangeEvent<DatePicker, LocalDate>> {

	private static final long serialVersionUID = 1L;

	@Override
	public void valueChanged(ComponentValueChangeEvent<DatePicker, LocalDate> event) {
	    appendValueLine(event);
	}

    }

}
