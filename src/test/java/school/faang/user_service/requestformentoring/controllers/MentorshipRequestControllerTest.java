package school.faang.user_service.requestformentoring.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controllers.MentorshipRequestController;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.services.MentorshipRequestService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestControllerTest {

    private static final MentorshipRequestDto menReqDto = new MentorshipRequestDto();
    private static final RequestFilterDto requestFilterDto = new RequestFilterDto();
    @InjectMocks
    private MentorshipRequestController mentorshipRequestController;
    @Mock
    private MentorshipRequestService mentorshipRequestService;

    @Test
    void requestMentorship() {

    }

    @Test
    void getRequests() {
        List<MentorshipRequestDto> mentorshipRequestDtos = List.of(new MentorshipRequestDto());

        when(mentorshipRequestService.getRequests(requestFilterDto)).thenReturn(mentorshipRequestDtos);

        List<MentorshipRequestDto> result = mentorshipRequestController.getRequests(requestFilterDto);
        assertEquals(mentorshipRequestDtos, result);
    }

    @Test
    void acceptRequest() {

    }

    @Test
    void rejectRequest() {

    }
}