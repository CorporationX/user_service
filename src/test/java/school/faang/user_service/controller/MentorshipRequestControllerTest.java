package school.faang.user_service.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
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

    @BeforeEach
    void initData() {
        correctRequestDto = new MentorshipRequestDto();
        incorrectRequestDto = new MentorshipRequestDto();
        correctRequestDto.setDescription("Some description for test");
        incorrectRequestDto.setDescription("     ");
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
}
