package school.faang.user_service.validator.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipEligibilityValidatorTest {

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @InjectMocks
    private MentorshipEligibilityValidator validator;

    @Test
    void validate_ThrowsException_WhenRequestIsTooSoon() {
        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setRequesterId(1L);
        dto.setReceiverId(2L);

        when(mentorshipRequestRepository.findLatestRequest(1L, 2L))
                .thenReturn(Optional.of(new MentorshipRequest() {{
                    setCreatedAt(LocalDateTime.now().minusMonths(2));
                }}));

        assertThrows(IllegalStateException.class, () -> validator.validate(dto));
    }

    @Test
    void validate_DoesNothing_WhenRequestIsOldEnough() {
        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setRequesterId(1L);
        dto.setReceiverId(2L);

        when(mentorshipRequestRepository.findLatestRequest(1L, 2L))
                .thenReturn(Optional.of(new MentorshipRequest() {{
                    setCreatedAt(LocalDateTime.now().minusMonths(5));
                }}));

        assertDoesNotThrow(() -> validator.validate(dto));
    }
}