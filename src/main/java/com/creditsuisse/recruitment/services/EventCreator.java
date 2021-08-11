package com.creditsuisse.recruitment.services;

import com.creditsuisse.recruitment.exceptions.DuplicateLogEntryException;
import com.creditsuisse.recruitment.models.Event;
import com.creditsuisse.recruitment.models.LogEntry;
import com.creditsuisse.recruitment.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventCreator {

    /*
    Events which are longer that 4 ms will have alert set to TRUE
     */
    private final static int ALERT_TRESHOLD = 4;

    private final EventRepository eventRepository;

    @Autowired
    public EventCreator(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event createAndSaveEvent(LogEntry firstLogEntry, LogEntry secondLogEntry) throws DuplicateLogEntryException {
        return eventRepository.save(createEvent(firstLogEntry, secondLogEntry));
    }

    public Event createEvent(LogEntry firstLogEntry, LogEntry secondLogEntry) throws DuplicateLogEntryException {
        int duration = calculateEventDuration(firstLogEntry, secondLogEntry);
        return new Event().id(firstLogEntry.getId()).host(firstLogEntry.getHost()).type(firstLogEntry.getType())
                .duration(duration)
                .alert(duration > ALERT_TRESHOLD);
    }

    private int calculateEventDuration(LogEntry first, LogEntry second) throws DuplicateLogEntryException {
        if (first.getState().equals(second.getState())) {
            throw new DuplicateLogEntryException(String.format("Duplicate log entries with the same id [%s] and state [%s]. Skipping...", first.getId(), first.getState()));
        }
        return Math.abs((int)(first.getTimestamp() - second.getTimestamp()));
    }
}
