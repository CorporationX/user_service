package school.faang.user_service.filter.recommendation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class RequestRejectionReasonFilterTest {

    private static final String REJECTION_REASON = "good";
    private static final String TOO_GOOD = "Too good is true";
    private static final String TOO_BAD = "Too bad is false";
    @InjectMocks
    private RequestRejectionReasonFilter requestRejectionReasonFilter;
    private RequestFilterDto filterDto;

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("If RequestFilterDto has rejectionReason field then true")
        public void whenRequestRejectionReasonFilterParameterNotNullThenReturnTrue() {
            filterDto = RequestFilterDto.builder()
                    .rejectionReason(REJECTION_REASON)
                    .build();

            assertTrue(requestRejectionReasonFilter.isApplicable(filterDto));
        }

        @Test
        @DisplayName("If RequestFilterDto has valid rejectionReason then return filtered RQs list")
        public void whenRequestRejectionReasonFilterWithValidParameterThenReturnFilteredDtoList() {
            Stream<RecommendationRequest> requests = Stream.of(
                    RecommendationRequest.builder()
                            .rejectionReason(TOO_GOOD)
                            .build(),
                    RecommendationRequest.builder()
                            .rejectionReason(TOO_BAD)
                            .build());

            filterDto = RequestFilterDto.builder()
                    .rejectionReason(REJECTION_REASON)
                    .build();

            List<RecommendationRequest> filteredRequests = List.of(
                    RecommendationRequest.builder()
                            .rejectionReason(TOO_GOOD)
                            .build());

            assertEquals(filteredRequests.size(), requestRejectionReasonFilter.applyFilter(requests,
                    filterDto).toList().size());
        }
    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("If rejectionReason is null then return false")
        public void whenRequestRejectionReasonFilterParameterIsNullThenReturnFalse() {
            filterDto = RequestFilterDto.builder()
                    .rejectionReason(null)
                    .build();

            assertFalse(requestRejectionReasonFilter.isApplicable(filterDto));
        }

        @Test
        @DisplayName("If rejectionReason is blank then return false")
        public void whenRequestRejectionReasonFilterParameterIsBlankThenReturnFalse() {
            filterDto = RequestFilterDto.builder()
                    .rejectionReason(" ")
                    .build();

            assertFalse(requestRejectionReasonFilter.isApplicable(filterDto));
        }
    }
}
