package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationMapper {
    @Mapping(target = "id", ignore = true)
    RecommendationDto toDto(Recommendation recommendation);
    @Mapping(target = "id", ignore = true)
    Recommendation toEntity(RecommendationDto recommendationDto);
}
