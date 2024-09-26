package school.faang.user_service.filter.recommendation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class RequestCreatedDateFilterTest {

    @InjectMocks
    private RequestCreatedDateFilter requestCreatedDateFilter;
    private RecommendationRequestFilterDto filterDto;
    private static final LocalDateTime FIRST_DATE = LocalDateTime
            .of(2024, 5, 15, 12, 30);
    private static final LocalDateTime SECOND_DATE = LocalDateTime
            .of(2024, 6, 30, 8, 45);

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("If createdAt not null then return true")
        public void whenRequestCreatedDateFilterParameterNotNullThenReturnTrue() {
            filterDto = RecommendationRequestFilterDto.builder()
                    .createdDate(LocalDate.from(FIRST_DATE))
                    .build();

            assertTrue(requestCreatedDateFilter.isApplicable(filterDto));
        }

        @Test
        @DisplayName("If createdAt valid then return filtered list")
        public void whenRequestCreatedDateFilterWithValidParameterThenReturnFilteredDtoList() {
            Stream<RecommendationRequest> requests = Stream.of(
                    RecommendationRequest.builder()
                            .createdAt(FIRST_DATE)
                            .build(),
                    RecommendationRequest.builder()
                            .createdAt(SECOND_DATE)
                            .build());

            filterDto = RecommendationRequestFilterDto.builder()
                    .createdDate(LocalDate.from(SECOND_DATE))
                    .build();

            List<RecommendationRequest> filteredRequests = List.of(
                    RecommendationRequest.builder()
                            .createdAt(SECOND_DATE)
                            .build());

            assertEquals(filteredRequests, requestCreatedDateFilter
                    .applyFilter(requests, filterDto).toList());
        }
    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("If createdAt is null then return false")
        public void whenRequestCreatedDateFilterParameterIsNullThenReturnFalse() {
            filterDto = RecommendationRequestFilterDto.builder()
                    .createdDate(null)
                    .build();

            assertFalse(requestCreatedDateFilter.isApplicable(filterDto));
        }
    }
}
