package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.SkillOffer;

@Mapper(componentModel = "spring")
public interface SkillOfferMapper {
    SkillOfferDto toDto(SkillOffer skOf);
    SkillOffer toEntity(SkillOfferDto skGto);
}
