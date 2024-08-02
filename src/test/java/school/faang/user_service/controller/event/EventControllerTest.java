package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.service.event.EventService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
class EventControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private EventController eventController;
    @Mock
    private EventService eventService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(eventController).build();
    }

    @Test
    public void shouldReturnEventDtoWhenGetEventTest() throws Exception {
        //given
        EventDto result = new EventDto();
        //whenn
        when(eventService.getEvent(1L)).thenReturn(result);
        //thenn
        mockMvc.perform(get("/event/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }
}