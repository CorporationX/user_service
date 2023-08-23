package school.faang.user_service.controller.mentorship;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MentorshipRequestControllerTest {
    @InjectMocks
    private MentorshipRequestController mentorshipRequestController;
    @Mock
    private MentorshipRequestService mentorshipRequestService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.6");

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(mentorshipRequestController).build();
    }

    @Test
    public void getAllProjectsTest() throws Exception {
        MentorshipRequestDto mentorshipRequestDto = MentorshipRequestDto.builder()
                .requesterId(1L)
                .receiverId(2L)
                .build();

        mockMvc.perform(post("/mentorship/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mentorshipRequestDto)))
                .andExpect(status().isOk());
    }
}