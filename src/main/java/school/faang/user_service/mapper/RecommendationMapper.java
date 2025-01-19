package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.List;

@Mapper(componentModel = "spring" , unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationMapper {

    @Mapping(source = "skillOffers", target = "skillOffers", qualifiedByName = "mapToDtoList")
    RecommendationDto toDto(Recommendation recommendation);

    Recommendation toEntity(RecommendationDto recommendationDto);

    @Named("mapToDtoList")
    default List<SkillOfferDto> mapToDtoList(List<SkillOffer> skillOffers) {
        return skillOffers.stream()
                .map(this::mapToDto)
                .toList();
    }

    private SkillOfferDto mapToDto(SkillOffer skillOffer) {
        SkillOfferDto skillOfferDto = new SkillOfferDto();
        skillOfferDto.setId(skillOffer.getId());
        skillOfferDto.setSkillId(skillOffer.getSkill().getId());
        return skillOfferDto;
    }
}
