package school.faang.user_service.filter.recommendation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class RequestStatusFilterTest {
    @InjectMocks
    private RequestStatusFilter requestStatusFilter;
    private RequestFilterDto filterDto;
    private static final RequestStatus REQUEST_STATUS_IS_ACCEPTED = RequestStatus.ACCEPTED;

    @Nested
    class PositiveTests {
        @Test
        @DisplayName("If filterDto status not null then true")
        public void validateRequestFilterDtoStatusNotNullTest() {
            filterDto = RequestFilterDto.builder()
                    .status(REQUEST_STATUS_IS_ACCEPTED)
                    .build();

            assertTrue(requestStatusFilter.isApplicable(filterDto));
        }

        @Test
        @DisplayName("If filterDto got status then filtered list returns")
        public void validateRequestFilterDtoReturnsFilteredListTest() {
            Stream<RecommendationRequest> recommendationRequests = Stream.of(
                    RecommendationRequest.builder()
                            .status(REQUEST_STATUS_IS_ACCEPTED)
                            .build(),
                    RecommendationRequest.builder()
                            .status(RequestStatus.REJECTED)
                            .build());

            filterDto = RequestFilterDto.builder()
                    .status(REQUEST_STATUS_IS_ACCEPTED)
                    .build();

            List<RecommendationRequest> requestsWithFilterApplied = List.of(
                    RecommendationRequest.builder()
                            .status(REQUEST_STATUS_IS_ACCEPTED)
                            .build());

            assertEquals(requestsWithFilterApplied, requestStatusFilter.applyFilter(recommendationRequests,
                    filterDto).toList());
        }
    }

    @Nested
    class NegativeTest {

        @Nested
        class isApplicable {

            @Test
            @DisplayName("If status null return false")
            public void validateRequestFilterDtoStatusIsNullTest() {
                filterDto = RequestFilterDto.builder()
                        .status(null)
                        .build();

                assertFalse(requestStatusFilter.isApplicable(filterDto));
            }
        }
    }

}
