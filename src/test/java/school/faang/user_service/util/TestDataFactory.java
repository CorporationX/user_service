package school.faang.user_service.util;

import lombok.experimental.UtilityClass;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static java.time.Month.*;
import static java.util.Arrays.*;
import static java.util.List.*;

@UtilityClass
public final class TestDataFactory {

    public static UserDto createUserDto() {
        return UserDto.builder()
                .id(1L)
                .username("Incognito1")
                .email("incognito1@gmail.com")
                .build();
    }

    public static User createUser() {
        return User.builder()
                .id(1L)
                .username("Incognito")
                .email("Incognito@gmail.com")
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
        var firstUser = createUser();
        var secondUser = User.builder()
                .id(2L)
                .username("Incognito2")
                .email("incognito2@gmail.com")
                .build();
        var thirdUser = User.builder()
                .id(3L)
                .username("Incognito3")
                .email("incognito3@gmail.com")
                .build();

        return of(firstUser, secondUser, thirdUser);
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
                .createdAt(LocalDateTime.of(2020, Month.JANUARY, 18, 0, 0))
                .updatedAt(LocalDateTime.of(2021, Month.JANUARY, 18, 0, 0))
                .build();
    }
}
