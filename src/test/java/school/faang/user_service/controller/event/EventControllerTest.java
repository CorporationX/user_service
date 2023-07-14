package school.faang.user_service.controller.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
@AutoConfigureMockMvc
class EventControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventService eventService;

    @Test
    public void testCreateEvent_Success() throws Exception {

        EventDto eventDto = createSampleEventDto();

        doNothing().when(eventService).create(any(EventDto.class));

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(eventDto.getId()))
                .andExpect(jsonPath("$.title").value(eventDto.getTitle()));
    }

    private @NotNull EventDto createSampleEventDto() {
        EventDto eventDto = new EventDto();
        eventDto.setId(1L);
        eventDto.setTitle("Sample Event");
        eventDto.setStartDate(LocalDateTime.now());
        eventDto.setEndDate(LocalDateTime.now().plusHours(2));
        eventDto.setOwnerId(1L);
        eventDto.setDescription("Sample event description");
        eventDto.setLocation("Sample location");
        eventDto.setMaxAttendees(100);

        return eventDto;
    }

}