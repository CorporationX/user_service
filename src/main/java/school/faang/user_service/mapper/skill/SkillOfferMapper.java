package school.faang.user_service.mapper.skill;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.dto.skill.SkillOfferDto;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillOfferMapper {

    @Mapping(source = "skill.id", target = "skillId")
    @Mapping(source = "recommendation.id", target = "recommendationId")
    SkillOfferDto toDto(SkillOffer skillOffer);

    List<SkillOfferDto> toDtos(List<SkillOffer> skillOffers);
}