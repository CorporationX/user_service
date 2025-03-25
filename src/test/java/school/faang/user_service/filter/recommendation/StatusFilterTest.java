package school.faang.user_service.filter.recommendation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class StatusFilterTest {

    @InjectMocks
    private StatusFilter statusFilter;

    @Test
    public void testIsApplicableStatusNullReturnsFalse() {
        var filterDto = new RequestFilterDto();

        var result = statusFilter.isApplicable(filterDto);

        assertFalse(result);
    }

    @Test
    public void testIsApplicableStatusPresentReturnsTrue() {
        var filterDto = new RequestFilterDto();
        filterDto.setStatus(RequestStatus.ACCEPTED);

        var result = statusFilter.isApplicable(filterDto);

        assertTrue(result);
    }

    @Test
    public void testApplyHasMatchedRecommendationRequestsReturnsNonEmptyStream() {
        var filterDto = new RequestFilterDto();
        filterDto.setStatus(RequestStatus.PENDING);
        var expectedItems = generateExpectedItems();
        var source = generateSource(expectedItems);

        var result = statusFilter.apply(source, filterDto);

        assertIterableEquals(expectedItems, result.toList());
    }

    private static List<RecommendationRequest> generateExpectedItems() {
        return List.of(
                RecommendationRequest.builder()
                        .id(1L)
                        .status(RequestStatus.PENDING)
                        .build(),
                RecommendationRequest.builder()
                        .id(2L)
                        .status(RequestStatus.PENDING)
                        .build());
    }

    private static Stream<RecommendationRequest> generateSource(List<RecommendationRequest> expectedItems) {
        return Stream.concat(
                expectedItems.stream(),
                Stream.of(
                        RecommendationRequest.builder()
                                .id(10L)
                                .status(RequestStatus.REJECTED)
                                .build(),
                        RecommendationRequest.builder()
                                .id(11L)
                                .status(RequestStatus.ACCEPTED)
                                .build()));
    }
}
