package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestControllerTest {

    @Mock
    private MentorshipRequestService mentorshipRequestService;

    @InjectMocks
    private MentorshipRequestController mentorshipRequestController;

    @Test
    void testNullDescription() {
        MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto();

        assertThrows(
                RuntimeException.class,
                () -> mentorshipRequestController.requestMentorship(mentorshipRequestDto));
    }

    @Test
    void testRequestMentoring() {
        MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto();
        mentorshipRequestDto.setRequesterId(1L);
        mentorshipRequestDto.setReceiverId(2L);
        mentorshipRequestDto.setDescription("test");

        mentorshipRequestController.requestMentorship(mentorshipRequestDto);

        Mockito.verify(mentorshipRequestService, Mockito.times(1))
                .requestMentorship(mentorshipRequestDto);
    }

    @Test
    void testGetRequests() {
        RequestFilterDto requestFilterDto = new RequestFilterDto();
        List<MentorshipRequestDto> mentorshipRequestDtos = new ArrayList<>();
        Mockito.when(mentorshipRequestService.getRequests(requestFilterDto))
                .thenReturn(mentorshipRequestDtos);

        mentorshipRequestController.getRequests(requestFilterDto);

        Mockito.verify(mentorshipRequestService, Mockito.times(1))
                .getRequests(requestFilterDto);
    }

    @Test
    void testAcceptRequest() {
        Long testId = 1L;

        mentorshipRequestController.acceptRequest(testId);

        Mockito.verify(mentorshipRequestService, Mockito.times(1))
                .acceptRequest(testId);
    }

    @Test
    void rejectRequest() {
        Long testId = 1L;
        RejectionDto rejectionDto = new RejectionDto("test");

        mentorshipRequestController.rejectRequest(testId, rejectionDto);

        Mockito.verify(mentorshipRequestService, Mockito.times(1))
                .rejectRequest(testId, rejectionDto);
    }
}