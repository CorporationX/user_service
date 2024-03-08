package school.faang.user_service.service.user;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserCreateDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.avatar.AvatarService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserFilter filter;
    @Spy
    private ArrayList<UserFilter> filterList;
    @Mock
    private AvatarService avatarService;
    @InjectMocks
    private UserService userService;

    private UserFilterDto userFilterDto;

    private UserDto userDto1;
    private UserDto userDto2;
    private UserCreateDto userCreateDto;
    private User user;


    @BeforeEach
    public void setUp() {
        filterList.add(filter);
        userFilterDto = new UserFilterDto();
        userDto1 = new UserDto();
        userDto2 = new UserDto();
        user = User.builder()
                .id(1L)
                .username("Viktor")
                .email("email@email.com")
                .phone("123456789")
                .password("password")
                .build();
        userCreateDto = UserCreateDto.builder()
                .username("Viktor")
                .email("email@email.com")
                .phone("123456789")
                .password("password")
                .build();
    }

    @Test
    public void shouldGetPremiumUsers() {
        when(userRepository.findPremiumUsers()).thenReturn(Stream.of(new User(), new User()));
        when(userMapper.toDto(any(User.class))).thenReturn(userDto1, userDto2);
        when(filter.isApplicable(userFilterDto)).thenReturn(true);
        when(filter.apply(any(Stream.class), any(UserFilterDto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<UserDto> expected = Arrays.asList(userDto1, userDto2);
        List<UserDto> actual = userService.getPremiumUsers(userFilterDto);
        assertEquals(expected, actual, "Метод getPremiumUsers должен возвращать список премиум пользователей.");

    }


    @Test
    public void testShouldGetUserById() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(userMapper.toDto(any(User.class))).thenReturn(userDto1);
        userService.getUserById(userId);
    }

    @Test
    public void testCreateUserSuccessful() {
        UserCreateDto expectedDto = UserCreateDto.builder()
                .username("Viktor")
                .email("email@email.com")
                .phone("123456789")
                .password("password")
                .build();

        when(userMapper.toEntity(any(UserCreateDto.class))).thenReturn(user);
        UserProfilePic userProfilePic = new UserProfilePic();
        when(avatarService.generateAndSaveAvatar(user)).thenReturn(Optional.of(userProfilePic));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserCreateDto(user)).thenReturn(userCreateDto);

        UserCreateDto result = userService.createUser(userCreateDto);
        assertEquals(expectedDto, result);
    }
}
