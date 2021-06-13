package de.kreth.clubhelper.attendance.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.function.SerializablePredicate;

import de.kreth.clubhelper.attendance.data.PersonAttendance;
import de.kreth.clubhelper.data.GroupDef;

public class PersonUiList implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<PersonAttendance> persons;
    private final List<GroupDef> filteredGroups = new ArrayList<>();
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
	boolean filterTextMatches = filterText == null
		|| filterText.trim().isEmpty()
		|| person.getPrename().toLowerCase().contains(filterText)
		|| person.getSurname().toLowerCase().contains(filterText);

	boolean filterGroupMatches = false;
	for (GroupDef groupDef : filteredGroups) {
	    if (person.hasGroup(groupDef)) {
		filterGroupMatches = true;
		break;
	    }
	}

	return filterTextMatches && filterGroupMatches;
    }

    public DataProvider<PersonAttendance, ?> getDataProvider() {
	return ofCollection;
    }

    public void setFilterGroups(List<GroupDef> filteredGroups) {
	this.filteredGroups.clear();
	this.filteredGroups.addAll(filteredGroups);
	ofCollection.refreshAll();
    }

}
