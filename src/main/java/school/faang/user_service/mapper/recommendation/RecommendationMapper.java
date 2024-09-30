package school.faang.user_service.mapper.recommendation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.model.dto.recommendation.RecommendationDto;
import school.faang.user_service.model.dto.recommendation.SkillOfferDto;
import school.faang.user_service.model.entity.recommendation.Recommendation;
import school.faang.user_service.model.entity.recommendation.SkillOffer;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationMapper {

    @Mapping(source = "skillOffers", target = "skillOffers", qualifiedByName = "mapSkillOffers")
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "receiver.id", target = "receiverId")
    RecommendationDto toDto(Recommendation recommendation);

    @Mapping(target = "skillOffers", ignore = true)
    @Mapping(source = "authorId", target = "author.id")
    @Mapping(source = "receiverId", target = "receiver.id")
    Recommendation toEntity(RecommendationDto recommendationDto);

    @Named("mapSkillOffers")
    default List<SkillOfferDto> mapSkillOffers(List<SkillOffer> skillOffers) {
        return skillOffers
                .stream()
                .map(skillOffer -> SkillOfferDto
                        .builder()
                        .id(skillOffer.getId())
                        .skillId(skillOffer.getSkill().getId())
                        .build())
                .toList();
    }
}
