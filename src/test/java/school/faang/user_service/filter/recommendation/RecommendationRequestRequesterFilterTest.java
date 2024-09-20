package school.faang.user_service.filter.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestRequesterFilterTest {
    @InjectMocks
    private RecommendationRequestRequesterFilter recommendationRequestRequesterFilter;
    @Mock
    private RecommendationRequestFilterDto filter;

    @BeforeEach
    void setUp() {
        recommendationRequestRequesterFilter = new RecommendationRequestRequesterFilter();
        filter = new RecommendationRequestFilterDto();
    }

    // Tests for isApplicable method
    @Test
    void isApplicable_ShouldReturnFalse_WhenFilterIsNull() {
        boolean result = recommendationRequestRequesterFilter.isApplicable(null);

        assertFalse(result);
    }

    @Test
    void isApplicable_ShouldReturnFalse_WhenFilterRequesterIdIsNull() {
        filter.setRequesterId(null);
        boolean result = recommendationRequestRequesterFilter.isApplicable(filter);

        assertFalse(result);
    }

    @Test
    void isApplicable_ShouldReturnTrue_WhenFilterHasRequesterId() {
        filter.setRequesterId(1L);

        boolean result = recommendationRequestRequesterFilter.isApplicable(filter);

        assertTrue(result);
    }

    // Tests for apply method
    @Test
    void apply_ShouldReturnFilteredEmptyStream_WhenNoRecommendationRequestMatchesRequesterId() {
        Stream<RecommendationRequest> recommendationRequests = prepareData();
        filter.setRequesterId(999L);

        List<RecommendationRequest> result = recommendationRequestRequesterFilter.apply(recommendationRequests, filter).toList();

        assertTrue(result.isEmpty());
    }

    @Test
    void apply_ShouldReturnFilteredStream_WhenRecommendationRequestRequesterIdMatches() {
        Stream<RecommendationRequest> recommendationRequests = prepareData();
        filter.setRequesterId(1L);

        List<RecommendationRequest> result = recommendationRequestRequesterFilter.apply(recommendationRequests, filter).toList();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getRequester().getId());
    }

    @Test
    void apply_ShouldReturnAllMatchingRequests_WhenMultipleRequestsMatchRequesterId() {
        Stream<RecommendationRequest> recommendationRequests = prepareData();
        filter.setRequesterId(2L);

        List<RecommendationRequest> result = recommendationRequestRequesterFilter.apply(recommendationRequests, filter).toList();

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(req -> req.getRequester().getId().equals(2L)));
    }

    // Подготовка данных для тестов
    private Stream<RecommendationRequest> prepareData() {
        User requester1 = new User();
        requester1.setId(1L);

        User requester2 = new User();
        requester2.setId(2L);

        RecommendationRequest request1 = new RecommendationRequest();
        request1.setRequester(requester1);

        RecommendationRequest request2 = new RecommendationRequest();
        request2.setRequester(requester2);

        RecommendationRequest request3 = new RecommendationRequest();
        request3.setRequester(requester2);

        List<RecommendationRequest> recommendationRequests = List.of(request1, request2, request3);

        return recommendationRequests.stream();
    }
}