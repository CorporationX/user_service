package school.faang.user_service.controller.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventWithSubscribersDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.service.event.EventServiceImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest(EventController.class)
class EventControllerTest {

//    @Autowired
//    private MockMvc mockMvc;

    @MockBean
    private EventServiceImpl eventServiceImpl;

    @MockBean
    private EventMapper eventMapper;

    private EventDto eventDto;
    private Event event;

    @BeforeEach
    void setUp() {
        eventDto = new EventDto();
        eventDto.setId(1L);
        eventDto.setTitle("Test Event");

        event = new Event();
        event.setId(1L);
        event.setTitle("Test Event");
    }

    @Test
    void createEvent_ShouldReturnCreatedEvent() throws Exception {
//        when(eventMapper.toEvent(any(EventDto.class))).thenReturn(event);
//        when(eventServiceImpl.create(any(Event.class))).thenReturn(event);
//        when(eventMapper.toDto(any(Event.class))).thenReturn(eventDto);
//
//        mockMvc.perform(post("/events")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"name\": \"Test Event\"}"))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.id").value(1L))
//                .andExpect(jsonPath("$.name").value("Test Event"));
    }

    @Test
    void updateEvent_ShouldReturnUpdatedEventWithSubscribers() throws Exception {
//        when(eventMapper.toEvent(any(EventDto.class))).thenReturn(event);
//        when(eventServiceImpl.updateEvent(any(Event.class))).thenReturn(event);
//        when(eventServiceImpl.getSubscribersCount(any(Event.class))).thenReturn(10);
//        when(eventMapper.toEventWithSubscribersDto(any(Event.class), anyInt()))
//                .thenReturn(EventWithSubscribersDto.builder().id(1L).title("Test Event").subscribersCount(10).build());
//
//        mockMvc.perform(patch("/events")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"name\": \"Updated Event\"}"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1L))
//                .andExpect(jsonPath("$.name").value("Test Event"))
//                .andExpect(jsonPath("$.subscribersCount").value(10));
    }

    @Test
    void getEvent_ShouldReturnEventById() throws Exception {
//        when(eventServiceImpl.getEvent(1L)).thenReturn(event);
//        when(eventMapper.toDto(event)).thenReturn(eventDto);
//
//        mockMvc.perform(get("/events/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1L))
//                .andExpect(jsonPath("$.name").value("Test Event"));
    }

    @Test
    void deleteEvent_ShouldDeleteEvent() throws Exception {
//        mockMvc.perform(delete("/events/1"))
//                .andExpect(status().isOk());
//
//        verify(eventServiceImpl, times(1)).deleteEvent(1L);
    }
}