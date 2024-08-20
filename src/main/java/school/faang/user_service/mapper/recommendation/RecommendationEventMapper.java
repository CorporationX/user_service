package school.faang.user_service.mapper.recommendation;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.event.recommendation.RecommendationEvent;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationEventMapper {
    @Mapping(source = "id", target = "recommendationId")
    @Mapping(source = "authorId", target = "authorId")
    @Mapping(source = "receiverId", target = "receiverId")
    @Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
    RecommendationEvent toEvent(RecommendationDto dto);
}
