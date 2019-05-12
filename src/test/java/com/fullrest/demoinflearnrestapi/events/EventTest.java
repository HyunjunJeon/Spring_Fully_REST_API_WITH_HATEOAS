package com.fullrest.demoinflearnrestapi.events;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitParamsRunner.class)
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

    // Parameters Type Safety 하게 변경함
    private Object[] parametersForTestFree(){
        return new Object[]{
            new Object[]{0,0,true},
            new Object[]{100,0,false},
            new Object[]{0,100,false },
            new Object[]{100,200,false}
        };
    }

    @Test
    //@Parameters({"0,0,true", "100,0,false", "0,100,false"})
    //@Parameters(method = "parametersForTestFree")
    @Parameters // Method Naming Convention 지켰을 때 (parametersFor~~)
    public void testFree(int basePrice, int maxPrice, boolean isFree){
        // Given
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();
        // When
        event.update();

        // Then
        assertThat(event.isFree()).isEqualTo(isFree);
    }

    private Object[] parametersForTestOffline(){
        return new Object[]{
                new Object[]{"장소가 존재하는 중~", true},
                new Object[]{null, false}
        };
    }

    @Test
    @Parameters
    public void testOffline(String location, boolean isOffline){
        // Given
        Event event = Event.builder()
                .location(location)
                .build();
        // When
        event.update();

        // Then
        assertThat(event.isOffline()).isEqualTo(isOffline);
    }
}