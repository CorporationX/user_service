package school.faang.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {
    private static final String CREATE_EVENT_URL = "/events/create";
    private static final String GET_EVENT_BY_ID_URL = "/events/{eventId}";
    private static final String GET_EVENTS_BY_FILTER_URL = "/events/filter";
    private static final String DELETE_EVENT_URL = "/events/{eventId}";
    private static final String UPDATE_EVENT_URL = "/events/update";
    private static final String GET_OWNED_EVENT_URL = "/events/user/{userId}";
    private static final String GET_PARTICIPATED_EVENTS_URL = "/events/participated/{userId}";

    @InjectMocks
    private EventController eventController;

    @Mock
    private EventService eventService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private EventDto eventDto;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(eventController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());


        LocalDateTime startDate = LocalDateTime.now().withNano(0);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1).withNano(0);

        eventDto = EventDto.builder()
                .id(1L)
                .title("New Event")
                .startDate(startDate)
                .endDate(endDate)
                .ownerId(1L)
                .description("Event Description")
                .relatedSkills(List.of(SkillDto.builder().id(1L).title("Java").build()))
                .location("Uzbekistan")
                .maxAttendance(100)
                .build();
    }

    @Test
    @DisplayName("Test Create Event")
    public void testCreateEvent() throws Exception {
        when(eventService.create(any(EventDto.class))).thenReturn(eventDto);

        String actualJson = mockMvc.perform(post(CREATE_EVENT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        EventDto actualEventDto = objectMapper.readValue(actualJson, EventDto.class);

        assertEquals(eventDto, actualEventDto);

        verify(eventService, times(1)).create(eventDto);
    }

    @Test
    @DisplayName("Test Get Event By Id")
    public void testGetEventById() throws Exception {
        when(eventService.getEventDto(anyLong())).thenReturn(eventDto);

        String actualJson = mockMvc.perform(get(GET_EVENT_BY_ID_URL, eventDto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        EventDto actualEventDto = objectMapper.readValue(actualJson, EventDto.class);

        assertEquals(eventDto, actualEventDto);

        verify(eventService, times(1)).getEventDto(anyLong());
    }

    @Test
    @DisplayName("Test Get Events By Filter")
    public void testGetEventsByFilter() throws Exception {
        when(eventService.getEventsByFilter(any(EventFilterDto.class))).thenReturn(List.of(eventDto));

        String actualJson = mockMvc.perform(get(GET_EVENTS_BY_FILTER_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        List<EventDto> actualEventDto = objectMapper.readValue(actualJson,
                objectMapper.getTypeFactory().constructCollectionType(List.class, EventDto.class));

        assertEquals(List.of(eventDto), actualEventDto);

        verify(eventService, times(1)).getEventsByFilter(any(EventFilterDto.class));
    }


    @Test
    @DisplayName("Test Delete Event")
    public void testDeleteEventById() throws Exception {
        doNothing().when(eventService).deleteEvent(anyLong());

        mockMvc.perform(delete(DELETE_EVENT_URL, eventDto.getId()))
                .andExpect(status().isNoContent());

        verify(eventService, times(1)).deleteEvent(eventDto.getId());
    }

    @Test
    @DisplayName("Test Update Event")
    public void testUpdateEvent() throws Exception {
        when(eventService.updateEvent(any(EventDto.class))).thenReturn(eventDto);

        String actualJson = mockMvc.perform(put(UPDATE_EVENT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        EventDto actualEventDto = objectMapper.readValue(actualJson, EventDto.class);

        assertEquals(eventDto, actualEventDto);

        verify(eventService, times(1)).updateEvent(any(EventDto.class));
    }

    @Test
    @DisplayName("Test Get Owned Events")
    public void testOwnedEvents() throws Exception {
        when(eventService.getOwnedEvents(anyLong())).thenReturn(List.of(eventDto));

        String actualJson = mockMvc.perform(get(GET_OWNED_EVENT_URL, eventDto.getOwnerId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        List<EventDto> actualEventDtos = objectMapper.readValue(actualJson,
                objectMapper.getTypeFactory().constructCollectionType(List.class, EventDto.class));

        assertEquals(List.of(eventDto), actualEventDtos);

        verify(eventService, times(1)).getOwnedEvents(anyLong());
    }

    @Test
    @DisplayName("Test Get Participated Events")
    public void testParticipatedEvents() throws Exception{
        when(eventService.getParticipatedEvents(anyLong())).thenReturn(List.of(eventDto));

        String actualJson = mockMvc.perform(get(GET_PARTICIPATED_EVENTS_URL, eventDto.getOwnerId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        List<EventDto> actualEventDtos = objectMapper.readValue(actualJson,
                objectMapper.getTypeFactory().constructCollectionType(List.class, EventDto.class));

        assertEquals(List.of(eventDto), actualEventDtos);

        verify(eventService, times(1)).getParticipatedEvents(anyLong());
    }
}
