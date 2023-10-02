package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.contact.ContactPreference;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.mapper.MapperUserDto;
import school.faang.user_service.mapper.MapperUserDtoImpl;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.messaging.ProfileViewEventPublisher;
import school.faang.user_service.messaging.events.ProfileViewEvent;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.filter.user.ActiveUserFilter;
import school.faang.user_service.filter.user.UserFilterDto;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserContext userContext;
    @Mock
    private ProfileViewEventPublisher profileViewEventMessagePublisher;
    @Spy
    private GoalMapper goalMapper;
    @Spy
    private SkillMapper skillMapper;
    @Spy
    private MapperUserDto userMapper = new MapperUserDtoImpl(goalMapper, skillMapper);

    private User user1;
    private User user2;
    private User user3;

    List<User> userList;
    private UserFilterDto filter;
    private LocalDateTime dateTime;

    @BeforeEach
    void setUp() {
        user1 = User.builder().id(1).active(true).premium(new Premium()).build();
        user2 = User.builder().id(2).active(false).premium(null).build();
        user3 = User.builder().id(3).active(true).premium(new Premium()).build();
        UserFilter userFilter = new ActiveUserFilter();
        List<UserFilter> userFilters = List.of(userFilter);
        userService = new UserService(userRepository, userFilters, userMapper,profileViewEventMessagePublisher, userContext);
        userList = List.of(user1, user2, user3);
        filter = new UserFilterDto();
        filter.setActive(true);
        dateTime = LocalDateTime.now();
    }

    @Test
    public void getAllPremiumUsersTest() {
        Mockito.when(userRepository.findPremiumUsers()).thenReturn(userList.stream());

        int expected = 2;
        int actual = userService.getPremiumUsers(filter).size();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testGetUser() {
        user1.setEmail("aaa");
        user1.setUsername("John");
        user1.setActive(false);
        user1.setContactPreference(ContactPreference.builder().preference(PreferredContact.EMAIL).build());
        UserDto expected = UserDto.builder()
                .id(1L)
                .email("aaa")
                .username("John")
                .followers(new ArrayList<>())
                .followees(new ArrayList<>())
                .mentors(new ArrayList<>())
                .mentees(new ArrayList<>())
                .goals(new ArrayList<>())
                .skills(new ArrayList<>())
                .build();

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        UserDto output = userService.getUser(2L, 1L);

        Assertions.assertEquals(expected, output);
    }

    @Test
    public void testGetUsersByIds() {
        List<Long> userIds = List.of(1L, 2L, 3L);
        user1.setEmail("aaa");
        user1.setUsername("John");
        user2.setEmail("ooo");
        user2.setUsername("Peter");
        user3.setEmail("uuu");
        user3.setUsername("Michel");

        UserDto userDto1 = UserDto.builder()
                .id(1L)
                .email("aaa")
                .username("John")
                .active(true)
                .followers(new ArrayList<>())
                .followees(new ArrayList<>())
                .mentors(new ArrayList<>())
                .mentees(new ArrayList<>())
                .goals(new ArrayList<>())
                .skills(new ArrayList<>())
                .build();

        UserDto userDto2 = UserDto.builder()
                .id(2L)
                .email("ooo")
                .username("Peter")
                .active(false)
                .followers(new ArrayList<>())
                .followees(new ArrayList<>())
                .mentors(new ArrayList<>())
                .mentees(new ArrayList<>())
                .goals(new ArrayList<>())
                .skills(new ArrayList<>())
                .build();

        UserDto userDto3 = UserDto.builder()
                .id(3L)
                .email("uuu")
                .username("Michel")
                .active(true)
                .followers(new ArrayList<>())
                .followees(new ArrayList<>())
                .mentors(new ArrayList<>())
                .mentees(new ArrayList<>())
                .goals(new ArrayList<>())
                .skills(new ArrayList<>())
                .build();

        List<UserDto> expected = List.of(userDto1, userDto2, userDto3);
        Mockito.when(userRepository.findAllById(userIds)).thenReturn(userList);

        List<UserDto> output = userService.getUsersByIds(userIds);

        Assertions.assertEquals(expected, output);
    }
}
