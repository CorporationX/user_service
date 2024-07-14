package school.faang.service.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.service.user.dto.recommendation.SkillOfferDto;
import school.faang.service.user.entity.recommendation.SkillOffer;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface SkillOfferMapper {

    @Mapping(source = "skill.id", target = "skillId")
    @Mapping(source = "recommendation.id", target = "recommendationId")
    SkillOfferDto toDto(SkillOffer skillOffer);

    @Mapping(source = "skillId", target = "skill.id")
    @Mapping(source = "recommendationId", target = "recommendation.id")
    SkillOffer toEntity(SkillOfferDto skillOfferDto);
}
