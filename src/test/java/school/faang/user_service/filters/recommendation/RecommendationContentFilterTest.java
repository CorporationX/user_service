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
import static school.faang.user_service.filters.recommendation.RecommendationTestData.REC_ID_1;
import static school.faang.user_service.filters.recommendation.RecommendationTestData.REC_ID_2;
import static school.faang.user_service.filters.recommendation.RecommendationTestData.REC_ID_3;
import static school.faang.user_service.filters.recommendation.RecommendationTestData.rec;

@ExtendWith(MockitoExtension.class)
public class RecommendationContentFilterTest {

    public static final String CONTENT_JAVA_1      = "Good Java Developer";
    public static final String CONTENT_PYTHON_1    = "Excellent Python Developer";
    public static final String CONTENT_JS_1        = "Good JavaScript Developer";
    public static final String CONTENT_JAVA_UPPER  = "Good JAVA Developer";
    public static final String CONTENT_JAVA_LOWER  = "Good java Developer";

    private final RecommendationContentFilter filter = new RecommendationContentFilter();

    public static FilterRecommendationRequestDto filterByContent(String content) {
        return new FilterRecommendationRequestDto(content, null, null);
    }

    @Test
    public void testIsApplicable_withNonEmptyContent_returnsTrue() {
        FilterRecommendationRequestDto filterDto = filterByContent("test");
        assertTrue(filter.isApplicable(filterDto));

        assertTrue(filter.isApplicable(filterByContent(null)));
        assertTrue(filter.isApplicable(filterByContent("   ")));
    }

    @Test
    public void testIsApplicable_withNullContent_returnsFalse() {
        assertFalse(filter.isApplicable(null));
    }

    @Test
    public void testApply_withMatchingContent_filtersRecommendationsCorrectly() {
        Recommendation rec1 = rec(REC_ID_1, AUTHOR_ID_1, RECEIVER_ID_1, CONTENT_JAVA_1);
        Recommendation rec2 = rec(REC_ID_2, AUTHOR_ID_1, RECEIVER_ID_1, CONTENT_PYTHON_1);
        Recommendation rec3 = rec(REC_ID_3, AUTHOR_ID_1, RECEIVER_ID_1, CONTENT_JS_1);

        FilterRecommendationRequestDto filterDto = filterByContent("Java");

        List<Recommendation> filtered = filter.apply(Stream.of(rec1, rec2, rec3), filterDto).toList();

        assertEquals(2, filtered.size());
        assertTrue(filtered.get(0).getContent().contains("Java"));
        assertTrue(filtered.get(1).getContent().contains("Java"));
    }

    @Test
    public void testApply_withCaseInsensitiveContent_filtersCorrectly() {
        Recommendation rec1 = rec(REC_ID_1, AUTHOR_ID_1, RECEIVER_ID_1, CONTENT_JAVA_UPPER);
        Recommendation rec2 = rec(REC_ID_2, AUTHOR_ID_1, RECEIVER_ID_1, CONTENT_JAVA_LOWER);

        FilterRecommendationRequestDto filterDto = filterByContent("JAva");

        List<Recommendation> filtered = filter.apply(Stream.of(rec1, rec2), filterDto).toList();

        assertEquals(2, filtered.size());
    }

    @Test
    public void testApply_withNullOrBlankContent_returnsAllRecommendations() {
        Recommendation rec1 = rec(REC_ID_1, AUTHOR_ID_1, RECEIVER_ID_1, CONTENT_JAVA_1);
        Recommendation rec2 = rec(REC_ID_2, AUTHOR_ID_1, RECEIVER_ID_1, CONTENT_PYTHON_1);

        List<Recommendation> withNull = filter.apply(Stream.of(rec1, rec2), filterByContent(null)).toList();
        assertEquals(2, withNull.size());

        List<Recommendation> withBlank = filter.apply(Stream.of(rec1, rec2), filterByContent("   ")).toList();
        assertEquals(2, withBlank.size());
    }
}