package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.service.filer.IdPatternFilter;

import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class IdPatternFilterTest {
    private final IdPatternFilter idPatternFilter = new IdPatternFilter();

    private List<RecommendationRequest> oneTestList;

    @BeforeEach
    void init() {
        RecommendationRequest requestOne = new RecommendationRequest();
        requestOne.setId(1L);
        RecommendationRequest requestTwo = new RecommendationRequest();
        requestTwo.setId(2L);
        oneTestList = List.of(requestOne, requestTwo);
    }

    @Test
    void testIdPatternReturnTrue() {
        RequestFilterDto requestFilterDto = new RequestFilterDto(1L, null);
        boolean isApplicable = idPatternFilter.isApplication(requestFilterDto);
        Assertions.assertTrue(isApplicable);
    }

    @Test
    void testIdPatternReturnFalse() {
        RequestFilterDto requestFilterDto = new RequestFilterDto();
        boolean isApplicable = idPatternFilter.isApplication(requestFilterDto);
        Assertions.assertFalse(isApplicable);
    }

    @Test
    void testIdPatternApply() {
        RequestFilterDto requestFilterDto = new RequestFilterDto(1L, null);
        RecommendationRequest request = new RecommendationRequest();
        request.setId(1L);

        List<RecommendationRequest> requestStreamOther = List.of(request);
        Stream<RecommendationRequest> result = idPatternFilter.apply(oneTestList.stream(), requestFilterDto);

        Assertions.assertEquals(requestStreamOther, result.toList());
    }
}