package com.creditsuisse.recruitment.services;

import com.creditsuisse.recruitment.exceptions.ValidationException;
import com.creditsuisse.recruitment.models.EventState;
import com.creditsuisse.recruitment.models.LogEntry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogEntryParserTest {

    private final static String LOG_ENTRY_LINE = "TEST ENTRY";
    private final LogEntry logEntry = new LogEntry("1", EventState.STARTED, 1000000000000L, null, null);

    @Mock
    private ObjectMapper mockObjectMapper;

    @Mock
    private Validator mockValidator;

    @InjectMocks
    private LogEntryParser logEntryParser;

    @Test
    public void testParseLogEntry_whenSuccess() {
        try {
            when(mockObjectMapper.readValue(LOG_ENTRY_LINE, LogEntry.class)).thenReturn(logEntry);
            when(mockValidator.validate(logEntry)).thenReturn(Collections.emptySet());
            LogEntry logEntryResponse = logEntryParser.parseLogEntry(LOG_ENTRY_LINE);
            verify(mockObjectMapper, times(1)).readValue(LOG_ENTRY_LINE, LogEntry.class);
            assertThat(logEntryResponse).isSameAs(logEntry);
        }
        catch (JsonProcessingException | ValidationException ex) {
            fail("Exception thrown");
        }
    }

    @Test
    public void testParseLogEntry_whenJsonProcessingException() {
        LogEntry logEntryResponse = null;
        try {
            doThrow(JsonProcessingException.class).when(mockObjectMapper).readValue(LOG_ENTRY_LINE, LogEntry.class);
            logEntryResponse = logEntryParser.parseLogEntry(LOG_ENTRY_LINE);
            fail("Exception not thrown");
        } catch (JsonProcessingException | ValidationException exception) {
            assertThat(exception).isExactlyInstanceOf(JsonProcessingException.class);
            assertThat(logEntryResponse).isNull();
        }
    }

    @Test
    public void testParseLogEntry_whenValidationException() {
        LogEntry logEntryResponse = null;
        ConstraintViolation<LogEntry> violation = ConstraintViolationImpl.forBeanValidation(null, null, null, "Validation Exception Test", LogEntry.class, logEntry, logEntry, null, null,null,null);
        Set<ConstraintViolation<LogEntry>> validationErrors = new HashSet<>();
        validationErrors.add(violation);
        try {
            when(mockObjectMapper.readValue(LOG_ENTRY_LINE, LogEntry.class)).thenReturn(logEntry);
            when(mockValidator.validate(logEntry)).thenReturn(validationErrors);
            logEntryResponse = logEntryParser.parseLogEntry(LOG_ENTRY_LINE);
            fail("Exception not thrown");
        } catch (JsonProcessingException | ValidationException exception) {
            assertThat(exception).isExactlyInstanceOf(ValidationException.class);
            assertThat(exception.getMessage()).isEqualTo("[Validation Exception Test]");
            assertThat(logEntryResponse).isNull();
        }
    }

}