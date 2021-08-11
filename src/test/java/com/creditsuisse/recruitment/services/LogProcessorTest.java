package com.creditsuisse.recruitment.services;

import com.creditsuisse.recruitment.exceptions.DuplicateLogEntryException;
import com.creditsuisse.recruitment.exceptions.ValidationException;
import com.creditsuisse.recruitment.models.EventState;
import com.creditsuisse.recruitment.models.LogEntry;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogProcessorTest {

    @Mock
    private BufferedReader mockBufferedReader;

    @Mock
    private EventCreator mockEventCreator;

    @Mock
    private LogEntryParser mockLogEntryParser;

    @InjectMocks
    private LogProcessor logProcessor;

    private LogEntry firstEntry;
    private LogEntry secondEntry;
    private List<String> logEntries;

    @BeforeEach
    public void init() {
        logEntries = preparelogEntriesList(2);
        when(mockBufferedReader.lines()).thenReturn(logEntries.stream());
        firstEntry = new LogEntry("1", EventState.STARTED, 1000000000000L, null, null);
        secondEntry = new LogEntry("1", EventState.FINISHED, 1000000000005L, null, null);
    }

    @Test
    public void testProcess_whenSuccess() throws JsonProcessingException, ValidationException, DuplicateLogEntryException {
        when(mockLogEntryParser.parseLogEntry(eq(logEntries.get(0)))).thenReturn(firstEntry);
        when(mockLogEntryParser.parseLogEntry(eq(logEntries.get(1)))).thenReturn(secondEntry);
        logProcessor.process();
        verify(mockLogEntryParser, times(2)).parseLogEntry(anyString());
        verify(mockEventCreator, times(1)).createAndSaveEvent(any(LogEntry.class), any(LogEntry.class));
    }

    @Test
    public void testProcess_whenJsonProcessingException() throws JsonProcessingException, ValidationException, DuplicateLogEntryException {
        doThrow(JsonProcessingException.class).when(mockLogEntryParser).parseLogEntry(eq(logEntries.get(0)));
        when(mockLogEntryParser.parseLogEntry(eq(logEntries.get(1)))).thenReturn(secondEntry);
        logProcessor.process();
        verify(mockLogEntryParser, times(2)).parseLogEntry(anyString());
        verify(mockEventCreator, never()).createAndSaveEvent(any(LogEntry.class), any(LogEntry.class));
    }

    private List<String> preparelogEntriesList(int size) {
        List<String> logEntries = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            logEntries.add("TEST ENTRY " + i);
        }
        return logEntries;
    }
}