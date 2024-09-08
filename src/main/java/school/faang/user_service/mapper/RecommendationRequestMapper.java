package school.faang.user_service.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recomendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recomendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationRequestMapper {
    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(target = "skills", qualifiedByName = "skillsToIds")
    RecommendationRequestDto toDto(RecommendationRequest recommendationRequest);

    @Mapping(target = "skills", ignore = true)
    @Mapping(source = "requesterId", target = "requester.id")
    @Mapping(source = "receiverId", target = "receiver.id")
    RecommendationRequest toEntity(CreateRecommendationRequestDto dto);

    default List<RecommendationRequestDto> listEntitiesToListDto(List<RecommendationRequest> requests) {
        return requests.stream().map(this::toDto).toList();
    }

    @Named("skillsToIds")
    default List<Long> mapSkillsToIds(List<SkillRequest> skills) {
        if (skills == null) {
            return new ArrayList<>();
        }

        return skills.stream()
                .map(s -> s.getSkill().getId())
                .toList();
    }
}