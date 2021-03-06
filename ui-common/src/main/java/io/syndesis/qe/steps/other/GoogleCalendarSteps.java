package io.syndesis.qe.steps.other;

import static org.assertj.core.api.Assertions.assertThat;

import io.syndesis.qe.pages.integrations.editor.add.connection.actions.google.EventForm;
import io.syndesis.qe.pages.integrations.editor.add.connection.actions.google.GetSpecificEvent;
import io.syndesis.qe.steps.CommonSteps;
import io.syndesis.qe.utils.DatatableUtils;
import io.syndesis.qe.utils.GoogleCalendarUtils;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GoogleCalendarSteps {
    @Autowired
    private GoogleCalendarUtils gcu;

    @When("^fill in get specific event form using account \"([^\"]*)\", calendar \"([^\"]*)\" and event \"([^\"]*)\"$")
    public void fillInGetSpecificEventFormUsingCalendarAndEventSummary(String account, String calendarSummary, String eventSummary) throws Throwable {
        String aliasedCalendarSummary = gcu.getAliasedCalendarName(calendarSummary);
        GetSpecificEvent getSpecificEvent = new GetSpecificEvent();
        String calendarId = gcu.getPreviouslyCreatedCalendar(account, aliasedCalendarSummary).getId();
        String eventId = gcu.getEventBySummary(account, calendarId, eventSummary).getId();
        getSpecificEvent.fillEventInput(aliasedCalendarSummary, eventId);
    }

    @When("^fill in create event form using calendar \"([^\"]*)\", summary \"([^\"]*)\" and description \"([^\"]*)\"$")
    public void fillInCreateEventFormUsingCalendarSummaryAndDescription(String calendar, String summary, String description) throws Throwable {
        new EventForm()
            .setCalendarName(gcu.getAliasedCalendarName(calendar))
            .setTitle(summary)
            .setDescription(description)
            .fill();
    }

    @When("^fill in update event form using calendar \"([^\"]*)\", summary \"([^\"]*)\" and description \"([^\"]*)\" for user \"([^\"]*)\"$")
    public void fillInUpdateEventFormUsingCalendarSummaryAndDescription(String calendarName, String summary, String description, String ga) {
        fillInUpdateEventForm(calendarName, null, summary, description);
    }

    @When("^fill in update event form using calendar \"([^\"]*)\", old summary \"([^\"]*)\", summary \"([^\"]*)\" and description \"([^\"]*)\" for " +
        "user \"([^\"]*)\"$")
    public void fillInUpdateEventFormUsingCalendarIdSummaryAndDescription(String calendarName, String oldSummary, String summary, String description,
        String ga) throws Throwable {
        String aliasedCalendarName = gcu.getAliasedCalendarName(calendarName);
        Calendar c = gcu.getPreviouslyCreatedCalendar(ga, aliasedCalendarName);
        assertThat(c).as("Calendar %s is not amongst the created calendars.", aliasedCalendarName).isNotNull();
        Event e = gcu.getEventBySummary(ga, c.getId(), oldSummary);
        assertThat(e).as(String.format("Event %s is not present in calendar %s.", oldSummary, aliasedCalendarName)).isNotNull();
        fillInUpdateEventForm(aliasedCalendarName, e.getId(), summary, description);
    }

    @When("^fill in aliased calendar values by data-testid$")
    public void fillInAliasedCalendarValues(DataTable data) {
        // we need to find the calendar name in the table and alias it before passing on to fillForm
        // we also need to create a copy of the raw DataTable lists, because the returned lists are unmodifiable
        List<List<String>> newCells = new ArrayList<>();
        for (List<String> row : data.cells()) {
            List<String> newRow = new ArrayList<>(row);
            newCells.add(row.stream().map(s -> s == null ? "" : s).collect(Collectors.toList()));
            if ("calendarid".equals(newRow.get(0))) {
                newRow.set(1, gcu.getAliasedCalendarName(newRow.get(1)));
            }
        }
        DataTable newData = DatatableUtils.create(newCells);
        new CommonSteps().fillFormViaTestID(newData);
    }

    /**
     * Fill in the form.
     *
     * @param calendarName
     * @param eventId
     * @param summary
     * @param description
     */
    private void fillInUpdateEventForm(String calendarName, String eventId, String summary, String description) {
        EventForm ce = new EventForm();
        ce.setCalendarName(calendarName);
        if (eventId != null) {
            ce.setEventId(eventId);
        }
        if (summary != null) {
            ce.setTitle(summary);
        }
        if (description != null) {
            ce.setDescription(description);
        }
        ce.fill();
    }
}
