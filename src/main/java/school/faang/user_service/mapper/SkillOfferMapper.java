package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)

public interface SkillOfferMapper {
    SkillOfferMapper INSTANCE = Mappers.getMapper(SkillOfferMapper.class);

    @Mapping(target = "skillId", source = "skill.id")
    SkillOfferDto toDTO(SkillOffer skillOffer);

    @Mapping(target = "skill", ignore = true)
    SkillOffer toEntity(SkillOfferDto skillOfferDTO);

    List<SkillOfferDto> toDto(List<SkillOffer> skillOffers);

    List<SkillOffer> toEntity(List<SkillOfferDto> skillOffers);
}
