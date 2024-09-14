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
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationMapper {

    @Mapping(source = "skillOffers", target = "skillOffers", qualifiedByName = "mapToSkillOfferDtoList")
    RecommendationDto toDto(Recommendation recommendation);

    Recommendation toEntity(RecommendationDto recommendationDto);

    @Named("mapToSkillOfferDtoList")
    default List<SkillOfferDto> mapToSkillOfferDtoList(List<SkillOffer> skillOffers) {
        return skillOffers.stream()
                .map(this::mapToSkillOfferDto)
                .collect(Collectors.toList());
    }

    private SkillOfferDto mapToSkillOfferDto(SkillOffer skillOffer) {
        SkillOfferDto dto = new SkillOfferDto();
        dto.setId(skillOffer.getId());
        dto.setSkillId(skillOffer.getSkill().getId());
        return dto;
    }
}
