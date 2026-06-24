package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import school.faang.user_service.event.EventParticipationService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class EventParticipationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventParticipationService eventParticipationService;

    @Test
    public void testRegisterParticipantWithNullEventId() throws Exception {
        mockMvc.perform(post("/events/null/register")
                        .param("userId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegisterParticipantWithNullUserId() throws Exception {
        mockMvc.perform(post("/events/1/register")
                        .param("userId", "null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUnregisterParticipantWithNullEventId() throws Exception {
        mockMvc.perform(delete("/events/null/unregister")
                        .param("userId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUnregisterParticipantWithNullUserId() throws Exception {
        mockMvc.perform(delete("/events/1/unregister")
                        .param("userId", "null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetParticipantsWithNullEventId() throws Exception {
        mockMvc.perform(get("/events/null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetParticipantsCountWithNullEventId() throws Exception {
        mockMvc.perform(get("/events/null/count"))
                .andExpect(status().isBadRequest());
    }
}