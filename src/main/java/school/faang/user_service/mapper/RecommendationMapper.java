package school.faang.user_service.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE, uses = SkillOfferMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RecommendationMapper {

    Recommendation toEntity(RecommendationDto recommendationDto);

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "skillOffers", target = "skillOffers", qualifiedByName = "toSkillOfferDtos")
    RecommendationDto toDto(Recommendation recommendation);

    @Named("toRecommendationDtos")
    default List<RecommendationDto> toRecommendationDtos(List<Recommendation> recommendations) {
        return recommendations.stream()
                .map(this::toDto).toList();
    }
}