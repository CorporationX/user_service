package school.faang.user_service.mapper.recommendation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.util.List;

@Mapper(componentModel = "spring",  uses = SkillOfferMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationMapper {

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "receiver.id", target = "receiverId")
    RecommendationDto toDto(Recommendation recommendation);

    @Mapping(
            target = "createdAt",
            expression = "java(recommendationDto.getCreatedAt() != null ?" +
                    " recommendationDto.getCreatedAt() :" +
                    " java.time.LocalDateTime.now())"
    )
    Recommendation toEntity(RecommendationDto recommendationDto);

    List<RecommendationDto> toDto(List<Recommendation> recommendations);
    List<Recommendation> toEntity(List<RecommendationDto> recommendationsDto);
}
