package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.UserValidator;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private AvatarService avatarService;
    @Mock
    private UserValidator userValidator;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private long userId;
    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        UserMapperImpl userMapperImpl = new UserMapperImpl();
        userId = 1L;

        userDto = UserDto.builder()
                .id(userId)
                .username("username")
                .password("password")
                .country(1L)
                .email("test@mail.com")
                .phone("123456")
                .build();

        user = userMapperImpl.toEntity(userDto);
    }

    @Test
    @DisplayName("testing createUser method with null multipartFile")
    public void testCreateUser() {
        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        userService.createUser(userDto, null);
        verify(userRepository, times(2)).save(user);
        verify(avatarService, times(1)).setRandomAvatar(user);
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    @DisplayName("testing updateUserAvatar method with null multipartFile")
    public void testUpdateUser() {
        when(userValidator.validateUserExistence(user.getId())).thenReturn(user);
        userService.updateUserAvatar(userId, null);
        verify(avatarService, times(1)).setRandomAvatar(user);
        verify(userRepository, times(1)).save(user);
    }
}