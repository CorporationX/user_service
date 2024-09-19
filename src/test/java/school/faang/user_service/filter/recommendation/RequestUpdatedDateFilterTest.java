package school.faang.user_service.filter.recommendation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class RequestUpdatedDateFilterTest {

    @InjectMocks
    private RequestUpdatedDateFilter requestUpdatedDateFilter;
    private RequestFilterDto filterDto;

    private static final LocalDateTime FIRST_DATE = LocalDateTime
            .of(2024, 5, 15, 12, 30);
    private static final LocalDateTime SECOND_DATE = LocalDateTime
            .of(2024, 6, 30, 8, 45);

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("If updatedAt not null then return true")
        public void whenRequestUpdatedDateFilterParameterNotNullThenReturnTrue() {
            filterDto = RequestFilterDto.builder()
                    .updatedAt(LocalDate.from(FIRST_DATE))
                    .build();

            assertTrue(requestUpdatedDateFilter.isApplicable(filterDto));
        }

        @Test
        @DisplayName("If updatedAt valid then return filtered list")
        public void whenRequestUpdatedDateFilterWithValidParameterThenReturnFilteredDtoList() {
            Stream<RecommendationRequest> requests = Stream.of(
                    RecommendationRequest.builder()
                            .updatedAt(FIRST_DATE)
                            .build(),
                    RecommendationRequest.builder()
                            .updatedAt(SECOND_DATE)
                            .build());

            filterDto = RequestFilterDto.builder()
                    .updatedAt(LocalDate.from(SECOND_DATE))
                    .build();

            List<RecommendationRequest> filteredRequests = List.of(
                    RecommendationRequest.builder()
                            .updatedAt(SECOND_DATE)
                            .build());

            assertEquals(filteredRequests, requestUpdatedDateFilter
                    .applyFilter(requests, filterDto).toList());
        }
    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("If updatedAt is null then return false")
        public void whenRequestUpdatedDateFilterParameterIsNullThenReturnFalse() {
            filterDto = RequestFilterDto.builder()
                    .updatedAt(null)
                    .build();

            assertFalse(requestUpdatedDateFilter.isApplicable(filterDto));
        }
    }
}
