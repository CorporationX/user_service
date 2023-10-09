package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationUpdateDto;
import school.faang.user_service.entity.recommendation.Recommendation;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {SkillOfferMapper.class})
public interface RecommendationMapper {

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    RecommendationDto toDto(Recommendation recommendation);

    @Mapping(target = "author.id", source = "authorId")
    @Mapping(target = "receiver.id", source = "receiverId")
    Recommendation toEntity(RecommendationDto recommendationDto);

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    RecommendationUpdateDto toUpdateDto(Recommendation recommendation);

    @Mapping(target = "author.id", source = "authorId")
    @Mapping(target = "receiver.id", source = "receiverId")
    Recommendation toUpdateEntity(RecommendationUpdateDto recommendationUpdateDto);
}

