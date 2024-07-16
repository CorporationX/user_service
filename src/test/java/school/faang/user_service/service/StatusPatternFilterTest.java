package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.service.filer.StatusPatternFilter;

import java.util.List;
import java.util.stream.Stream;

class StatusPatternFilterTest {
    private StatusPatternFilter statusPatternFilter;
    private List<RecommendationRequest> OneTestList;

    @BeforeEach
    void init() {
        statusPatternFilter = new StatusPatternFilter();
        RecommendationRequest requestOne = new RecommendationRequest();
        requestOne.setStatus(RequestStatus.ACCEPTED);
        RecommendationRequest requestTwo = new RecommendationRequest();
        requestTwo.setStatus(RequestStatus.PENDING);
        OneTestList = List.of(requestOne, requestTwo);
    }

    @Test
    void testStatusPatternTrue() {
        RequestFilterDto requestFilterDto = new RequestFilterDto(RequestStatus.ACCEPTED);
        boolean isApplication = statusPatternFilter.isApplication(requestFilterDto);
        Assertions.assertTrue(isApplication);
    }

    @Test
    void testStatusPatternFalse() {
        RequestFilterDto requestFilterDto = new RequestFilterDto();
        boolean isApplication = statusPatternFilter.isApplication(requestFilterDto);
        Assertions.assertFalse(isApplication);
    }

    @Test
    void testStatusPatternApply() {
        RequestFilterDto requestFilterDto = new RequestFilterDto(RequestStatus.ACCEPTED);
        RecommendationRequest request = new RecommendationRequest();
        request.setStatus(RequestStatus.ACCEPTED);

        List<RecommendationRequest> twoTestList = List.of(request);
        Stream<RecommendationRequest> result = statusPatternFilter.apply(OneTestList.stream(), requestFilterDto);

        Assertions.assertEquals(twoTestList, result.toList());
    }
}