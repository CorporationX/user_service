package school.faang.user_service.filter.recommendation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class MessageFilterTest {

    @InjectMocks
    private MessageFilter messageFilter;

    @Test
    public void testIsApplicableMessageNullReturnsFalse() {
        var filterDto = new RequestFilterDto();

        var result = messageFilter.isApplicable(filterDto);

        assertFalse(result);
    }

    @Test
    public void testIsApplicableMessageEmptyReturnsFalse() {
        var filterDto = new RequestFilterDto();
        filterDto.setMessagePattern("");

        var result = messageFilter.isApplicable(filterDto);

        assertFalse(result);
    }

    @Test
    public void testIsApplicableMessageBlankReturnsFalse() {
        var filterDto = new RequestFilterDto();
        filterDto.setMessagePattern("   ");

        var result = messageFilter.isApplicable(filterDto);

        assertFalse(result);
    }

    @Test
    public void testIsApplicableMessageNonEmptyReturnsTrue() {
        var filterDto = new RequestFilterDto();
        filterDto.setMessagePattern("Test");

        var result = messageFilter.isApplicable(filterDto);

        assertTrue(result);
    }

    @Test
    public void testApplyHasMatchedRecommendationRequestsReturnsNonEmptyStream() {
        var filterDto = new RequestFilterDto();
        filterDto.setMessagePattern("[a-z ]+");
        var expectedItems = generateExpectedItems();
        var source = generateSource(expectedItems);

        var result = messageFilter.apply(source, filterDto);

        assertIterableEquals(expectedItems, result.toList());
    }

    private static List<RecommendationRequest> generateExpectedItems() {
        return List.of(
                RecommendationRequest.builder()
                        .id(1L)
                        .message("first request")
                        .build(),
                RecommendationRequest.builder()
                        .id(2L)
                        .message("request")
                        .build());
    }

    private static Stream<RecommendationRequest> generateSource(List<RecommendationRequest> expectedItems) {
        return Stream.concat(
                expectedItems.stream(),
                Stream.of(
                        RecommendationRequest.builder()
                                .id(10L)
                                .message("2nd request")
                                .build(),
                        RecommendationRequest.builder()
                                .id(11L)
                                .message("Third Request")
                                .build()));
    }
}
