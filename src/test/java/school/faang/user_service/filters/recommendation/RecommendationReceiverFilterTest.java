package school.faang.user_service.filters.recommendation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.FilterRecommendationRequestDto;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static school.faang.user_service.filters.recommendation.RecommendationTestData.AUTHOR_ID_1;
import static school.faang.user_service.filters.recommendation.RecommendationTestData.RECEIVER_ID_1;
import static school.faang.user_service.filters.recommendation.RecommendationTestData.RECEIVER_ID_2;
import static school.faang.user_service.filters.recommendation.RecommendationTestData.REC_ID_1;
import static school.faang.user_service.filters.recommendation.RecommendationTestData.REC_ID_2;
import static school.faang.user_service.filters.recommendation.RecommendationTestData.REC_ID_3;
import static school.faang.user_service.filters.recommendation.RecommendationTestData.rec;

@ExtendWith(MockitoExtension.class)
public class RecommendationReceiverFilterTest {
    private final RecommendationReceiverFilter filter = new RecommendationReceiverFilter();

    public static FilterRecommendationRequestDto filterByReceiver(Long receiverId) {
        return new FilterRecommendationRequestDto(null, null, receiverId);
    }

    @Test
    public void testIsApplicable_withNonNullReceiverId_returnsTrue() {
        assertTrue(filter.isApplicable(filterByReceiver(RECEIVER_ID_2))); // 456L
        assertTrue(filter.isApplicable(filterByReceiver(null)));
    }

    @Test
    public void testIsApplicable_withNullReceiverId_returnsFalse() {
        assertFalse(filter.isApplicable(null));
    }

    @Test
    public void testApply_withMatchingReceiverId_filtersRecommendationsCorrectly() {
        Recommendation rec1 = rec(REC_ID_1, AUTHOR_ID_1, RECEIVER_ID_2, "Recommendation 1"); // 456L
        Recommendation rec2 = rec(REC_ID_2, AUTHOR_ID_1, RECEIVER_ID_1, "Recommendation 2"); // 789L
        Recommendation rec3 = rec(REC_ID_3, AUTHOR_ID_1, RECEIVER_ID_1, "Recommendation 3"); // 789L

        List<Recommendation> filtered = filter
                .apply(Stream.of(rec1, rec2, rec3), filterByReceiver(RECEIVER_ID_2))
                .toList();

        assertEquals(1, filtered.size());
        assertEquals(RECEIVER_ID_2, filtered.get(0).getReceiver().getId());
    }

    @Test
    public void testApply_withNonMatchingReceiverId_excludesAllRecommendations() {
        Recommendation rec1 = rec(REC_ID_1, AUTHOR_ID_1, RECEIVER_ID_2, "Recommendation 1");
        Recommendation rec2 = rec(REC_ID_2, AUTHOR_ID_1, RECEIVER_ID_1, "Recommendation 2");
        Recommendation rec3 = rec(REC_ID_3, AUTHOR_ID_1, RECEIVER_ID_1, "Recommendation 3");

        List<Recommendation> filtered = filter
                .apply(Stream.of(rec1, rec2, rec3), filterByReceiver(999L))
                .toList();

        assertTrue(filtered.isEmpty());
    }

    @Test
    public void testApply_withNullReceiverId_doesNotFilterRecommendations() {
        Recommendation rec1 = rec(REC_ID_1, AUTHOR_ID_1, RECEIVER_ID_2, "Recommendation 1");
        Recommendation rec2 = rec(REC_ID_2, AUTHOR_ID_1, RECEIVER_ID_1, "Recommendation 2");
        Recommendation rec3 = rec(REC_ID_3, AUTHOR_ID_1, RECEIVER_ID_1, "Recommendation 3");

        List<Recommendation> filtered = filter
                .apply(Stream.of(rec1, rec2, rec3), filterByReceiver(null))
                .toList();

        assertEquals(3, filtered.size());
    }
}