package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RequestStatusDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RecommendationRequestMapper {

    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    @Mapping(target = "recommendation", ignore = true)
    @Mapping(target = "status", source = "status", qualifiedByName = "mapStatusToEntity")
    RecommendationRequest toEntity(RecommendationRequestDto dto);

    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(target = "skillIds", expression = "java(mapSkillsToIds(entity.getSkills()))")
    @Mapping(target = "status", source = "status", qualifiedByName = "mapStatusToDto")
    RecommendationRequestDto toDto(RecommendationRequest entity);

    @Named("mapStatusToDto")
    default RequestStatusDto mapStatusToDto(RequestStatus status) {
        return status != null ? new RequestStatusDto(status.name()) : null;
    }

    @Named("mapStatusToEntity")
    default RequestStatus mapStatusToEntity(RequestStatusDto statusDto) {
        if (statusDto == null || statusDto.getStatus() == null) {
            return null;
        }

        try {
            return RequestStatus.valueOf(statusDto.getStatus());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + statusDto.getStatus());
        }
    }

    default List<Long> mapSkillsToIds(List<SkillRequest> skills) {
        return skills == null ? Collections.emptyList() :
                skills.stream().map(skillRequest -> skillRequest.getSkill().getId()).collect(Collectors.toList());
    }
}
