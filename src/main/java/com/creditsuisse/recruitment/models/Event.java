package com.creditsuisse.recruitment.models;

import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
@Accessors(fluent = true, chain = true)
public class Event {

    @Id
    private String id;
    private int duration;
    private String type;
    private String host;
    private boolean alert;

}
