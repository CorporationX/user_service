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
public class ReceiverIdFilterTest {

    @InjectMocks
    private ReceiverIdFilter receiverIdFilter;
    private RecommendationRequestFilterDto filterDto;
    private static final long RECEIVER_ID_ONE = 1L;
    private static final long RECEIVER_ID_TWO = 2L;
    private static final long RECEIVER_ID_NEGATIVE_ONE = -1L;

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("If receiverId not null then true")
        public void whenReceiverIdFilterParameterNotNullThenReturnTrue() {
            filterDto = RecommendationRequestFilterDto.builder()
                    .receiverId(RECEIVER_ID_ONE)
                    .build();

            assertTrue(receiverIdFilter.isApplicable(filterDto));
        }

        @Test
        @DisplayName("If receiverId is valid then filtered list return")
        public void whenReceiverIdFilterWithValidParameterThenReturnFilteredDtoList() {
            User receiverOne = User.builder()
                    .id(RECEIVER_ID_ONE)
                    .build();
            User receiverTwo = User.builder()
                    .id(RECEIVER_ID_TWO)
                    .build();

            Stream<RecommendationRequest> requests = Stream.of(
                    RecommendationRequest.builder()
                            .receiver(receiverOne)
                            .build(),
                    RecommendationRequest.builder()
                            .receiver(receiverTwo)
                            .build());

            filterDto = RecommendationRequestFilterDto.builder()
                    .receiverId(RECEIVER_ID_TWO)
                    .build();

            List<RecommendationRequest> filteredRequests = List.of(
                    RecommendationRequest.builder()
                            .receiver(receiverTwo)
                            .build());

            assertEquals(filteredRequests, receiverIdFilter
                    .applyFilter(requests, filterDto).toList());
        }
    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("If receiverId is null return false")
        public void whenReceiverIdFilterParameterIsNullThenReturnFalse() {
            filterDto = RecommendationRequestFilterDto.builder()
                    .requesterId(null)
                    .build();

            assertFalse(receiverIdFilter.isApplicable(filterDto));
        }

        @Test
        @DisplayName("If receiverId is negative return false")
        public void whenReceiverIdFilterParameterIsNegativeReturnFalse() {
            filterDto = RecommendationRequestFilterDto.builder()
                    .receiverId(RECEIVER_ID_NEGATIVE_ONE)
                    .build();

            assertFalse(receiverIdFilter.isApplicable(filterDto));
        }
    }
}
