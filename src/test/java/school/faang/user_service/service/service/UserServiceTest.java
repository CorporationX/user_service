package school.faang.user_service.service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mydto.CountryDto;
import school.faang.user_service.dto.mydto.GoalDto;
import school.faang.user_service.dto.mydto.SkillDto;
import school.faang.user_service.dto.mydto.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.mymappers.CountryMapperImpl;
import school.faang.user_service.mapper.mymappers.GoalMapperImpl;
import school.faang.user_service.mapper.mymappers.SkillMapperImpl;
import school.faang.user_service.mapper.mymappers.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.UserService;
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


    private GoalMapperImpl goalMapper = new GoalMapperImpl();


    private SkillMapperImpl skillMapper = new SkillMapperImpl();


    private CountryMapperImpl countryMapper = new CountryMapperImpl();

    @Spy
    private UserMapperImpl userMapper = new UserMapperImpl(goalMapper, skillMapper, countryMapper);

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