package school.faang.user_service.controller.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDro;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestControllerTest {
    @Mock
    private MentorshipRequestService mentorshipRequestService;

    @Mock
    private MentorshipRequestValidator mentorshipRequestValidator;

    @InjectMocks
    private MentorshipRequestController mentorshipRequestController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(mentorshipRequestController).build();
    }

    @Test
    void requestMentorship() throws Exception {
        MentorshipRequestDto requestDto = new MentorshipRequestDto();

        mockMvc.perform(post("/mentorship/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\": \"Description\"}"))
                .andExpect(status().isOk());

        verify(mentorshipRequestValidator).validateDescription(any(MentorshipRequestDto.class));
        verify(mentorshipRequestService).requestMentorship(any(MentorshipRequestDto.class));
    }

    @Test
    void getRequests() throws Exception {
        when(mentorshipRequestService.getRequests(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/mentorship/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(mentorshipRequestService).getRequests(any(RequestFilterDro.class));
    }

    @Test
    void acceptRequest() throws Exception {
        mockMvc.perform(put("/mentorship/request/1/accept"))
                .andExpect(status().isOk());

        verify(mentorshipRequestService).acceptRequest(1L);
    }

    @Test
    void rejectRequest() throws Exception {
        RejectionDto rejectionDto = new RejectionDto("Not interested");

        mockMvc.perform(put("/mentorship/request/1/reject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\": \"reason\"}"))
                .andExpect(status().isOk());

        verify(mentorshipRequestService).rejectRequest(eq(1L), any(RejectionDto.class));
    }
}
