package com.fullrest.demoinflearnrestapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest // Slicing Test (계층별 테스트)
public class EventControllerTest {

    @Autowired
    MockMvc mockMvc; // Mocking 되어있는 DispatcherServlet 사용 가능 -> Web-Server를 띄우진 않아

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void creatEvent() throws Exception{
        Event event = Event.builder()
                .id(10)
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
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                    .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists());
    }
}
