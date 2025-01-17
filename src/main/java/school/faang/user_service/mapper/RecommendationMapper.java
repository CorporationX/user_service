package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationMapper {

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(target = "skillOffers", ignore = true)
    RecommendationDto toDto(Recommendation recommendation);

    @Mapping(target = "author", expression = "java(mapUser(recommendationDto.getAuthorId()))")
    @Mapping(target = "receiver", expression = "java(mapUser(recommendationDto.getReceiverId()))")
    @Mapping(target = "skillOffers", ignore = true)
    @Mapping(target = "createdAt", source = "createdAt")
    Recommendation toEntity(RecommendationDto recommendationDto);

    List<RecommendationDto> toRecommendationDtoList(List<Recommendation> entities);

    default User mapUser(Long userId) {
        if (userId == null) {
            return null;
        }
        return User.builder()
                .id(userId)
                .build();
    }
}
