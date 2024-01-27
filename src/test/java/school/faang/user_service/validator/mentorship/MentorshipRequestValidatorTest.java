package school.faang.user_service.validator.mentorship;


import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.exception.mentorship.DataNotFoundException;
import school.faang.user_service.exception.mentorship.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestValidatorTest {

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @InjectMocks
    private MentorshipRequestValidator mentorshipRequestValidator;

    private MentorshipRequestDto mentorshipRequestDto;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MentorshipRequest mentorshipRequest;

    private User requester;

    private User receiver;

    private User sameUser;

    @BeforeEach
    public void init() {
        mentorshipRequestDto = new MentorshipRequestDto();
        mentorshipRequestDto.setRequester(1L);
        mentorshipRequestDto.setId(3L);
        mentorshipRequestDto.setDescription("Description");
        mentorshipRequestDto.setRequester(88L);
        mentorshipRequestDto.setReceiver(77L);
        mentorshipRequestDto.setCreatedAt(LocalDateTime.now());
        requester = new User();
        requester.setId(1L);
        requester.setUsername("John");
        receiver = new User();
        receiver.setId(2L);
        receiver.setUsername("Mike");
        sameUser = new User();
        sameUser.setId(2L);
        sameUser.setUsername("Mike");
    }

    @Test
    public void testExceptionForEmptyData() {
        Assert.assertThrows(DataNotFoundException.class, () ->
                mentorshipRequestValidator.validateUserData(new User(), new User()));
    }

    @Test
    public void testCommonCheck() {
        try {
            mentorshipRequestValidator.commonCheck(mentorshipRequestDto);
        } catch (DataNotFoundException e) {
            fail("Should not have thrown any exception");
        }
    }

    @Test
    public void testExceptionWhenSameUser() {
        Assert.assertThrows(DataNotFoundException.class, () ->
                mentorshipRequestValidator.validateUserData(receiver, sameUser));
    }

    @Test
    public void testSecondRequestInLast90Days() {
        Mockito.when(mentorshipRequestRepository
                .findLatestRequest(requester.getId(), receiver.getId())).thenReturn(Optional.of(mentorshipRequest));
        mentorshipRequest.setCreatedAt(LocalDateTime.now().minusMonths(1));
        Assert.assertThrows(DataValidationException.class, () ->
                mentorshipRequestValidator.validateUserData(receiver, requester));
    }

    @Test
    public void testCorrectData() {
        try {
            mentorshipRequestValidator.validateUserData(receiver, requester);
        } catch (DataNotFoundException e) {
            fail("Should not have thrown any exception");
        }
    }
}