package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentor.MentorshipRequestDto;
import school.faang.user_service.dto.mentor.RejectionDto;
import school.faang.user_service.dto.mentor.RequestFilterDto;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestControllerTest {

    @Mock
    private MentorshipRequestService mentorshipRequestService;

    @InjectMocks
    private MentorshipRequestController mentorshipRequestController;

    private long requestId;

    @BeforeEach
    void setUp() {
        requestId = 1L;
    }

    @Test
    void requestMentorship() {
        MentorshipRequestDto requestDto = new MentorshipRequestDto();
        requestDto.setId(requestId);

        mentorshipRequestController.requestMentorship(requestDto);

        verify(mentorshipRequestService, times(1)).requestMentorship(requestDto);
    }

    @Test
    void getRequests() {
        RequestFilterDto filter = new RequestFilterDto();
        List<RequestFilterDto> expectedRequest = List.of(new RequestFilterDto());
        when(mentorshipRequestService.getRequests(filter)).thenReturn(expectedRequest);

        List<RequestFilterDto> result = mentorshipRequestController.getRequests(filter);

        assertEquals(expectedRequest, result);
        verify(mentorshipRequestService, times(1)).getRequests(filter);
    }

    @Test
    void acceptRequest() {
        mentorshipRequestController.acceptRequest(requestId);

        verify(mentorshipRequestService, times(1)).acceptRequest(requestId);
    }

    @Test
    void rejectRequest() {
        RejectionDto rejectionDto = new RejectionDto();

        mentorshipRequestController.rejectRequest(requestId, rejectionDto);

        verify(mentorshipRequestService, times(1)).rejectRequest(requestId, rejectionDto);
    }
}