package school.faang.user_service.requestformentoring.helper.validator;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.helper.validator.MentorshipRequestValidator;
import school.faang.user_service.services.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestValidatorTest {
    private static final long MONTHS_REQUEST = 3;
    private static final long REQUESTER_ID = 1L;
    private static final long RECEIVER_ID = 2L;
    private static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.of(2024, 5, 22, 20, 20, 20);
    private final MentorshipRequestDto menReqDto = new MentorshipRequestDto();
    private final MentorshipRequest menReqEntity = new MentorshipRequest();
    private final User receiver = new User();
    private final User requester = new User();
    @Mock
    private UserService userService;
    @InjectMocks
    private MentorshipRequestValidator menReqValidator;
    private List<User> users = new ArrayList<>();

    @BeforeEach
    void init() {
        receiver.setId(RECEIVER_ID);

        requester.setId(REQUESTER_ID);

        menReqDto.setRequesterId(REQUESTER_ID);
        menReqDto.setReceiverId(RECEIVER_ID);

        menReqEntity.setCreatedAt(LOCAL_DATE_TIME);
        menReqEntity.setRequester(requester);
        menReqEntity.setReceiver(receiver);
    }

    @Nested
    class PositiveTest {

        @Test
        @DisplayName("Выбрасываем исключение если ID пользователей равны")
        void whenRequesterIdEqualsReceiverIdThenException() {
            menReqDto.setRequesterId(2L);

            assertEquals("The user cannot designate himself as a requester.",
                    assertThrows(IllegalArgumentException.class, () -> {
                        menReqValidator.validateReceiverNoEqualsRequester(menReqDto);
                    }).getMessage());
        }

        @Test
        @DisplayName("Выбрасываем исключение если пользователь отсутсвует в БД")
        void whenValidateDBThenException() {
            List<Long> userIds = new ArrayList<>();
            userIds.add(REQUESTER_ID);
            userIds.add(RECEIVER_ID);

            users.add(requester);

            when(userService.getUsersById(userIds)).thenReturn(users);

            assertEquals("The user ID is not correct",
                    assertThrows(EntityNotFoundException.class, () -> {
                        menReqValidator.validateAvailabilityUsersDB(menReqDto);
                    }).getMessage());

            verify(userService).getUsersById(userIds);
        }

        @Test
        @DisplayName("Выбрасываем исключение если с момента последней подачи реквеста не прошло MONTHS_REQUEST месяца")
        void whenValidateUpdateThenException() {
            assertEquals("It should pass " + MONTHS_REQUEST +
                            " one month since the last application for mentoring",
                    assertThrows(IllegalArgumentException.class, () -> {
                        menReqValidator.validateDataCreateRequest(menReqEntity);
                    }).getMessage());
        }

        @Test
        @DisplayName("Выбрасываем исключение если пользователь является реквестером ресипиента")
        void whenValidateMentorsThenException() {
            List<User> mentors = new ArrayList<>();
            mentors.add(requester);

            receiver.setMentors(mentors);

            assertEquals("The user is already your mentor",
                    assertThrows(IllegalArgumentException.class, () -> {
                        menReqValidator.validateMentorsContainsReceiver(menReqEntity);
                    }).getMessage());
        }
    }
}