package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataRetrievalFailureException;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserFilterMapperImpl;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private List<UserDto> userDtoList;
    private Stream<User> userStream;

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapperImpl userMapper;

    @Spy
    private UserFilterMapperImpl userFilterMapper;

    @InjectMocks
    private UserServiceImpl userService;

    long userId;

    @BeforeEach
    public void init() {
        userId = 10L;
    }

    @Test
    public void testGetUserById_UserIsFound_ReturnsUser() {
        var testUser = User.builder()
                .id(userId)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        var result = userService.getUserById(userId);

        verify(userRepository, times(1)).findById(userId);
        assertEquals(testUser, result);
    }

    @Test
    public void testGetUserById_UserIsNotFound_Throws() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(
                DataRetrievalFailureException.class,
                () -> userService.getUserById(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @Nested
    class TestGetPremiumUsers {
        @BeforeEach
        void setUp() {
            userStream = Stream.of(
                    User.builder()
                            .id(1L).username("name1").email("email1").experience(6).build(),
                    User.builder()
                            .id(2L).username("name2").email("email2").experience(2).build(),
                    User.builder()
                            .id(3L).username("name3").email("email3").experience(4).build()
            );

            userDtoList = List.of(
                    new UserDto(1L, "name1", "email1"),
                    new UserDto(2L, "name2", "email2"),
                    new UserDto(3L, "name3", "email3")
            );
        }

        @Test
        void whenFilterIsNull_ReturnsAllUserDtos() {
            when(userRepository.findPremiumUsers()).thenReturn(userStream);

            List<UserDto> result = userService.getPremiumUsers(null);

            assertEquals(userDtoList, result);
        }

        @Test
        void whenFilterIsNullAndPremiumUsersListIsEmpty_ReturnEmptyUserDtos() {
            List<UserDto> emptyDtoList = List.of();
            when(userRepository.findPremiumUsers()).thenReturn(Stream.empty());

            List<UserDto> result = userService.getPremiumUsers(null);

            assertEquals(emptyDtoList, result);
        }

        @Test
        void whenApplyFilterWithExperienceFrom3To8_ReturnUserDtos() {
            UserFilterDto userFilterDto = new UserFilterDto(null, null, 3, 8);
            List<UserDto> filtereduserDtoList = List.of(
                    new UserDto(1L, "name1", "email1"),
                    new UserDto(3L, "name3", "email3")
            );
            when(userRepository.findPremiumUsers()).thenReturn(userStream);

            List<UserDto> result = userService.getPremiumUsers(userFilterDto);

            assertEquals(filtereduserDtoList, result);
        }

        @Test
        void whenApplyFilterWithExperienceFrom3To8ButPremiumUsersListIsEmpty_ReturnEmptyUserDtos() {
            UserFilterDto userFilterDto = new UserFilterDto(null, null, 3, 8);
            List<UserDto> emptyDtoList = List.of();
            when(userRepository.findPremiumUsers()).thenReturn(Stream.empty());

            List<UserDto> result = userService.getPremiumUsers(userFilterDto);

            assertEquals(emptyDtoList, result);
        }
    }
}