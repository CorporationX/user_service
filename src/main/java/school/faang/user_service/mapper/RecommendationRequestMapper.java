package school.faang.user_service.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.SkillRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = SkillRequestMapper.class
)
public interface RecommendationRequestMapper {

    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "recieverId")
    @Mapping(source = "skills", target = "skillIds")
    RecommendationRequestDto toDto(RecommendationRequest recommendationRequest);

    @Mapping(source = "requesterId", target = "requester.id")
    @Mapping(source = "recieverId", target = "receiver.id")
    @Mapping(source = "skillIds", target = "skills")
    RecommendationRequest toEntity(RecommendationRequestDto recommendationRequestDto);

    List<SkillRequestDto> toListDto(List<SkillRequest> skillRequests);

    List<SkillRequest> toListEntity(List<SkillRequestDto> skillRequestsDto);
}
