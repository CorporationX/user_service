package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.recommendation.CreateRecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.UpdateRecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface RecommendationMapper {

    Recommendation toRecommendation(CreateRecommendationDto recommendationDto);

    void update(UpdateRecommendationDto recommendationDto, @MappingTarget Recommendation entity);

    RecommendationDto toRecommendationDto(Recommendation recommendation);
}
