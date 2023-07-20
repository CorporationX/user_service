package school.faang.user_service.mapper;

import org.mapstruct.*;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, unmappedTargetPolicy = ReportingPolicy.IGNORE)

public interface SkillOfferMapper {
    @Mapping(source = "skill.id", target = "skillId")
    @Mapping(source = "recommendation.id", target = "recommendationId")
    SkillOfferDto toDto(SkillOffer skillOffer);
    SkillOffer toEntity(SkillOfferDto skillOfferDto);

    @Named("listSkillOffersDto")
    default List<SkillOfferDto> listOffersDto(List<SkillOffer> skillOffers) {
        List<SkillOfferDto> skillOfferDtos = skillOffers.stream()
                .map(this::toDto)
                .toList();
        return skillOfferDtos;
    }
}
