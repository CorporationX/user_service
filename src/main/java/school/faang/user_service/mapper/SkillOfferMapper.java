package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillOfferMapper {

    @Mapping(source = "skill.id", target = "skillId")
    @Mapping(source = "recommendation.id", target = "recommendationId")
    SkillOfferDto toSkillOfferDto(SkillOffer skillOffer);

    @Mapping(target = "skill", expression = "java(mapSkill(skillOfferDto.getSkillId()))")
    @Mapping(target = "recommendation", expression = "java(mapSkill(skillOfferDto.getRecommendationId()))")

    List<SkillOffer> toSkillOfferList(List<SkillOfferDto> skillOfferDtoList);

    List<SkillOfferDto> toSkillOfferDtoList(List<SkillOffer> skillOfferList);

    default Skill mapSkill(Long skillId) {
        if (skillId == null) {
            return null;
        }
        return Skill.builder()
                .id(skillId)
                .build();
    }

    default Recommendation mapRecommendation(Long recommendationId) {
        if (recommendationId == null) {
            return null;
        }
        return Recommendation.builder()
                .id(recommendationId)
                .build();
    }
}
