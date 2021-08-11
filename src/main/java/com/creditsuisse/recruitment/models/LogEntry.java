package com.creditsuisse.recruitment.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogEntry {

    @NotBlank(message = "The 'id' field must not be null or empty")
    private String id;
    @NotNull(message = "The 'state' field must not be null")
    private EventState state;
    @Positive(message = "The 'timestamp' field must be greater than zero")
    private long timestamp;
    private String type;
    private String host;

}
