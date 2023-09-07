package school.faang.user_service.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.redis.RecommendationEventDto;
import school.faang.user_service.dto.redis.EventRecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.publisher.RecommendationEvent;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = SkillOfferMapper.class)
public interface RecommendationMapper {
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "skillOffers", target = "skillOffers", qualifiedByName = "listSkillOffersDto")
    RecommendationDto toDto(Recommendation recommendation);

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    RecommendationEventDto toRecommendationEventDto(Recommendation recommendation);
  
    @Mapping(source = "id", target = "recommendationId")
    @Mapping(source = "author.id", target = "actorId")
    EventRecommendationDto toEventDto(Recommendation recommendation);
}
