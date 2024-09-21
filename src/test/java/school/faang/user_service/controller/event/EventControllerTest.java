package school.faang.user_service.controller.event;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private EventDto eventDto;
    private EventDto anotherEventDto;
    private EventFilterDto eventFilterDto;

    @Mock
    private EventService userService;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private EventController eventController;

    @BeforeEach
    public void setUp() {
        eventDto = new EventDto();
        eventDto.setId(2L);
        anotherEventDto = new EventDto();
        anotherEventDto.setId(3L);

        eventFilterDto = new EventFilterDto();

        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(eventController).build();
    }

    @Test
    @DisplayName("Should return filtered events successfully with correct user ID and filters")
    public void testGetFilteredUsers_Success() throws Exception {
        Long userId = 1L;
        String requestBody = objectMapper.writeValueAsString(eventFilterDto);

        when(userContext.getUserId()).thenReturn(userId);
        when(userService.getFilteredEvents(eventFilterDto, userId)).thenReturn(List.of(eventDto, anotherEventDto));

        mockMvc.perform(get("/events/filtered")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[1].id").value(3L));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when invalid json is provided")
    public void testGetFilteredUsers_InvalidRequest() throws Exception {
        String invalidJson = "{invalid json}";

        mockMvc.perform(get("/events/filtered")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}
