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
import static school.faang.user_service.filters.recommendation.RecommendationTestData.AUTHOR_ID_2;
import static school.faang.user_service.filters.recommendation.RecommendationTestData.RECEIVER_ID_1;
import static school.faang.user_service.filters.recommendation.RecommendationTestData.CONTENT_1;
import static school.faang.user_service.filters.recommendation.RecommendationTestData.CONTENT_2;
import static school.faang.user_service.filters.recommendation.RecommendationTestData.CONTENT_3;
import static school.faang.user_service.filters.recommendation.RecommendationTestData.REC_ID_1;
import static school.faang.user_service.filters.recommendation.RecommendationTestData.REC_ID_2;
import static school.faang.user_service.filters.recommendation.RecommendationTestData.REC_ID_3;
import static school.faang.user_service.filters.recommendation.RecommendationTestData.rec;

@ExtendWith(MockitoExtension.class)
public class RecommendationAuthorFilterTest {
    private final RecommendationAuthorFilter filter = new RecommendationAuthorFilter();

    public static FilterRecommendationRequestDto filterByContentAndAuthor(String content, Long authorId) {
        return new FilterRecommendationRequestDto(content, authorId, null);
    }

    public static FilterRecommendationRequestDto filterByAuthor(Long authorId) {
        return new FilterRecommendationRequestDto(null, authorId, null);
    }

    @Test
    public void testIsApplicable_withNonNullAuthorId_returnsTrue() {
        FilterRecommendationRequestDto filterDto = filterByAuthor(AUTHOR_ID_1);
        boolean result = filter.isApplicable(filterDto);
        assertTrue(result);
    }

    @Test
    public void testIsApplicable_withNullAuthorId_returnsFalse() {
        boolean result = filter.isApplicable(null);
        assertFalse(result);
    }

    @Test
    public void testApply_withMatchingAuthorId_filtersRecommendationsCorrectly() {
        Recommendation rec1 = rec(REC_ID_1, AUTHOR_ID_1, RECEIVER_ID_1, CONTENT_1);
        Recommendation rec2 = rec(REC_ID_2, AUTHOR_ID_2, RECEIVER_ID_1, CONTENT_2);
        Recommendation rec3 = rec(REC_ID_3, AUTHOR_ID_2, RECEIVER_ID_1, CONTENT_3);

        FilterRecommendationRequestDto filterDto = filterByContentAndAuthor(CONTENT_1, AUTHOR_ID_1);

        List<Recommendation> filtered = filter.apply(Stream.of(rec1, rec2, rec3), filterDto).toList();

        assertEquals(1, filtered.size());
        assertEquals(AUTHOR_ID_1, filtered.get(0).getAuthor().getId());
    }

    @Test
    public void testApply_withNonMatchingAuthorId_excludesAllRecommendations() {
        Recommendation rec1 = rec(REC_ID_1, AUTHOR_ID_1, RECEIVER_ID_1, CONTENT_1);
        Recommendation rec2 = rec(REC_ID_2, AUTHOR_ID_2, RECEIVER_ID_1, CONTENT_2);
        Recommendation rec3 = rec(REC_ID_3, AUTHOR_ID_2, RECEIVER_ID_1, CONTENT_3);

        FilterRecommendationRequestDto filterDto = filterByAuthor(999L);

        List<Recommendation> filtered = filter.apply(Stream.of(rec1, rec2, rec3), filterDto).toList();

        assertTrue(filtered.isEmpty());
    }

    @Test
    public void testApply_withNullAuthorId_doesNotFilterRecommendations() {
        Recommendation rec1 = rec(REC_ID_1, AUTHOR_ID_1, RECEIVER_ID_1, CONTENT_1);
        Recommendation rec2 = rec(REC_ID_2, AUTHOR_ID_2, RECEIVER_ID_1, CONTENT_2);
        Recommendation rec3 = rec(REC_ID_3, AUTHOR_ID_2, RECEIVER_ID_1, CONTENT_3);

        FilterRecommendationRequestDto filterDto = filterByAuthor(null);

        List<Recommendation> filtered = filter.apply(Stream.of(rec1, rec2, rec3), filterDto).toList();

        assertEquals(3, filtered.size());
    }
}