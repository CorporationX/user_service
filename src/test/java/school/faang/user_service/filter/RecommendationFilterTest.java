package school.faang.user_service.filter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.util.TestDataFactory;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RecommendationFilterTest {

    @InjectMocks
    private RecommendationFilter recommendationFilter;

    @Test
    void matchesFilter() {
        // given - precondition
        var requestFilterDto = TestDataFactory.createRequestFilterDto();
        var recommendationRequest = TestDataFactory.createRecommendationRequest();

        // when - action
        var actualResul = recommendationFilter.matchesFilter(recommendationRequest, requestFilterDto);

        // then - verify the output
        assertThat(actualResul).isTrue();
    }
}