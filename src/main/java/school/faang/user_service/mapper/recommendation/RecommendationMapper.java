package school.faang.user_service.mapper.recommendation;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = SkillOfferMapper.class)
public interface RecommendationMapper {
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "skillOffers", target = "skillOffers", qualifiedByName = "toListOffersDto")
    RecommendationDto toDto(Recommendation recommendation);

    @Mapping(target = "author.id", source = "authorId")
    @Mapping(target = "receiver.id", source = "receiverId")
    @Mapping(target = "skillOffers", source = "skillOffers", qualifiedByName = "toListOffersEntity", ignore = true)
    Recommendation toEntity(RecommendationDto recommendationDto);

    default List<RecommendationDto> toListDto(List<Recommendation> recommendations) {
        return recommendations.stream().map(this::toDto).toList();
    }
}