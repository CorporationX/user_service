package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SkillOfferMapper {
    SkillOffer skillOfferDtoToSkillOffer(SkillOfferDto skillOfferDto);

    SkillOfferDto skillOfferToSkillOfferDto(SkillOffer skillOffer);

    List<SkillOffer> skillOfferDtosToSkillOffers(List<SkillOfferDto> skillOfferDtos);

    List<SkillOfferDto> SkillOffersToSkillOfferDtos(List<SkillOffer> skillOffers);
}
