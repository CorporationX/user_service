package school.faang.user_service.controller.mentorship;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestControllerTest {
    @InjectMocks
    private MentorshipRequestController mentorshipRequestController;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(mentorshipRequestController).build();
    }

    @Test
    public void getAllProjectsTest() throws Exception {
        MentorshipRequestDto mentorshipRequestDto = MentorshipRequestDto.builder()
                .requesterId(1L)
                .receiverId(2L)
                .description("description")
                .build();

        mockMvc.perform(post("/mentorship/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mentorshipRequestDto)))
                .andExpect(status().isOk());
    }
}