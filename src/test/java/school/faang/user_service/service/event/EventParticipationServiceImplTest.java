package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.entity.contact.ContactPreference;
import school.faang.user_service.entity.contact.ContactType;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.ContactMapperImpl;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;


@ExtendWith(MockitoExtension.class)
class EventParticipationServiceImplTest {
    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Spy
    private UserMapperImpl userMapper;

    @Spy
    private ContactMapperImpl contactMapper;

    @InjectMocks
    private EventParticipationServiceImpl eventParticipationService;

    private List<User> userList;
    List<UserDto> expectedDtoList;
    private long eventId;
    private long userId;

    @BeforeEach
    void setup() {
        eventId = 1L;
        userId = 1L;

        userList = List.of(
                createTestUser(1L, "user1", "user1@example.com"),
                createTestUser(2L, "user2", "user2@example.com")
        );

        expectedDtoList = List.of(
                new UserDto(1L, "user1", "user1@example.com"),
                new UserDto(2L, "user2", "user2@example.com")
        );
    }

    @Test
    @DisplayName("Регистрация пользователя, по eventId и userId. Пользователь в БД уже присутствует. Выбрасывание ошибки.")
    void testRegisterParticipant_ParticipantIsPresentInDB() {
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(userList);

        assertThrows(DataValidationException.class, () -> eventParticipationService.registerParticipant(eventId, userId));
    }

    @Test
    @DisplayName("Регистрация пользователя, по eventId и userId. Пользователь в БД отсутствует. Успешная регистрация.")
    void testRegisterParticipant_ParticipantNotPresentInDB() {
        userId = 4L;
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(userList);

        eventParticipationService.registerParticipant(eventId, userId);

        verify(eventParticipationRepository, times(1)).register(eventId, userId);
    }

    @Test
    @DisplayName("Разрегистрация пользователя, по eventId. Пользователь в БД отсутствует. Выбрасывание ошибки.")
    void testUnregisterParticipant_ParticipantNotPresentInDB() {
        userId = 4L;
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(userList);

        assertThrows(DataValidationException.class, () -> eventParticipationService.unregisterParticipant(eventId, userId));
    }

    @Test
    @DisplayName("Разрегистрация пользователя, по eventId. Пользователь в БД присутствует.")
    void testUnregisterParticipant_ParticipantIsPresentInDB() {
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(userList);

        eventParticipationService.unregisterParticipant(eventId, userId);

        verify(eventParticipationRepository, times(1)).unregister(eventId, userId);
    }

    @Test
    @DisplayName("Проверка на получение списка UserDto по eventId.")
    void testGetParticipant_WhenParticipantsExist_ShouldReturnListOfUserDtos() {
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(userList);

        List<UserDto> userDtoList = eventParticipationService.getParticipant(eventId);

        assertNotNull(userDtoList);
        assertEquals(2, userDtoList.size());
        assertEquals(expectedDtoList, userDtoList);
        verify(userMapper, times(userDtoList.size())).toUserDto(any(User.class));
    }

    @Test
    @DisplayName("Проверка на пустой список участников")
    void testGetParticipant_WhenNoParticipants_ShouldReturnEmptyList() {
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(List.of());

        List<UserDto> userDtoList = eventParticipationService.getParticipant(eventId);

        assertNotNull(userDtoList);
        assertEquals(0, userDtoList.size());
    }

    @Test
    @DisplayName("Проверка на поиск верного кол-ва участников по eventId")
    void testGetParticipantsCount_ShouldReturnCorrectCount() {
        long expectedCount = 5L;
        when(eventParticipationRepository.countParticipants(eventId)).thenReturn((int) expectedCount);

        long actualCount = eventParticipationService.getParticipantsCount(eventId);

        assertEquals(expectedCount, actualCount);
        verify(eventParticipationRepository).countParticipants(eventId);
    }

    @Test
    @DisplayName("Проверка на отсутствие участников в БД")
    void testGetParticipantsCount_WhenNoParticipants() {
        eventId = 5L;
        when(eventParticipationRepository.countParticipants(eventId)).thenReturn(0);

        long actualCount = eventParticipationService.getParticipantsCount(eventId);

        assertEquals(0, actualCount);
        verify(eventParticipationRepository).countParticipants(eventId);
    }

    private User createTestUser(Long id, String username, String email) {
        return User.builder()
                .id(id)
                .username(username)
                .email(email)
                .password("password")
                .contactPreference(ContactPreference.builder().preference(PreferredContact.TELEGRAM).build())
                .contacts(List.of(Contact.builder().type(ContactType.TELEGRAM).contact("contact1").build()))
                .active(true)
                .build();
    }
}