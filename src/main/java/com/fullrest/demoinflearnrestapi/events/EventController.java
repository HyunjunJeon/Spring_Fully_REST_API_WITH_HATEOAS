package com.fullrest.demoinflearnrestapi.events;

import com.fullrest.demoinflearnrestapi.common.ErrorsResource;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
public class EventController {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;

    private EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator){
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.eventValidator = eventValidator;
    }

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors){
        if(errors.hasErrors()){ // 요청단에서 에러 발생하면... => 애노테이션 에러
            return badRequest(errors);
        }

        eventValidator.validate(eventDto, errors);
        if(errors.hasErrors()){ // Validator 관련 에러 발생시
            return badRequest(errors);
        }

        Event event = modelMapper.map(eventDto, Event.class); // JSON 으로 변환
        event.update();
        Event newEvent = this.eventRepository.save(event);

        // _links 추가하는 부분
        ControllerLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createdURI = selfLinkBuilder.toUri();
        EventResource eventResource = new EventResource(event); // event-Resource를 본문에 넣어주기 위해서 event 객체 전송
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(selfLinkBuilder.withRel("update-event"));
        eventResource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));
        return ResponseEntity.created(createdURI).body(eventResource);
    }

    private ResponseEntity badRequest(Errors errors){
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }
}
