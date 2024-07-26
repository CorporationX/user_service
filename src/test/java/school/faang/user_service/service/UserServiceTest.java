package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.StyleAvatarConfig;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private StyleAvatarConfig styleAvatarConfig;

    @Mock
    private  AmazonS3Service amazonS3Service;

    @Mock
    private  RestTemplateService restTemplateService;
    @Mock
    private AvatarService avatarService = new AvatarService(styleAvatarConfig,amazonS3Service, restTemplateService);
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserService userService;

    private UserDto userDto;
    private User user;

    @BeforeEach
    public void init() {
        Country country = Country.builder()
                .title("country")
                .build();

        user = User.builder()
                .username("test user")
                .email("user@email.com")
                .phone("+79211234567")
                .country(country)
                .password("abracadabra")
                .build();
        userDto = UserDto.builder()
                .username("test user")
                .email("user@email.com")
                .phone("+79211234567")
                .countryId(1L)
                .password("abracadabra")
                .build();
    }

    @Test
    @DisplayName("save new user")
    public void saveUserTest() {

        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        userService.createUser(userDto);

        verify(userMapper, times(1)).toEntity(userDto);
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toDto(user);
    }
}
