package com.fullrest.demoinflearnrestapi.events;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

public class EventResource extends Resource<Event> {
    public EventResource(Event content, Link... links) {
        super(content, links);
    }
}
