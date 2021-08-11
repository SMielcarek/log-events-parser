package com.creditsuisse.recruitment;

import com.creditsuisse.recruitment.exceptions.MissingLogFilePathException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.Validator;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

@Slf4j
@Configuration
public class AppContextConfig {

    private final static String ERROR_MSG = "Missing command line 'logfile' argument! Usage: java -jar log-events-parser.jar --logfile=<PATH_TO_LOG_FILE>";

    @Value("${logfile:}")
    private String logFilePath;

    @Bean
    public BufferedReader getBufferedReader() {
        try {
            validateLogFilePath();
            return new BufferedReader(new FileReader(logFilePath));
        }
        catch (FileNotFoundException | MissingLogFilePathException ex) {
            throw new BeanCreationException(ex.getMessage());
        }
    }

    @Bean
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public Validator getValidator() {
        return new LocalValidatorFactoryBean();
    }

    private void validateLogFilePath() throws MissingLogFilePathException {
        if (logFilePath.isEmpty()) {
            log.error(ERROR_MSG);
            throw new MissingLogFilePathException(ERROR_MSG);
        }
    }
}
