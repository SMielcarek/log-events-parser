package com.creditsuisse.recruitment.services;

import com.creditsuisse.recruitment.exceptions.DuplicateLogEntryException;
import com.creditsuisse.recruitment.exceptions.ValidationException;
import com.creditsuisse.recruitment.models.Event;
import com.creditsuisse.recruitment.models.LogEntry;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class LogProcessor {

    /*
    Every 50 log entries info about the progress will be logged
     */
    private final static int SIZE_INTERVAL = 50;

    private final BufferedReader logReader;
    private final LogEntryParser logEntryParser;
    private final EventCreator eventCreator;

    private final Map<String, LogEntry> logEntries = new HashMap<>();
    private long logEntryCounter;
    private long logEntryErrorCounter;

    @Autowired
    public LogProcessor(BufferedReader logReader, LogEntryParser logEntryParser, EventCreator eventCreator) {
        this.logReader = logReader;
        this.logEntryParser = logEntryParser;
        this.eventCreator = eventCreator;
    }

    public void process() {
        logEntryCounter = 0;
        logEntryErrorCounter = 0;
        logReader.lines().forEach(this::processLogEntry);
        log.info(String.format("Processing finished. Total number of log entries processed: %s. Log entry errors: %s", logEntryCounter, logEntryErrorCounter));
    }

    private void processLogEntry(String logEntryLine) {
        try {
            LogEntry logEntry = logEntryParser.parseLogEntry(logEntryLine);
            storeLogEntryOrCreateEvent(logEntry);
            logEntryCounter++;
            if (logEntryCounter % SIZE_INTERVAL == 0) {
                log.info(String.format("Log entries processed: %s", logEntryCounter));
            }
        }
        catch (JsonProcessingException | DuplicateLogEntryException | ValidationException ex) {
            logEntryErrorCounter++;
            log.error(ex.getMessage());
            log.debug(ex.toString(), ex);
        }
    }

    private void storeLogEntryOrCreateEvent(LogEntry logEntry) throws DuplicateLogEntryException {
        if (!logEntries.containsKey(logEntry.getId())) {
            logEntries.put(logEntry.getId(), logEntry);
        }
        else {
            Event event = eventCreator.createAndSaveEvent(logEntries.get(logEntry.getId()), logEntry);
            logEntries.remove(logEntry.getId());
            log.debug(String.format("Event created and saved: %s", event));
        }
    }

}
