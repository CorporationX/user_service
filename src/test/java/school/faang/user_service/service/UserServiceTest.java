package school.faang.user_service.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.service.filters.UserCityPattern;
import school.faang.user_service.service.filters.UserFilter;
import school.faang.user_service.service.filters.UserSkillPattern;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private List<UserFilter> userFilter = new ArrayList<>();

    @InjectMocks
    private UserService userService;

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
}
