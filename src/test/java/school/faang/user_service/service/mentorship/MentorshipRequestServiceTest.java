package school.faang.user_service.service.mentorship;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.mentorship.MentorshipRequestController;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.mentorship.MentorshipMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import java.time.LocalDateTime;
import java.util.Random;

import static org.mockito.Mockito.when;

@ExtendWith(value = {MockitoExtension.class})
class MentorshipRequestServiceTest {

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;

    @Mock
    private MentorshipMapper mentorshipMapper;

    @Mock
    private MentorshipFilter mentorshipFilter;

    @InjectMocks
    private MentorshipRequestController mentorshipRequestController;

    private RequestFilterDto requestFilterDto;

    @BeforeEach
    void setUp() {
        mentorshipRequestService = new MentorshipRequestService(mentorshipRequestRepository, mentorshipMapper, mentorshipFilter);
    }

    @Test
    void descriptionIsEmpty() {
        Long requestUserId = new Random().nextLong();
        Long receiverUserId = new Random().nextLong();
        MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto(LocalDateTime.now(), "  ", requestUserId, receiverUserId, RequestStatus.ACCEPTED);

        Assert.assertThrows(IllegalArgumentException.class, () -> {
            mentorshipRequestController.requestMentorship(mentorshipRequestDto);
        });
    }

    @Test
    void requestUserEqualReceiver() {
        Long userId = new Random().nextLong();

        MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto(LocalDateTime.now(), "Test", userId, userId, RequestStatus.ACCEPTED);

        Assert.assertThrows(IllegalArgumentException.class, () -> {
            mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        });
    }

    @Test
    void requestUserReceiverUserZero() {
        Long userId = 0L;
        MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto(LocalDateTime.now(), "Test", userId, userId, RequestStatus.ACCEPTED);

        Assert.assertThrows(IllegalArgumentException.class, () -> {
            mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        });
    }



    @Test
    void requesterNotEqualReceiver() {
        Long requestUserId = new Random().nextLong();
        Long receiverUserId = new Random().nextLong();

        User requestUser = new User().builder().id(requestUserId).build();
        User receiverUser = new User().builder().id(receiverUserId).build();

        mentorshipRequestRepository.create(requestUser.getId(), receiverUser.getId(), "Test");

        Assertions.assertDoesNotThrow(() -> new MentorshipRequestDto(LocalDateTime.now(), "Test", requestUser.getId(), receiverUser.getId(), RequestStatus.ACCEPTED));

    }

    @Test
    void requestFilterDtoIsWorked() {
        RequestFilterDto requestFilterDto = new RequestFilterDto("Test", 0L, 1L, RequestStatus.ACCEPTED);
        Assertions.assertDoesNotThrow(() -> mentorshipRequestService.getRequests(requestFilterDto));
    }

    @Test
    void requestFilterDtoIsNotWorked() {
        when(mentorshipRequestService.getRequests(null)).thenThrow(NullPointerException.class);
        Assertions.assertThrows(NullPointerException.class, () -> mentorshipRequestService.getRequests(null));
    }
}