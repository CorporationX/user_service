package school.faang.user_service.util;

import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static java.util.List.of;

public final class TestDataFactory {

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
                .createdAt(LocalDateTime.of(2020, Month.JANUARY, 18, 0, 0))
                .updatedAt(LocalDateTime.of(2021, Month.JANUARY, 18, 0, 0))
                .build();

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

    public static List<SkillRequest> createSkillRequests() {
        SkillRequest skillRequest1 = new SkillRequest(1L, new RecommendationRequest(), null);
        SkillRequest skillRequest2 = new SkillRequest(2L, new RecommendationRequest(), null);

        return Arrays.asList(skillRequest1, skillRequest2);
    }
}
