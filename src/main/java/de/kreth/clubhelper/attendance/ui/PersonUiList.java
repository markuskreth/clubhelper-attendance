package de.kreth.clubhelper.attendance.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.function.SerializablePredicate;

import de.kreth.clubhelper.attendance.data.PersonAttendance;

public class PersonUiList implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<PersonAttendance> persons;
    private String filterText;
    private ConfigurableFilterDataProvider<PersonAttendance, Void, SerializablePredicate<PersonAttendance>> ofCollection;

    public PersonUiList() {
	persons = new ArrayList<>();
	ofCollection = DataProvider.ofCollection(persons).withConfigurableFilter();
	ofCollection.setFilter(this::matches);
    }

    public void setPersons(Collection<PersonAttendance> update) {
	persons.clear();
	persons.addAll(update);
	ofCollection.refreshAll();
    }

    public void update(PersonAttendance result) {
	int index = persons.indexOf(result);
	persons.remove(index);
	persons.add(index, result);

	ofCollection.refreshItem(result);
    }

    public String getFilterText() {
	return filterText;
    }

    public void setFilterText(String filterText) {
	this.filterText = filterText.toLowerCase();
	ofCollection.refreshAll();
    }

    private boolean matches(PersonAttendance person) {
	return filterText == null
		|| filterText.trim().isEmpty()
		|| person.getPrename().toLowerCase().contains(filterText)
		|| person.getSurname().toLowerCase().contains(filterText);
    }

    public DataProvider<PersonAttendance, ?> getDataProvider() {
	return ofCollection;
    }

}
