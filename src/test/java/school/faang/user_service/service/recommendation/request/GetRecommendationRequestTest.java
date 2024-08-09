package school.faang.user_service.service.recommendation.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class GetRecommendationRequestTest {

    private record GetRecommendationRequestTestParam(
            long requestedRecommendationID,
            Optional<RecommendationRequest> mockRecommendationRequest,
            Class<? extends Throwable> expectedException,
            RecommendationRequestDto expectedDto
    ) {
    }

    @MockBean
    private RecommendationRequestRepository recommendationRequestRepository;

    @Autowired
    private RecommendationRequestService recommendationRequestService;

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideTestData")
    @DisplayName("Test GetRecommendationRequest")
    void testGetRecommendationRequest(String testName, GetRecommendationRequestTestParam param) {
        long id = param.requestedRecommendationID();

        when(recommendationRequestRepository.findById(id)).thenReturn(param.mockRecommendationRequest());

        if (param.expectedException() != null) {
            assertThrows(param.expectedException(), () -> recommendationRequestService.getRecommendationRequest(id));
        } else {
            RecommendationRequestDto result = recommendationRequestService.getRecommendationRequest(id);
            assertEquals(param.expectedDto(), result);
        }
    }

    private static Stream<Arguments> provideTestData() {
        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setId(1L);
        recommendationRequest.setMessage("Test message");
        recommendationRequest.setStatus(RequestStatus.ACCEPTED);

        SkillRequest skillRequest1 = new SkillRequest();
        skillRequest1.setId(1L);
        SkillRequest skillRequest2 = new SkillRequest();
        skillRequest2.setId(2L);
        recommendationRequest.setSkills(List.of(skillRequest1, skillRequest2));

        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        recommendationRequest.setRequester(user1);
        recommendationRequest.setReceiver(user2);

        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        recommendationRequest.setCreatedAt(createdAt);
        recommendationRequest.setUpdatedAt(updatedAt);

        RecommendationRequestDto recommendationRequestDto = new RecommendationRequestDto(
                1L,
                "Test message",
                RequestStatus.ACCEPTED,
                List.of(1L, 2L),
                1L,
                2L,
                createdAt,
                updatedAt
        );

        return Stream.of(
                Arguments.of(
                        "Test with valid RecommendationRequest",
                        new GetRecommendationRequestTestParam(
                                1,
                                Optional.of(recommendationRequest),
                                null,
                                recommendationRequestDto)
                ),
                Arguments.of(
                        "Test requesting  for a non-existent recommendation request",
                        new GetRecommendationRequestTestParam(
                                -1,
                                Optional.empty(),
                                NoSuchElementException.class,
                                null)
                )
        );
    }
}
