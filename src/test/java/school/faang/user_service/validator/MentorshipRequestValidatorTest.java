package school.faang.user_service.validator;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.model.dto.MentorshipRequestDto;
import school.faang.user_service.model.entity.MentorshipRequest;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.mentorship.MentorshipRequestValidator;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestValidatorTest {
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MentorshipRequestValidator mentorshipRequestValidator;

    @Test
    @DisplayName("Validate Mentorship Request Test Success")
    void testValidateMentorshipRequest() {
        var dto = MentorshipRequestDto.builder()
                .requesterId(1L)
                .receiverId(2L)
                .description("Test Description")
                .createdAt(LocalDateTime.now().plusDays(1))
                .build();

        doReturn(true).when(userRepository).existsById(anyLong());
        mentorshipRequestValidator.validateMentorshipRequest(dto);
        verify(userRepository).existsById(anyLong());
        assertThatNoException().isThrownBy(() -> mentorshipRequestValidator.validateMentorshipRequest(dto));

    }

    @Test
    @DisplayName("Validate Mentorship Request With blank description")
    void testValidateMentorshipRequestWithBlankDescription() {
        var dto = MentorshipRequestDto.builder()
                .requesterId(1L)
                .receiverId(2L)
                .description("")
                .createdAt(LocalDateTime.now().plusDays(1))
                .build();

        doReturn(true).when(userRepository).existsById(anyLong());
        assertThatExceptionOfType(DataValidationException.class).isThrownBy(() ->
                mentorshipRequestValidator.validateMentorshipRequest(dto))
                .withMessage("Description is blank, please enter a description");
    }

    @Test
    @DisplayName("Validate Mentorship Request With Equal users")
    void testValidateMentorshipRequestWithEqualUsers() {
        var dto = MentorshipRequestDto.builder()
                .requesterId(1L)
                .receiverId(1L)
                .description("")
                .createdAt(LocalDateTime.now().plusDays(1))
                .build();

        doReturn(true).when(userRepository).existsById(anyLong());
        assertThatExceptionOfType(DataValidationException.class).isThrownBy(() ->
                        mentorshipRequestValidator.validateMentorshipRequest(dto))
                .withMessage("You can't make request to yourself");
    }

    @Test
    @DisplayName("Validate Mentorship Request With Not Existing User Throws Exception")
    void testValidateMentorshipRequestWithNotExistingUser() {
        var dto = MentorshipRequestDto.builder()
                .requesterId(1L)
                .receiverId(2L)
                .description("")
                .createdAt(LocalDateTime.now().plusDays(1))
                .build();

        doReturn(false).when(userRepository).existsById(anyLong());
        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() ->
                        mentorshipRequestValidator.validateMentorshipRequest(dto))
                .withMessage("Requester or Receiver not found");
    }

    @Test
    @DisplayName("Validate Date Create Request Doesn't Throws Exception")
    void testValidateDateCreateRequestDoesNotThrowException() {
        var request = MentorshipRequest.builder()
                .createdAt(LocalDateTime.now().minusMonths(3))
                .build();
        doReturn(Optional.of(request)).when(mentorshipRequestRepository).findLatestRequest(anyLong(), anyLong());
        assertThatNoException().isThrownBy(() ->
                mentorshipRequestValidator.validateDateCreateRequest(anyLong(), anyLong()));
    }

    @Test
    @DisplayName("Validate Date Create Request Throws Exception")
    void testValidateDateCreateRequestThrowsException() {
        var request = MentorshipRequest.builder()
                .createdAt(LocalDateTime.now().minusMonths(1))
                .build();

        doReturn(Optional.of(request)).when(mentorshipRequestRepository).findLatestRequest(anyLong(), anyLong());
        assertThatExceptionOfType(DataValidationException.class).isThrownBy(() ->
                mentorshipRequestValidator.validateDateCreateRequest(anyLong(), anyLong()))
                .withMessage("Must be 3 months from last mentoring request");
    }

}