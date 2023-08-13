package school.faang.user_service.service.reccomendation.filter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.filter.RequestFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.util.filter.recommendationRequest.RecommendationRequestSkillIdsFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecommendationRequestSkillIdsFilterTest {

    RecommendationRequestSkillIdsFilter filter = new RecommendationRequestSkillIdsFilter();

    @Test
    @DisplayName("isApplicable returns true if request has at least one skill")
    void isApplicable_positiveTest() {
        RequestFilterDto filterDto = new RequestFilterDto();
        filterDto.setSkillIds(List.of(1L));
        assertTrue(filter.isApplicable(filterDto));
    }

    @Test
    @DisplayName("isApplicable returns false if request does not have at least one skill")
    void isApplicable_negativeTest() {
        RequestFilterDto filterDto = new RequestFilterDto();
        filterDto.setSkillIds(new ArrayList<>());
        assertFalse(filter.isApplicable(filterDto));
    }

    @Test
    @DisplayName("isApplicable returns false if request has null skill ids")
    void isApplicable_nullTest() {
        RequestFilterDto filterDto = new RequestFilterDto();
        filterDto.setSkillIds(null);
        assertFalse(filter.isApplicable(filterDto));
    }

    @Test
    @DisplayName("apply return correct recommendation requests")
    void apply_positiveTest() {
        RequestFilterDto filterDto = new RequestFilterDto();
        filterDto.setSkillIds(List.of(23L, 11L, 20L));

        Skill skill23 = new Skill();
        Skill skill11 = new Skill();
        Skill skill20 = new Skill();
        Skill skill100 = new Skill();

        skill23.setId(23L);
        skill11.setId(11L);
        skill20.setId(20L);
        skill100.setId(100L);

        SkillRequest skillRequest23 = new SkillRequest();
        SkillRequest skillRequest11 = new SkillRequest();
        SkillRequest skillRequest20 = new SkillRequest();
        SkillRequest skillRequest100 = new SkillRequest();

        skillRequest23.setSkill(skill23);
        skillRequest11.setSkill(skill11);
        skillRequest20.setSkill(skill20);
        skillRequest100.setSkill(skill100);

        RecommendationRequest allSkills = new RecommendationRequest();
        RecommendationRequest withoutSkills = new RecommendationRequest();
        RecommendationRequest someSkills = new RecommendationRequest();
        RecommendationRequest onlyCorrectSkills = new RecommendationRequest();
        onlyCorrectSkills.setSkills(List.of(skillRequest23, skillRequest11, skillRequest20));
        someSkills.setSkills(List.of(skillRequest100, skillRequest11, skillRequest20));
        withoutSkills.setSkills(new ArrayList<>());
        allSkills.setSkills(List.of(skillRequest23, skillRequest11, skillRequest20, skillRequest100));

        List<RecommendationRequest> result
                = filter.apply(
                        Stream.of(onlyCorrectSkills, withoutSkills, someSkills, onlyCorrectSkills),
                        filterDto
                )
                .toList();

        assertTrue(result.contains(onlyCorrectSkills));
    }
}