package school.faang.user_service.util;

import com.json.student.Address;
import com.json.student.ContactInfo;
import com.json.student.Person;
import lombok.experimental.UtilityClass;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Long.MAX_VALUE;
import static java.time.Month.JANUARY;
import static java.util.Arrays.asList;
import static java.util.List.*;

@UtilityClass
public final class TestDataFactory {

    public static final Long USER_ID = 1L;
    public static final Long INVALID_USER_ID = MAX_VALUE;

    public static UserDto createUserDto() {
        return UserDto.builder()
                .id(1L)
                .username("John_Smith")
                .email("incognito1@gmail.com")
                .active(true)
                .goalIds(of(12L))
                .participatedEventIds(of(123L))
                .menteeIds(of(2L, 3L))
                .build();
    }

    public static User createUser() {
        User user = createUserWithoutGoalsAndEvents();
        Goal goal = createGoalWithoutUsers();

        user.setGoals(new ArrayList<>(asList(goal)));
        goal.setUsers(new ArrayList<>(asList(user)));

        user.setParticipatedEvents(new ArrayList<>(of(createEvent())));
        user.setMentees(new ArrayList<>(createUsersList().subList(1, 3)));
        user.getMentees().forEach(mentee -> {
            mentee.setMentors(new ArrayList<>());
            mentee.getMentors().add(user);
        });

        return user;
    }
    private static User createUserWithoutGoalsAndEvents() {
        return User.builder()
                .id(1L)
                .username("John_Smith")
                .email("Incognito@gmail.com")
                .active(true)
                .mentees(new ArrayList<>())
                .build();
    }

    public static List<UserDto> createUserDtosList() {
        var userDtoFirst = createUserDto();
        var userDtoSecond = UserDto.builder()
                .id(2L)
                .username("Incognito2")
                .email("incognito2@gmail.com")
                .build();
        var userDtoThird = UserDto.builder()
                .id(3L)
                .username("Incognito3")
                .email("incognito3@gmail.com")
                .build();

        return of(userDtoFirst, userDtoSecond, userDtoThird);
    }

    public static List<User> createUsersList() {
        var firstUser = createUserWithoutGoalsAndEvents();
        var secondUser = User.builder()
                .id(2L)
                .username("Incognito2")
                .email("incognito2@gmail.com")
                .goals(new ArrayList<>(of(Goal.builder().id(234L).build())))
                .build();
        var thirdUser = User.builder()
                .id(3L)
                .username("Incognito3")
                .email("incognito3@gmail.com")
                .goals(new ArrayList<>(of(Goal.builder().id(456L).build())))
                .build();

        return new ArrayList<>(Arrays.asList(firstUser, secondUser, thirdUser));
    }

    public static RequestFilterDto createRequestFilterDto() {
        return RequestFilterDto.builder()
                .id(1L)
                .status("Pending")
                .requesterId(1001L)
                .receiverId(1002L)
                .build();
    }

    public static RecommendationRequest createRecommendationRequest() {
        return RecommendationRequest
                .builder()
                .id(1L)
                .message("Please provide a recommendation.")
                .status(RequestStatus.PENDING)
                .skills(createSkillRequests())
                .requester(User.builder().id(1001L).build())
                .receiver(User.builder().id(1002L).build())
                .createdAt(LocalDateTime.of(2020, JANUARY, 18, 0, 0))
                .updatedAt(LocalDateTime.of(2021, JANUARY, 18, 0, 0))
                .build();
    }

    public static List<SkillRequest> createSkillRequests() {
        SkillRequest skillRequest1 = new SkillRequest(1L, new RecommendationRequest(), null);
        SkillRequest skillRequest2 = new SkillRequest(2L, new RecommendationRequest(), null);

        return asList(skillRequest1, skillRequest2);
    }

    public static RecommendationRequestDto createRecommendationRequestDto() {
        return RecommendationRequestDto.builder()
                .id(1L)
                .message("Please provide a recommendation.")
                .status("pending")
                .skillIds(of(1L, 2L))
                .requesterId(1001L)
                .receiverId(1002L)
                .createdAt(LocalDateTime.of(2020, JANUARY, 18, 0, 0))
                .updatedAt(LocalDateTime.of(2021, JANUARY, 18, 0, 0))
                .build();
    }

    public static Goal createGoal() {
        Goal goal = createGoalWithoutUsers();
        goal.setUsers(createUsersList());
        return goal;
    }

    private static Goal createGoalWithoutUsers() {
        return Goal.builder()
                .id(12L)
                .description("Goal description")
                .status(GoalStatus.ACTIVE)
                .users(new ArrayList<>())
                .build();
    }
    public static Event createEvent(){
        return Event.builder()
                .id(123L)
                .build();
    }
    public static Goal createNewGoal() {
        Goal goal = createGoalWithoutUsers();
        goal.setId(13L);
        goal.setUsers(new ArrayList<>(Arrays.asList(createUser(), User.builder().id(123L).build())));
        return goal;
    }

    public static Person createPerson() {
        var address = new Address();
        address.setCity("New York");
        address.setCountry("USA");

        var contactInfo = new ContactInfo();
        contactInfo.setAddress(address);
        contactInfo.setEmail("incognito1@gmail.com");

        var person = new Person();
        person.setFirstName("John");
        person.setLastName("Smith");
        person.setContactInfo(contactInfo);

        return person;
    }

    public static Country createCounty(){
        return Country.builder()
                .id(23L)
                .title("USA")
                .build();
    }
}
