package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.mydto.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.filter.user.AboutPatternFilter;
import school.faang.user_service.filter.user.CityPatternFilter;
import school.faang.user_service.filter.user.ContactPatternFilter;
import school.faang.user_service.filter.user.CountryPatternFilter;
import school.faang.user_service.filter.user.EmailPatternFilter;
import school.faang.user_service.filter.user.ExperienceRangeFilter;
import school.faang.user_service.filter.user.NamePatternFilter;
import school.faang.user_service.filter.user.PhonePatternFilter;
import school.faang.user_service.filter.user.SkillPatternFilter;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.exception.notFoundExceptions.contact.UserNotFoundException;
import school.faang.user_service.mapper.mymappers.Country1MapperImpl;
import school.faang.user_service.mapper.mymappers.Goal1MapperImpl;
import school.faang.user_service.mapper.mymappers.Skill1MapperImpl;
import school.faang.user_service.mapper.mymappers.User1MapperImpl;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

    private List<UserFilter> userFilters = new ArrayList<>(List.of(new AboutPatternFilter(), new CityPatternFilter(),
            new ContactPatternFilter(), new CountryPatternFilter(), new EmailPatternFilter(), new ExperienceRangeFilter(),
            new NamePatternFilter(), new PhonePatternFilter(), new SkillPatternFilter()));

    private UserService service;

    @BeforeEach
    public void setup() {
        service = new UserService(repository, userMapper, userFilters);
    }

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

    @Test
    void getPremiumUsers_UsernameFilter() {
        UserFilterDto filterDto = new UserFilterDto();
        filterDto.setNamePattern("name3");
        List<User> users = List.of(
                buildUser(1L),
                buildUser(2L),
                buildUser(3L)
        );
        when(repository.findPremiumUsers())
                .thenReturn(users.stream());
        List<UserDto> userDtoList = users.stream().map(user -> userMapper.toDto(user)).toList();

        assertEquals(userDtoList.subList(2, 3), service.getPremiumUsers(filterDto));
    }

    @Test
    void getPremiumUsers() {
        UserFilterDto filterDto = new UserFilterDto();
        List<User> users = List.of(
                buildUser(1L),
                buildUser(2L),
                buildUser(3L)
        );
        when(repository.findPremiumUsers())
                .thenReturn(users.stream());
        service.getPremiumUsers(filterDto);
        verify(repository).findPremiumUsers();
        verify(userMapper, times(3)).toDto(any());
    }

    private User buildUser(long id) {
        return User.builder()
                .id(id)
                .username("name" + id)
                .email("email" + id)
                .phone("phone" + id)
                .aboutMe("aboutMe" + id)
                .active(true)
                .city("city" + id)
                .experience((int) id)
                .followers(List.of(User.builder().id(1L).build(), User.builder().id(2L).build()))
                .followees(List.of(User.builder().id(3L).build(), User.builder().id(4L).build()))
                .mentors(List.of(User.builder().id(5L).build(), User.builder().id(6L).build()))
                .mentees(List.of(User.builder().id(7L).build(), User.builder().id(8L).build()))
                .country(Country.builder().id(1L).title("title").build())
                .goals(List.of(Goal.builder().id(1L).build(), Goal.builder().id(2L).build()))
                .skills(List.of(Skill.builder().id(1L).title("skill" + id).build(), Skill.builder().id(2L).title("skill" + id).build()))
                .build();
    }
}