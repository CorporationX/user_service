package school.faang.user_service.filter.recommendation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class RequesterIdFilterTest {

    @InjectMocks
    private RequesterIdFilter requesterIdFilter;
    private RecommendationRequestFilterDto filterDto;
    private static final long REQUESTER_ID_ONE = 1L;
    private static final long REQUESTER_ID_TWO = 2L;
    private static final long REQUESTER_ID_NEGATIVE_ONE = -1L;

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("If requesterId not null then true")
        public void whenRequesterIdFilterParameterNotNullThenReturnTrue() {
            filterDto = RecommendationRequestFilterDto.builder()
                    .requesterId(REQUESTER_ID_ONE)
                    .build();

            assertTrue(requesterIdFilter.isApplicable(filterDto));
        }

        @Test
        @DisplayName("If RequesterId is valid then filtered list return")
        public void whenRequesterIdFilterWithValidParameterThenReturnFilteredDtoList() {
            User requesterOne = User.builder()
                    .id(REQUESTER_ID_ONE)
                    .build();
            User requesterTwo = User.builder()
                    .id(REQUESTER_ID_TWO)
                    .build();

            Stream<RecommendationRequest> requests = Stream.of(
                    RecommendationRequest.builder()
                            .requester(requesterOne)
                            .build(),
                    RecommendationRequest.builder()
                            .requester(requesterTwo)
                            .build());

            filterDto = RecommendationRequestFilterDto.builder()
                    .requesterId(REQUESTER_ID_TWO)
                    .build();

            List<RecommendationRequest> filteredRequests = List.of(
                    RecommendationRequest.builder()
                            .requester(requesterTwo)
                            .build());

            assertEquals(filteredRequests, requesterIdFilter
                    .applyFilter(requests, filterDto).toList());
        }
    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("If requesterId is null return false")
        public void whenRequesterIdFilterParameterIsNullThenReturnFalse() {
            filterDto = RecommendationRequestFilterDto.builder()
                    .requesterId(null)
                    .build();

            assertFalse(requesterIdFilter.isApplicable(filterDto));
        }

        @Test
        @DisplayName("If requesterId is negative return false")
        public void whenRequesterIdFilterParameterIsNegativeThenReturnFalse() {
            filterDto = RecommendationRequestFilterDto.builder()
                    .requesterId(REQUESTER_ID_NEGATIVE_ONE)
                    .build();

            assertFalse(requesterIdFilter.isApplicable(filterDto));
        }
    }
}
