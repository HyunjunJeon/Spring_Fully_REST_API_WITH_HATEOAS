package com.fullrest.demoinflearnrestapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullrest.demoinflearnrestapi.common.TestDescription;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
//@WebMvcTest // Slicing Test (계층별 테스트)
@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTest {

    @Autowired
    MockMvc mockMvc; // Mocking 되어있는 DispatcherServlet 사용 가능 -> Web-Server를 띄우진 않아

    @Autowired
    ObjectMapper objectMapper;

//    @MockBean
//    EventRepository eventRepository;

    @Test
    @TestDescription("정상적으로 이벤트를 생성하는 테스트")
    public void creatEvent() throws Exception{
        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2019,5,7,13,31,30))
                .closeEnrollmentDateTime(LocalDateTime.of(2019,5,8,13,31,30))
                .beginEventDateTime(LocalDateTime.of(2019,5,9,13,31,30))
                .endEventDateTime(LocalDateTime.of(2019,5,10,13,32,30))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 StartUp Factory")
                .build();

        // Event Repository를 MockBean으로 호출하면 객체에 null 이 담겨져 있어 NullPointerException 발생
        // Mockito.when(eventRepository.save(event)).thenReturn(event);

        mockMvc.perform(post("/api/events/")
                .contentType(MediaTypes.HAL_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8_VALUE))
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("free").value(Matchers.not(true)))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andExpect(jsonPath("_links.query-events").exists())
        ;
    }

    @Test
    @TestDescription("입력받을 수 없는 값을 사용하는 경우 BadRequest를 발생하는 테스트")
    // ObjectMapper가 객체를 Deserialize 할때 Unknown Properties 가 있으면 Fail 되게끔 yaml 설정을 해줬음(Spring boot 제공)
    public void creatEvent_Bad_Request() throws Exception{
        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2019,05,07,13,31,30))
                .closeEnrollmentDateTime(LocalDateTime.of(2019,05,8,13,31,30))
                .beginEventDateTime(LocalDateTime.of(2019,05,9,13,31,30))
                .endEventDateTime(LocalDateTime.of(2019,05,10,13,32,30))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 StartUp Factory")
                .build();

        mockMvc.perform(post("/api/events/")
                .contentType(MediaTypes.HAL_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력값이 비어있는 경우 BadRequest를 발생하는 테스트")
    public void createEvent_Bad_Request_Empty_Input() throws Exception{
        EventDto eventDto = EventDto.builder().build();

        mockMvc.perform(post("/api/events/")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());

    }

    @Test
    @TestDescription("틀린 값을 입력한경우 BadRequest가 발생하는 이벤트")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception{
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2019,05,07,13,31,30))
                .closeEnrollmentDateTime(LocalDateTime.of(2019,05,8,13,31,30))
                .beginEventDateTime(LocalDateTime.of(2019,05,9,13,31,30))
                .endEventDateTime(LocalDateTime.of(2019,05,10,13,32,30))
                .basePrice(10000) // Annotation으로 검증하기 애매한 경우 -> Validator 를 만들어주어야함
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 StartUp Factory")
                .build();

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].objectName").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].code").exists());
    }

    @Test
    @TestDescription("비지니스 로직 적용검증")
    public void creatEvent_Biz_Logic() throws Exception{
        Event event = Event.builder()
                .id(100)
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2019,5,7,13,31,30))
                .closeEnrollmentDateTime(LocalDateTime.of(2019,5,8,13,31,30))
                .beginEventDateTime(LocalDateTime.of(2019,5,9,13,31,30))
                .endEventDateTime(LocalDateTime.of(2019,5,10,13,32,30))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 StartUp Factory")
                .free(true)
                .offline(false)
                .build();

        mockMvc.perform(post("/api/events/")
                .contentType(MediaTypes.HAL_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8_VALUE))
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(EventStatus.DRAFT.name()));
    }
}
