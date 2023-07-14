package school.faang.user_service.controller.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.time.LocalDateTime;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.service.EventService;

class EventControllerTest {
    private MockMvc mockMvc;

    @Mock
    private EventService eventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        EventController eventController = new EventController(eventService);
        mockMvc = MockMvcBuilders.standaloneSetup(eventController).build();
    }

    @Test
    void testGetEvent() throws Exception {
        EventDto eventDto = new EventDto();
        eventDto.setId(1L);
        eventDto.setTitle("Test Event");
        eventDto.setStartDate(LocalDateTime.now());
        eventDto.setEndDate(LocalDateTime.now().plusHours(2));

        when(eventService.getEvent(1L)).thenReturn(eventDto);

        mockMvc.perform(get("/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Event"))
                .andDo(print());
    }

}