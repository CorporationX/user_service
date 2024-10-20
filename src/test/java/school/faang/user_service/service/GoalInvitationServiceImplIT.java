package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
class GoalInvitationServiceImplIT {
    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoalInvitationService goalInvitationService;

    @Autowired
    private GoalInvitationRepository goalInvitationRepository;

    @Autowired
    private GoalInvitationMapper goalInvitationMapper;

    @Autowired
    private CountryRepository countryRepository;


    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        goalRepository.deleteAll();
        countryRepository.deleteAll();
        goalInvitationRepository.deleteAll();
    }

    /*@Test
    void getInvitations() {
        Country country = countryRepository.save(Country.builder().title("Country").build());
        User user1 = userRepository.save(User.builder()
                .username("user1")
                .email("user1@example.com")
                .phone("1234567890")
                .password("pass234")
                .active(true)
                .aboutMe("I'm user1")
                .country(country)
                .build());
        User user2 = userRepository.save(User.builder()
                .username("user2")
                .email("user2@example.com")
                .phone("9876543210")
                .password("pass123")
                .active(true)
                .aboutMe("Hello! I'm user2")
                .country(country)
                .build());

        User user3 = userRepository.save(User.builder()
                .username("user3")
                .email("user3@example.com")
                .phone("5555555555")
                .password("pass345")
                .active(false)
                .aboutMe("I'm user3")
                .country(country)
                .build());

        User user4 = userRepository.save(User.builder()
                .username("user4")
                .email("user4@example.com")
                .phone("4444444444")
                .password("pass456")
                .active(true)
                .aboutMe("Nice to meet you, I'm user4")
                .country(country)
                .build());

        User user5 = userRepository.save(User.builder()
                .username("user5")
                .email("user5@example.com")
                .phone("3333333333")
                .password("pass567")
                .active(true)
                .aboutMe("Hello world! This is user5")
                .country(country)
                .build());

        Goal goal = goalRepository.save(Goal.builder()
                .title("Learn Java")
                .description("1Description")
                .status(GoalStatus.ACTIVE)
                .build());
        Goal goal2 = goalRepository.save(Goal.builder()
                .title("2Learn Java2")
                .description("2Description")
                .status(GoalStatus.ACTIVE)
                .build());
        Goal goal3 = goalRepository.save(Goal.builder()
                .title("3Learn Java3")
                .description("3Description")
                .status(GoalStatus.ACTIVE)
                .build());

        goalInvitationRepository.save(goalInvitationMapper.toEntity(new GoalInvitationDto(2L, 1L),
                goal, user1, user2));
        goalInvitationRepository.save(goalInvitationMapper.toEntity(new GoalInvitationDto(3L, 2L),
                goal2, user2, user3));
        goalInvitationRepository.save(goalInvitationMapper.toEntity(new GoalInvitationDto(4L, 3L),
                goal3, user3, user4));
        goalInvitationRepository.save(goalInvitationMapper.toEntity(new GoalInvitationDto(5L, 2L),
                goal2, user4, user5));

        List<GoalInvitationDto> goalInvitationDtoList = goalInvitationService.getInvitations(InvitationFilterDto.builder()
                .inviterNamePattern("ser").build(), 0, 10);
        assertEquals(goalInvitationDtoList.size(), 4);
        assertEquals(goalInvitationDtoList.get(0).getInviterId(), 1L);
        assertEquals(goalInvitationDtoList.get(1).getInviterId(), 2L);
        assertEquals(goalInvitationDtoList.get(2).getInviterId(), 3L);
        assertEquals(goalInvitationDtoList.get(3).getInviterId(), 4L);

        List<GoalInvitationDto> goalInvitationDtoList2 = goalInvitationService.getInvitations(InvitationFilterDto.builder()
                .invitedId(15L).build(), 0, 10);

        List<GoalInvitationDto> goalInvitationDtoList3 = goalInvitationService.getInvitations(InvitationFilterDto.builder()
                .inviterNamePattern("user1").inviterId(11L).build(), 0, 10);

        List<GoalInvitationDto> goalInvitationDtoList4 = goalInvitationService.getInvitations(InvitationFilterDto.builder()
                .build(), 0, 10);
        assertEquals(goalInvitationDtoList4.size(), 4);
        assertEquals(goalInvitationDtoList4.get(0).getInviterId(), 1L);
        assertEquals(goalInvitationDtoList4.get(1).getInviterId(), 2L);
        assertEquals(goalInvitationDtoList4.get(2).getInviterId(), 3L);
        assertEquals(goalInvitationDtoList4.get(3).getInviterId(), 4L);
    }*/
}