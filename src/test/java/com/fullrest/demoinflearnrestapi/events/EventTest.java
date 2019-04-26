package com.fullrest.demoinflearnrestapi.events;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EventTest {
    @Test
    public void builder(){
        Event event = Event.builder()
                .name("Inflearn Class - Spring REST API")
                .description("FULLY REST API Development")
                .build();
        assertThat(event).isNotNull();
    }

    @Test
    public void javaBean(){
        // Given
        String name = "Inflearn Class - Spring REST API";
        String des = "FULLY REST API Development";

        // When
        Event event = new Event();
        event.setName(name);
        event.setDescription(des);

        // Then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(des);
    }
}