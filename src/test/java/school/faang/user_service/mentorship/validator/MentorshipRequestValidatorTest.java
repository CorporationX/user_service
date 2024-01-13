package school.faang.user_service.mentorship.validator;


import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataValidationException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.com.google.common.base.Optional;
import school.faang.user_service.entity.User;
import school.faang.user_service.mentorship.dto.MentorshipRequestDto;
import school.faang.user_service.mentorship.exception.MentorshipRequestException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestValidatorTest {

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @InjectMocks
    private MentorshipRequestValidator mentorshipRequestValidator;

    private MentorshipRequestDto mentorshipRequestDto;

    @Mock
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void init() {
        mentorshipRequestDto = new MentorshipRequestDto();
        mentorshipRequestDto.setRequester(1L);
        mentorshipRequestDto.setDescription("Description");
        mentorshipRequestDto.setRequester(88L);
        mentorshipRequestDto.setReceiver(77L);
        mentorshipRequestDto.setCreatedAt(LocalDateTime.now());
        user = new User();
        user.setId(1L);
        user.setUsername("John");
    }

    @Test
    public void testExceptionForEmptyData() {
        Assert.assertThrows(MentorshipRequestException.class, () ->
                mentorshipRequestValidator.mainMentorshipRequestValidation(mentorshipRequestDto));
    }

    @Test
    public void testExceptionWhenSameUser() {
        Mockito.when(userRepository.findById(mentorshipRequestDto.getRequester())).thenReturn();
        mentorshipRequestDto.setRequester(77L);
        mentorshipRequestDto.setReceiver(77L);
        Assert.assertThrows(MentorshipRequestException.class, () ->
                mentorshipRequestValidator.mainMentorshipRequestValidation(mentorshipRequestDto));
    }
}