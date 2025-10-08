package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface RecommendationMapper {

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorUsername", source = "author.username")
    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "receiverUsername", source = "receiver.username")
    @Mapping(target = "skillIds", source = "skillOffers", qualifiedByName = "mapSkillOffersToSkillIds")
    RecommendationDto toRecommendationDto(Recommendation recommendation);

    @Named("mapSkillOffersToSkillIds")
    default List<Long> mapSkillOffersToSkillIds(List<SkillOffer> skillOffers) {
        if (skillOffers == null) {
            return Collections.emptyList();
        }
        return skillOffers.stream()
                .map(offer -> offer.getSkill().getId())
                .toList();
    }
}
