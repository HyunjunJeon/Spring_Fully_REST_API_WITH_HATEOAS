package com.fullrest.demoinflearnrestapi.events;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

public class EventResource extends Resource<Event> {
    public EventResource(Event event, Link... links) {
        super(event, links);
        add(linkTo(EventController.class).slash(event.getId()).withSelfRel()); // Type-Safe 하게 만들어줌 + Resource 에서 생성할 때 부터 만들어주니까 controller에서는 제거
    }
}
