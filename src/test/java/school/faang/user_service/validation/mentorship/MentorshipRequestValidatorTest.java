package school.faang.user_service.validation.mentorship;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.handler.exception.DataValidationException;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.mentorship.MentorshipRequestValidator;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestValidatorTest {
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;


    @InjectMocks
    private MentorshipRequestValidator mentorshipRequestValidator;

    @Test
    void requestMentorshipValidationUserIdsTestReceiverIdEqualsRequesterId() {
        long receiverId = 1;
        long requesterId = 1;
        Assertions.assertThrows(IllegalArgumentException.class, () -> mentorshipRequestValidator.requestMentorshipValidationUserIds(requesterId, receiverId));
    }

    @Test
    void requestMentorshipValidationUserIdsTest() {
        long receiverId = 1;
        long requesterId = 2;

        Assertions.assertDoesNotThrow(() -> mentorshipRequestValidator.requestMentorshipValidationUserIds(requesterId, receiverId));
    }

    @Test
    void requestMentorshipValidationLatestRequestAssertTest() {
        when(mentorshipRequestRepository.findLatestRequest(anyLong(), anyLong()))
                .thenReturn(Optional.of(MentorshipRequest.builder()
                        .createdAt(LocalDateTime.of(2024, Month.MARCH, 10, 10, 10, 0))
                        .build()));
        Assertions.assertThrows(DataValidationException.class, () -> mentorshipRequestValidator.requestMentorshipValidationLatestRequest(1, 2));
    }

    @Test
    void requestMentorshipValidationLatestRequestNonAssertTest() {
        when(mentorshipRequestRepository.findLatestRequest(anyLong(), anyLong()))
                .thenReturn(Optional.of(MentorshipRequest.builder()
                        .createdAt(LocalDateTime.of(2023, Month.MARCH, 10, 10, 10, 0))
                        .build()));
        Assertions.assertDoesNotThrow(() -> mentorshipRequestValidator.requestMentorshipValidationLatestRequest(1, 2));
    }
}
