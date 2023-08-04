package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mydto.UserDto;
import school.faang.user_service.mapper.mymappers.Country1MapperImpl;
import school.faang.user_service.mapper.mymappers.Goal1MapperImpl;
import school.faang.user_service.mapper.mymappers.Skill1MapperImpl;
import school.faang.user_service.mapper.mymappers.User1MapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.exception.UserNotFoundException;

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


    private Goal1MapperImpl goalMapper = new Goal1MapperImpl();


    private Skill1MapperImpl skillMapper = new Skill1MapperImpl();


    private Country1MapperImpl countryMapper = new Country1MapperImpl();

    @Spy
    private User1MapperImpl userMapper = new User1MapperImpl(goalMapper, skillMapper, countryMapper);

    @InjectMocks
    private UserService service;

    @Test
    void getUser_UserNotFound_ShouldThrowException() {
        when(repository.findById(1L))
                .thenReturn(Optional.empty());

        UserNotFoundException e = assertThrows(UserNotFoundException.class,
                () -> service.getUser(1L));
        assertEquals("User with id 1 not found", e.getMessage());
    }

    @Test
    void getUsersByIds_UsersNotFound_ListShouldBeEmpty() {
        when(repository.findAllById(any()))
                .thenReturn(Collections.emptyList());

        List<UserDto> actual = service.getUsersByIds(List.of(1L, 2L, 3L));

        assertEquals(0, actual.size());
    }
}