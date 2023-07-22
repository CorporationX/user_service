package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.service.RecommendationService;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {SkillOfferMapper.class})
public interface RecommendationMapper {

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    RecommendationDto toDto(Recommendation recommendation);

    @Mapping(target = "author", expression = "java(recommendationService.getUser(recommendationDto.getAuthorId()))")
    @Mapping(target = "receiver", expression = "java(recommendationService.getUser(recommendationDto.getReceiverId()))")
    Recommendation toEntity(RecommendationDto recommendationDto, RecommendationService recommendationService);


}

