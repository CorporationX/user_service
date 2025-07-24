package school.faang.user_service.validator.mentorship;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CheckingRequestDateValidatorTest {
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    private CheckingRequestDateValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CheckingRequestDateValidator(mentorshipRepository, mentorshipRequestRepository);
    }

    @Test
    @DisplayName("validate should throw when request was recent")
    void validateShouldThrowWhenRequestTooRecent() {
        MentorshipRequestDto dto = new MentorshipRequestDto(1L, 2L, "test");
        MentorshipRequest recentRequest = MentorshipRequest.builder()
                .createdAt(LocalDateTime.now().minusMonths(1)).build();

        Mockito.when(mentorshipRequestRepository.findLatestRequest(1L, 2L))
                .thenReturn(Optional.of(recentRequest));

        Assertions.assertThrows(ResponseStatusException.class, () -> validator.validate(dto));
    }

    @Test
    @DisplayName("validate should pass when request too old")
    void validate_shouldPass_whenRequestOldEnough() {
        MentorshipRequestDto dto = new MentorshipRequestDto(1L, 2L, "test");
        MentorshipRequest oldRequest = MentorshipRequest.builder()
                .createdAt(LocalDateTime.now().minusMonths(6)).build();
        Mockito.when(mentorshipRequestRepository.findLatestRequest(1L, 2L))
                .thenReturn(Optional.of(oldRequest));

        Assertions.assertDoesNotThrow(() -> validator.validate(dto));
    }

    @Test
    @DisplayName("validate should pass when no previous request")
    void validate_shouldPass_whenNoPreviousRequest() {
        MentorshipRequestDto dto = new MentorshipRequestDto(1L, 2L, "test");
        Mockito.when(mentorshipRequestRepository.findLatestRequest(1L, 2L))
                .thenReturn(Optional.empty());
        Assertions.assertDoesNotThrow(() -> validator.validate(dto));
    }
}