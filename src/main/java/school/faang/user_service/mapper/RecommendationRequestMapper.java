package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationRequestMapper {

    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    public RecommendationRequest toEntity(RecommendationRequestDto requestDto);

    @Mapping(source = "skills", target = "skillIds", qualifiedByName = "mapSkills")
    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    public RecommendationRequestDto toDto(RecommendationRequest request);

    @Named("mapSkills")
    default List<Long> mapSkills(List<SkillRequest> skills) {
        if (skills != null) {
            return skills.stream().map(SkillRequest::getId).toList();
        }
        return new ArrayList<>();
    }
}
