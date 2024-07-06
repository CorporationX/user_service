package school.faang.user_service.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.exception.message.ExceptionMessage.*;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestValidatorTest {

    @InjectMocks
    private MentorshipRequestValidator mentorshipRequestValidator;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    private final MentorshipRequestDto mentorshipRequestDto = MentorshipRequestDto.builder()
            .requesterId(1L)
            .receiverId(2L)
            .build();

    @Nested
    class PositiveTests {

        @DisplayName("should pass all methods when everything alright")
        @Test
        void validateMentorshipRequestTest() {
            MentorshipRequest mentorshipRequest = new MentorshipRequest();
            mentorshipRequest.setCreatedAt(LocalDateTime.of(2020, 7, 2, 16, 6));
            long requesterId = mentorshipRequestDto.getRequesterId();
            long receiverId = mentorshipRequestDto.getReceiverId();

            when(userRepository.existsById(requesterId)).thenReturn(true);
            when(userRepository.existsById(receiverId)).thenReturn(true);
            when(mentorshipRequestRepository.findLatestRequest(requesterId, receiverId)).thenReturn(Optional.of(mentorshipRequest));

            mentorshipRequestValidator.validateMentorshipRequest(mentorshipRequestDto);

            verify(userRepository).existsById(requesterId);
            verify(userRepository).existsById(receiverId);
            verify(mentorshipRequestRepository).findLatestRequest(requesterId, receiverId);
        }
    }

    @Nested
    class NegativeTests {

        @DisplayName("should throw exception when there is no requester in DB")
        @Test
        void validateMentorshipRequestWithoutRequesterTest() {
            when(userRepository.existsById(mentorshipRequestDto.getRequesterId())).thenReturn(false);

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> mentorshipRequestValidator.validateMentorshipRequest(mentorshipRequestDto));
            assertEquals(NO_SUCH_USER_EXCEPTION.getMessage(), exception.getMessage());
        }

        @DisplayName("should throw exception when there is no receiver in DB")
        @Test
        void validateMentorshipRequestWithoutReceiverTest() {
            when(userRepository.existsById(mentorshipRequestDto.getRequesterId())).thenReturn(true);
            when(userRepository.existsById(mentorshipRequestDto.getReceiverId())).thenReturn(false);

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> mentorshipRequestValidator.validateMentorshipRequest(mentorshipRequestDto));
            assertEquals(NO_SUCH_USER_EXCEPTION.getMessage(), exception.getMessage());
        }

        @DisplayName("should throw exception when requester equals receiver")
        @Test
        void validateMentorshipRequestWhenEquals() {
            mentorshipRequestDto.setReceiverId(1L);

            when(userRepository.existsById(mentorshipRequestDto.getRequesterId())).thenReturn(true);
            when(userRepository.existsById(mentorshipRequestDto.getReceiverId())).thenReturn(true);

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> mentorshipRequestValidator.validateMentorshipRequest(mentorshipRequestDto));
            assertEquals(REQUESTER_ID_EQUALS_RECEIVER_ID.getMessage(), exception.getMessage());
        }

        @DisplayName("should throw exception when requester already has status")
        @Test
        void validateMentorshipRequestWhenStatusTest() {
            mentorshipRequestDto.setStatus(RequestStatus.PENDING);

            when(userRepository.existsById(mentorshipRequestDto.getRequesterId())).thenReturn(true);
            when(userRepository.existsById(mentorshipRequestDto.getReceiverId())).thenReturn(true);

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> mentorshipRequestValidator.validateMentorshipRequest(mentorshipRequestDto));
            assertEquals(REQUEST_ALREADY_HAS_STATUS.getMessage(), exception.getMessage());
        }

        @DisplayName("should throw exception when last request was in 3 months")
        @Test
        void validateMentorshipRequestWhenTooMuchRequestsInDBTest() {
            MentorshipRequest mentorshipRequest = new MentorshipRequest();
            mentorshipRequest.setCreatedAt(LocalDateTime.now());

            when(userRepository.existsById(mentorshipRequestDto.getRequesterId())).thenReturn(true);
            when(userRepository.existsById(mentorshipRequestDto.getReceiverId())).thenReturn(true);
            when(mentorshipRequestRepository.findLatestRequest(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto
                    .getReceiverId())).thenReturn(Optional.of(mentorshipRequest));

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> mentorshipRequestValidator.validateMentorshipRequest(mentorshipRequestDto));
            assertEquals(TOO_MUCH_REQUESTS.getMessage(), exception.getMessage());
        }
    }
}
