package com.creditsuisse.recruitment;

import com.creditsuisse.recruitment.services.LogProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class LogEventsParserApp implements ApplicationRunner {

    private final LogProcessor logProcessor;

    @Autowired
    public LogEventsParserApp(LogProcessor logProcessor) {
        this.logProcessor = logProcessor;
    }

    public static void main(String[] args) {
        SpringApplication.run(LogEventsParserApp.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        logProcessor.process();
    }
}
