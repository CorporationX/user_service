package school.faang.user_service.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.json.student.Address;
import com.json.student.ContactInfo;
import com.json.student.Person;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.publisher.ProfileViewEvent;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.BadRequestException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.filters.user.UserFilter;
import school.faang.user_service.mapper.DeactivatedUserMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.publisher.ProfileViewEventPublisher;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.adapter.EventParticipationRepositoryAdapter;
import school.faang.user_service.repository.adapter.EventRepositoryAdapter;
import school.faang.user_service.repository.adapter.GoalRepositoryAdapter;
import school.faang.user_service.repository.adapter.UserRepositoryAdapter;
import school.faang.user_service.service.mentorship.MentorshipService;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private UserService userService;

    private List<UserFilter> userFilters;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserContext userContext;

    @Mock
    private ProfileViewEventPublisher profileViewEventPublisher;

    @Mock
    private DeactivatedUserMapper deactivatedUserMapper;

    @Mock
    private UserRepositoryAdapter userRepositoryAdapter;

    @Mock
    private GoalRepositoryAdapter goalRepositoryAdapter;

    @Mock
    private EventRepositoryAdapter eventRepositoryAdapter;

    @Mock
    private EventParticipationRepositoryAdapter eventParticipationRepositoryAdapter;

    @Mock
    private MentorshipService mentorshipService;

    @Mock
    private CountryRepository countryRepository;

    @Captor
    private ArgumentCaptor<List<User>> listUsers;

    @Captor
    private ArgumentCaptor<ProfileViewEvent> profileViewEvent;

    private final UserDto dto = new UserDto();

    private final User user = new User();

    @BeforeEach
    void init() {
        dto.setId(1L);
        dto.setUsername("John");
        dto.setEmail("john@gmail.com");

        user.setId(1L);
        user.setUsername("John");
        user.setEmail("john@gmail.com");

        UserFilter mockFirstUserFilter = Mockito.mock(UserFilter.class);
        UserFilter mockSecondUserFilter = Mockito.mock(UserFilter.class);
        userFilters = List.of(mockFirstUserFilter, mockSecondUserFilter);

        userService =
                new UserService(
                        userRepository,
                        userFilters,
                        profileViewEventPublisher,
                        userContext,
                        userMapper,
                        deactivatedUserMapper,
                        userRepositoryAdapter,
                        goalRepositoryAdapter,
                        eventRepositoryAdapter,
                        eventParticipationRepositoryAdapter,
                        countryRepository,
                        mentorshipService);
    }

    @Test
    void testGetPremiumUsers() {
        User user1 = new User();
        User user2 = new User();

        UserFilterDto userFilterDto = new UserFilterDto();

        UserDto firstUserDto = new UserDto();

        userFilterDto.setCityPattern("Moscow");
        userFilterDto.setNamePattern("Maxim");

        List<User> users = List.of(user1, user2);

        when(
                userFilters
                        .get(0)
                        .apply(userRepository.findPremiumUsers().toList(), userFilterDto))
                .thenReturn(users);
        when(userFilters.get(0).isApplicable(userFilterDto)).thenReturn(true);
        when(userMapper.toDto(user1)).thenReturn(firstUserDto);

        userService.getPremiumUsers(userFilterDto);

        verify(userFilters.get(0), times(1)).isApplicable(userFilterDto);
        verify(userFilters.get(0), times(1))
                .apply(listUsers.capture(), Mockito.eq(userFilterDto));
    }

    @Test
    @DisplayName("Test must return user when id is exist")
    void testGetUserByIdSuccess() {
        mockUserRepositoryAdapterAndMapper();

        UserDto result = userService.getUser(1L);

        Assertions.assertEquals(dto.getUsername(), result.getUsername());
        Assertions.assertEquals(dto, result);
    }

    @Test
    @DisplayName(
            "The test successfully publishes an event about viewing the user's profile to redis")
    void testSuccessfullyPublishedEventAboutProfileViewing() {
        mockUserRepositoryAdapterAndMapper();

        when(userContext.getUserId()).thenReturn(2L);

        userService.getUser(1L);

        verify(profileViewEventPublisher, times(1))
                .publish(profileViewEvent.capture());

        ProfileViewEvent resultEvent = profileViewEvent.getValue();
        Assertions.assertEquals(1, resultEvent.profileId());
        Assertions.assertEquals(2, resultEvent.viewId());
    }

    @Test
    @DisplayName("Test must return exception when user not exist data base")
    void testGetUserByIdFailed() {
        Long userId = 1L;
        when(userRepositoryAdapter.getById(userId))
                .thenThrow(new EntityNotFoundException("User not found with id: " + userId));

        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.getUser(userId));
    }

    @Test
    @DisplayName("Test must return users when id is exist")
    void testGetUsersByIdsSuccess() {
        Long userId = 1L;

        List<Long> userIds = List.of(userId);

        List<UserDto> dtoList = List.of(dto);

        List<User> userList = List.of(user);

        when(userRepositoryAdapter.getAllById(userIds)).thenReturn(userList);
        when(userMapper.toDtoList(userList)).thenReturn(dtoList);

        List<UserDto> result = userService.getUsersByIds(userIds);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("John", result.get(0).getUsername());
        Assertions.assertEquals(dtoList, result);
    }

    @Test
    @DisplayName(
            "Test should throw BadRequestException when the ID of the deactivated user is transmitted")
    void testDeactivateUserThrowBadRequestException() {
        long deactivatedUserId = 1L;

        User deactivatedUser = new User();
        deactivatedUser.setId(deactivatedUserId);
        deactivatedUser.setActive(false);

        when(userRepositoryAdapter.getById(deactivatedUserId)).thenReturn(deactivatedUser);
        Assertions.assertThrows(
                BadRequestException.class, () -> userService.deactivateUser(deactivatedUserId));
    }

    @Test
    @DisplayName(
            "Test should return DeactivatedUserDto when the ID of the non-deactivated user is transmitted")
    void testDeactivateUserSuccessful() {
        long userId = 1L;

        long event1Id = 1L;
        long event2Id = 2L;

        User user = new User();
        user.setId(userId);
        user.setActive(true);

        List<User> userList = new ArrayList<>();
        userList.add(user);

        Goal userGoal = new Goal();
        userGoal.setId(1L);
        userGoal.setUsers(userList);

        List<Goal> goalList = new ArrayList<>();
        goalList.add(userGoal);
        user.setGoals(goalList);

        Event userEvent1 = new Event();
        userEvent1.setId(event1Id);
        Event userEvent2 = new Event();
        userEvent2.setId(event2Id);

        List<Event> eventList = new ArrayList<>();
        eventList.add(userEvent1);
        eventList.add(userEvent2);

        user.setOwnedEvents(eventList);

        when(userRepositoryAdapter.getById(userId)).thenReturn(user);

        Mockito.doNothing().when(goalRepositoryAdapter).delete(userGoal);
        Mockito.doNothing().when(goalRepositoryAdapter).removeUserGoals(user.getId());
        Mockito.doNothing().when(eventParticipationRepositoryAdapter).unregisterAll(event1Id);
        Mockito.doNothing().when(eventParticipationRepositoryAdapter).unregisterAll(event2Id);
        Mockito.doNothing().when(eventRepositoryAdapter).deleteAll(user.getOwnedEvents());
        Mockito.doNothing().when(mentorshipService).stopMentorship(user);

        userService.deactivateUser(userId);

        verify(goalRepositoryAdapter, times(1)).delete(userGoal);
        verify(goalRepositoryAdapter, times(1)).removeUserGoals(user.getId());
        verify(eventParticipationRepositoryAdapter, times(1))
                .unregisterAll(event1Id);
        verify(eventParticipationRepositoryAdapter, times(1))
                .unregisterAll(event1Id);
        verify(eventRepositoryAdapter, times(1)).deleteAll(user.getOwnedEvents());

        Assertions.assertFalse(user.isActive());

        verify(mentorshipService, times(1)).stopMentorship(user);
        verify(deactivatedUserMapper, times(1)).toDto(user);
    }

    private void mockUserRepositoryAdapterAndMapper() {
        when(userRepositoryAdapter.getById(1L)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(dto);
    }

    @Test
    public void testUploadFile() {

        Person person = new Person();
        ContactInfo contactInfo = new ContactInfo();
        Address address = new Address();
        address.setCountry("Germany");
        contactInfo.setAddress(address);
        person.setContactInfo(contactInfo);

        List<Person> people = List.of(person);

        User user = new User();
        Country country = new Country();
        country.setTitle("Germany");

        when(userMapper.toUser(person)).thenReturn(user);
        when(countryRepository.findByName("Germany")).thenReturn(Optional.empty());
        when(countryRepository.save(country)).thenReturn(country);

        userService.saveUsers(people);

        verify(userMapper, times(1)).toUser(person);

        verify(countryRepository, times(1)).findByName("Germany");
        verify(countryRepository, times(1)).save(any(Country.class));

        verify(userRepository, times(1)).saveAll(anyList());

    }
}
