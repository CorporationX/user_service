package school.faang.user_service.mapper.recommendation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendations.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;

/**
 * @author Alexander Bulgakov
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationMapper {
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "skillOffers", target = "skillOfferDtoList")
    RecommendationDto toDto(Recommendation recommendation);

//    @Mapping(source = "authorId", target = "author")
//    @Mapping(source = "receiverId", target = "receiver")
    @Mapping(source = "skillOfferDtoList", target = "skillOffers")
    Recommendation toEntity(RecommendationDto recommendationDto);
}
