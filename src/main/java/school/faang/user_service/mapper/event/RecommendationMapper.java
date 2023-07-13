package school.faang.user_service.mapper.event;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.RecomendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationMapper {
    RecomendationDto toDto(Recommendation recommendation);
    Recommendation toEntity(RecomendationDto recommendationDto);
}
