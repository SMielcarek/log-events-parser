package com.creditsuisse.recruitment.services;

import com.creditsuisse.recruitment.exceptions.ValidationException;
import com.creditsuisse.recruitment.models.LogEntry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Iterator;
import java.util.Set;

@Slf4j
@Service
public class LogEntryParser {

    private final ObjectMapper objectMapper;
    private final Validator logEntryValidator;

    @Autowired
    public LogEntryParser(ObjectMapper objectMapper, Validator validator) {
        this.objectMapper = objectMapper;
        this.logEntryValidator = validator;
    }

    public LogEntry parseLogEntry(String logEntryLine) throws JsonProcessingException, ValidationException {
        LogEntry logEntry = objectMapper.readValue(logEntryLine, LogEntry.class);
        validateLogEntry(logEntry);
        return logEntry;
    }

    private void validateLogEntry(LogEntry logEntry) throws ValidationException {
        Set<ConstraintViolation<LogEntry>> validationErrors = logEntryValidator.validate(logEntry);
        if (!validationErrors.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            Iterator<ConstraintViolation<LogEntry>> iterator = validationErrors.iterator();
            while (iterator.hasNext()) {
                stringBuilder.append("[").append(iterator.next().getMessage()).append("]");
            }
            throw new ValidationException(stringBuilder.toString());
        }
    }
}
