package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface RecommendationMapper {
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    RecommendationDto toRecommendationDto(Recommendation recommendation);

}
