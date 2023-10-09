package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.dto.recommendation.SkillOfferUpdateDto;
import school.faang.user_service.entity.recommendation.SkillOffer;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillOfferMapper {

    @Mapping(target = "skillId", source = "skill.id")
    @Mapping(target = "recommendationId", source = "recommendation.id")
    SkillOfferDto toDto(SkillOffer skillOffer);

    @Mapping(target = "skill.id", source = "skillId")
    @Mapping(target = "recommendation.id", source = "recommendationId")
    SkillOffer toEntity(SkillOfferDto skillOfferDto);

    @Mapping(target = "skillId", source = "skill.id")
    @Mapping(target = "recommendationId", source = "recommendation.id")
    SkillOfferUpdateDto toDtoUpdate(SkillOffer skillOffer);

    @Mapping(target = "skill.id", source = "skillId")
    @Mapping(target = "recommendation.id", source = "recommendationId")
    SkillOffer toEntityUpdate(SkillOfferUpdateDto skillOfferDto);
}
