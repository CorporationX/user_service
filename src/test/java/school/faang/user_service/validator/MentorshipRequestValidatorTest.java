package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.MentorshipRequestRepository;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestValidatorTest {
    @InjectMocks
    MentorshipRequestValidator mentorshipRequestValidator;
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private UserRepository userRepository;

    private MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto();

    private void prepareData(String string) {
        mentorshipRequestDto.setDescription(string);

        assertThrows(NoSuchElementException.class,
                () -> mentorshipRequestValidator.mentorshipRequestValidation(mentorshipRequestDto));
    }

    @Test
    public void testRequestMentorshipGetDescriptionNotNull() {
        prepareData(null);
    }

    @Test
    public void testRequestMentorshipWithExistingDescription() {
        prepareData("  ");
    }

    @Test
    public void testCheckingUsersInRepository_RequesterId() {
        assertThrows(NoSuchElementException.class,
                () -> mentorshipRequestValidator.checkingUsersInRepository(mentorshipRequestDto));
    }

    @Test
    public void testCheckingUsersInRepository_ReceiverId() {
        User user = new User();
        user.setId(1l);
        mentorshipRequestDto.setRequesterId(user.getId());
        when(userRepository.findById(mentorshipRequestDto.getRequesterId())).thenReturn(Optional.of(user));
        assertThrows(NoSuchElementException.class,
                () -> mentorshipRequestValidator.checkingUsersInRepository(mentorshipRequestDto));
    }

    @Test
    public void testCheckingForIdenticalIdsUsers() {
        mentorshipRequestDto.setRequesterId(1l);
        mentorshipRequestDto.setReceiverId(1l);

        assertThrows(NoSuchElementException.class,
                () -> mentorshipRequestValidator.checkingUsersInRepository(mentorshipRequestDto));
    }

    @Test
    public void testSpamCheckNoRequestWithinThreeMonths() {
        mentorshipRequestDto.setRequesterId(1L);

        MentorshipRequest oldRequest = new MentorshipRequest();
        oldRequest.setCreatedAt(LocalDateTime.now().minusMonths(4));

        when(mentorshipRequestRepository.findAllByRequesterId(1L)).thenReturn(Collections.singletonList(oldRequest));
        assertDoesNotThrow(() -> mentorshipRequestValidator.spamCheck(mentorshipRequestDto));
    }

    @Test
    public void testSpamCheckRequestWithinThreeMonths() {
        mentorshipRequestDto.setRequesterId(1L);

        MentorshipRequest recentRequest = new MentorshipRequest();
        recentRequest.setCreatedAt(LocalDateTime.now().minusMonths(2));

        when(mentorshipRequestRepository.findAllByRequesterId(1L)).thenReturn(Collections.singletonList(recentRequest));
        assertThrows(RuntimeException.class, () -> mentorshipRequestValidator.spamCheck(mentorshipRequestDto));
    }

    @Test
    public void testSpamCheckNoRequestsFound() {
        mentorshipRequestDto.setRequesterId(1L);

        when(mentorshipRequestRepository.findAllByRequesterId(1L)).thenReturn(Collections.emptyList());
        assertThrows(RuntimeException.class, () -> mentorshipRequestValidator.spamCheck(mentorshipRequestDto));
    }
}
