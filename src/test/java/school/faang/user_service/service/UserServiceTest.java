package school.faang.user_service.service;

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
import school.faang.user_service.util.exception.UserNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Spy
    private UserMapperImpl mapper;

    @InjectMocks
    private UserService service;

    @Test
    void getUser_UserNotFound_ShouldThrowException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        UserNotFoundException e = assertThrows(UserNotFoundException.class,
                () -> service.getUser(1L));
        assertEquals("User with id 1 not found", e.getMessage());
    }

    @Test
    void getUser_UserFound_ShouldReturnDto() {
        when(repository.findById(any()))
                .thenReturn(Optional.of(buildUser()));

        UserDto actual = service.getUser(1L);

        assertEquals(buildUserDto(), actual);
    }

    @Test
    void getUsersByIds_UsersNotFound_ListShouldBeEmpty() {
        when(repository.findAllById(any()))
                .thenReturn(Collections.emptyList());

        List<UserDto> actual = service.getUsersByIds(List.of(1L, 2L, 3L));

        assertEquals(0, actual.size());
    }

    @Test
    void getUsersByIds_UsersFound_ShouldReturnDtos() {
        when(repository.findAllById(any()))
                .thenReturn(buildListOfUsers());

        List<UserDto> actual = service.getUsersByIds(List.of(1L, 2L, 3L));

        assertIterableEquals(buildListOfDtos(), actual);
    }

    private User buildUser() {
        return User.builder()
                .id(1L)
                .username("name")
                .email("email")
                .build();
    }

    private UserDto buildUserDto() {
        return UserDto.builder()
                .id(1L)
                .username("name")
                .email("email")
                .build();
    }

    private List<User> buildListOfUsers() {
        return List.of(
                User.builder().id(1L).build(),
                User.builder().id(2L).build(),
                User.builder().id(3L).build()
        );
    }

    private List<UserDto> buildListOfDtos() {
        return List.of(
                UserDto.builder().id(1L).build(),
                UserDto.builder().id(2L).build(),
                UserDto.builder().id(3L).build()
        );
    }
}