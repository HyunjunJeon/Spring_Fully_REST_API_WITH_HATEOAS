package com.fullrest.demoinflearnrestapi.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

@Component
public class EventValidator {
    public void validate(EventDto eventDto, Errors errors){
        // 무제한인경우는 있을 수 있지만 max가 0일때만 가능
        if(eventDto.getMaxPrice() < eventDto.getBasePrice() && eventDto.getMaxPrice() != 0){
            //errors.rejectValue("basePrice", "Wrong Value", "Base Price is Wrong");
            //errors.rejectValue("maxPrice", "Wrong Value", "Max Price is Wrong");
            errors.reject("wrong Price", "Values for prices are wrong"); // Global Error
        }

        /*
            이벤트 끝나는 시간
            조건1:
            조건2:
            조건3:
         */
        // TODO endEventDateTime
        LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
        if(endEventDateTime.isBefore(eventDto.getBeginEventDateTime()) ||
            endEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime()) ||
            endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime())) {
            errors.rejectValue("endEventDateTime", "Wrong Value", "endEventDateTime is Wrong");
        }

        /*
            이벤트 시작 시간
            조건1:
            조건2:
            조건3:
         */
        // TODO beginEventDateTime
        LocalDateTime beginEventDateTime = eventDto.getBeginEventDateTime();
        if(beginEventDateTime.isAfter(eventDto.getEndEventDateTime()) ||
            beginEventDateTime.isAfter(eventDto.getBeginEnrollmentDateTime()) ||
            beginEventDateTime.isAfter(eventDto.getCloseEnrollmentDateTime())){
            errors.rejectValue("beginEventDateTime", "Wrong Value", "beginEventDateTime is Wrong");
        }

        /*
            이벤트 등록 마감시간
            조건1:
            조건2:
            조건3:
         */
        // TODO CloseEnrollmentDateTime
        LocalDateTime CloseEnrollmentDateTime = eventDto.getCloseEnrollmentDateTime();
        if(CloseEnrollmentDateTime.isBefore(eventDto.getBeginEventDateTime()) ||
            CloseEnrollmentDateTime.isAfter(eventDto.getEndEventDateTime()) ||
            CloseEnrollmentDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())){
            errors.rejectValue("CloseEnrollmentDateTime", "Wrong Value", "CloseEnrollmentDateTime is Wrong");
        }
    }
}
