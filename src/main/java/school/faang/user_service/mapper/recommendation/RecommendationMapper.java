package school.faang.user_service.mapper.recommendation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.event.RecommendationEventPublisher;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = SkillOfferMapper.class)
public interface RecommendationMapper {

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "receiver.id", target = "receiverId")
    RecommendationDto toDto(Recommendation recommendation);

    @Mapping(source = "authorId", target = "author.id")
    @Mapping(source = "receiverId", target = "receiver.id")
    Recommendation toEntity(RecommendationDto dto);

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "createdAt", target = "receivedAt")
    RecommendationEventPublisher toRecommendationEventPublisher(Recommendation recommendation);
}