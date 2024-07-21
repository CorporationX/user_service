package school.faang.user_service.controller.event;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.service.event.EventParticipationService;
import school.faang.user_service.validator.EventParticipationValidator;

public class EventParticipationControllerTest {

    @Mock
    private EventParticipationService eventParticipationService;

    @Mock
    private EventParticipationValidator eventParticipationValidator;

    @InjectMocks
    private EventParticipationController eventParticipationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(eventParticipationController).build();
    }
}

