package school.faang.user_service.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.filters.UserCityPattern;
import school.faang.user_service.service.filters.UserFilter;
import school.faang.user_service.service.filters.UserSkillPattern;
import school.faang.user_service.service.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private List<UserFilter> userFilter = new ArrayList<>();

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDto userDto = new UserDto();
    private Long userId = 1L;
    private List<Long> idsList = new ArrayList<>();
    private List<User> userList = new ArrayList<>();
    private List<UserDto> userDtoList = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(userId);
    }

    @Test
    @DisplayName("Find premium user test")
    public void testFindPremiumUsers() {

        Skill skillOne = Skill.builder()
                .id(10L)
                .title("Java")
                .build();

        Skill skillTwo = Skill.builder()
                .id(11L)
                .title("SQL")
                .build();

        Skill skillThree = Skill.builder()
                .id(12L)
                .title("Spring")
                .build();

        User userOne = User.builder()
                .id(1L)
                .username("Frank")
                .city("Moscow")
                .skills(List.of(skillOne))
                .build();

        User userTwo = User.builder()
                .id(2L)
                .username("John")
                .city("SPb")
                .skills(List.of(skillTwo, skillThree))
                .build();

        User userThree = User.builder()
                .id(3L)
                .username("Ben")
                .city("SPb")
                .skills(List.of(skillOne, skillThree))
                .build();

        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setCityPattern("SPb");
        userFilterDto.setSkillPattern("Java");

        when(userRepository.findPremiumUsers()).thenReturn(Stream.of(userOne, userTwo, userThree));
        when(userFilter.stream()).thenReturn(Stream.of(
                new UserCityPattern(),
                new UserSkillPattern()
        ));

        List<User> premiumUsers = userService.findPremiumUser(userFilterDto);
        Assertions.assertThat(premiumUsers.get(0)).usingRecursiveComparison().isEqualTo(userThree);
    }

    @Test
    public void getUserTest() {
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        Mockito.when(mapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.getUser(userId);

        assertEquals(result, userDto);
        Mockito.verify(userRepository, atLeastOnce()).findById(userId);
        Mockito.verify(mapper, atLeastOnce()).toDto(user);
    }

    @Test
    public void getUsersByIdsTest(){
        Mockito.when(userRepository.findAllById(idsList)).thenReturn(userList);
        Mockito.when(mapper.toDto(userList)).thenReturn(userDtoList);

        List<UserDto> result = userService.getUsersByIds(idsList);

        assertEquals(result, userDtoList);
        Mockito.verify(userRepository, atLeastOnce()).findAllById(idsList);
        Mockito.verify(mapper, atLeastOnce()).toDto(userList);
    }
}
