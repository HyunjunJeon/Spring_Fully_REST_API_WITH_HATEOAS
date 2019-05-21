package com.fullrest.demoinflearnrestapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullrest.demoinflearnrestapi.common.RestDocsConfiguration;
import com.fullrest.demoinflearnrestapi.common.TestDescription;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
//@WebMvcTest // Slicing Test (계층별 테스트)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test") // application-test.yml 을 사용하기 위해서
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
                .andExpect(jsonPath("free").value(Matchers.not(true)))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andDo(document("create-event",
                        links(
                            linkWithRel("self").description("link to self"),
                            linkWithRel("query-events").description("link to query event"),
                            linkWithRel("update-event").description("link to update event"),
                            linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content-type")
                        ),
                        requestFields(
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin enroll of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close enroll of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of Enrollment of new event"),
                                fieldWithPath("location").description("location of new event")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Response Location"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Response Content-Type ~ HAL_JSON")
                        ),
                        responseFields( // relaxed <= Prefix를 통해 응답의 일부분만 테스트 가능(정확한 문서를 생성하지 못하는 단점이 존재함)
                                fieldWithPath("id").description("identifier of new event"), // req 와 다른 부분
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin enroll of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close enroll of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of Enrollment of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("free").description("it tells if this event is free or not"), // req 와 다른 부분
                                fieldWithPath("offline").description("it tells if this event is offline or not"), // req 와 다른 부분
                                fieldWithPath("eventStatus").description("event status"),// req 와 다른 부분
                                // relaxed를 빼고 사용하기 위해서는
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-events.href").description("link to query events"),
                                fieldWithPath("_links.update-event.href").description("link to update event"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ))
        ;
    }

    @Test
    @TestDescription("입력받을 수 없는 값을 사용하는 경우 BadRequest를 발생하는 테스트")
    // ObjectMapper가 객체를 Deserialize 할때 Unknown Properties 가 있으면 Fail 되게끔 yaml 설정을 해줬음(Spring boot 제공)
    public void creatEvent_Bad_Request() throws Exception{
        Event event = Event.builder()
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
                // JSON Array 에는 JsonUnWrapped 가 적용되지 않음 따라서 테스트 코드를 약간 수정함
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("content[0].code").exists())
                // 에러가 나면 index 페이지로 갈 수 있도록 조정
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

    @Test
    @TestDescription("비지니스 로직 적용검증")
    public void creatEvent_Biz_Logic() throws Exception{
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
                .andExpect(jsonPath("offline").value(true));
    }
}
