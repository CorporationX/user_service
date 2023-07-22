package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.service.RecommendationService;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillOfferMapper {

    SkillOfferDto toDto(SkillOffer skillOffer);

    @Mapping(target = "skill", expression = "java(recommendationService.getSkill(skillOfferDto.getSkillId()))")
    SkillOffer toEntity(SkillOfferDto skillOfferDto, RecommendationService recommendationService);
}
