package school.faang.user_service.filter.recomendation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendationRequest.RecommendationRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

class StatusPatternFilterTest {
    private StatusPatternFilterRecommendation statusPatternFilter;
    private List<RecommendationRequest> OneTestList;

    @BeforeEach
    void init() {
        statusPatternFilter = new StatusPatternFilterRecommendation();
        RecommendationRequest requestOne = new RecommendationRequest();
        requestOne.setStatus(RequestStatus.ACCEPTED);
        RecommendationRequest requestTwo = new RecommendationRequest();
        requestTwo.setStatus(RequestStatus.PENDING);
        OneTestList = List.of(requestOne, requestTwo);
    }

    @Test
    void testStatusPatternTrue() {
        RecommendationRequestFilterDto recommendationRequestFilterDto = new RecommendationRequestFilterDto(RequestStatus.ACCEPTED);
        boolean isApplication = statusPatternFilter.isApplication(recommendationRequestFilterDto);
        Assertions.assertTrue(isApplication);
    }

    @Test
    void testStatusPatternFalse() {
        RecommendationRequestFilterDto recommendationRequestFilterDto = new RecommendationRequestFilterDto();
        boolean isApplication = statusPatternFilter.isApplication(recommendationRequestFilterDto);
        Assertions.assertFalse(isApplication);
    }

    @Test
    void testStatusPatternApply() {
        RecommendationRequestFilterDto recommendationRequestFilterDto = new RecommendationRequestFilterDto(RequestStatus.ACCEPTED);
        RecommendationRequest request = new RecommendationRequest();
        request.setStatus(RequestStatus.ACCEPTED);

        List<RecommendationRequest> twoTestList = List.of(request);
        Stream<RecommendationRequest> result = statusPatternFilter.apply(OneTestList.stream(), recommendationRequestFilterDto);

        Assertions.assertEquals(twoTestList, result.toList());
    }
}