package school.faang.user_service.controller;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestControllerTest {

    @Mock
    private MentorshipRequestService mentorshipRequestService;

    @InjectMocks
    private MentorshipRequestController mentorshipRequestController;

    private MockMvc mockMvc;
    private MentorshipRequestDto badRequest;
    private MentorshipRequestDto mentorshipRequestDto;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(mentorshipRequestController).build();

        mentorshipRequestDto = MentorshipRequestDto.builder()
                .requesterId(1L)
                .receiverId(2L)
                .description("Code")
                .build();

        badRequest = MentorshipRequestDto.builder() // No mandatory field 'description' in this one
                .requesterId(1L)
                .receiverId(1L)
                .build();
    }

    @Test
    @DisplayName("Test: Valid request, should return 201")
    public void testControllerReturns200isAccepted() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/mentorship/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(mentorshipRequestDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Test: Bad request, should return 400")
    public void testControllerReturns400badRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/mentorship/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(badRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }
}