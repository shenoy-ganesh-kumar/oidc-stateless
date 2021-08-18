package com.okta.developer.jugtours.model;

import java.time.Instant;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    private Long id;
    private Instant date;
    private String title;
    private String description;
    private Set<User> attendees;
}