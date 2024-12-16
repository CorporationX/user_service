package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.subscribe.UserDTO;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.exceptions.ParticipantRegistrationException;
import school.faang.user_service.mapper.UserDTOMapperImpl;
import school.faang.user_service.publisher.EventRegistrationEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventParticipationServiceTest {
    private static final long EVENT_ID = 1L;
    private static final long USER_ID = 1L;

    @InjectMocks
    private EventParticipationService service;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EventParticipationRepository repository;
    @Mock
    private EventRegistrationEventPublisher eventRegistrationEventPublisher;
    @Spy
    private UserDTOMapperImpl userDTOMapper;


    @Test
    void register_shouldRegisterUserForEvent_whenUserNotRegisteredYet() {
        // given
        User user = createUser();
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(repository.existsByEventIdAndUserId(EVENT_ID, USER_ID)).thenReturn(false);

        // when
        service.register(EVENT_ID, USER_ID);

        // then
        verify(repository).register(EVENT_ID, USER_ID);
        verify(eventRegistrationEventPublisher, times(1)).publish(any());
    }

    @Test
    void register_shouldThrowException_whenUserAlreadyRegistered() {
        // given
        User user = createUser();
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(repository.existsByEventIdAndUserId(EVENT_ID, USER_ID)).thenReturn(true);

        // when
        ParticipantRegistrationException exception = assertThrows(ParticipantRegistrationException.class, () -> {
            service.register(EVENT_ID, USER_ID);
        });

        // then
        verify(repository, never()).register(EVENT_ID, USER_ID);

        assertEquals("User already registered", exception.getMessage());
    }


    @Test
    void unregister_shouldUnregisterUserFromEvent_whenUserIsRegistered() {
        // given
        when(repository.existsByEventIdAndUserId(EVENT_ID, USER_ID)).thenReturn(true);

        // when
        service.unregister(EVENT_ID, USER_ID);

        // then
        verify(repository).unregister(EVENT_ID, USER_ID);
    }

    @Test
    void unregister_shouldThrowException_whenUserIsNotRegistered() {
        // given
        when(repository.existsByEventIdAndUserId(EVENT_ID, USER_ID)).thenReturn(false);

        // when
        ParticipantRegistrationException exception = assertThrows(ParticipantRegistrationException.class,
                () -> service.unregister(EVENT_ID, USER_ID));

        // then
        verify(repository, never()).unregister(EVENT_ID, USER_ID);
        assertEquals("User isn't registered", exception.getMessage());
    }

    @Test
    void findAllParticipantsByEventId_shouldReturnListOfParticipants() {
        // given
        List<User> expectedUsers = getUserList();
        List<UserDTO> expectedUserDTOs = getUserDTOList();
        when(repository.findAllParticipantsByEventId(EVENT_ID)).thenReturn(expectedUsers);

        // when
        List<UserDTO> actualUsers = service.findAllParticipantsByEventId(EVENT_ID);

        // then
        verify(repository).findAllParticipantsByEventId(Mockito.anyLong());
        verify(userDTOMapper).toDTO(expectedUsers);
        assertEquals(expectedUserDTOs, actualUsers);
    }

    @Test
    void countParticipants_shouldReturnNumberOfParticipants() {
        // given
        int expectedCount = 5;
        when(repository.countParticipants(EVENT_ID)).thenReturn(expectedCount);

        // when
        int actualCount = service.countParticipants(EVENT_ID);


        // then
        verify(repository).countParticipants(Mockito.anyLong());
        assertEquals(expectedCount, actualCount);
    }

    private List<UserDTO> getUserDTOList(){
        List<UserDTO> users = new ArrayList<>();
        UserDTO user1 = new UserDTO(1L, "john_doe", "john@example.com");
        UserDTO user2 = new UserDTO(2L, "jane_smith", "jane@example.com");
        UserDTO user3 = new UserDTO(3L, "alice_brown", "alice@example.com");

        users.add(user1);
        users.add(user2);
        users.add(user3);

        return users;
    }

    private List<User> getUserList(){
        List<User> users = new ArrayList<>();
        User user1 = User.builder()
                .id(1L)
                .username("john_doe")
                .email("john@example.com")
                .phone("+1234567890")
                .password("password123")
                .active(true)
                .aboutMe("Software developer with 5 years of experience.")
                .country(new Country(1L, "USA", List.of()))
                .city("New York")
                .experience(5)
                .build();

        User user2 = User.builder()
                .id(2L)
                .username("jane_smith")
                .email("jane@example.com")
                .phone("+0987654321")
                .password("password456")
                .active(true)
                .aboutMe("Project manager and team leader.")
                .country(new Country(2L, "Canada", List.of()))
                .city("Toronto")
                .experience(8)
                .build();

        User user3 = User.builder()
                .id(3L)
                .username("alice_brown")
                .email("alice@example.com")
                .phone("+1122334455")
                .password("password789")
                .active(false)
                .aboutMe("Graphic designer with a love for minimalism.")
                .country(new Country(3L, "UK", List.of()))
                .city("London")
                .experience(3)
                .build();

        users.add(user1);
        users.add(user2);
        users.add(user3);

        return users;
    }

    private User createUser(){
        return User.builder()
                .id(2L)
                .username("jane_smith")
                .email("jane@example.com")
                .phone("+0987654321")
                .password("password456")
                .active(true)
                .aboutMe("Project manager and team leader.")
                .country(new Country(2L, "Canada", List.of()))
                .city("Toronto")
                .experience(8)
                .build();
    }
}
