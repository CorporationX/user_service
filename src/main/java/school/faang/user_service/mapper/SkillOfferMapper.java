package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.recommendation.SkillOfferDto;

import school.faang.user_service.entity.recommendation.SkillOffer;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)

public interface SkillOfferMapper {

    SkillOfferMapper INSTANCE = Mappers.getMapper(SkillOfferMapper.class);

    SkillOfferDto toDTO(SkillOffer skillOffer);

    SkillOffer toEntity(SkillOfferDto skillOfferDTO);
}
