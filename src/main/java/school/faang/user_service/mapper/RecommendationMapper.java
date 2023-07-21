package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationMapper {

    @Mapping(target = "authorId", expression = "java(recommendation.getAuthorId())")
    @Mapping(target = "receiverId", expression = "java(recommendation.getReceiverId())")
    @Mapping(target = "skillOffers", expression = "java(recommendationDto.getSkillOffers())")
    RecommendationDto toDto(Recommendation recommendation);


    @Mapping(target = "author", expression = "java(recommendationDto.getAuthorId())")
    @Mapping(target = "receiver", expression = "java(recommendationDto.getReceiverId())")
    @Mapping(target = "skillOffers", expression = "java(recommendationDto.getSkillOffers())")
    Recommendation toEntity(RecommendationDto recommendationDto);
}
