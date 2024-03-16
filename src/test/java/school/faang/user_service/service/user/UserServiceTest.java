package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.filter.UserFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private UserService userService;
    private User premiumUser;
    private UserDto premiumUserDto;
    private UserRepository userRepository;
    private UserMapper userMapper;
    private UserFilter userFilter;

    @BeforeEach
    void setUp() {
        premiumUser = User.builder()
                .id(1L)
                .username("Valid username")
                .email("valid@email.com")
                .phone("+71234567890")
                .premium(new Premium())
                .build();
        premiumUserDto = UserDto.builder()
                .id(premiumUser.getId())
                .username(premiumUser.getUsername())
                .email(premiumUser.getEmail())
                .phone(premiumUser.getPhone())
                .isPremium(true)
                .build();
        userRepository = mock(UserRepository.class);
        userMapper = mock(UserMapper.class);
        userFilter = mock(UserFilter.class);
        userService = new UserService(userMapper, userRepository, List.of(userFilter));
    }

    @Test
    void getPremiumUsers_usersFoundAndFiltered_ThenReturnedAsDto() {
        when(userRepository.findPremiumUsers()).thenReturn(Stream.of(premiumUser));
        when(userFilter.isApplicable(any(UserFilterDto.class))).thenReturn(true);
        doNothing().when(userFilter).apply(anyList(), any(UserFilterDto.class));
        when(userMapper.toDto(List.of(premiumUser))).thenReturn(List.of(premiumUserDto));

        userService.getPremiumUsers(new UserFilterDto());

        verify(userRepository, times(1)).findPremiumUsers();
        verify(userMapper, times(1)).toDto(List.of(premiumUser));
    }
}
