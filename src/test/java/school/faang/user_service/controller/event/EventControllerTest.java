package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

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
    public void shouldReturnEventDtoWhenCreateTest() throws Exception {
        EventDto eventDto = new EventDto();
        eventDto.setRelatedSkillsIds(List.of(1L, 2L));

        when(eventService.create(eventDto)).thenReturn(eventDto);

        mockMvc.perform(post("/event/"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}