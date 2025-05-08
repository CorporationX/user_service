package school.faang.user_service.filter.recommendation;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.filter.recommendationrequest.RecommendationRequestSkillsFilter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecommendationRequestSkillsFilterTest {
    @Test
    public void testIsApplicableReturnsTrueWhenSkillsAreNotNullAndNotEmpty() {
        RequestFilterDto dto = new RequestFilterDto();
        SkillRequest skill1 = new SkillRequest();
        skill1.setId(1L);
        SkillRequest skill2 = new SkillRequest();
        skill1.setId(2L);
        List<SkillRequest> requiredSkills = Arrays.asList(skill1, skill2);
        dto.setSkills(requiredSkills);
        RecommendationRequestSkillsFilter filter = new RecommendationRequestSkillsFilter();

        assertTrue(filter.isApplicable(dto));
    }

    @Test
    public void testIsApplicableReturnsFalseWhenSkillsAreNull() {
        RequestFilterDto dto = new RequestFilterDto();
        dto.setSkills(null);
        RecommendationRequestSkillsFilter filter = new RecommendationRequestSkillsFilter();

        assertFalse(filter.isApplicable(dto));
    }

    @Test
    public void testIsApplicableReturnsFalseWhenSkillsAreEmpty() {
        RequestFilterDto dto = new RequestFilterDto();
        dto.setSkills(Collections.emptyList());
        RecommendationRequestSkillsFilter filter = new RecommendationRequestSkillsFilter();

        assertFalse(filter.isApplicable(dto));
    }

    @Test
    public void testApplyFiltersRequestsBySkills() {
        // Create skills for the filter
        SkillRequest skillJava = new SkillRequest();
        skillJava.setId(1L);
        SkillRequest skillSQL = new SkillRequest();
        skillSQL.setId(2L);
        SkillRequest skillPython = new SkillRequest();
        skillPython.setId(3L);
        List<SkillRequest> requiredSkills = Arrays.asList(skillJava, skillSQL);

        RequestFilterDto dto = new RequestFilterDto();
        dto.setSkills(requiredSkills);
        RecommendationRequestSkillsFilter filter = new RecommendationRequestSkillsFilter();

        RecommendationRequest req1 = new RecommendationRequest();
        req1.setSkills(Arrays.asList(skillJava, skillSQL, skillPython));

        RecommendationRequest req2 = new RecommendationRequest();
        req2.setSkills(List.of(skillJava));

        RecommendationRequest req3 = new RecommendationRequest();

        List<RecommendationRequest> requests = Arrays.asList(req1, req2, req3);

        List<RecommendationRequest> result = filter.apply(requests.stream(), dto)
                .toList();

        assertEquals(1, result.size());
        assertTrue(result.contains(req1));
    }
}