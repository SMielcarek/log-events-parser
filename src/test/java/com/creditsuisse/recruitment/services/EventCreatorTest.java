package com.creditsuisse.recruitment.services;

import com.creditsuisse.recruitment.exceptions.DuplicateLogEntryException;
import com.creditsuisse.recruitment.models.Event;
import com.creditsuisse.recruitment.models.EventState;
import com.creditsuisse.recruitment.models.LogEntry;
import com.creditsuisse.recruitment.repositories.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventCreatorTest {

    @Mock
    private EventRepository mockEventRepository;

    @InjectMocks
    private EventCreator eventCreator;

    private LogEntry firstLogEntry, secondLogEntry, duplicateLogEntry;

    @BeforeEach
    public void init() {
        firstLogEntry = new LogEntry("1", EventState.STARTED, 1000000000000L, null, null);
        secondLogEntry = new LogEntry("1", EventState.FINISHED, 1000000000005L, null, null);
        duplicateLogEntry = new LogEntry("1", EventState.STARTED, 1000000000005L, null, null);
    }

    @ParameterizedTest
    @MethodSource("provideLogEntriesForTesting")
    public void testCreateEvent_whenSuccess(LogEntry first, LogEntry second, boolean expectedAlert) {
        try {
            Event event = eventCreator.createEvent(first, second);
            assertThat(event.alert()).isEqualTo(expectedAlert);
        }
        catch (DuplicateLogEntryException ex) {
            fail("Exception thrown");
        }
    }

    @Test
    public void testCreateAndSaveEvent_whenSuccess() {
        try {
            Event event = eventCreator.createEvent(firstLogEntry, secondLogEntry);
            when(mockEventRepository.save(any(Event.class))).thenReturn(event);
            Event eventResponse = eventCreator.createAndSaveEvent(firstLogEntry, secondLogEntry);
            verify(mockEventRepository, times(1)).save(any(Event.class));
            assertThat(eventResponse).isEqualTo(event);
        }
        catch (DuplicateLogEntryException ex) {
            fail("Exception thrown");
        }
    }

    @Test
    public void testCreateAndSaveEvent_whenDuplicateLogEntryException() {
        Event eventResponse = null;
        try {
            eventResponse = eventCreator.createAndSaveEvent(firstLogEntry, duplicateLogEntry);
        }
        catch (DuplicateLogEntryException exception) {
            verify(mockEventRepository, never()).save(any(Event.class));
            assertThat(eventResponse).isNull();
        }
    }

    private static Stream<Arguments> provideLogEntriesForTesting() {
        return Stream.of(
                Arguments.of(
                        new LogEntry("1", EventState.STARTED, 1000000000000L, null, null),
                        new LogEntry("1", EventState.FINISHED, 1000000000003L, null, null), false
                ),
                Arguments.of(
                        new LogEntry("1", EventState.STARTED, 1000000000000L, null, null),
                        new LogEntry("1", EventState.FINISHED, 1000000000004L, null, null), false
                ),
                Arguments.of(
                        new LogEntry("1", EventState.STARTED, 1000000000000L, null, null),
                        new LogEntry("1", EventState.FINISHED, 1000000000005L, null, null), true
                )
        );
    }
}