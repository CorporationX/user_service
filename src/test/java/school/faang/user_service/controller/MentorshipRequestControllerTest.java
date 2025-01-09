package school.faang.user_service.controller;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.MentorshipRequestService;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestControllerTest {
    @Mock
    private MentorshipRequestService mentorshipRequestService;
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MentorshipRequestController mentorshipRequestController;

    private static final Long REQUESTER_ID = 1L;
    private static final Long RECEIVER_ID = 2L;
    private static final String DESCRIPTION = "I want you to be my mentor";
    private static final RequestStatus REQUEST_STATUS = RequestStatus.PENDING;

    @Test
    public void testDtoIsNull() {
        MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto();

        Assert.assertThrows(
                NullPointerException.class,
                () -> mentorshipRequestController.requestMentorship(mentorshipRequestDto));
    }

    @Test
    public void testDescriptionIsEmpty() {
        MentorshipRequestDto mentorshipRequestDto = prepareDataToDto(REQUESTER_ID,
                RECEIVER_ID,
                "",
                REQUEST_STATUS);

        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> mentorshipRequestController.requestMentorship(mentorshipRequestDto));
    }

    @Test
    public void testMentorshipRequestCreate() {
        MentorshipRequestDto mentorshipRequestDto = prepareDataToDto(
                REQUESTER_ID,
                RECEIVER_ID,
                DESCRIPTION,
                REQUEST_STATUS);
        mentorshipRequestController.requestMentorship(mentorshipRequestDto);
        Mockito.verify(mentorshipRequestService, Mockito.times(1))
                .requestMentorship(mentorshipRequestDto);
    }

    private MentorshipRequestDto prepareDataToDto(Long requesterId, Long receiverId, String description, RequestStatus requestStatus) {
        MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto();
        mentorshipRequestDto.setRequesterId(requesterId);
        mentorshipRequestDto.setReceiverId(receiverId);
        mentorshipRequestDto.setDescription(description);
        mentorshipRequestDto.setStatus(requestStatus);
        return mentorshipRequestDto;
    }
}
