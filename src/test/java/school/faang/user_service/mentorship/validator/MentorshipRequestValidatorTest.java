package school.faang.user_service.mentorship.validator;


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
import school.faang.user_service.exception.mentorship.MentorshipRequestException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.mentorship.MentorshipRequestValidator;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

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

    @BeforeEach
    public void init() {
        mentorshipRequestDto = new MentorshipRequestDto();
        mentorshipRequestDto.setRequester(1L);
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
    }

    @Test
    public void testExceptionForEmptyData() {
        Assert.assertThrows(MentorshipRequestException.class, () ->
                mentorshipRequestValidator.mainMentorshipRequestValidation(mentorshipRequestDto));
    }

    @Test
    public void testExceptionWhenSameUser() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(requester));
        Assert.assertThrows(MentorshipRequestException.class, () ->
                mentorshipRequestValidator.mainMentorshipRequestValidation(mentorshipRequestDto));
    }

    @Test
    public void testSecondRequestInLast90Days() {
        Mockito.when(userRepository.findById(mentorshipRequestDto.getRequester())).thenReturn(Optional.of(requester));
        Mockito.when(userRepository.findById(mentorshipRequestDto.getReceiver())).thenReturn(Optional.of(receiver));
        Mockito.when(mentorshipRequestRepository
                .findLatestRequest(requester.getId(), receiver.getId())).thenReturn(Optional.of(mentorshipRequest));
        mentorshipRequest.setCreatedAt(LocalDateTime.now().minusMonths(1));
        Assert.assertThrows(MentorshipRequestException.class, () ->
                mentorshipRequestValidator.mainMentorshipRequestValidation(mentorshipRequestDto));
    }

    @Test
    public void testCorrectData() {
        Mockito.when(userRepository.findById(mentorshipRequestDto.getRequester())).thenReturn(Optional.of(requester));
        Mockito.when(userRepository.findById(mentorshipRequestDto.getReceiver())).thenReturn(Optional.of(receiver));
        Mockito.when(mentorshipRequestRepository
                .findLatestRequest(requester.getId(), receiver.getId())).thenReturn(Optional.of(mentorshipRequest));
        mentorshipRequest.setCreatedAt(LocalDateTime.now().minusMonths(4));
        mentorshipRequestValidator.mainMentorshipRequestValidation(mentorshipRequestDto);
    }
}