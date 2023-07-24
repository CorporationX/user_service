package school.faang.user_service.mappers;

import org.mapstruct.*;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.util.List;


@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = SkillOfferMapper.class)
public interface RecommendationMapper {
    @Mapping(source = "skillOffers", target = "skillOffers", qualifiedByName = "toSkillOfferDtos")
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "receiver.id", target = "receiverId")
    RecommendationDto toDto(Recommendation entity);


    default List<RecommendationDto> toRecommendationDtos(List<Recommendation> recommendations) {
        return recommendations.stream()
                .map(this::toDto)
                .toList();
    }
}
