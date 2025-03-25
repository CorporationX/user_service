package school.faang.user_service.filter.recommendation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.lang.Nullable;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class CreatedAtFilterTest {

    @InjectMocks
    private CreatedAtFilter createdAtFilter;

    @Test
    public void testIsApplicableCreatedAtFromAndToAreNullReturnsFalse() {
        var filterDto = new RequestFilterDto();

        var result = createdAtFilter.isApplicable(filterDto);

        assertFalse(result);
    }

    @Test
    public void testIsApplicableCreatedAtFromIsSetReturnsTrue() {
        var filterDto = new RequestFilterDto();
        filterDto.setCreatedAtFrom(LocalDateTime.now());

        var result = createdAtFilter.isApplicable(filterDto);

        assertTrue(result);
    }

    @Test
    public void testIsApplicableCreatedAtToIsSetReturnsTrue() {
        var filterDto = new RequestFilterDto();
        filterDto.setCreatedAtTo(LocalDateTime.now());

        var result = createdAtFilter.isApplicable(filterDto);

        assertTrue(result);
    }

    @Test
    public void testIsApplicableCreatedAtFromAndToAreSetReturnsTrue() {
        var filterDto = new RequestFilterDto();
        filterDto.setCreatedAtFrom(LocalDateTime.now());
        filterDto.setCreatedAtTo(LocalDateTime.now());

        var result = createdAtFilter.isApplicable(filterDto);

        assertTrue(result);
    }

    @Test
    public void testApplyFromIsSetAndHasMatchedRequestsReturnsNonEmptyStream() {
        // Arrange
        var from = LocalDateTime.now().minusHours(10);

        var filterDto = new RequestFilterDto();
        filterDto.setCreatedAtFrom(from);

        var expectedItems = generateExpectedItems(from, null);
        var source = generateSource(expectedItems, from, null);

        // Act
        var result = createdAtFilter.apply(source, filterDto);

        // Assert
        assertIterableEquals(expectedItems, result.toList());
    }

    @Test
    public void testApplyToIsSetAndHasMatchedRequestsReturnsNonEmptyStream() {
        // Arrange
        var to = LocalDateTime.now();

        var filterDto = new RequestFilterDto();
        filterDto.setCreatedAtTo(to);

        var expectedItems = generateExpectedItems(null, to);
        var source = generateSource(expectedItems, null, to);

        // Act
        var result = createdAtFilter.apply(source, filterDto);

        // Assert
        assertIterableEquals(expectedItems, result.toList());
    }

    @Test
    public void testApplyFromAndToAreSetAndHasMatchedRequestsReturnsNonEmptyStream() {
        // Arrange
        var from = LocalDateTime.now().minusHours(1);
        var to = LocalDateTime.now();

        var filterDto = new RequestFilterDto();
        filterDto.setCreatedAtFrom(from);
        filterDto.setCreatedAtTo(to);

        var expectedItems = generateExpectedItems(from, to);
        var source = generateSource(expectedItems, from, to);

        // Act
        var result = createdAtFilter.apply(source, filterDto);

        // Assert
        assertIterableEquals(expectedItems, result.toList());
    }

    private static List<RecommendationRequest> generateExpectedItems(
            @Nullable LocalDateTime from,
            @Nullable LocalDateTime to) {
        List<RecommendationRequest> result = new ArrayList<>();

        if (from != null) {
            result.add(RecommendationRequest.builder()
                    .id(1L)
                    .createdAt(from)
                    .build());
            result.add(RecommendationRequest.builder()
                    .id(2L)
                    .createdAt(from.plusMinutes(1))
                    .build());
        }

        if (to != null) {
            result.add(RecommendationRequest.builder()
                    .id(3L)
                    .createdAt(to.minusMinutes(1))
                    .build());
            result.add(RecommendationRequest.builder()
                    .id(4L)
                    .createdAt(to)
                    .build());
        }

        return result;
    }

    private static Stream<RecommendationRequest> generateSource(
            List<RecommendationRequest> expectedItems,
            @Nullable LocalDateTime from,
            @Nullable LocalDateTime to) {
        List<RecommendationRequest> unmatchedItems = new ArrayList<>();

        if (from != null) {
            unmatchedItems.add(RecommendationRequest.builder()
                    .id(10L)
                    .createdAt(from.minusMinutes(10))
                    .build());
        }

        if (to != null) {
            unmatchedItems.add(RecommendationRequest.builder()
                    .id(11L)
                    .createdAt(to.plusMinutes(10))
                    .build());
        }

        unmatchedItems.addAll(0, expectedItems);

        return unmatchedItems.stream();
    }
}
