package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface RecommendationRequestMapper {

    @Mapping(source = "requesterId", target = "requester", qualifiedByName = "idToUser")
    @Mapping(source = "receiverId", target = "receiver", qualifiedByName = "idToUser")
    @Mapping(source = "status", target = "status", qualifiedByName = "stringToEnum")
    @Mapping(source = "skillIds", target = "skills", qualifiedByName = "skillRequestIdToSkillRequest")
    RecommendationRequest toEntity(RecommendationRequestDto recommendationRequestDto);

    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "status", target = "status", qualifiedByName = "enumToString")
    @Mapping(source = "skills", target = "skillIds", qualifiedByName = "skillRequestToSkillRequestId")
    RecommendationRequestDto toDto(RecommendationRequest recommendationRequest);

    @Named("idToUser")
    default User idToUser(Long id) {
        if (id == null) return null;
        User user = new User();
        user.setId(id);
        return user;
    }

    @Named("stringToEnum")
    default RequestStatus stringToEnum(String status) {
        if (status == null) return null;
        return RequestStatus.valueOf(status.toUpperCase());
    }

    @Named("skillRequestToSkillRequestId")
    default List<Long> skillRequestToSkillRequestId(List<SkillRequest> skillRequestList) {
        return skillRequestList.stream()
                .map(SkillRequest::getId)
                .toList();
    }

    @Named("skillRequestIdToSkillRequest")
    default List<SkillRequest> skillRequestIdToSillRequestId(List<Long> skillRequestIdList) {
        return skillRequestIdList.stream()
                .map(id -> {
                    SkillRequest skillRequest = new SkillRequest();
                    skillRequest.setId(id);
                    return skillRequest;
                })
                .toList();
    }

    @Named("enumToString")
    default String enumToString(RequestStatus status) {
        if (status == null) return null;
        return status.name();
    }
}