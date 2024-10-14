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
import school.faang.user_service.controller.EventController;
import school.faang.user_service.model.dto.EventDto;
import school.faang.user_service.model.filter_dto.EventFilterDto;
import school.faang.user_service.exception.handler.GlobalRestExceptionHandler;
import school.faang.user_service.service.impl.EventServiceImpl;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private EventServiceImpl eventService;

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

        mockMvc = MockMvcBuilders.standaloneSetup(eventController)
                .setControllerAdvice(new GlobalRestExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Should return filtered events successfully with correct user ID and filters")
    public void testGetFilteredEvents_Success() throws Exception {
        Long userId = 1L;
        String requestBody = objectMapper.writeValueAsString(eventFilterDto);

        when(userContext.getUserId()).thenReturn(userId);
        when(eventService.getFilteredEvents(eventFilterDto, userId)).thenReturn(List.of(eventDto, anotherEventDto));

        mockMvc.perform(post("/events/filtered")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[1].id").value(3L));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when invalid json is provided")
    public void testGetFilteredEvents_InvalidRequest() throws Exception {
        String invalidJson = "{invalid json}";

        mockMvc.perform(post("/events/filtered")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid Input Format"))
                .andExpect(jsonPath("$.message")
                        .value(org.hamcrest.Matchers.containsString("JSON parse error")));
    }
}
