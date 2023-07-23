package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationMapper {
    RecommendationMapper INSTANCE = Mappers.getMapper(RecommendationMapper.class);

    RecommendationDto toDto(Recommendation entity);

    Recommendation toEntity(RecommendationDto dto);

}
