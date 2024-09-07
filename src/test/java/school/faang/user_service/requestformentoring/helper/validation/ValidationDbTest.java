package school.faang.user_service.requestformentoring.helper.validation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.requestformentoring.helper.exeptions.MentorshipRequestNotFoundException;
import school.faang.user_service.requestformentoring.helper.exeptions.TemporaryDataIsIncorrect;
import school.faang.user_service.requestformentoring.helper.exeptions.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ValidationDbTest {
    private static final long RECEIVER_ID = 1;
    private static final long REQUESTER_ID = 2;
    private static final int MONTH = 3;
    private static final User REQUESTER = new User();
    private static final MentorshipRequest mentorshipRequest = new MentorshipRequest();
    private static final LocalDateTime DATA_LAST_REQUEST = LocalDateTime.of(2024, 1, 1, 0, 0);
    @InjectMocks
    private ValidationDb validationDb;
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private UserRepository userRepository;

    @BeforeAll
    static void init() {
        REQUESTER.setId(RECEIVER_ID);
        mentorshipRequest.setUpdatedAt(DATA_LAST_REQUEST);
    }

    @Test
    void testCheckingAvailabilityUsersDbPositive() {
        when(userRepository.findUserById(RECEIVER_ID)).thenReturn(Optional.of(REQUESTER));

        Optional<User> result = userRepository.findUserById(RECEIVER_ID);
        assertTrue(result.isPresent());
        assertDoesNotThrow(() -> validationDb.checkingAvailabilityUsersDb(REQUESTER.getId()));
        assertEquals(REQUESTER.getId(), result.get().getId());
    }

    @Test
    void testCheckingAvailabilityUsersDbNegative() {
        when(userRepository.findUserById(RECEIVER_ID)).thenReturn(Optional.empty());

        Optional<User> result = userRepository.findUserById(RECEIVER_ID);

        assertFalse(result.isPresent());
        assertEquals("Пользователь не найден.",
                assertThrows(UserNotFoundException.class, () ->
                        validationDb.checkingAvailabilityUsersDb(RECEIVER_ID))
                        .getMessage());
    }

    @Test
    void testChecksLastRequestMentoringPositive() {
        LocalDateTime dateLastRequest = LocalDateTime.now().minusMonths(MONTH);
        mentorshipRequest.setUpdatedAt(dateLastRequest);

        when(mentorshipRequestRepository.findLatestRequest(REQUESTER_ID, RECEIVER_ID))
                .thenReturn(Optional.of(mentorshipRequest));

        Optional<MentorshipRequest> result = mentorshipRequestRepository
                .findLatestRequest(REQUESTER_ID, RECEIVER_ID);
        assertTrue(result.isPresent());
        assertEquals(result.get().getUpdatedAt(), mentorshipRequest.getUpdatedAt());
        assertDoesNotThrow(() -> validationDb.checksLastRequestMentoring(REQUESTER_ID, RECEIVER_ID, MONTH));

    }

    @Test
    void testChecksLastRequestMentoringNegative() {
        LocalDateTime dateLastRequest = LocalDateTime.now().plusMonths(MONTH);
        mentorshipRequest.setUpdatedAt(dateLastRequest);

        when(mentorshipRequestRepository.findLatestRequest(REQUESTER_ID, RECEIVER_ID))
                .thenReturn(Optional.of(mentorshipRequest));

        Optional<MentorshipRequest> result = mentorshipRequestRepository
                .findLatestRequest(REQUESTER_ID, RECEIVER_ID);
        assertTrue(result.isPresent());
        assertEquals("3 months must have passed since the request was submitted!",
                assertThrows(TemporaryDataIsIncorrect.class, () ->
                        validationDb.checksLastRequestMentoring(REQUESTER_ID, RECEIVER_ID, MONTH))
                        .getMessage());
    }

    @Test
    void testCheckAvailabilityMentorshipRequestDbPositive() {
        when(mentorshipRequestRepository.findMentorshipRequestById(RECEIVER_ID))
                .thenReturn(Optional.of(mentorshipRequest));

        Optional<MentorshipRequest> result = mentorshipRequestRepository
                .findMentorshipRequestById(RECEIVER_ID);
        assertTrue(result.isPresent());
        assertEquals(result.get(), mentorshipRequest);
        assertDoesNotThrow(() -> validationDb.checkAvailabilityMentorshipRequestDb(RECEIVER_ID));
    }

    @Test
    void testCheckAvailabilityMentorshipRequestDbNegative() {
        when(mentorshipRequestRepository.findMentorshipRequestById(RECEIVER_ID))
                .thenReturn(Optional.empty());

        Optional<MentorshipRequest> result = mentorshipRequestRepository
                .findMentorshipRequestById(RECEIVER_ID);
        assertFalse(result.isPresent());
        assertEquals("Запрос не найден. Для начала нужно создать запрос.",
                assertThrows(MentorshipRequestNotFoundException.class,
                        () -> validationDb.checkAvailabilityMentorshipRequestDb(RECEIVER_ID))
                        .getMessage());
    }
}