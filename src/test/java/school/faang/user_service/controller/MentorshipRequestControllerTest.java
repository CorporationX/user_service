package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestControllerTest {

    @Mock
    private MentorshipRequestService requestService;
    @InjectMocks
    private MentorshipRequestController requestController;

    private MentorshipRequestDto correctRequestDto;
    private MentorshipRequestDto incorrectRequestDto;
    private RequestFilterDto incorrectFilterDto;
    private RequestFilterDto correctFilterDto;

    @BeforeEach
    void initData() {
        correctRequestDto = MentorshipRequestDto.builder()
                .description("Some description for test")
                .build();
        incorrectRequestDto = MentorshipRequestDto.builder()
                .description("     ")
                .build();
        correctFilterDto = RequestFilterDto.builder().build();
    }

    @Test
    void testRequestMentorshipWithDescription() {
        requestController.requestMentorship(correctRequestDto);
        verify(requestService, times(1)).requestMentorship(correctRequestDto);
    }

    @Test
    void testRequestMentorshipWithoutDescription() {
        assertThrows(DataValidationException.class, () -> requestController.requestMentorship(incorrectRequestDto));
    }

    @Test
    void testGetRequestsWithoutFilter() {
        assertThrows(DataValidationException.class, () -> requestController.getRequests(incorrectFilterDto));
    }

    @Test
    void testGetRequest() {
        requestController.getRequests(correctFilterDto);
        verify(requestService, times(1)).getRequests(correctFilterDto);
    }
}
