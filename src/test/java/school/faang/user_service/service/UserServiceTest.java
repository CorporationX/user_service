package school.faang.user_service.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.service.user.filter.UserFilter;
import school.faang.user_service.service.user.filter.UserFilterByCities;
import school.faang.user_service.service.user.filter.UserFilterBySkills;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

        SkillDto skillDtoOne = new SkillDto();
        skillDtoOne.setId(skillOne.getId());
        skillDtoOne.setTitle(skillOne.getTitle());

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


        UserFilterDto firstFilter = new UserFilterDto();
        firstFilter.setCountry(List.of("Moscow"));
        firstFilter.setSkills(List.of(skillDtoOne));

        when(userRepository.findPremiumUsers()).thenReturn(Stream.of(userOne, userTwo));
        when(userFilter.stream()).thenReturn(Stream.of(
                new UserFilterByCities(),
                new UserFilterBySkills()
        ));

        String expectedUserName = "Frank";
        int expectedPremiumUsersSize = 1;

        List<User> premiumUser = userService.findPremiumUser(firstFilter);
        assertEquals(expectedPremiumUsersSize, premiumUser.size());
        assertEquals(expectedUserName, premiumUser.get(0).getUsername());
    }
}
