package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;
    @Spy
    private UserMapperImpl userMapper;

    private final List<Long> USERS_IDS = List.of(1L, 2L, 3L);
    private long USER_ID = 1L;
    private User user;
    private UserDto userDto;

    @Test
    void testGetUsersByIds() {
        when(userRepository.findAllById(USERS_IDS)).thenReturn(getListOfUser());
        List<UserDto> actualLust = userService.getUsersByIds(USERS_IDS);
        List<UserDto> expectedList = getCorrectListOfUserDto();

        assertEquals(expectedList, actualLust);
    }

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(USER_ID)
                .username("Nikita")
                .build();
        userDto = UserDto.builder()
                .id(USER_ID)
                .username("Nikita")
                .build();
    }

    @Test
    void getUserWithoutUserInDataBase() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.getUser(USER_ID));
    }

    private List<User> getListOfUser() {
        return List.of(User.builder().id(1L).build(),
                       User.builder().id(2L).build(),
                       User.builder().id(3L).build());
    }

    private List<UserDto> getCorrectListOfUserDto() {
        return List.of(UserDto.builder().id(1L).build(),
                UserDto.builder().id(2L).build(),
                UserDto.builder().id(3L).build());
    }

    @Test
    void getUser() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.ofNullable(user));
        UserDto actualUser = userService.getUser(USER_ID);
        assertEquals(userDto, actualUser);
    }
}