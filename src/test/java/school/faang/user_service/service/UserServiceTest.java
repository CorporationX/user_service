package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.mydto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.filter.user.AboutPatternFilter;
import school.faang.user_service.filter.user.CityPatternFilter;
import school.faang.user_service.filter.user.ContactPatternFilter;
import school.faang.user_service.filter.user.CountryPatternFilter;
import school.faang.user_service.filter.user.EmailPatternFilter;
import school.faang.user_service.filter.user.ExperienceRangeFilter;
import school.faang.user_service.filter.user.NamePatternFilter;
import school.faang.user_service.filter.user.PhonePatternFilter;
import school.faang.user_service.filter.user.SkillPatternFilter;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.exception.notFoundExceptions.contact.UserNotFoundException;
import school.faang.user_service.mapper.mymappers.Country1MapperImpl;
import school.faang.user_service.mapper.mymappers.Goal1MapperImpl;
import school.faang.user_service.mapper.mymappers.Skill1MapperImpl;
import school.faang.user_service.mapper.mymappers.User1MapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    GoalService goalService;

    @Mock
    EventService eventService;

    @Mock
    private UserRepository userRepository;
    @Mock
    ContactService contactService;

    @Mock
    private UserProfilePicService userProfilePicService;

    private Goal1MapperImpl goalMapper = new Goal1MapperImpl();


    private Skill1MapperImpl skillMapper = new Skill1MapperImpl();


    private Country1MapperImpl countryMapper = new Country1MapperImpl();

    @Spy
    private User1MapperImpl userMapper = new User1MapperImpl(goalMapper, skillMapper, countryMapper);

    private List<UserFilter> userFilters = new ArrayList<>(List.of(new AboutPatternFilter(), new CityPatternFilter(),
            new ContactPatternFilter(), new CountryPatternFilter(), new EmailPatternFilter(), new ExperienceRangeFilter(),
            new NamePatternFilter(), new PhonePatternFilter(), new SkillPatternFilter()));
    private UserService userService;
    private MultipartFile multipartFile;
    private UserProfilePic userProfilePic;

    User user;

    @BeforeEach
    void setUp() throws IOException {
        user = User.builder().id(1).build();
        userService = new UserService(userRepository, userMapper, goalService, eventService, userFilters, contactService
                , userProfilePicService);


        multipartFile = new MockMultipartFile("file", byte[].class.getResourceAsStream("file"));
        userProfilePic = UserProfilePic.builder().fileId("FILE").smallFileId("FILE").build();
    }

    @Test
    void getUser_UserNotFound_ShouldThrowException() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        UserNotFoundException e = assertThrows(UserNotFoundException.class,
                () -> userService.getUser(1L));
        assertEquals("User with id 1 not found", e.getMessage());
    }

    @Test
    void getUsersByIds_UsersNotFound_ListShouldBeEmpty() {
        when(userRepository.findAllById(any()))
                .thenReturn(Collections.emptyList());

        List<UserDto> actual = userService.getUsersByIds(List.of(1L, 2L, 3L));

        assertEquals(0, actual.size());
    }

    @Test
    void getPremiumUsers_UsernameFilter() {
        UserFilterDto filterDto = new UserFilterDto();
        filterDto.setNamePattern("name3");
        List<User> users = List.of(
                buildUser(1L),
                buildUser(2L),
                buildUser(3L)
        );
        when(userRepository.findPremiumUsers())
                .thenReturn(users.stream());
        List<UserDto> userDtoList = users.stream().map(user -> userMapper.toDto(user)).toList();

        assertEquals(userDtoList.subList(2, 3), userService.getPremiumUsers(filterDto));
    }

    @Test
    void getPremiumUsers() {
        UserFilterDto filterDto = new UserFilterDto();
        List<User> users = List.of(
                buildUser(1L),
                buildUser(2L),
                buildUser(3L)
        );
        when(userRepository.findPremiumUsers())
                .thenReturn(users.stream());
        userService.getPremiumUsers(filterDto);
        verify(userRepository).findPremiumUsers();
        verify(userMapper, times(3)).toDto(any());
    }

    private User buildUser(long id) {
        return User.builder()
                .id(id)
                .username("name" + id)
                .email("email" + id)
                .phone("phone" + id)
                .aboutMe("aboutMe" + id)
                .active(true)
                .city("city" + id)
                .experience((int) id)
                .followers(List.of(User.builder().id(1L).build(), User.builder().id(2L).build()))
                .followees(List.of(User.builder().id(3L).build(), User.builder().id(4L).build()))
                .mentors(List.of(User.builder().id(5L).build(), User.builder().id(6L).build()))
                .mentees(List.of(User.builder().id(7L).build(), User.builder().id(8L).build()))
                .country(Country.builder().id(1L).title("title").build())
                .goals(List.of(Goal.builder().id(1L).build(), Goal.builder().id(2L).build()))
                .skills(List.of(Skill.builder().id(1L).title("skill" + id).build(), Skill.builder().id(2L).title("skill" + id).build()))
                .build();
    }

    @Test
    void deactivateUserNotFoundExceptionTest() {
        var userId = 1L;
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.deactivateUser(userId)
        );
    }

    @Test
    void deactivateUserTest() {
        var userId = 1L;
        Event event = Event.builder().owner(user).status(EventStatus.IN_PROGRESS).build();
        User mentee = User.builder().mentors(new ArrayList<>(List.of(user))).build();
        Goal goal = Goal.builder().users(new ArrayList<>(List.of(user))).build();
        user = User.builder().id(1).ownedEvents(new ArrayList<>(List.of(event))).mentees(new ArrayList<>(List.of(mentee))).goals(new ArrayList<>(List.of(goal))).build();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        userService.deactivateUser(userId);

        verify(userRepository, times(1)).save(any());
        verify(userMapper, times(1)).toDto(any());
        assertEquals(user.getOwnedEvents().get(0).getStatus(), EventStatus.CANCELED);
    }

    @Test
    void userBanEventSave() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        userService.userBanEventSave(String.valueOf(1L));
        assertTrue(user.isBanned());
        Mockito.verify(userRepository, Mockito.times(1))
                .save(user);
    }

    @Test
    void saveAvatar() {
        User user = User.builder().id(2).userProfilePic(userProfilePic).build();
        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(user));

        userService.saveAvatar(2L, multipartFile);
        Mockito.verify(userRepository, Mockito.times(1))
                .save(user);
    }

    @Test
    void deleteProfilePic() {
        user = User.builder().id(2).userProfilePic(userProfilePic).build();
        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(user));

        userService.deleteProfilePic(2L);
        assertNull(user.getUserProfilePic());
        Mockito.verify(userRepository, Mockito.times(1))
                .save(user);
    }
}