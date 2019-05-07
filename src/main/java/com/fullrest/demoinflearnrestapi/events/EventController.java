package com.fullrest.demoinflearnrestapi.events;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Controller
public class EventController {
    @PostMapping("/api/events")
    public ResponseEntity createEvent(){
        linkTo(EventController.class)
        return ResponseEntity.created();
    }
}
