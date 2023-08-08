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
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.mentorship.MentorshipMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import java.time.LocalDateTime;
import java.util.Random;

@ExtendWith(value = {MockitoExtension.class})
class MentorshipRequestServiceTest {

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;

    @Mock
    private MentorshipMapper mentorshipMapper;

    @InjectMocks
    private MentorshipRequestController mentorshipRequestController;

    @BeforeEach
    void setUp() {
        mentorshipRequestService = new MentorshipRequestService(mentorshipRequestRepository, mentorshipMapper);
    }

    @Test
    void descriptionIsEmpty() {
        Long requestUserId = new Random().nextLong();
        Long receiverUserId = new Random().nextLong();
        MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto(LocalDateTime.now(), "  ", requestUserId, receiverUserId);

        Assert.assertThrows(IllegalArgumentException.class, () -> {
            mentorshipRequestController.requestMentorship(mentorshipRequestDto);
        });
    }

    @Test
    void requestUserEqualReceiver() {
        Long userId = new Random().nextLong();

        MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto(LocalDateTime.now(), "Test", userId, userId);

        Assert.assertThrows(IllegalArgumentException.class, () -> {
            mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        });
    }

    @Test
    void requestUserReceiverUserZero() {
        Long userId = 0L;
        MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto(LocalDateTime.now(), "Test", userId, userId);

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

        Assertions.assertDoesNotThrow(() -> new MentorshipRequestDto(LocalDateTime.now(), "Test", requestUser.getId(), receiverUser.getId()));

    }
}